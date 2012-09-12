package uk.ac.diamond.scisoft.rp.api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author vgb98675 Contains static methods for generating .info files.
 */
public class AvizoImageUtils {

	/**
	 * Generates the contents of the .info file as a string based on a list of
	 * files.
	 * 
	 * @param files
	 *            ArrayList<String> of absolute path names of the images to be
	 *            used
	 * @param pixelSizeX
	 *            how many pixels each pixel in the set of images represents
	 *            along the x axis
	 * @param pixelSizeY
	 *            how many pixels each pixel in the set of images represents
	 *            along the y axis
	 * @param zPositionInc
	 *            the increment in position between the stacked images along the
	 *            z axis
	 * @return result the text which makes up the .info file
	 */
	private static String getStackedImageInfoString(ArrayList<String> files,
			int pixelSizeX, int pixelSizeY, int zPositionInc) {

		if (files.isEmpty()) {
			System.out.println("No files found when creating info file.");
		}

		String result = "# Amira Stacked Slices \n \n";

		result += "pixelsize " + pixelSizeX + " " + pixelSizeY + " " + "\n \n";
		int i = 0;
		int zPos = 0;
		while (i < files.size()) {
			result += (files.get(i) + " " + +zPos + "\n");
			i++;
			zPos += zPositionInc;
		}
		return result;
	}

	/**
	 * Generates the contents of the .info file as a string based on a list of
	 * files. Assumes the pixel size of x,y and z are all 1.
	 * 
	 * @param files
	 *            ArrayList<String> of absolute path names of the images to be
	 *            used
	 * @return result the text which makes up the .info file
	 */
	private static String getStackedImageInfoString(ArrayList<String> files) {
		return getStackedImageInfoString(files, 1, 1, 1);
	}

	/**
	 * Generates the contents of the .info file as a string using baseName-00000
	 * to baseName-stackSize.
	 * 
	 * @param baseName
	 *            the common prefix of all the images
	 * @param pixelSizeX
	 *            how many pixels each pixel in the set of images represents
	 *            along the x axis
	 * @param pixelSizeY
	 *            how many pixels each pixel in the set of images represents
	 *            along the y axis
	 * @param zPositionInc
	 *            the increment in position between the stacked images along the
	 *            z axis
	 * @param stackSize
	 *            the amount of images in the image stack
	 * @param imageExtension
	 *            the file extension of the images in the stack
	 * @return the text which makes up the .info file
	 */
	private static String getStackedImageInfoString(String baseName,
			int pixelSizeX, int pixelSizeY, int zPositionInc, int stackSize,
			String imageExtension) {

		String result = "# Amira Stacked Slices \n \n";

		result += "pixelsize " + pixelSizeX + " " + pixelSizeY + " " + "\n \n";
		int i = 0;
		int zPos = 0;
		while (i <= stackSize) {
			if (i <= 9) {
				result += (baseName + "0000" + i + "." + imageExtension + " "
						+ +zPos + "\n");
			} else if (i <= 99) {
				result += (baseName + "000" + i + "." + imageExtension + " "
						+ zPos + "\n");
			} else if (i <= 999) {
				result += (baseName + "00" + i + "." + imageExtension + " "
						+ zPos + "\n");
			} else if (i <= 9999) {
				result += (baseName + "0" + i + "." + imageExtension + " "
						+ zPos + "\n");
			} else {
				result += (baseName + i + "." + imageExtension + " " + zPos + "\n");
			}
			i++;
			zPos += zPositionInc;
		}
		return result;
	}

	/**
	 * Returns the file names of files, in the given folder which have the given
	 * prefix and suffix
	 * 
	 * @param folder
	 *            the absolute path name to the folder to be searched
	 * @param prefix
	 *            files must have this prefix to be returned in the result list
	 * @param suffix
	 *            files must have this suffix to be returned in the result list
	 * @return the list of file names
	 */
	private static ArrayList<String> getFilesInFolder(String folder,
			String prefix, String suffix) {
		ArrayList<String> results = new ArrayList<String>();
		File[] files = new File(folder).listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					String f = file.getName();
					if (f.startsWith(prefix) && f.endsWith(suffix)) {
						results.add(f);
					}
				}
			}
		}
		// sort files in alphabetical order
		Collections.sort(results);
		return results;
	}

	/**
	 * Gets the absolute path names of every file in a folder.
	 * 
	 * @param folder
	 *            the absolute path name to the folder to look inside.
	 * @return the list of every file in the folder
	 */
	public static ArrayList<String> getFilesInFolderAbsolute(String folder) {
		if (folder == null || folder == "" || !new File(folder).isDirectory()) {
			return new ArrayList<String>(0);
		}
		ArrayList<String> results = new ArrayList<String>();
		File[] files = new File(folder).listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					results.add(file.getPath());
				}
			}
		}
		// sort files in alphabetical order
		Collections.sort(results);
		return results;
	}

	/**
	 * Gets the absolute path name of a file with the extension .info, which is
	 * directly inside the given folder
	 * 
	 * @param folder
	 *            the folder to search
	 * @return the path of the .info file, is no .info file is found null is
	 *         returned *
	 */
	public static String getDirOfInfoFile(String folder) {
		File[] files = new File(folder).listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					String f = file.getName();
					if (f.endsWith(".info")) {
						return file.getPath();
					}
				}
			}
		}
		return null;
	}

	/**
	 * Writes a .info file to the same directory as the given file. The .info
	 * file will contain all other files in the same directory, with the same
	 * base name and extension of the given file.
	 * 
	 * @param file
	 *            the file which the .info file will be based on
	 * @param pixelSizeX
	 *            how many pixels each pixel in the set of images represents
	 *            along the x axis
	 * @param pixelSizeY
	 *            how many pixels each pixel in the set of images represents
	 *            along the y axis
	 * @param zPositionInc
	 *            the increment in position between the stacked images along the
	 *            z axis
	 */
	public static void writeStackedImageInfoFile(File file, int pixelSizeX,
			int pixelSizeY, int zPositionInc) {
		if (!file.isFile()) {
			System.out
					.println("Error when writing .info file. File does not exist.");
			return;
		}
		String folder = file.getParent();
		String fileItemName = file.getName();
		String prefix = fileItemName.substring(0,
				getIndexOfFirstDigit(fileItemName));
		String suffix = getExtension(fileItemName);

		ArrayList<String> images = getFilesInFolder(folder, prefix, suffix);

		String data = getStackedImageInfoString(images, pixelSizeX, pixelSizeY,
				zPositionInc);

		String outputDir = folder + ".info";
		writeTextToFile(data, outputDir);
	}

	/**
	 * Writes a .info file to the given output directory. The .info file will
	 * contain all other files in the same directory, with the same base name
	 * and extension of the given file.
	 * 
	 * @param file
	 *            the file which the .info file will be based on
	 * @param pixelSizeX
	 *            how many pixels each pixel in the set of images represents
	 *            along the x axis
	 * @param pixelSizeY
	 *            how many pixels each pixel in the set of images represents
	 *            along the y axis
	 * @param zPositionInc
	 *            the increment in position between the stacked images along the
	 *            z axis
	 * @param outputDir
	 *            the file directory where the .info file will be written to
	 */
	public static void writeStackedImageInfoFile(File file, String prefix,
			String suffix, int pixelSizeX, int pixelSizeY, int zPositionInc,
			String outputDir) {
		if (!file.isDirectory()) {
			System.out
					.println("Error when writing .info file. Folder to search does not exit.");
			return;
		}
		String folder = file.getPath();

		ArrayList<String> images = getFilesInFolder(folder, prefix, suffix);

		String data = getStackedImageInfoString(images, pixelSizeX, pixelSizeY,
				zPositionInc);

		if (!getExtension(outputDir).equals("info")) {
			outputDir += ".info";
		}

		writeTextToFile(data, outputDir);
	}

	/**
	 * Writes a .info file to the given output directory. The .info file will
	 * contain all other files in the same directory, with the same base name
	 * and extension of the given file. Assumes the pixel size of x,y and z are
	 * all 1.
	 * 
	 * @param file
	 *            the file which the .info file will be based on
	 */
	public static void writeStackedImageInfoFile(File file) {
		if (!file.isFile()) {
			System.out
					.println("Error when writing .info file. File does not exit.");
			return;
		}
		String folder = file.getParent();
		String fileItemName = file.getName();
		String prefix = fileItemName.substring(0,
				getIndexOfFirstDigit(fileItemName));
		String suffix = getExtension(fileItemName);

		ArrayList<String> images = getFilesInFolder(folder, prefix, suffix);

		String data = getStackedImageInfoString(images);

		String outputDir = folder + "/" + prefix + ".info";
		writeTextToFile(data, outputDir);
	}

	/**
	 * 
	 * Generates the contents of the .info file as a string using baseName-00000
	 * to baseName-stackSize and writes it to a given file.
	 * 
	 * @param baseName
	 *            the common prefix of all the images
	 * @param pixelSizeX
	 *            how many pixels each pixel in the set of images represents
	 *            along the x axis
	 * @param pixelSizeY
	 *            how many pixels each pixel in the set of images represents
	 *            along the y axis
	 * @param zPositionInc
	 *            the increment in position between the stacked images along the
	 *            z axis
	 * @param stackSize
	 *            the amount of images in the image stack
	 * @param imageExtension
	 *            the file extension of the images in the stack
	 * @param location
	 *            the absolute path name where the .info file will be written to
	 */
	public static void writeStackedImageInfoFile(String baseName,
			int pixelSizeX, int pixelSizeY, int zPositionInc, int stackSize,
			String imageExtension, String location) {
		String data = getStackedImageInfoString(baseName, pixelSizeX,
				pixelSizeY, zPositionInc, stackSize, imageExtension);
		String outputDir = location;
		if (!getExtension(outputDir).equals("info")) {
			outputDir += ".info";
		}
		writeTextToFile(data, outputDir);
	}

	
	/**
	 * Gets the index of the position of the first digit of the given string
	 * @param s the string to look at
	 * @return the int index of the first digit
	 */
	private static int getIndexOfFirstDigit(String s) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= '0' && c <= '9') {
				return i;
			}
		}
		return 0;
	}

	/**
	 * gets the extension substring of a given string
	 * @param s the String to look at
	 * @return the suffix of s after '.'
	 */
	private static String getExtension(String s) {
		int pos = s.lastIndexOf('.');
		String ext = s.substring(pos + 1);
		return ext;
	}

	/**
	 * Uses a BufferedWriter to write a string to a file
	 * @param data the String to be written to file
	 * @param outputDir the absolute path of the new file to be created, all directories in this path must already exist
	 */
	private static void writeTextToFile(String data, String outputDir) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(outputDir);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(data);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

}
