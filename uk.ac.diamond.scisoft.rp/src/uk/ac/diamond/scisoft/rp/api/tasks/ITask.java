package uk.ac.diamond.scisoft.rp.api.tasks;

import java.util.List;

public interface ITask {	
	
	public abstract List<String> getParameterList();
		
	public abstract boolean isSubmitted();	

	public abstract void setTaskAsSubmitted();	
	
}
