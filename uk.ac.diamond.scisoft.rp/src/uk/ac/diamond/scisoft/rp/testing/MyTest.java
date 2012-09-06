package uk.ac.diamond.scisoft.rp.testing;

import uk.ac.diamond.scisoft.rp.api.taskHandlers.LocalTaskHandler;
import uk.ac.diamond.scisoft.rp.api.taskHandlers.QLoginTaskHandler;
import uk.ac.diamond.scisoft.rp.api.taskHandlers.QSubTaskHandler;
import uk.ac.diamond.scisoft.rp.api.tasks.AvizoRotationAnimationTask;

public class MyTest {

	private static final String outputTestName1 = "test";

	public static void main(String[] args) {

		/*
		 * AvizoRotationAnimationTask rat = new AvizoRotationAnimationTask(
		 * "/dls_sw/i12/software/avizo/64/data/tutorials/motor.am", "x", "0",
		 * "0", "0", "0.2", "360", "2", "2", "1", "5", "1", "0", "0.7", "848",
		 * "480", "/home/vgb98675/test_output" + "/" + outputTestName1, "0");
		 */

		AvizoRotationAnimationTask rat = new AvizoRotationAnimationTask(true,
				"/dls_sw/i12/software/avizo/64/data/tutorials/motor.am", "x",
				"0", "0", "0", "0.2", "360", "2", "2", "1", "1", "15", "1",
				"0", "0.7", "848", "480", "/home/vgb98675/test_output" + "/"
						+ outputTestName1, "0");

		LocalTaskHandler th = new LocalTaskHandler();

		// SGETaskHandler th = new SGETaskHandler();

		// SSHTaskHandler th = new SSHTaskHandler(true, "ws049");
		// SSHTaskHandler th = new SSHTaskHandler(true, "ws013");
		// SSHTaskHandler th = new SSHTaskHandler(true, "i12-ws011");

		// QLoginTaskHandler th = new QLoginTaskHandler();

		System.out.println(rat.getParameterString());

		th.submitTask(rat);



	}

}
