/*
 * Copyright © 2011 Diamond Light Source Ltd.
 * Contact :  ScientificSoftware@diamond.ac.uk
 * 
 * This is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 * 
 * This software is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this software. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.diamond.sda.polling.monitor;

import org.eclipse.core.runtime.jobs.Job;

import uk.ac.diamond.sda.polling.jobs.AbstractPollJob;

public class SystemOutPollMonitor implements IPollMonitor {

	long loopCount = 0;
	
	@Override
	public void pollLoopStart() {
		System.out.println(String.format("Starting Poling Loop %d",loopCount));
		loopCount += 1;
	}

	@Override
	public void processingJobs() {
		System.out.println("Processing Jobs");

	}

	@Override
	public void schedulingJob(AbstractPollJob pollJob) {
		System.out.println("Scheduling Job : "+ pollJob.toString());

	}

	@Override
	public void processingJobsComplete(long timeTillNextJob) {
		System.out.println(String.format("Processing Jobs Complete Time to Wait %d",timeTillNextJob));

	}

	@Override
	public void jobAdded(Job job) {
		System.out.println("Scheduling Job : "+ job.toString());

	}

}
