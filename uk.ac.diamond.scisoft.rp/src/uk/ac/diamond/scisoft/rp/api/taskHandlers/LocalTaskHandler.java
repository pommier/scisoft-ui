package uk.ac.diamond.scisoft.rp.api.taskHandlers;

import uk.ac.diamond.scisoft.rp.api.ScriptUtils;
import uk.ac.diamond.scisoft.rp.api.tasks.ITask;

public class LocalTaskHandler extends TaskHandler {

	@Override
	public void submitTask(ITask task) {

		if (task.isSubmitted()) {
			System.out.println("Task has already been submitted");
			return;
		}
		
		ScriptUtils.runScript(task.getParameterList());
		task.setTaskAsSubmitted();

	}
}
