/**
 * 
 */
package uk.ac.diamond.scisoft.rp.api.taskHandlers;

import uk.ac.diamond.scisoft.rp.api.tasks.ITask;

/**
 * @author vgb98675
 *
 */
public interface ITaskHandler {	
	
	public abstract void initialiseRemoteModules();
	
	public abstract void loadTaskRequiredModules();
	
	public abstract void submitTask(ITask task);
		
	public abstract void cleanUp();
}
