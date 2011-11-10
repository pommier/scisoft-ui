/*-
 * Copyright © 2011 Diamond Light Source Ltd.
 *
 * This file is part of GDA.
 *
 * GDA is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 *
 * GDA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along
 * with GDA. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.diamond.sda.polling.jobs;
import java.io.IOException;

import org.eclipse.core.runtime.jobs.Job;


public abstract class AbstractPollJob extends Job {

	private static final String POLL_TIME = "PollTime";
	private JobParameters jobParameters = null;
	private long lastRun;
	private String status = "Starting";

	private void runJob() {
		try {
			jobParameters.refresh();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lastRun = System.currentTimeMillis();
		this.schedule();
	}
	
	public long timeToSchedule() {
		long pollTime = (long) (Double.parseDouble(jobParameters.get(POLL_TIME))*1000.0);
		long time = (lastRun+pollTime)-System.currentTimeMillis();
		if (time < 0) {
			runJob();
			time = pollTime;
		}
		return time;
	}
	
	
	public AbstractPollJob(String name) {
		super(name);
		lastRun = System.currentTimeMillis();
	}

	public JobParameters getJobParameters() {
		return jobParameters;
	}

	public void setJobParameters(JobParameters jobParameters) {
		this.jobParameters = jobParameters;
	}

	public String getPollTime() {
		return jobParameters.get(POLL_TIME);
	}

	public void setJobParametersFilename(String fileName) throws IOException {
		jobParameters = new JobParameters(fileName);		
	}
	
	public String getJobParametersFilename() {
		return jobParameters.getParameterFile().getAbsolutePath();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
