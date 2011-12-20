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

package uk.ac.diamond.scisoft.analysis.rcp.imagegrid;

import java.util.ArrayList;

import uk.ac.diamond.scisoft.analysis.plotserver.GridImageEntry;

/**
 * A generic Image grid container without any real implementation on how to display and store the individual images in
 * the grid
 */
public abstract class AbstractImageGrid {

	public static int MAXTHUMBWIDTH = 96;
	public static int MAXTHUMBHEIGHT = 96;
	public static int MINTHUMBWIDTH = 64;
	public static int MINTHUMBHEIGHT = 64;
	public static int MAXMEMORYUSAGE = 1024 * 1024 * 60;
	protected int gridWidth;
	protected int gridHeight;
	protected AbstractGridEntry[] table;
	protected int nextEntryX = 0;
	protected int nextEntryY = 0;
	protected GridEntryMonitor monitor = null;

	public AbstractImageGrid() {
		gridWidth = 10;
		gridHeight = 10;
		table = new AbstractGridEntry[gridWidth * gridHeight];
		int i = 0;
		for (int y = 0; y < gridHeight; y++)
			for (int x = 0; x < gridWidth; x++)
				table[i++] = null;
	}

	public AbstractImageGrid(int width, int height) {
		gridWidth = width;
		gridHeight = height;
		nextEntryX = 0;
		nextEntryY = 0;
		table = new AbstractGridEntry[gridWidth * gridHeight];
		int i = 0;
		for (int y = 0; y < gridHeight; y++)
			for (int x = 0; x < gridWidth; x++)
				table[i++] = null;
	}

	public void addEntry(AbstractGridEntry newEntry) {
		synchronized (table) {
			table[nextEntryX + nextEntryY * gridWidth] = newEntry;
		}
		if (monitor != null)
			monitor.addEntry(newEntry, nextEntryX, nextEntryY);
		synchronized (table) {
			determineNextEntryPos();
		}
	}

	public void setSize(int newWidth, int newHeight) {
		resizeGrid(newWidth, newHeight);
	}

	public void addEntry(AbstractGridEntry newEntry, int xPos, int yPos) {
		synchronized (table) {
			if (xPos > gridWidth - 1 || yPos > gridHeight - 1)
				resizeGrid(Math.max(xPos + 1, gridWidth), Math.max(yPos + 1, gridHeight));

			table[xPos + yPos * gridWidth] = newEntry;
		}

		if (monitor != null)
			monitor.addEntry(newEntry, xPos, yPos);
		synchronized (table) {
			determineNextEntryPos();
		}
	}

	protected void determineNextEntryPos() {
		while (table[nextEntryX + nextEntryY * gridWidth] != null) {
			nextEntryX++;
			if (nextEntryX > gridWidth - 1) {
				nextEntryX = 0;
				nextEntryY++;
			}
			if (nextEntryY > gridHeight - 1) {
				resizeGrid(gridWidth, (nextEntryY + 2));
			}
		}
	}

	protected void resizeGrid(int newWidth, int newHeight) {
		AbstractGridEntry[] newTable = new AbstractGridEntry[newWidth * newHeight];
		for (int y = 0; y < Math.min(gridHeight, newHeight); y++) {
			for (int x = 0; x < Math.min(gridWidth, newWidth); x++) {
				newTable[x + y * newWidth] = table[x + y * gridWidth];
				table[x + y * gridWidth] = null;
			}
		}
		for (int y = gridHeight; y < newHeight; y++) {
			for (int x = gridWidth; x < newWidth; x++) {
				newTable[x + y * newWidth] = null;
			}
		}
		monitor.gridResize(newWidth, newHeight);
		table = newTable;
		gridWidth = newWidth;
		gridHeight = newHeight;
	}

	public AbstractGridEntry getGridEntry(int x, int y) {
		AbstractGridEntry returnGrid = null;
		synchronized (table) {
			if (x < gridWidth && y < gridHeight) {
				returnGrid = table[x + y * gridWidth];
			}
		}
		return returnGrid;
	}

	public abstract void displayGrid();

	public abstract void dispose();

	public abstract void setOverviewMode(boolean overview);

	public abstract boolean getOverviewMode();

	public abstract ArrayList<String> getSelection();

	public abstract void stopLoading();

	public int getGridWidth() {
		return gridWidth;
	}

	public int getGridHeight() {
		return gridHeight;
	}

	public ArrayList<GridImageEntry> getListOfEntries() {
		ArrayList<GridImageEntry> returnList = new ArrayList<GridImageEntry>();
		int i = 0;
		for (int y = 0; y < gridHeight; y++)
			for (int x = 0; x < gridWidth; x++) {
				if (table[i] != null) {
					GridImageEntry entry = new GridImageEntry(table[i].getFilename(), x, y);
					returnList.add(entry);
				}
				i++;
			}
		return returnList;
	}
}
