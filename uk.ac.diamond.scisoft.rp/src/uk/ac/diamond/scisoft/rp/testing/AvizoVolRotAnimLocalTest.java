package uk.ac.diamond.scisoft.rp.testing;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.diamond.scisoft.rp.api.FieldVerifyUtils;
import uk.ac.diamond.scisoft.rp.api.ScriptUtils;
import uk.ac.diamond.scisoft.rp.api.taskHandlers.LocalTaskHandler;
import uk.ac.diamond.scisoft.rp.api.tasks.AvizoRotationAnimationTask;

public class AvizoVolRotAnimLocalTest {

	private static File dir;
	private final String outputTestName1 = "test";

	@BeforeClass
	public static void oneTimeSetUp() {
		dir = new File(ScriptUtils.getAbsolutePath()
				+ "/JUnitTestOutputAvizoLocal");
		if (!dir.isDirectory()) { // folder does not exist
			// create this folder for testing
			dir.mkdir();
		} else {
			deleteFolderContents(dir);
		}
	}

	@After
	public void tearDown() {
		deleteFolderContents(dir);
	}

	@AfterClass
	public static void oneTimeTearDown() {
		dir.delete();
	}

	@Test
	public void testAvizoRotateAroundOriginjpg() {
		LocalTaskHandler lth = new LocalTaskHandler();

		// task is to output 5 .jpg images
		AvizoRotationAnimationTask rat = new AvizoRotationAnimationTask(false,
				"/dls_sw/i12/software/avizo/64/data/tutorials/motor.am", "x",
				"0", "0", "0", "0", "0", "0", "0.2", "360", "2", "2", "1", "1",
				"5", "1", "0", "0.7", "848", "480",
				dir + "/" + outputTestName1, "0");

		assertNotNull(lth);
		assertNotNull(rat);
		assertFalse(rat.isSubmitted());
		assertNotNull(rat.getParameterList());
		lth.submitTask(rat);

		assertTrue(rat.isSubmitted());

		// test output
		assertTrue(FieldVerifyUtils.isFile(dir + "/" + outputTestName1
				+ ".0000.jpg"));
		assertTrue(FieldVerifyUtils.isFile(dir + "/" + outputTestName1
				+ ".0001.jpg"));
		assertTrue(FieldVerifyUtils.isFile(dir + "/" + outputTestName1
				+ ".0002.jpg"));
		assertTrue(FieldVerifyUtils.isFile(dir + "/" + outputTestName1
				+ ".0003.jpg"));
		assertTrue(FieldVerifyUtils.isFile(dir + "/" + outputTestName1
				+ ".0004.jpg"));
		assertTrue(FieldVerifyUtils.isFile(dir + "/" + "test.amov"));
	}

	@Test
	public void testAvizoRotateAroundOriginmpg() {
		LocalTaskHandler lth = new LocalTaskHandler();

		// task is to output 5 .jpg images
		AvizoRotationAnimationTask rat = new AvizoRotationAnimationTask(false,
				"/dls_sw/i12/software/avizo/64/data/tutorials/motor.am", "x",
				"0", "0", "0", "0", "0", "0", "0.2", "360", "2", "2", "1", "1",
				"5", "0", "0", "0.7", "848", "480",
				dir + "/" + outputTestName1, "0");

		assertNotNull(lth);
		assertNotNull(rat);
		assertFalse(rat.isSubmitted());
		assertNotNull(rat.getParameterList());
		lth.submitTask(rat);

		assertTrue(rat.isSubmitted());

		// test output
		assertTrue(FieldVerifyUtils
				.isFile(dir + "/" + outputTestName1 + ".mpg"));
	}

	@Test
	public void testAvizoRotateAroundCenterjpg() {
		LocalTaskHandler lth = new LocalTaskHandler();

		// task is to output 5 .jpg images
		AvizoRotationAnimationTask rat = new AvizoRotationAnimationTask(false,
				"/dls_sw/i12/software/avizo/64/data/tutorials/motor.am", "x",
				"0", "0", "0", "0.2", "360", "2", "2", "1", "1", "5", "1", "0",
				"0.7", "848", "480", dir + "/" + outputTestName1, "0");

		assertNotNull(lth);
		assertNotNull(rat);
		assertFalse(rat.isSubmitted());
		assertNotNull(rat.getParameterList());
		lth.submitTask(rat);

		assertTrue(rat.isSubmitted());

		// test output
		assertTrue(FieldVerifyUtils.isFile(dir + "/" + outputTestName1
				+ ".0000.jpg"));
		assertTrue(FieldVerifyUtils.isFile(dir + "/" + outputTestName1
				+ ".0001.jpg"));
		assertTrue(FieldVerifyUtils.isFile(dir + "/" + outputTestName1
				+ ".0002.jpg"));
		assertTrue(FieldVerifyUtils.isFile(dir + "/" + outputTestName1
				+ ".0003.jpg"));
		assertTrue(FieldVerifyUtils.isFile(dir + "/" + outputTestName1
				+ ".0004.jpg"));
		assertTrue(FieldVerifyUtils.isFile(dir + "/" + "test.amov"));
	}

	@Test
	public void testAvizoRotateAroundCentermpg() {
		LocalTaskHandler lth = new LocalTaskHandler();

		// task is to output mpg movie
		AvizoRotationAnimationTask rat = new AvizoRotationAnimationTask(false,
				"/dls_sw/i12/software/avizo/64/data/tutorials/motor.am", "x",
				"0", "0", "0", "0.2", "360", "2", "2", "1", "1", "5", "0", "0",
				"0.7", "848", "480", dir + "/" + outputTestName1, "0");

		assertNotNull(lth);
		assertNotNull(rat);
		assertFalse(rat.isSubmitted());
		assertNotNull(rat.getParameterList());
		lth.submitTask(rat);

		assertTrue(rat.isSubmitted());

		// test output
		assertTrue(FieldVerifyUtils
				.isFile(dir + "/" + outputTestName1 + ".mpg"));
	}

	private static void deleteFolderContents(File folder) {
		final File file = folder;
		String[] myFiles;
		if (file.isDirectory()) {
			myFiles = file.list();
			for (int i = 0; i < myFiles.length; i++) {
				File myFile = new File(file, myFiles[i]);
				myFile.delete();
			}
		}
	}

}
