package uk.ac.diamond.scisoft.qstatmonitor.views;

import java.util.ArrayList;

import org.dawb.common.ui.util.EclipseUtils;
import org.dawb.common.ui.views.ImageMonitorView;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.progress.UIJob;

import uk.ac.diamond.scisoft.analysis.SDAPlotter;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IntegerDataset;
import uk.ac.diamond.scisoft.analysis.rcp.views.PlotView;
import uk.ac.diamond.scisoft.qstatmonitor.Activator;
import uk.ac.diamond.scisoft.qstatmonitor.QStatMonitorPreferencePage;
import uk.ac.diamond.scisoft.qstatmonitor.api.Utils;

public class QStatMonitorView extends ViewPart {

	public static final String ID = "uk.ac.diamond.qstatmonitor.views.QStatMonitorView";

	private Table table;
	private final String[] titles = { "Job Number", "Priority", "Job Name",
			"Owner", "State", "Submission Time", "Queue Name", "Slots", "Tasks" };

	private ArrayList<String> jobNumberList = new ArrayList<String>();
	private ArrayList<String> priorityList = new ArrayList<String>();
	private ArrayList<String> jobNameList = new ArrayList<String>();
	private ArrayList<String> ownerList = new ArrayList<String>();
	private ArrayList<String> stateList = new ArrayList<String>();
	private ArrayList<String> submissionTimeList = new ArrayList<String>();
	private ArrayList<String> queueNameList = new ArrayList<String>();
	private ArrayList<String> slotsList = new ArrayList<String>();
	private ArrayList<String> tasksList = new ArrayList<String>();

	private int sleepTimeMilli;
	private String qStatQuery;
	private String userArg;

	private final RefreshAction refreshAction = new RefreshAction();
	private final OpenPreferencesAction openPreferencesAction = new OpenPreferencesAction();
	private UpdaterThread updaterThread;

	/*
	 * Runs the Qstat query and stores the resulting items in relevant arrays,
	 * then calls the redrawing of the table.
	 */
	private final Job getQstatInfoJob = new Job("Fetching QStat Info") {
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				ArrayList<String>[] lists = Utils.getTableLists(qStatQuery,
						userArg);
				jobNumberList = lists[0];
				priorityList = lists[1];
				jobNameList = lists[2];
				ownerList = lists[3];
				stateList = lists[4];
				submissionTimeList = lists[5];
				queueNameList = lists[6];
				slotsList = lists[7];
				tasksList = lists[8];
				redrawTable();
			} catch (StringIndexOutOfBoundsException e) {
				stopThreadAndJobs();
			} catch (NullPointerException npe) {
				stopThreadAndJobs();
				updateContentDescriptionError();
			}
			return Status.OK_STATUS;
		}
	};

	/*
	 * removes all current items from the table, then adds the contents of the
	 * arrays to the relevant columns, then packs the table
	 */
	private final UIJob redrawTableJob = new UIJob("Redrawing Table") {
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			try {
				table.removeAll();
				for (int i = 0; i < jobNumberList.size(); i++) {
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(0, jobNumberList.get(i));
					item.setText(1, priorityList.get(i));
					item.setText(2, jobNameList.get(i));
					item.setText(3, ownerList.get(i));
					item.setText(4, stateList.get(i));
					item.setText(5, submissionTimeList.get(i));
					item.setText(6, queueNameList.get(i));
					item.setText(7, slotsList.get(i));
					item.setText(8, tasksList.get(i));
				}
				packTable();
				updateContentDescription();
			} catch (SWTException e) {
				stopThreadAndJobs();
			}
			return Status.OK_STATUS;
		}
	};

	private final Runnable setContentDescriptionOnError = new Runnable() {
		@Override
		public void run() {
			setContentDescription("Inalid Qstat query entered.");
		}
	};

	/*
	 * Runs a loop which updates the table
	 */
	private class UpdaterThread extends Thread {
		private boolean runCondition = true;

		@Override
		public void run() {
			while (runCondition) {
				try {
					sleep(sleepTimeMilli);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				updateTable();
			}
		}

		public void stopThread() {
			runCondition = false;
		}
	};

	/**
	 * Constructor, gets preferences from the preference store and stores them
	 * as variables within this object
	 */
	public QStatMonitorView() {
		final IPreferenceStore store = new ScopedPreferenceStore(
				InstanceScope.INSTANCE, "uk.ac.diamond.scisoft.qstatMonitor");
		sleepTimeMilli = store.getInt(QStatMonitorPreferencePage.SLEEP);
		qStatQuery = store.getString(QStatMonitorPreferencePage.QUERY);
		userArg = store.getString(QStatMonitorPreferencePage.USER);
		if (!store.getBoolean(QStatMonitorPreferencePage.DISABLE_AUTO_REFRESH)) {
			System.out.println("Creating thread");
			updaterThread = new UpdaterThread();
		}

		try {
			final PlotView view = (PlotView) EclipseUtils.getPage().showView(
					"uk.ac.diamond.scisoft.qstatMonitor.qstatPlot");
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		plotResults();

	}

	private void plotResults() {

		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(1);
		arr.add(55);
		arr.add(22);
		arr.add(88);

		ArrayList<Integer> arr2 = new ArrayList<Integer>();
		arr2.add(5);
		arr2.add(65);
		arr2.add(22);
		arr2.add(77);

		ArrayList<Integer> time = new ArrayList<Integer>();
		time.add(5);
		time.add(10);
		time.add(12);
		time.add(15);

		final PlotView view = (PlotView) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage()
				.findView("uk.ac.diamond.scisoft.qstatMonitor.qstatPlot");

		IntegerDataset timeDataset = (IntegerDataset) IntegerDataset
				.createFromList(time);
		timeDataset.setName("Time");

		IntegerDataset[] datasetArr = {
				(IntegerDataset) IntegerDataset.createFromList(arr),
				(IntegerDataset) IntegerDataset.createFromList(arr2) };

		datasetArr[0].setName("Num tasks");
		datasetArr[1].setName("Slots");
		
		if (view != null) {
			try {
				SDAPlotter.plot("QStat Monitor Plot", timeDataset, datasetArr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().add(openPreferencesAction);
		bars.getToolBarManager().add(refreshAction);

		table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}
		updateTable();
		redrawTable();
	}

	/**
	 * schedules the getQstatInfoJob, cancelling it if it is already running
	 */
	public void updateTable() {
		getQstatInfoJob.cancel();
		getQstatInfoJob.schedule();
	}

	/**
	 * schedules the redrawTableJob, cancelling it if it is already running
	 */
	private void redrawTable() {
		redrawTableJob.cancel();
		redrawTableJob.schedule();
	}

	/**
	 * Packs each column of the table so that all titles and items are visible
	 * without resizing
	 */
	private void packTable() {
		for (int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {
		stopThreadAndJobs();
		super.dispose();
	}

	/**
	 * stops the updaterThread, getQstatInfoJob and redrawTableJob
	 */
	private void stopThreadAndJobs() {
		if (updaterThread != null) {
			updaterThread.stopThread();
		}
		getQstatInfoJob.cancel();
		redrawTableJob.cancel();
	}

	/**
	 * Updates the content description to show the number of tasks been
	 * displayed in the table
	 */
	private void updateContentDescription() {
		int numItems = jobNumberList.size();
		if (numItems == 1) {
			setContentDescription("Showing 1 task.");
		} else {
			setContentDescription("Showing " + numItems + " tasks.");
		}
	}

	private void updateContentDescriptionError() {
		PlatformUI.getWorkbench().getDisplay()
				.asyncExec(setContentDescriptionOnError);
	}

	/**
	 * setter for sleepTimeMilli
	 * 
	 * @param seconds
	 *            new value in seconds
	 */
	public void setSleepTimeSecs(double seconds) {
		sleepTimeMilli = (int) Math.round(seconds * 1000);
	}

	/**
	 * setter for sleepTimeMilli
	 * 
	 * @param seconds
	 *            new value in milliseconds
	 */
	public void setSleepTimeMilli(int milliSeconds) {
		sleepTimeMilli = milliSeconds;
	}

	/**
	 * setter for qStatQuery
	 * 
	 * @param query
	 */
	public void setQuery(String query) {
		qStatQuery = query;
	}

	/**
	 * stops the updaterThread by setting its run condition to false
	 */
	public void stopRefreshing() {
		if (updaterThread != null) {
			updaterThread.stopThread();
		}
	}

	/**
	 * starts a new updaterThread, if one is already running it is stopped
	 */
	public void startRefreshing() {
		if (updaterThread != null && updaterThread.isAlive()) {
			updaterThread.stopThread();
		}
		updaterThread = new UpdaterThread();
		updaterThread.start();
	}

	/**
	 * setter for userArg
	 * 
	 * @param userID
	 */
	public void setUserArg(String userID) {
		userArg = userID;
	}

	/*
	 * **************************************** ACTION
	 * ********************************
	 */

	class RefreshAction extends Action {
		RefreshAction() {
			setText("Refresh table");
			setImageDescriptor(Activator.getDefault().getWorkbench()
					.getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
		}

		public void run() {
			updateTable();
			redrawTable();
		}
	}

	class OpenPreferencesAction extends Action {
		OpenPreferencesAction() {
			setText("Preferences");
			setImageDescriptor(Activator.getDefault().getWorkbench()
					.getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
		}

		public void run() {
			PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), QStatMonitorPreferencePage.ID, null,
					null);
			if (pref != null) {
				pref.open();
			}
		}
	}

}