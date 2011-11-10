/*-
 * Copyright © 2009 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rcp.util;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.DatasetUtils;
import uk.ac.diamond.scisoft.analysis.dataset.DoubleDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.io.DataSetProvider;
import uk.ac.diamond.scisoft.analysis.io.LoaderFactory;
import uk.ac.diamond.scisoft.analysis.plotserver.AxisMapBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataBeanException;
import uk.ac.diamond.scisoft.analysis.plotserver.DataSetWithAxisInformation;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiParameters;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiPlotMode;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlotWindow;
import uk.ac.diamond.scisoft.analysis.rcp.views.nexus.ExpressionObject;
import uk.ac.gda.monitor.ProgressMonitorWrapper;
import uk.ac.gda.monitor.IMonitor;

public class PlotUtils {

	private static final Logger logger = LoggerFactory.getLogger(PlotUtils.class);
	
	/**
	 * The selection array must file java.io.File[] or org.eclipse.core.resources.IFile[]
	 * @param files -  java.io.File[] or org.eclipse.core.resources.IFile[]
	 * @param selections - list of either String data set name or ExpressionObject
	 */
	public static void createComparisionPlot(final Object[] files, final List<Object> selections, final PlotMode plotMode, final PlotWindow window, final IProgressMonitor monitor) throws Exception{

		Object xSel = selections.get(0);

		final List<Object> ySel = new ArrayList<Object>(3);
		for (int i = 1; i < selections.size(); i++) {
			ySel.add(selections.get(i));
		}

		final File[]         fa = getFiles(files);
		AbstractDataset             x  = getLargestDataSet(fa, xSel, monitor);
		final List<AbstractDataset> ys = getDataSets(fa, ySel, true, monitor);
		
		if (ys.isEmpty()) {
			ys.add(x);
			x = DoubleDataset.arange(ys.get(0).getSize());
			x.setName("Index");
		}

		PlotUtils.create1DPlot(x, ys, plotMode, window, monitor);
	}

	private static List<AbstractDataset> getDataSets(final File[] files, final List<Object> namesOrExpressions, final boolean useFileName, final IProgressMonitor monitor) throws Exception{
		final List<AbstractDataset> ret = new ArrayList<AbstractDataset>(files.length*namesOrExpressions.size());
		for (int i = 0; i < files.length; i++) {
			for (Object n : namesOrExpressions) {
				ret.add(PlotUtils.getDataSet(files[i], n, useFileName, monitor));
			}
		}
		return ret;
	}

	@SuppressWarnings("unused")
	private static AbstractDataset getLargestDataSet(final File[] files, final Object nameOrExpression, final IProgressMonitor monitor) throws Exception {
		
		AbstractDataset ret = null;
		int    size = Integer.MIN_VALUE;
		for (int i = 0; i < files.length; i++) {
			try {
				final AbstractDataset set = getDataSet(files[i], nameOrExpression, false, monitor);
				if (set!=null) {
					if (set.getSize() > size) {
						size = set.getSize();
						ret  = set;
					}

				}
			} catch (Exception ignored) {
				continue; // Cannot be sure file has this set.
			}
		}

		return ret;
       	
  	}
	
	private static AbstractDataset getDataSet(final File file, final Object nameOrExpression, final boolean useFileName, final IProgressMonitor monitor) throws Exception {
        
		AbstractDataset set=null;
		if (nameOrExpression instanceof String) {
		    set = LoaderFactory.getDataSet(file.getAbsolutePath(), (String)nameOrExpression, new ProgressMonitorWrapper(monitor));
       	
        } else if (nameOrExpression instanceof ExpressionObject) {
        	final DataSetProvider prov = new DataSetProvider() {
				@Override
				public AbstractDataset getDataSet(String name, IMonitor monitor) {
					try {
						return LoaderFactory.getDataSet(file.getAbsolutePath(), name, monitor);
					} catch (Exception e) {
						return new DoubleDataset();
					}
				}

				@Override
				public boolean isDataSetName(String name, IMonitor monitor) {
					return true;
				}
        	};
        	((ExpressionObject)nameOrExpression).setProvider(prov);
        	set = ((ExpressionObject)nameOrExpression).getDataSet(monitor);
        }
		
		if (set!=null&&useFileName) {
			final String name = set.getName();
			if (name!=null) {
				set.setName(name+" ("+file.getName()+")");
			}
		}
		return set;
	}

	private static File[] getFiles(Object[] objects) {
		final File[] files = new File[objects.length];
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] instanceof File) {
			    files[i] = (File)objects[i];
			} else if (objects[i] instanceof IFile) {
				files[i] = ((IFile)objects[i]).getLocation().toFile();
			}
		}
		return files;
	}
	
	/**
	 * Thread safe
	 * @param plotWindow 
	 * @param monitor 
	 * @param plotMode 
	 * @param xDataSet 
	 * @param yDataSets 
	 */
	public static void create1DPlot(final AbstractDataset         xDataSet, 
			                        final List<AbstractDataset>   yDataSets, 
			                        final PlotMode         plotMode, 
			                        final PlotWindow       plotWindow, 
			                        final IProgressMonitor monitor) {
		
		if (xDataSet.getRank() != 1) return;

		// We allow yDataSets to be null if they like.
		final AbstractDataset x;
		final List<AbstractDataset> ys;
		if (yDataSets==null) {
			ys = new ArrayList<AbstractDataset>(1);
			ys.add(xDataSet);
			x = DoubleDataset.arange(ys.get(0).getSize());
		} else {
			x  = xDataSet;
			ys = yDataSets;
		}
		
		final Display display = PlatformUI.getWorkbench().getDisplay();
		if (display.isDisposed()) return;
		
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					plotWindow.updatePlotMode(plotMode.getGuiPlotMode());

					if (monitor!=null&&monitor.isCanceled()) return;
					
					// generate the bean to send the Gui information
					GuiBean guiBean = new GuiBean();
					guiBean.put(GuiParameters.PLOTMODE, plotMode.getGuiPlotMode());
					guiBean.put(GuiParameters.TITLE,    getTitle(x, ys, true));
					plotWindow.processGUIUpdate(guiBean);
					if (monitor!=null&&monitor.isCanceled()) return;

					DataBean dataBean = new DataBean(plotMode.getGuiPlotMode());
					dataBean.addAxis(AxisMapBean.XAXIS, x);
					
					// TODO use PM3D for z, currently hard codes something, in process of fixing.
					if (PlotMode.PM3D==plotMode) {
						final AbstractDataset z = new DoubleDataset(new double[]{-15,1,200});
						dataBean.addAxis(AxisMapBean.ZAXIS, z);
					}

					for (int i = 0; i < ys.size(); i++) {
						// now add it to the plot data
						try {
							dataBean.addData(DataSetWithAxisInformation.createAxisDataSet(ys.get(i)));
							if (monitor!=null&&monitor.isCanceled()) return;
						} catch (DataBeanException e) {
							logger.error("Problem adding data to bean as axis key does not exist", e);
						}
					}

					if (monitor!=null&&monitor.isCanceled()) return;
					plotWindow.processPlotUpdate(dataBean);
					
				} catch (Exception ne) {
					logger.error("Cannot create plot required.", ne);
				}
			}
			
		});
	}
	
	/**
	 * Attempts to create a plot with the data passed in.
	 * 
	 * Thread safe.
	 * 
	 * @param data
	 * @param axes
	 * @param mode
	 * @param plotWindow
	 * @param monitor
	 */
	public static void createPlot(final AbstractDataset       data,
			                      final List<AbstractDataset> axes,
			                      final GuiPlotMode           mode, 
			                      final PlotWindow            plotWindow, 
			                      final IProgressMonitor monitor) {
		
		
		final Display display = PlatformUI.getWorkbench().getDisplay();
		if (display.isDisposed()) return;
		
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					plotWindow.updatePlotMode(mode);

					if (monitor!=null&&monitor.isCanceled()) return;
					
					// generate the bean to send the Gui information
					GuiBean guiBean = new GuiBean();
					guiBean.put(GuiParameters.PLOTMODE, mode);
					guiBean.put(GuiParameters.TITLE,    data.getName());
					plotWindow.processGUIUpdate(guiBean);
					if (monitor!=null&&monitor.isCanceled()) return;

					DataBean dataBean = new DataBean(mode);
					DataSetWithAxisInformation axisData = new DataSetWithAxisInformation();
					AxisMapBean axisMapBean = new AxisMapBean(AxisMapBean.DIRECT);
					
					dataBean.addAxis(AxisMapBean.XAXIS, axes.get(0));
					dataBean.addAxis(AxisMapBean.YAXIS, axes.get(1));
					// note that the DataSet plotter's 2D mode is row-major
					axisData.setData(DatasetUtils.transpose(data, new int[] {1, 0}));
					axisData.setAxisMap(axisMapBean);

					dataBean.addData(axisData);

					if (monitor!=null&&monitor.isCanceled()) return;
					plotWindow.processPlotUpdate(dataBean);
					
				} catch (Exception ne) {
					logger.error("Cannot create plot required.", ne);
				}
			}
			
		});
	}


	private static Serializable getTitle(AbstractDataset x, List<AbstractDataset> ys, final boolean isFileName) {
		
		final StringBuilder buf = new StringBuilder();
		buf.append("Plot of");
		final Set<String> used = new HashSet<String>(7);
		for (IDataset dataSet : ys) {
			String name = dataSet.getName();
			
			if (isFileName) {
			    // Strip off file name
				final Matcher matcher = Pattern.compile("(.*) \\(.*\\)").matcher(name);
				if (matcher.matches()) name = matcher.group(1);
			}
			
			if (used.contains(name)) continue;
			used.add(name);
			buf.append(" ");
			buf.append(name);
			buf.append(",");
		}
		final int index = buf.length()-1;
		buf.delete(index, index+1);
		buf.append(" against ");
		buf.append(x.getName());
		return buf.toString();
	}



}
