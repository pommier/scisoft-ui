package uk.ac.diamond.scisoft.rp.api.taskHandlers;

import java.util.List;

import uk.ac.diamond.scisoft.rp.api.ScriptUtils;
import uk.ac.diamond.scisoft.rp.api.tasks.ITask;

public class QSubTaskHandler extends TaskHandler {

	@Override
	public void submitTask(ITask task) {
		if (task.isSubmitted()) {
			System.out.println("Task has already been submitted");
			return;
		}

		List<String> list = task.getParameterList();

		String mainScriptDir = ScriptUtils.getAbsoluteScriptPath()
				+ "submitScripts/qsub.sh";

		list.add(0, mainScriptDir);

		ScriptUtils.runScript(list);

		task.setTaskAsSubmitted();

	}

}
