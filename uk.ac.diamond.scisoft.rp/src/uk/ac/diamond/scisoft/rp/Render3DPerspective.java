package uk.ac.diamond.scisoft.rp;

import org.dawb.common.ui.views.ImageMonitorView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import uk.ac.diamond.scisoft.analysis.rcp.views.ImageExplorerView;
import uk.ac.diamond.scisoft.rp.views.RenderImagesMainView;

public class Render3DPerspective implements IPerspectiveFactory {

	public static final String ID = "uk.ac.diamond.scisoft.rp.Render3DPerspective";

	/**
	 * Creates the initial layout for a page.
	 */
	public void createInitialLayout(IPageLayout layout) {

		String editorArea = layout.getEditorArea();
		IFolderLayout navigatorFolder = layout.createFolder("navigator-folder",
				IPageLayout.LEFT, 0.2f, editorArea);
		navigatorFolder.addView("org.eclipse.ui.navigator.ProjectExplorer");
		navigatorFolder.addView("uk.ac.diamond.sda.navigator.views.FileView");
		{
			IFolderLayout folderLayout = layout.createFolder("folder",
					IPageLayout.RIGHT, 0.6f, IPageLayout.ID_EDITOR_AREA);
			folderLayout.addView(RenderImagesMainView.ID);
			folderLayout.addView("org.dawb.workbench.views.dataSetView");
			folderLayout
					.addView("org.dawb.workbench.plotting.views.toolPageView.2D");
		}
		{
			IFolderLayout folderLayout = layout.createFolder("folder_1",
					IPageLayout.BOTTOM, 0.7f, IPageLayout.ID_EDITOR_AREA);
			folderLayout.addView(ImageMonitorView.ID);
			folderLayout.addView(ImageExplorerView.ID);					
			folderLayout.addView("org.dawb.passerelle.views.ValueView");
			folderLayout.addView("org.eclipse.ui.console.ConsoleView");
		}
	}

}
