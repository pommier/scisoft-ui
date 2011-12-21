/*
 * Copyright 2012 Diamond Light Source Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
