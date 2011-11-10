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

package uk.ac.diamond.scisoft.analysis.rcp.views.plot;

import java.util.Collection;
import java.util.Map;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.AxisValues;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.DataSetPlotter;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.IPlotUI;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.Plot1DAppearance;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.Plot1DUIAdapter;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlotColorUtility;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlotDataTableDialog;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlottingMode;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.AxisMode;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.Plot1DStyles;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.PlotActionComplexEvent;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.PlotActionEvent;

/**
 * Class is extended by classes that require the ability to take a graph snap shot and put it into a static plot. Not
 * currently an interface as intention is to add some common methods and fields here.
 */
public abstract class AbstractPlotView extends ViewPart implements PlotView {

	protected DataSetPlotter plotter;
	protected AxisValues xAxisValues;
	
	protected abstract String getYAxis();

	protected abstract String getXAxis();

	protected abstract String getGraphTitle();

	/**
	 * Override as required.
	 * 
	 * @param parent
	 * @return IPlotUI
	 */
	protected IPlotUI createPlotActions(final Composite parent) {
		return new Plot1DUIAdapter(getViewSite().getActionBars(), plotter, parent, getPartName()) {
			@Override
			public void buildToolActions(IToolBarManager manager) {
				manager.add(StaticScanPlotView.getOpenStaticPlotAction(AbstractPlotView.this));
				manager.add(createShowLegend());
				super.buildToolActions(manager);
			}

			@Override
			public void plotActionPerformed(final PlotActionEvent event) {
				if (event instanceof PlotActionComplexEvent) {
					parent.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							PlotDataTableDialog dataDialog = new PlotDataTableDialog(parent.getShell(),
									(PlotActionComplexEvent) event);
							dataDialog.open();
						}
					});
				} else {

					parent.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							plotter.getComposite().setToolTipText(event.toString() + "\nRight click to view data.");
						}
					});
				}
			}
		};
	}


	/**
	 * Create contents of the view part
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {

		final GridLayout grid = new GridLayout(1, false);
		grid.marginBottom = 0;
		grid.marginTop = 0;
		grid.horizontalSpacing = 0;
		grid.marginWidth = 0;
		grid.verticalSpacing = 0;
		grid.marginHeight = 0;
		parent.setLayout(grid);

		this.plotter = new DataSetPlotter(PlottingMode.ONED, parent, false);
		plotter.getComposite().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.xAxisValues = new AxisValues();
		plotter.setAxisModes(getXAxisMode(), getYAxisMode(), AxisMode.LINEAR);
		plotter.setXAxisValues(xAxisValues, 1);
		plotter.setXAxisLabel(getXAxis());
		plotter.setYAxisLabel(getYAxis());
		plotter.setPlotActionEnabled(true);
		plotter.setPlotRightClickActionEnabled(true);

		final IPlotUI plotUI = createPlotActions(parent);
		plotter.registerUI(plotUI);

		configurePlot(plotter);
	}

	/**
	 * Optionally override if extra plot config needed.
	 * 
	 * @param plotter
	 */
	public void configurePlot(@SuppressWarnings("unused") final DataSetPlotter plotter) {
		// Does nothing
	}

	/**
	 * Create legends from DataSet Map
	 * 
	 * @param p
	 * @param sets
	 */
	public static void createMultipleLegend(final DataSetPlotter p, final Map<String, ? extends AbstractDataset> sets) {
		int iplot = 1;
		for (String name : sets.keySet()) {
			final Plot1DAppearance app = new Plot1DAppearance(PlotColorUtility.getDefaultColour(iplot, name),
					Plot1DStyles.SOLID, 1, name);
			p.getColourTable().addEntryOnLegend(app);
			++iplot;
		}
	}

	/**
	 * Create legends from DataSet Map
	 * 
	 * @param p
	 * @param sets
	 */
	public static void createMultipleLegend(final DataSetPlotter p, final Collection<AbstractDataset> sets) {
		p.getColourTable().clearLegend();
		int iplot = 1;
		for (IDataset set : sets) {
			final Plot1DAppearance app = new Plot1DAppearance(PlotColorUtility.getDefaultColour(iplot, set.getName()),
					Plot1DStyles.SOLID, 1, set.getName());
			p.getColourTable().addEntryOnLegend(app);
			++iplot;
		}
	}

	/**
	 * Optionally override to change xAxis mode.
	 * 
	 * @return mode.
	 */
	public AxisMode getXAxisMode() {
		return AxisMode.CUSTOM;
	}

	/**
	 * Optionally override to change yAxis mode.
	 * 
	 * @return mode.
	 */
	public AxisMode getYAxisMode() {
		return AxisMode.LINEAR;
	}

	@Override
	public void setFocus() {
		plotter.requestFocus();
	}

	@Override
	public void dispose() {
		if (plotter != null) {
			plotter.cleanUp();
		}
		super.dispose();
	}

	/**
	 * Get the DataSetPlotter object
	 * 
	 * @return the DataSetPlotter object
	 */
	public DataSetPlotter getPlotter() {
		return plotter;
	}
}
