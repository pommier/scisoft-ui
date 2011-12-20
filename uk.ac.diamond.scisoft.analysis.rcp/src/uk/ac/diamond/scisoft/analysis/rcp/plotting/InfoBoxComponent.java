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

package uk.ac.diamond.scisoft.analysis.rcp.plotting;


import javax.vecmath.Vector3d;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import uk.ac.diamond.scisoft.analysis.diffraction.QSpace;

/**
 *
 */
public class InfoBoxComponent extends Composite {

	private Table tblInfo;
	private TableItem item;
	private boolean isDiffractionImg;
	
	InfoBoxComponent(Composite parent, int style) {
		super(parent,style);
		this.setLayout(new FillLayout());
		tblInfo = new Table(this, SWT.DOUBLE_BUFFERED);
		tblInfo.setHeaderVisible(true);
		tblInfo.setLinesVisible(true);
		{
			TableColumn tblXpos = new TableColumn(tblInfo, SWT.NONE);
			tblXpos.setWidth(82);
			tblXpos.setText("X position");
		}
		{
			TableColumn tblYpos = new TableColumn(tblInfo, SWT.NONE);
			tblYpos.setWidth(82);
			tblYpos.setText("Y position");
		}
		{
			TableColumn tblData = new TableColumn(tblInfo, SWT.NONE);
			tblData.setWidth(80);
			tblData.setText("Data value");
		}
		{
			TableColumn tblXpos = new TableColumn(tblInfo, SWT.NONE);
			tblXpos.setWidth(82);
			tblXpos.setText("q X (1/\u00c5)");
		}
		{
			TableColumn tblYpos = new TableColumn(tblInfo, SWT.NONE);
			tblYpos.setWidth(82);
			tblYpos.setText("q Y (1/\u00c5)");
		}
		{
			TableColumn tblYpos = new TableColumn(tblInfo, SWT.NONE);
			tblYpos.setWidth(82);
			tblYpos.setText("q Z (1/\u00c5)");
		}
		{
			TableColumn tblYpos = new TableColumn(tblInfo, SWT.NONE);
			tblYpos.setWidth(82);
			tblYpos.setText("2\u03b8 (\u00b0)");
		}
		{
			TableColumn tblYpos = new TableColumn(tblInfo, SWT.NONE);
			tblYpos.setWidth(95);
			tblYpos.setText("Resolution (\u00c5)");
		}
		{
			TableColumn tblYpos = new TableColumn(tblInfo, SWT.NONE);
			tblYpos.setWidth(82);
			tblYpos.setText("Dataset name");
		}		
		item = new TableItem(tblInfo,SWT.DOUBLE_BUFFERED);
		item.setText(0,"----");
		item.setText(1,"----");
		item.setText(2,"----");
		item.setText(3,"----");
		item.setText(4,"----");
		item.setText(5,"----");
		item.setText(6,"----");
		item.setText(7,"----");
	}

	public void setPositionInfo(double x, double y, double value)
	{
		item.setText(0,String.format("%4.4f",x));
		item.setText(1,String.format("%4.4f",y));
		item.setText(2,String.format("%4.4f",value));
	}

	public void isDiffractionImage(boolean isDiffraction) {
		isDiffractionImg = isDiffraction;
		if (!isDiffractionImg) {
			tblInfo.getColumn(3).setWidth(0);
			tblInfo.getColumn(4).setWidth(0);
			tblInfo.getColumn(5).setWidth(0);
			tblInfo.getColumn(6).setWidth(0);
			tblInfo.getColumn(7).setWidth(0);
			tblInfo.redraw();
		} else {
			tblInfo.getColumn(3).setWidth(80);
			tblInfo.getColumn(4).setWidth(80);
			tblInfo.getColumn(5).setWidth(80);
			tblInfo.getColumn(6).setWidth(80);
			tblInfo.getColumn(7).setWidth(80);
			tblInfo.redraw();			
		}
	}
	
	public void setQSpaceInfo(double x, double y, QSpace qSpace) {
		Vector3d q = qSpace.qFromPixelPosition(x, y);
		item.setText(3,String.format("% 4.4f", q.x));
		item.setText(4,String.format("% 4.4f", q.y));
		item.setText(5,String.format("% 4.4f", q.z));
		item.setText(6,String.format("% 3.3f", Math.toDegrees(qSpace.scatteringAngle(q))));
		item.setText(7,String.format("% 4.4f", (2*Math.PI)/q.length()));
	}
	
	public void setName(String name) {
		item.setText(8,name);
	}
}
