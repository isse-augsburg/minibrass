package isse.mbr.tools.execution;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.SystemUtils;

/**
 * API connection to access MiniZinc starting from 2.2.1 
 * The class is responsible for executing MiniZinc with a given configuration
 * and providing the 
 * @author alexander
 *
 */
public class MiniZincRunner {
	// Configuration settings for the model and executing solver
	private MiniZincConfiguration configuration;
	
	// Configuration settings for the runner
	private boolean doLog = true;
	private final static Logger LOGGER = Logger.getGlobal();

	// Constants 
	private final static int NO_TIMEOUT = -1;
	
	/**
	 * {@code timeout} defaults to no time out at all (-1)
	 *
	 * @see MiniZincRunner#solve(File, int)
	 */
	public MiniZincResult solve(File modelFile) {
		return solve(modelFile, NO_TIMEOUT);
	}
	
	/**
	 * {@code dataFiles} defaults to the empty list.
	 *
	 * @see MiniZincRunner#solve(File, List, int)
	 */
	public MiniZincResult solve(File modelFile, int timeout) {
		return solve(modelFile, Collections.emptyList(), timeout);
	}
	
	/**
	 * {@code timeout} defaults to no time out at all (-1)
	 *
	 * @see MiniZincRunner#solve(File, dataFiles, int)
	 */
	public MiniZincResult solve(File modelFile, List<File> dataFiles) {
		return solve(modelFile, Collections.emptyList(), NO_TIMEOUT);
	}
	
	/**
	 * Performs a call to MiniZinc with the solver defined in the {@code configuration} object
	 * @param modelFile one MiniZinc constraint model file
	 * @param dataFiles 0 or more data files
	 * @param timeout in seconds
	 */
	public MiniZincResult solve(File modelFile, List<File> dataFiles, int timeout) {
		ProcessBuilder pb = getDefaultProcessBuilder();		
		if(configuration == null) {
			LOGGER.info("No configuration specified, resorting to default configuration");
			configuration = new MiniZincConfiguration();
		}
			
		addOptions(pb);
		
		pb.command().add(modelFile.getPath());
		
		// finally add all data files (only valid if we are not reading from stdin
		if(dataFiles != null) {
			for(File dataFile : dataFiles) {
				String dataPath = dataFile != null ? dataFile.getPath() : "";
				pb.command().add(dataPath);
			}
		}
			
		MiniZincResult result = runProcess(pb, timeout);		
		return result;
	}

	private MiniZincResult runProcess(ProcessBuilder pb, int timeout) {
		MiniZincResult result = new MiniZincResult();
		
		LOGGER.info("About to start: " + pb.command());
				
		pb.inheritIO();
		pb.redirectErrorStream(true);
		pb.redirectOutput(Redirect.PIPE);
		
		final Process p;
		try {
			p = pb.start();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			
			List<String> lines = new LinkedList<>();
			while ((line = reader.readLine()) != null) {
				lines.add(line);
				result.lookForError(line);
			}
			result.processRawMiniZincOutputLines(lines);
			if(timeout > 0) {
				Timer timer = new Timer();
				int timeoutInMillisecs = timeout * 1000;
				timer.schedule(new TimerTask() {
	
					@Override
					public void run() {
						LOGGER.info("Destroyed by timeout ... ");
						p.destroy();
					}
				}, Math.round(timeoutInMillisecs * 1.05));
	
				p.waitFor();
				timer.cancel();	
			}
			cleanup();
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			result.invalidate();
		} 
		return result;
	}
	
	/**
	 * {@code dataContents} defaults to the empty list.
	 *
	 * @see MiniZincRunner#solve(String, List)
	 */
	public MiniZincResult solve(String modelContent) {
		return solve(modelContent, Collections.emptyList());
	}
	
	public MiniZincResult solve(String modelContent, List<String> dataContents) {
		// TODO implement
		throw new NotImplementedException("Solving without intermediate files is not supported yet.");
	}
	
	private void cleanup() {
		try {
			// need to make sure fzn-gecode is truly killed
			// have to implement that also for windows
			String killScriptPath = "./killscript.sh";
			
			// if is windows ...
			if(SystemUtils.IS_OS_WINDOWS) {
				killScriptPath = "./killscript.cmd";
			}
			ProcessBuilder killBuilder = null;
			File killScript = new File(killScriptPath);
			
			if(killScript.exists()) {
				killBuilder = new ProcessBuilder(killScriptPath);
			} else {
				if(doLog)
					LOGGER.warning("Killscript not found locally. Trying in jar location");
				URL jarLocation = getClass().getProtectionDomain().getCodeSource().getLocation();	
				File classPathDir = new File(jarLocation.toURI());
				killScript = new File(classPathDir.getParent(), killScriptPath);
				if(killScript.exists()) {
					killBuilder = new ProcessBuilder(killScript.getAbsolutePath());
				} else {
					if(doLog)
						LOGGER.warning("No killscript found at " +killScript.getAbsolutePath()+ ". Do not attempt further cleanup.");
				}
			}
			
			if(killBuilder != null) {
				killBuilder.redirectErrorStream(true);
				
				Process killProcess = killBuilder.start();
				killProcess.waitFor();
				}
		} catch (IOException e1) {
			LOGGER.severe(e1.getMessage());
		} catch (InterruptedException e) {
			LOGGER.severe(e.getMessage());
		} catch (URISyntaxException e) {
			LOGGER.severe(e.getMessage());
		}
	}
	
	//================== Private utility methods =====================
	
	private ProcessBuilder getDefaultProcessBuilder() {
		ProcessBuilder pb;
		pb = new ProcessBuilder("minizinc" );
		return pb;
	}
	

	private void addOptions(ProcessBuilder pb) {
		pb.command().add("--output-mode");
		pb.command().add("dzn");
		
		if(configuration.isUseAllSolutions())
			pb.command().add("-a");
		if(configuration.getSolverId() != null) {
			pb.command().add("--solver");
			pb.command().add(configuration.getSolverId());
		}
		if(configuration.getTimeout() != null)  {
			pb.command().add("--time-limit");
			pb.command().add(configuration.getTimeout().toString());
		}
	}
	
	//================== Setters and getters start =====================
	public MiniZincConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(MiniZincConfiguration configuration) {
		this.configuration = configuration;
	}
}
