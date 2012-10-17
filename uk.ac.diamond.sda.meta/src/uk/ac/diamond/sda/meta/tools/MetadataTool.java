package uk.ac.diamond.sda.meta.tools;

import org.dawb.common.ui.plot.tool.AbstractToolPage;
import org.dawb.common.ui.plot.trace.IImageTrace;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPageSite;

import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.sda.meta.Activator;
import uk.ac.diamond.sda.meta.page.DiffractionMetadataComposite;
import uk.ac.diamond.sda.meta.page.IDiffractionMetadataCompositeListener;

public class MetadataTool extends AbstractToolPage {

	protected DiffractionMetadataComposite diffMetadataComp;
	protected Composite composite;
	
	public MetadataTool() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void createControl(Composite parent) {
		diffMetadataComp = new DiffractionMetadataComposite();
		diffMetadataComp.setEditable(true);
		IImageTrace imageTrace = getImageTrace();
		if (imageTrace != null) {
			AbstractDataset dataset = imageTrace.getData();
			if (dataset != null) {
				diffMetadataComp.setData(dataset);
				IMetaData meta = dataset.getMetadata();
				diffMetadataComp.setMetaData(meta);
			}
		}

		composite = diffMetadataComp.createComposite(parent);
		
		// Add the MXPlotImageEditor as a listener
		IWorkbenchPart viewPart = getPart();
		if (viewPart instanceof IDiffractionMetadataCompositeListener) {
			IDiffractionMetadataCompositeListener diffMetaCompListener = (IDiffractionMetadataCompositeListener)viewPart;
			diffMetadataComp.addDiffractionMetadataCompositeListener(diffMetaCompListener);
		}
		
		final IPageSite site = getSite();
		
		final Action reselect = new Action("Reset all fields", Activator.getImageDescriptor("icons/book_previous.png")) {
			@Override
			public void run() {
				diffMetadataComp.resetAllToOriginal();
			}
		};
		
		site.getActionBars().getToolBarManager().add(reselect);
		
		activate();
	}

	@Override
	public void dispose() {
		super.dispose();
		
		// Remove the MXPlotImageEditor as a listener
		IWorkbenchPart viewPart = getPart();
		if (viewPart instanceof IDiffractionMetadataCompositeListener) {
			IDiffractionMetadataCompositeListener diffMetaCompListener = (IDiffractionMetadataCompositeListener)viewPart;
			diffMetadataComp.removeDiffractionMetadataCompositeListener(diffMetaCompListener);
		}
	}

	@Override
	public Control getControl() {
		return composite;
	}

	@Override
	public ToolPageRole getToolPageRole() {
		return ToolPageRole.ROLE_2D;
	}

	@Override
	public void setFocus() {
		composite.setFocus();
	}
}
