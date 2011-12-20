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

package uk.ac.diamond.sda.polling.server;

import uk.ac.diamond.sda.polling.jobs.AbstractPollJob;

public class PollSheduler implements Runnable {

	private static final int DEFAULT_MAXIMUM_POLL_TIME = 10000;
	public static boolean SCHEDULER_RUNNING = false;
	PollServer pollServer = null;

	public PollSheduler(PollServer pollServer) {
		this.pollServer = pollServer;
	}

	@Override
	/**
	 * check all jobs, if the time till the next poll is negative 
	 * run the job, if its positive then log it
	 * Finally wait until the next step
	 */
	public void run() {

		SCHEDULER_RUNNING = true;
		
		//TODO should make this easily killable
		while(SCHEDULER_RUNNING) {

			pollServer.pollMonitor.pollLoopStart();
			long timeTillNextJob = processAllJobs(); 

			try {
				Thread.sleep(timeTillNextJob);
			} catch (InterruptedException e) {
				// Kill the loop and carry on
				continue;
			}

		}
	}

	private long processAllJobs() {

		pollServer.pollMonitor.processingJobs();

		long timeTillNextJob = DEFAULT_MAXIMUM_POLL_TIME;

		for (AbstractPollJob pollJob : this.pollServer.getPollJobs()) {
			pollServer.pollMonitor.schedulingJob(pollJob);

			long timeTillNextEvent = pollJob.timeToSchedule();

			if ((timeTillNextEvent > 0)&&(timeTillNextEvent < timeTillNextJob)) {
				timeTillNextJob = timeTillNextEvent;				
			}
		}
		pollServer.pollMonitor.processingJobsComplete(timeTillNextJob);
		return timeTillNextJob;
	}

}
