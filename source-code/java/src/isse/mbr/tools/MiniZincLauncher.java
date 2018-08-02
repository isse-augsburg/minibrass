package isse.mbr.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import isse.mbr.tools.processsources.DefaultMiniZincSource;


/**
 * This class allows easy access to MiniZinc/MiniSearch models and their solutions
 * @author Alexander Schiendorfer
 *
 */
public class MiniZincLauncher {
	Collection<MiniZincResultListener> listeners;


	private boolean useDefault = false; // passing no arguments to minizinc/minisearch 
	private boolean useAllSolutions = true;
	private String flatzincExecutable = "fzn-gecode";
	private String minizincGlobals = "gecode";  
	private MiniZincProcessSource processSource;
	private boolean doLog = true;


	private boolean debug = false;
	private boolean deleteAfterRun = false;
	private File lastModel;
	
	private final static Logger LOGGER = Logger.getGlobal();
	
	public MiniZincLauncher() {
		listeners = new LinkedList<>();
		processSource = new DefaultMiniZincSource();
	}
	
	public void addMiniZincResultListener(MiniZincResultListener listener){
		listeners.add(listener);
	}
	
	public void runMiniZincModel(File model, List<File> dataFiles, int timeout) {
		// for now, just use Gecode
		
		int timeoutInMillisecs = timeout * 1000; // wait for 30 seconds
	
		ProcessBuilder pb = processSource.getMiniZincProcessBuilder();

		pb.command().add(model.getPath());
		
		if(dataFiles != null) {
			for(File dataFile : dataFiles) {
				String dataPath = dataFile != null ? dataFile.getPath() : "";
				pb.command().add(dataPath);
			}
		}
		
			
		lastModel = model;
		
		runProcess(pb, timeoutInMillisecs);
		cleanup();
	}
	
	/**
	 * Only call this method if data is not null (i.e., you have data)
	 * If you do not want to pass any data, please call it with an empty list
	 * @param model
	 * @param data
	 * @param timeout
	 */
	public void runMiniZincModel(File model, File data, int timeout) {
		runMiniZincModel(model, Arrays.asList(data), timeout);
	}
	
	public void runMiniSearchModel(File model, File data, int timeout) {
		// for now, just use Gecode
		
		int timeoutInMillisecs = timeout*1000; // wait for 30 seconds
	
		String dataPath = data != null ? data.getPath() : "";
		ProcessBuilder pb;
		if(useDefault) {
			pb = new ProcessBuilder("minisearch", model.getPath());
		} else {
			pb = new ProcessBuilder("minisearch","--solver",flatzincExecutable, "-G"+minizincGlobals, model.getPath());
		}
		if(data != null)
			pb.command().add(dataPath);
		lastModel = model;
		
		runProcess(pb, timeoutInMillisecs);
		
		cleanup();
	}

	private void runProcess(ProcessBuilder pb, int timeoutInMillisecs) {
		LOGGER.info("About to start: " + pb.command());
		System.out.println("About to start: " + pb.command());
		File log = new File("log");
		if (log.exists())
			log.delete();

		pb.inheritIO();
		pb.redirectErrorStream(true);
		pb.redirectOutput(Redirect.PIPE);
		
		final Process p;
		try {
			p = pb.start();
			// these lines manage to write both to stdout and the log file 
			FileWriter fw = new FileWriter(log);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				System.out.println(line);
				fw.write(line);
				fw.write(System.getProperty("line.separator"));
			}
			fw.close();
			
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					LOGGER.info("Destroyed by timeout ... ");
					p.destroy();
				}
			}, Math.round(timeoutInMillisecs * 1.05));

			p.waitFor();
			timer.cancel();

						
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// process the result as desired: 
		processResult(log);
	}
	
	public boolean isDoLog() {
		return doLog;
	}

	public void setDoLog(boolean doLog) {
		this.doLog = doLog;
	}
	
	private void cleanup() {
		try {
			// need to make sure fzn-gecode is truly killed
			
			ProcessBuilder killBuilder = null;
			File killScript = new File("./killscript.sh");
			
			if(killScript.exists()) {
				killBuilder = new ProcessBuilder("./killscript.sh");
			} else {
				if(doLog)
					LOGGER.warning("Killscript not found locally. Trying in jar location");
				URL jarLocation = getClass().getProtectionDomain().getCodeSource().getLocation();	
				File classPathDir = new File(jarLocation.toURI());
				killScript = new File(classPathDir.getParent(), "killscript.sh");
				if(killScript.exists()) {
					killBuilder = new ProcessBuilder(killScript.getAbsolutePath());
				} else {
					if(doLog)
						LOGGER.severe("No killscript found at " +killScript.getAbsolutePath()+ ". Do not attempt further cleanup.");
				}
			}
			
			if(killBuilder != null) {
				killBuilder.redirectErrorStream(true);
				
				Process killProcess = killBuilder.start();
				killProcess.waitFor();
				}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(deleteAfterRun)
			lastModel.delete();
	}

	public void processResult(File log) {
		Scanner sc = null;
		final String optimalitySep = "==========";
		final String solutionSep = "----------";
		StringBuilder outputBuilder = new StringBuilder();
		boolean error = false;
		try {
			sc = new Scanner(log);

			int noSolutions = 0; // how many solutions did we actually see
									// during optimization
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				outputBuilder.append(line);
				outputBuilder.append("\n");
				
				boolean broadcast = true;	
				if(debug)
					System.out.println(line);

				if (line.contains(optimalitySep)) {
					// System.out.println("Solved optimally!");
					for(MiniZincResultListener listener : listeners) {
						listener.notifySolved();
						listener.notifyOptimality();
					}
					broadcast = false;
				}

				if (line.contains(solutionSep)) {
					++noSolutions;
					for(MiniZincResultListener listener : listeners) {
						listener.notifySolved();
					}
					broadcast = false;
				}
				
				if(line.toLowerCase().contains("error") ) {
					broadcast = false; // not really necessary, but I like it for clarity
					error = true;
				}
				
				if(broadcast) {
					for(MiniZincResultListener listener : listeners) {
						listener.notifyLine(line);
					}
				}
				
			}
			String output = outputBuilder.toString();
			for(MiniZincResultListener listener : listeners) {
				listener.notifyOutput(output);
			}
	
			LOGGER.fine("All in all, we saw "+noSolutions + " solutions.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sc != null)
				sc.close();
		}
		if(error) 
			System.err.println("Apparently, an error happened.");
	}
	
	public static void main(String[] args) {
		MiniZincLauncher mznLauncher = new MiniZincLauncher();
		File model = new File("mzn-model.mzn");
		File data = new File("data.dzn");
		
		mznLauncher.runMiniSearchModel(model, data, 5);

	}


	public boolean isUseDefault() {
		return useDefault;
	}

	public void setUseDefault(boolean useDefault) {
		this.useDefault = useDefault;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isDeleteAfterRun() {
		return deleteAfterRun;
	}

	public void setDeleteAfterRun(boolean deleteAfterRun) {
		this.deleteAfterRun = deleteAfterRun;
	}
	
	public boolean isUseAllSolutions() {
		return useAllSolutions;
	}

	public void setUseAllSolutions(boolean useAllSolutions) {
		this.useAllSolutions = useAllSolutions;
		// TODO refactor, should not be done here
		if(processSource instanceof DefaultMiniZincSource) {
			MiniZincProcessSource dms = processSource;
			dms.setUseAllSolutions(useAllSolutions);
		}
	}

	public String getFlatzincExecutable() {
		return flatzincExecutable;
	}

	public void setFlatzincExecutable(String flatzincExecutable) {
		this.flatzincExecutable = flatzincExecutable;
		// TODO refactor, should not be done here
		if(processSource instanceof DefaultMiniZincSource) {
			DefaultMiniZincSource dms = (DefaultMiniZincSource) processSource;
			dms.setFlatzincExecutable(flatzincExecutable);
		}
	}

	public String getMinizincGlobals() {
		return minizincGlobals;
	}

	public void setMinizincGlobals(String minizincGlobals) {
		// TODO refactor, should not be done here
		if(processSource instanceof DefaultMiniZincSource) {
			DefaultMiniZincSource dms = (DefaultMiniZincSource) processSource;
			dms.setMinizincGlobals(minizincGlobals);
		}
		
		this.minizincGlobals = minizincGlobals;
	}

	public MiniZincProcessSource getProcessSource() {
		return processSource;
	}

	public void setProcessSource(MiniZincProcessSource processSource) {
		this.processSource = processSource;
	}
}
