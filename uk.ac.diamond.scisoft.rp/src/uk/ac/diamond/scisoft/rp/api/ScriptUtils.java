/**
 * 
 */
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
		java.io.File f = new File(url.getFile());
		if (f == null) {
			System.out.print("No path found returning null");
			return null;
		}

		String resultTest = f.getAbsolutePath();

		if (resultTest == null) {
			System.out.print("No path found returning null");
			return null;
		}

		if (FieldVerifyUtils.isFolder(resultTest + "/scripts")) { //not in script folder
			return resultTest + "/scripts/";
		} else {
			File file = new File(resultTest);
			return file.getParent()+"/scripts/";
		}

	}
	
	
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
		java.io.File f = new File(url.getFile());
		if (f == null) {
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