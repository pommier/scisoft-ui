package uk.ac.diamond.scisoft.rp.views;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;
import uk.ac.diamond.scisoft.rp.Activator;
import uk.ac.diamond.scisoft.rp.Render3DPreferencePage;
import uk.ac.diamond.scisoft.rp.api.AvizoImageUtils;
import uk.ac.diamond.scisoft.rp.composites.AvizoRotAnimComposite;
import uk.ac.diamond.scisoft.rp.composites.AvizoRotSnapshotComposite;
import uk.ac.diamond.scisoft.rp.composites.AvizoSliceAnimComposite;
import uk.ac.diamond.scisoft.rp.composites.AvizoSliceSnapshotComposite;
import uk.ac.diamond.scisoft.rp.composites.CreateImageInfoComposite;
import uk.ac.diamond.scisoft.rp.composites.IJRotSnapshotComposite;

public class RenderImagesMainView extends ViewPart {

	public static final String ID = "uk.ac.diamond.scisoft.rp.MainView";

	private Composite compositeParent;
	private ScrolledComposite sc;
	private Action preferencesAction = new PreferencesAction();
	private Action avizoSliceSnapshotCCAction = new AvizoSliceSnapshotCCAction();
	private Action avizoRotAimCCAction = new AvizoRotAimCCAction();
	private Action avizoSliceAnimCCAction = new AvizoSliceAnimCCAction();
	private Action avizoRotSnapshotCCAction = new AvizoRotSnapshotCCAction();
	private Action createInfoFileCCAction = new CreateInfoFileCCAction();
	private Action iJRotSnapshotCCAction = new IJRotSnapshotCCAction();

	private String folderDir;
	private String infoFileDir;

	private AvizoSliceSnapshotComposite avizoSliceSnapshotComposite;
	private AvizoSliceAnimComposite avizoSliceAnimComposite;
	private AvizoRotAnimComposite avizoRotAnimComposite;
	private AvizoRotSnapshotComposite avizoRotSnapshotComposite;
	private CreateImageInfoComposite createImageInfoComposite;
	private IJRotSnapshotComposite iJRotSnapshotComposite;

	private IFolder ifolder;
	
	@Override
	public void createPartControl(Composite parent) {
		compositeParent = parent;

		setAllActionsAsUnchecked();

		// set default composite
		avizoRotAimCCAction.run();

		IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().add(iJRotSnapshotCCAction);
		bars.getMenuManager().add(createInfoFileCCAction);
		bars.getMenuManager().add(preferencesAction);

		bars.getToolBarManager().add(avizoRotAimCCAction);
		bars.getToolBarManager().add(avizoSliceSnapshotCCAction);
		bars.getToolBarManager().add(avizoSliceAnimCCAction);
		bars.getToolBarManager().add(avizoRotSnapshotCCAction);
	}

	private void clearOpenComposite() {
		if (sc != null) {
			sc.dispose();
		}
	}

	private void refreshView() {
		compositeParent.layout();
	}

	private void setAllActionsAsUnchecked() {
		avizoSliceSnapshotCCAction.setChecked(false);
		avizoRotAimCCAction.setChecked(false);
		avizoSliceAnimCCAction.setChecked(false);
		avizoRotSnapshotCCAction.setChecked(false);
	}

	public void setFolderDir(String folder) {
		this.folderDir = folder;
		String infoDir = AvizoImageUtils.getDirOfInfoFile(folderDir);
		if (infoDir == null) { // folder directory does not contain .info file
			// open view for creating .info file
			createInfoFileCCAction.run();
		} else {
			this.infoFileDir = infoDir;
			avizoRotAimCCAction.run();
		}
	}
	
	public void setIFolder(IFolder ifolder){
		this.ifolder = ifolder;
		avizoRotAimCCAction.run();
	}

	public void setFileDir(String fileDir) {
		this.infoFileDir = fileDir;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	/*************************************** Actions *************************************/

	class AvizoSliceSnapshotCCAction extends Action {
		AvizoSliceSnapshotCCAction() {
			setText("Avizo Slice Snapshot");
			setImageDescriptor(Activator.getDefault().getWorkbench()
					.getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
		}

		public void run() {
			setAllActionsAsUnchecked();
			clearOpenComposite();
			sc = new ScrolledComposite(compositeParent, SWT.H_SCROLL
					| SWT.V_SCROLL | SWT.BORDER);
			avizoSliceSnapshotComposite = new AvizoSliceSnapshotComposite(sc,
					SWT.None);
			if (infoFileDir != null) {
				avizoSliceSnapshotComposite.setDirectory(infoFileDir);
			}
			if(ifolder != null){
				avizoSliceSnapshotComposite.setIFolder(ifolder);
			}
			sc.setContent(avizoSliceSnapshotComposite);
			sc.setExpandHorizontal(true);
			sc.setExpandVertical(true);
			sc.setMinSize(avizoSliceSnapshotComposite.computeSize(500, 330));
			setContentDescription("Avizo Slice Snapshot");
			setChecked(true);
			refreshView();
		}
	}

	class AvizoSliceAnimCCAction extends Action {
		AvizoSliceAnimCCAction() {
			setText("Avizo Slice Animation");
			setImageDescriptor(Activator.getDefault().getWorkbench()
					.getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
		}

		public void run() {
			setAllActionsAsUnchecked();
			clearOpenComposite();
			sc = new ScrolledComposite(compositeParent, SWT.H_SCROLL
					| SWT.V_SCROLL | SWT.BORDER);
			avizoSliceAnimComposite = new AvizoSliceAnimComposite(sc, SWT.None);
			if (infoFileDir != null) {
				avizoSliceAnimComposite.setDirectory(infoFileDir);
			}
			if(ifolder != null){
				avizoSliceAnimComposite.setIFolder(ifolder);
			}
			sc.setContent(avizoSliceAnimComposite);
			sc.setExpandHorizontal(true);
			sc.setExpandVertical(true);
			sc.setMinSize(avizoSliceAnimComposite.computeSize(500, 300));
			setContentDescription("Avizo Slice Animation");
			setChecked(true);
			refreshView();
		}
	}

	class AvizoRotAimCCAction extends Action {
		AvizoRotAimCCAction() {
			setText("Avizo Rotation Animation");
			setImageDescriptor(Activator.getDefault().getWorkbench()
					.getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
		}

		public void run() {
			setAllActionsAsUnchecked();
			clearOpenComposite();
			sc = new ScrolledComposite(compositeParent, SWT.H_SCROLL
					| SWT.V_SCROLL | SWT.BORDER);
			avizoRotAnimComposite = new AvizoRotAnimComposite(sc, SWT.None);
			if (infoFileDir != null) {
				avizoRotAnimComposite.setDirectory(infoFileDir);
			}
			if(ifolder != null){
				avizoRotAnimComposite.setIFolder(ifolder);
			}
			sc.setContent(avizoRotAnimComposite);
			sc.setExpandHorizontal(true);
			sc.setExpandVertical(true);
			sc.setMinSize(avizoRotAnimComposite.computeSize(500, 580));
			setContentDescription("Avizo Rotation Animation");
			setChecked(true);
			refreshView();
		}
	}

	class AvizoRotSnapshotCCAction extends Action {
		AvizoRotSnapshotCCAction() {
			setText("Avizo Rotation Snapshot");
			setImageDescriptor(Activator.getDefault().getWorkbench()
					.getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
		}

		public void run() {
			setAllActionsAsUnchecked();
			clearOpenComposite();
			sc = new ScrolledComposite(compositeParent, SWT.H_SCROLL
					| SWT.V_SCROLL | SWT.BORDER);
			avizoRotSnapshotComposite = new AvizoRotSnapshotComposite(sc,
					SWT.None);
			if (infoFileDir != null) {
				avizoRotSnapshotComposite.setDirectory(infoFileDir);
			}
			if(ifolder != null){
				avizoRotSnapshotComposite.setIFolder(ifolder);
			}
			sc.setContent(avizoRotSnapshotComposite);
			sc.setExpandHorizontal(true);
			sc.setExpandVertical(true);
			sc.setMinSize(avizoRotSnapshotComposite.computeSize(500, 580));
			setContentDescription("Avizo Rotation Snapshot");
			setChecked(true);
			refreshView();
		}
	}

	class CreateInfoFileCCAction extends Action {
		CreateInfoFileCCAction() {
			setText("Create info file");
			setImageDescriptor(Activator.getDefault().getWorkbench()
					.getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
		}

		public void run() {
			setAllActionsAsUnchecked();
			clearOpenComposite();
			sc = new ScrolledComposite(compositeParent, SWT.H_SCROLL
					| SWT.V_SCROLL | SWT.BORDER);
			createImageInfoComposite = new CreateImageInfoComposite(sc,
					SWT.None);
			if (folderDir != null) {
				createImageInfoComposite.setDirectory(folderDir);
			}
			if(ifolder != null){
				createImageInfoComposite.setIFolder(ifolder);
			}
			sc.setContent(createImageInfoComposite);
			sc.setExpandHorizontal(true);
			sc.setExpandVertical(true);
			sc.setMinSize(createImageInfoComposite.computeSize(500, 330));
			setContentDescription("Create info file");
			refreshView();
		}
	}

	class IJRotSnapshotCCAction extends Action {
		IJRotSnapshotCCAction() {
			setText("ImageJ Rotation Snapshot");
			setImageDescriptor(Activator.getDefault().getWorkbench()
					.getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
		}

		public void run() {
			setAllActionsAsUnchecked();
			clearOpenComposite();
			sc = new ScrolledComposite(compositeParent, SWT.H_SCROLL
					| SWT.V_SCROLL | SWT.BORDER);
			iJRotSnapshotComposite = new IJRotSnapshotComposite(sc, SWT.None);
			if (infoFileDir != null) {
				iJRotSnapshotComposite.setDirectory(infoFileDir);
			}
			if(ifolder != null){
				iJRotSnapshotComposite.setIFolder(ifolder);
			}
			sc.setContent(iJRotSnapshotComposite);
			sc.setExpandHorizontal(true);
			sc.setExpandVertical(true);
			sc.setMinSize(iJRotSnapshotComposite.computeSize(500, 335));
			setContentDescription("ImageJ Rotation Snapshot");
			refreshView();
		}
	}

	class PreferencesAction extends Action {
		PreferencesAction() {
			setText("Preferences");
			setImageDescriptor(Activator.getDefault().getWorkbench()
					.getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_OBJS_TASK_TSK));
		}

		public void run() {
			PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), Render3DPreferencePage.ID, null, null);
			if (pref != null) {
				pref.open();
			}
		}
	}

}
