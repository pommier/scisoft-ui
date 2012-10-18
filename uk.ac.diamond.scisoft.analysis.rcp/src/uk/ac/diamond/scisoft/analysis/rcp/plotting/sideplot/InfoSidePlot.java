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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dawnsci.plotting.jreality.overlay.Overlay2DConsumer;
import org.dawnsci.plotting.jreality.overlay.Overlay2DProvider2;
import org.dawnsci.plotting.jreality.overlay.OverlayProvider;
import org.dawnsci.plotting.jreality.overlay.OverlayType;
import org.dawnsci.plotting.jreality.overlay.objects.PointListObject;
import org.dawnsci.plotting.jreality.overlay.primitives.PrimitiveType;
import org.dawnsci.plotting.jreality.tool.IImagePositionEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;

import uk.ac.diamond.scisoft.analysis.dataset.ByteDataset;
import uk.ac.diamond.scisoft.analysis.dataset.DoubleDataset;
import uk.ac.diamond.scisoft.analysis.dataset.FloatDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IndexIterator;
import uk.ac.diamond.scisoft.analysis.dataset.IntegerDataset;
import uk.ac.diamond.scisoft.analysis.dataset.LongDataset;
import uk.ac.diamond.scisoft.analysis.dataset.ShortDataset;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.IPlotUI;

/**
 *
 */
public class InfoSidePlot extends SidePlot implements Overlay2DConsumer, SelectionListener {

	private Text txtMaxValue;
	private Text txtMinValue;
	private Button btnShowMax;
	private Button btnShowMin;
	private List<Integer> xMaxPos = new ArrayList<Integer>();
	private List<Integer> yMaxPos = new ArrayList<Integer>();
	private List<Integer> xMinPos = new ArrayList<Integer>();
	private List<Integer> yMinPos = new ArrayList<Integer>();
	private Overlay2DProvider2 provider = null;
	private PointListObject maxPoints = null;
	private PointListObject minPoints = null;
	
	@Override
	public Action createSwitchAction(final int index, final IPlotUI plotUI) {
		Action action = super.createSwitchAction(index, plotUI);
		action.setId("uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot.InfoSidePlot");
		action.setText("Information");
		action.setToolTipText("Get some raw information");
		action.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor("icons/new.png"));
		return action;
	}
	
	@Override
	public void addToHistory() {
		// Nothing to do

	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		Group grpValues = new Group(container, SWT.NONE);
		grpValues.setLayout(new GridLayout(3,false));
		grpValues.setText("Dataset information");
		Label lblMaxValue = new Label(grpValues,SWT.NONE);
		lblMaxValue.setText("Maximum data value: ");
		txtMaxValue = new Text(grpValues,SWT.NONE);
		txtMaxValue.setEditable(false);
		GridData grdData = new GridData(SWT.CENTER,SWT.CENTER,true,false);
		grdData.minimumWidth = 80;
		grdData.widthHint = 80;
		txtMaxValue.setLayoutData(grdData);
		txtMaxValue.setText(Integer.toString(0));
		btnShowMax = new Button(grpValues,SWT.TOGGLE);
		btnShowMax.setText("Show position(s) in plot");
		btnShowMax.addSelectionListener(this);
		Label lblMinValue = new Label(grpValues,SWT.NONE);
		lblMinValue.setText("Minimum data value: ");
		txtMinValue = new Text(grpValues,SWT.NONE);
		txtMinValue.setEditable(false);
		txtMinValue.setLayoutData(grdData);
		txtMinValue.setText(Integer.toString(0));
		btnShowMin = new Button(grpValues,SWT.TOGGLE);
		btnShowMin.setText("Show position(s) in plot");
		btnShowMin.addSelectionListener(this);

	}

	@Override
	public void generateMenuActions(IMenuManager manager, IWorkbenchPartSite site) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateToolActions(IToolBarManager manager) {
		// TODO Auto-generated method stub

	}

	private boolean determineMinMaxXYPos(IDataset data, float min, float max) {
		boolean disableButtons = false;
		if (data instanceof DoubleDataset) {
			DoubleDataset dblData = (DoubleDataset)data;
			double[] dbl = dblData.getData();
			IndexIterator iter = dblData.getIterator();
			while (iter.hasNext()) {
				if (dbl[iter.index] == max) {
					int pos[] = dblData.getNDPosition(iter.index);
					if (pos.length > 1) {
						yMaxPos.add(pos[0]);
						xMaxPos.add(pos[1]);
					} else {
						xMaxPos.add(pos[0]);
					}
				} else if (dbl[iter.index]== min) {
					int pos[] = dblData.getNDPosition(iter.index);
					if (pos.length > 1) {
						yMinPos.add(pos[0]);
						xMinPos.add(pos[1]);
					} else {
						xMinPos.add(pos[0]);
					}						
				}
			}
		} else if (data instanceof FloatDataset) {
			FloatDataset fltData = (FloatDataset)data;				
			float[] flt = fltData.getData();
			IndexIterator iter = fltData.getIterator();
			while (iter.hasNext()) {
				if (flt[iter.index] == max) {
					int pos[] = fltData.getNDPosition(iter.index);
					if (pos.length > 1) {
						yMaxPos.add(pos[0]);
						xMaxPos.add(pos[1]);
					} else {
						xMaxPos.add(pos[0]);
					}
				} else if (flt[iter.index] == min) {
					int pos[] = fltData.getNDPosition(iter.index);
					if (pos.length > 1) {
						yMinPos.add(pos[0]);
						xMinPos.add(pos[1]);
					} else {
						xMinPos.add(pos[0]);
					}						
				}
			}
		} else if (data instanceof LongDataset) {
			LongDataset lngData = (LongDataset)data;
			long[] lng = lngData.getData();
			IndexIterator iter = lngData.getIterator();
			while (iter.hasNext()) {
				if (lng[iter.index] == (long)max) {
					int pos[] = lngData.getNDPosition(iter.index);
					if (pos.length > 1) {
						yMaxPos.add(pos[0]);
						xMaxPos.add(pos[1]);
					} else {
						xMaxPos.add(pos[0]);
					}
				} else	if (lng[iter.index] == (long)min) {
					int pos[] = lngData.getNDPosition(iter.index);
					if (pos.length > 1) {
						yMinPos.add(pos[0]);
						xMinPos.add(pos[1]);
					} else {
						xMinPos.add(pos[0]);
					}
				}
			}
		} else if (data instanceof IntegerDataset) {
			IntegerDataset ingData = (IntegerDataset)data;
			int[] intB = ingData.getData();
			IndexIterator iter = ingData.getIterator();
			while (iter.hasNext()) {
				if (intB[iter.index] == (int)max) {
					int pos[] = ingData.getNDPosition(iter.index);
					if (pos.length > 1) {
						yMaxPos.add(pos[0]);
						xMaxPos.add(pos[1]);
					} else {
						xMaxPos.add(pos[0]);
					}
				} else if (intB[iter.index] == (int)min) {
					int pos[] = ingData.getNDPosition(iter.index);
					if (pos.length > 1) {
						yMinPos.add(pos[0]);
						xMinPos.add(pos[1]);
					} else {
						xMinPos.add(pos[0]);
					}
				}
			}
		} else if (data instanceof ShortDataset) {
			ShortDataset shrtData = (ShortDataset)data;
			short[] shrt = shrtData.getData();
			IndexIterator iter = shrtData.getIterator();
			while (iter.hasNext()) {
				if (shrt[iter.index] == (short)max) {
					int pos[] = shrtData.getNDPosition(iter.index);
					if (pos.length > 1) {
						yMaxPos.add(pos[0]);
						xMaxPos.add(pos[1]);
					} else {
						xMaxPos.add(pos[0]);
					}
				} else if (shrt[iter.index] == (short)min) {
					int pos[] = shrtData.getNDPosition(iter.index);
					if (pos.length > 1) {
						yMinPos.add(pos[0]);
						xMinPos.add(pos[1]);
					} else {
						xMinPos.add(pos[0]);
					}
				}
			}
		} else if (data instanceof ByteDataset) {
			ByteDataset bytData = (ByteDataset)data;
			byte[] byt = bytData.getData();
			IndexIterator iter = bytData.getIterator();
			while (iter.hasNext()) {
				if (byt[iter.index] == (byte)max) {
					int pos[] = bytData.getNDPosition(iter.index);
					if (pos.length > 1) {
						yMaxPos.add(pos[0]);
						xMaxPos.add(pos[1]);
					} else {
						xMaxPos.add(pos[0]);
					}						
				} else if (byt[iter.index] == (byte)min) {
					int pos[] = bytData.getNDPosition(iter.index);
					if (pos.length > 1) {
						yMinPos.add(pos[0]);
						xMinPos.add(pos[1]);
					} else {
						xMinPos.add(pos[0]);
					}	
				}
			}
		} else {
			disableButtons = true;
		}
		return disableButtons;
	}
	
	@Override
	public void processPlotUpdate() {
		if (mainPlotter == null) {
			return;
		}		
		List<IDataset> dataList = mainPlotter.getCurrentDataSets();
		
		xMaxPos.clear();
		yMaxPos.clear();
		xMinPos.clear();
		yMinPos.clear();
		if (minPoints != null)
			minPoints = null;
		if (maxPoints != null)
			maxPoints = null;
		
		if (dataList != null && dataList.size() > 0) {
			IDataset data = dataList.get(0);
			final float min = data.min().floatValue();
			final float max = data.max().floatValue();
			final boolean shouldDisableButtons = determineMinMaxXYPos(data,min,max);
			txtMaxValue.getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					txtMaxValue.setText(Float.toString(max));
					txtMinValue.setText(Float.toString(min));
					btnShowMax.setEnabled(!shouldDisableButtons);
					btnShowMin.setEnabled(!shouldDisableButtons);
					btnShowMax.setSelection(false);
					btnShowMin.setSelection(false);
				}
			});
		}
	}

	@Override
	public void removeFromHistory() {
		// Nothing to do
	}

	@Override
	public void showSidePlot() {
		processPlotUpdate();
	}

	@Override
	public int updateGUI(GuiBean bean) {
		// Nothing to do
		return 0;
	}

	@Override
	public void registerProvider(OverlayProvider provider) {
		if (provider instanceof Overlay2DProvider2)
			this.provider = (Overlay2DProvider2)provider; 
	}

	@Override
	public void removePrimitives() {
		if (maxPoints != null)
			maxPoints = null;
		if (minPoints != null)
			minPoints = null;

	}

	@Override
	public void unregisterProvider() {
		provider = null;

	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void hideOverlays() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showOverlays() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void imageDragged(IImagePositionEvent event) {
		// Nothing to do
		
	}

	@Override
	public void imageFinished(IImagePositionEvent event) {
		// Nothing to do
		
	}

	@Override
	public void imageStart(IImagePositionEvent event) {
		// Nothing to do
		
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// Nothing to do
		
	}
	
	private void displayMax() {
		if (provider != null) {
			if (maxPoints == null)
				maxPoints = (PointListObject)provider.registerObject(PrimitiveType.POINTLIST);
			double[] xPos = new double[xMaxPos.size()];
			double[] yPos = new double[yMaxPos.size()];
			int index = 0;
			Iterator<Integer> xIter = xMaxPos.iterator();
			Iterator<Integer> yIter = yMaxPos.iterator();
			while (xIter.hasNext()) {
				xPos[index] = xIter.next();
				yPos[index++] = yIter.next();
			}
			maxPoints.setPointPositions(xPos,yPos);
			provider.begin(OverlayType.VECTOR2D);
			maxPoints.setColour(java.awt.Color.RED);
			maxPoints.setThick(true);
			maxPoints.draw();
			provider.end(OverlayType.VECTOR2D);
		}
	}

	private void displayMin() {
		if (provider != null) {
			if (minPoints == null)
				minPoints = (PointListObject)provider.registerObject(PrimitiveType.POINTLIST);
			double[] xPos = new double[xMinPos.size()];
			double[] yPos = new double[yMinPos.size()];
			int index = 0;
			Iterator<Integer> xIter = xMinPos.iterator();
			Iterator<Integer> yIter = yMinPos.iterator();
			while (xIter.hasNext()) {
				xPos[index] = xIter.next();
				yPos[index++] = yIter.next();
			}
			minPoints.setPointPositions(xPos,yPos);
			provider.begin(OverlayType.VECTOR2D);
			minPoints.setColour(java.awt.Color.BLUE);
			minPoints.setThick(true);
			minPoints.draw();
			provider.end(OverlayType.VECTOR2D);
		}
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource().equals(btnShowMax)) {
			if (btnShowMax.getSelection()) {
				if (maxPoints == null)
					displayMax();
				else {
					if (provider != null) {
						provider.begin(OverlayType.VECTOR2D);
						maxPoints.setVisible(true);
						provider.end(OverlayType.VECTOR2D);
					}
				}
			} else {
				if (maxPoints != null) {
					if (provider != null) {
						provider.begin(OverlayType.VECTOR2D);
						maxPoints.setVisible(false);
						provider.end(OverlayType.VECTOR2D);
					}
				}
			}
		} else if (e.getSource().equals(btnShowMin)) {
			if (btnShowMin.getSelection()) {
				if (minPoints == null)
					displayMin();
				else {
					if (provider != null) {
						provider.begin(OverlayType.VECTOR2D);
						minPoints.setVisible(true);
						provider.end(OverlayType.VECTOR2D);
					}
				}
			} else {
				if (minPoints != null) {
					if (provider != null) {
						provider.begin(OverlayType.VECTOR2D);
						minPoints.setVisible(false);
						provider.end(OverlayType.VECTOR2D);
					}
				}
			}
		}		
	}
	
}
