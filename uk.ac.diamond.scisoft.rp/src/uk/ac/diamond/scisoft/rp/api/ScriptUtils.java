package uk.ac.diamond.scisoft.rp.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author vgb98675
 * 
 */
public class ScriptUtils {

	private static Process process = null;

	// used for testing
	public static void main(String[] args) {
		System.out.println(getAbsoluteScriptPath());		
	}


	/**
	 * Gets the absolute path of the script where it is been run from, this script folder is in the same directory as the scr folder
	 * @return path
	 */
	public static String getAbsoluteScriptPath() {

		java.security.ProtectionDomain pd = ScriptUtils.class
				.getProtectionDomain();
		if (pd == null) {
			System.out.print("No path found returning null");
			return null;
		}
		java.security.CodeSource cs = pd.getCodeSource();
		if (cs == null) {
			System.out.print("No path found returning null");
			return null;
		}
		java.net.URL url = cs.getLocation();
		if (url == null) {
			System.out.print("No path found returning null");
			return null;
		}

		java.io.File f;
		try {
			f = new File(url.getFile());
		} catch (Exception e) {
			System.out.print("No path found returning null");
			return null;
		}
		
		String resultTest = f.getAbsolutePath();

		if (resultTest == null) {
			System.out.print("No path found returning null");
			return null;
		}

		if (isFolder(resultTest + "/scripts")) { //not in script folder
			return resultTest + "/scripts/";
		}
		File file = new File(resultTest);
		return file.getParent()+"/scripts/";

	}
	
	private static boolean isFolder(String input) {
		File file = new File(input);
		return file.isDirectory();
	}	
	
	/**
	 * Gets the absolute path of where it is been run from
	 * @return path
	 */
	public static String getAbsolutePath() {
		java.security.ProtectionDomain pd = ScriptUtils.class
				.getProtectionDomain();
		if (pd == null) {
			System.out.print("No path found returning null");
			return null;
		}
		java.security.CodeSource cs = pd.getCodeSource();
		if (cs == null) {
			System.out.print("No path found returning null");
			return null;
		}
		java.net.URL url = cs.getLocation();
		if (url == null) {
			System.out.print("No path found returning null");
			return null;
		}
		java.io.File f;
		try {
			f = new File(url.getFile());
		} catch (Exception e) {
			System.out.print("No path found returning null");
			return null;
		}

		String resultTest = f.getAbsolutePath();
		if (resultTest == null) {
			System.out.print("No path found returning null");
			return null;
		}		
		return resultTest;	
	}


	/**
	 * Runs given command and writes output to System.out.println
	 * @param list the list of commands, where the first item is usualy the file to be run and all subsequent items are the arguments 
	 */
	public static void runScript(List<String> list) {

		ProcessBuilder processBuilder = new ProcessBuilder(list);

		processBuilder.redirectErrorStream(true);
		try {
			process = processBuilder.start();
		} catch (IOException ioe) {
			System.out.println(ioe);
			process = null;
		}

		if (process != null) {
			BufferedReader brOut = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			try {
				while ((line = brOut.readLine()) != null) {
					System.out.println(line);
				}
				brOut.close();
			} catch (IOException ioe) {
				System.out.println(ioe);
			}
		}

	}

}