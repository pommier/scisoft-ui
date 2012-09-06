/**
 * 
 */
package uk.ac.diamond.scisoft.rp.api.taskHandlers;

import java.util.List;

import uk.ac.diamond.scisoft.rp.api.ScriptUtils;
import uk.ac.diamond.scisoft.rp.api.tasks.ITask;

/**
 * @author vgb98675
 * 
 */
public class SSHTaskHandler extends TaskHandler {

	private final boolean GRAPHICS_OPTION;
	private final String NODE;

	public SSHTaskHandler(boolean graphicsOption, String node) {
		this.GRAPHICS_OPTION = graphicsOption;
		this.NODE = node;
	}

	@Override
	public void submitTask(ITask task) {
		if (task.isSubmitted()) {
			System.out.println("Task has already been submitted");
			return;
		}

		String go = "0";
		if (GRAPHICS_OPTION) {
			go = "1";
		}
		System.out.println("SSH Submitting rotation animation task");

		List<String> list = task.getParameterList();

		String mainScriptDir = ScriptUtils.getAbsoluteScriptPath()
				+ "submitScripts/sshSub.sh";

		list.add(0, mainScriptDir);

		list.add(1, go);
		list.add(2, this.NODE);

		ScriptUtils.runScript(list);
		task.setTaskAsSubmitted();

	}

}
