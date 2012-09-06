package uk.ac.diamond.scisoft.rp.api;

import java.io.File;

public class FieldVerifyUtils {

		
	public static boolean isFile(String input) {
		File file = new File(input);
		return file.isFile();
	}
	
	public static boolean isFolder(String input) {
		File file = new File(input);		
		return file.isDirectory();
	}
	
	public static boolean isOutputValid(String input) {
		File file = new File(input);	
		if(file == null){
			return false;
		}
		return file.getParentFile().isDirectory() && !file.isDirectory();
	}

	public static boolean isPositiveInteger(String input) {
		if (isInteger(input)) {
			int i = Integer.parseInt(input);
			if (i >= 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isNumeric(String input) {
		try {
			Double.parseDouble(input);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static boolean isPossitiveNumeric(String input) {
		if (isNumeric(input)) {
			double d = Double.parseDouble(input);
			if (d > 0) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isNonNegNumeric(String input) {
		if (isNumeric(input)) {
			double d = Double.parseDouble(input);
			if (d >= 0) {
				return true;
			}
		}
		return false;
	}

}
