package isse.mbr.integration;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;


/**
 * This class allows easy access to MiniZinc/MiniSearch models and their solutions
 * @author Alexander Schiendorfer
 *
 */
public class MiniZincLauncher {
	Collection<MiniZincResultListener> listeners;

	private String flatzincExecutable = "fzn-gecode";
	private String minizincGlobals = "gecode";  
	private boolean useDefault = false; // passing no arguments to minizinc/minisearch
	private boolean debug = false;
	
	private final static Logger LOGGER = Logger.getGlobal();
	
	public MiniZincLauncher() {
		listeners = new LinkedList<>();
	}
	
	public void addMiniZincResultListener(MiniZincResultListener listener){
		listeners.add(listener);
	}
	

	public void runMiniZincModel(File model, File data, int timeout) {
		// for now, just use Gecode
		
		int timeoutInMillisecs = timeout*1000; // wait for 30 seconds
	
		String dataPath = data != null ? data.getPath() : "";
		ProcessBuilder pb;
		if(useDefault) {
			pb = new ProcessBuilder("minizinc", "-a", model.getPath());
		} else {
			pb = new ProcessBuilder("minizinc", "-a","-f",flatzincExecutable, "-G"+minizincGlobals, model.getPath());
		}
		
		if(data != null)
			pb.command().add(dataPath);
		
		runProcess(pb, timeoutInMillisecs);
		cleanup();
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
		
		runProcess(pb, timeoutInMillisecs);
		cleanup();
	}

	private void runProcess(ProcessBuilder pb, int timeoutInMillisecs) {
		LOGGER.info("About to start: " + pb.command());
		System.out.println("About to start: " + pb.command());
		File log = new File("log");
		if (log.exists())
			log.delete();

		pb.redirectErrorStream(true);
		pb.redirectOutput(Redirect.to(log));

		final Process p;
		try {
			p = pb.start();
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

			assert pb.redirectInput() == Redirect.PIPE;
			assert pb.redirectOutput().file() == log;
			assert p.getInputStream().read() == -1;

						
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// process the result as desired: 
		processResult(log);
	}
	
	private void cleanup() {
		// need to make sure fzn-gecode is truly killed
		ProcessBuilder killBuilder = new ProcessBuilder("./killscript.sh");
		killBuilder.redirectErrorStream(true);
		try {
			Process killProcess = killBuilder.start();
			killProcess.waitFor();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void processResult(File log) {
		Scanner sc = null;
		final String optimalitySep = "==========";
		final String solutionSep = "----------";
		boolean error = false;
		try {
			sc = new Scanner(log);

			int noSolutions = 0; // how many solutions did we actually see
									// during optimization
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
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

	public String getFlatzincExecutable() {
		return flatzincExecutable;
	}

	public void setFlatzincExecutable(String flatzincExecutable) {
		this.flatzincExecutable = flatzincExecutable;
	}

	public String getMinizincGlobals() {
		return minizincGlobals;
	}

	public void setMinizincGlobals(String minizincGlobals) {
		this.minizincGlobals = minizincGlobals;
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

}
