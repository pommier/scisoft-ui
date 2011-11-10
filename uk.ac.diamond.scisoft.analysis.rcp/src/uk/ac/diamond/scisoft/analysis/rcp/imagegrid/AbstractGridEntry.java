/*-
 * Copyright © 2010 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rcp.imagegrid;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;

/**
 * An abstract entry for the ImageGridTable
 */

public abstract class AbstractGridEntry {

	public static final int SELECTEDSTATUS = 666;
	public static final int INVALIDSTATUS = -1;

	protected String filename = null;
	protected String thumbnailFilename = null;
	protected int status = 0;
	protected Object additionalInfo = null;
	
	public AbstractGridEntry(String filename) {
		this.filename = filename;
	}

	public AbstractGridEntry(String filename, Object additionalInfo) {
		this.filename = filename;
		this.additionalInfo = additionalInfo;
	}

	public abstract void setNewfilename(String newFilename);

	public abstract void setStatus(int newStatus);

	public abstract void deActivate();

	public abstract boolean isDeactivated();
	
	public abstract void dispose();

	public abstract void createImage(AbstractDataset ds);

	public abstract String getToolTipText();

	public String getFilename() {
		return filename;
	}

	public String getThumbnailFilename() {
		return thumbnailFilename;
	}

	public Object getAdditionalInfo() {
		return additionalInfo;
	}

	public int getStatus() {
		return status;
	}
}
