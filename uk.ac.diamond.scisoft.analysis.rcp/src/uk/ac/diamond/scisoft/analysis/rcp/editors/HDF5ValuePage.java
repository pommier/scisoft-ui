package uk.ac.diamond.scisoft.analysis.rcp.editors;

import java.util.Iterator;

import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.hdf5.HDF5Attribute;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5Dataset;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5Group;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5Node;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5NodeLink;

public class HDF5ValuePage extends Page  implements ISelectionListener, IPartListener {

	private static Logger logger = LoggerFactory.getLogger(HDF5ValuePage.class);
	
	protected CLabel       label;
	protected SourceViewer sourceViewer;
	protected StructuredSelection lastSelection;

	protected Composite container;

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		
		this.container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		final GridLayout layout = (GridLayout)container.getLayout();
		layout.horizontalSpacing=0;
		layout.verticalSpacing  =0;
		layout.marginBottom     =0;
		layout.marginTop        =0;
		layout.marginLeft       =0;
		layout.marginRight      =0;
		layout.marginHeight     =0;
		layout.marginWidth      =0;
		container.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		this.label  = new CLabel(container, SWT.LEFT);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		this.sourceViewer = new SourceViewer(container, null, SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY );
		sourceViewer.setEditable(false);
		sourceViewer.getTextWidget().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    		
		getSite().getPage().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
		getSite().getPage().addPartListener(this);
		
		final IWorkbenchPage page = getActivePage();
		if (page != null)
			updateSelection(page.getSelection());
	}

	@Override
	public Control getControl() {
		return container;
	}
	
	@Override
	public void setFocus() {
		sourceViewer.getTextWidget().setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
		getSite().getPage().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		getSite().getPage().removePartListener(this);
		lastSelection=null;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		try {
			updateSelection(selection);
		} catch (Exception e) {
			logger.error("Cannot update value", e);
		}
	}

	protected void updateSelection(ISelection selection) {
		
		if (selection instanceof StructuredSelection) {
			this.lastSelection = (StructuredSelection)selection;
			final Object sel = lastSelection.getFirstElement();
			
			updateObjectSelection(sel);				
			
			sourceViewer.refresh();
			label.getParent().layout(new Control[]{label, sourceViewer.getTextWidget()});
			
			return;
		}
		
		clear();
	}

	/**
	 * Set it back to blank
	 */
	private void clear() {
		label.setText("");
		sourceViewer.getTextWidget().setText("");
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part == this) {
			try {
				updateSelection(lastSelection);
			} catch (Throwable ignored) {
				// There might not be a selection or page.
			}
		}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		
	}

	public void updateObjectSelection(Object sel) {
		
		if (sel instanceof HDF5NodeLink) {
			final HDF5NodeLink node = (HDF5NodeLink)sel;
			createH5Value(node);
 		} 
//		else if (sel instanceof H5Path) { // Might be nexus part.
//			
//			try {
//				final H5Path h5Path = (H5Path)sel;
//				final String path   = h5Path.getPath();
//				final IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
//				if (part instanceof IH5Editor) {
//					final String filePath = ((IH5Editor)part).getFilePath();
//					final IHierarchicalDataFile file = HierarchicalDataFactory.getReader(filePath);
//					final HObject ob = file.getData(path);
//					createH5Value(ob);
//				}
//				
//			} catch (Exception ne) {
//				logger.error(ne.getMessage()); // Not serious, no need for stack.
//			}
//			
//		}
	}
	
	private void createH5Value(HDF5NodeLink ob) {
		
		if (ob.isDestinationADataset()) {
			final HDF5Dataset  set   = (HDF5Dataset)ob.getDestination();
			
			final StringBuilder buf = new StringBuilder();
			label.setText("Dataset name of '"+ob.getName()+"' value:");
			buf.append(set.toString());
			appendAttributes(set, buf);
			sourceViewer.getTextWidget().setText(buf.toString());
			
		} if (ob.isDestinationAGroup()) {
			final HDF5Group  grp   = (HDF5Group)ob.getDestination();
			label.setText("Group name of '"+ob.getName()+"' children:");
			
			final StringBuilder buf = new StringBuilder();
			buf.append("[");
			for (Iterator<String> it = grp.getNodeNameIterator(); it.hasNext() ; ) {
				final String name = it.next();
				buf.append(name);
				
				if (it.hasNext()) {
					buf.append(", ");
					
				}
			}
			buf.append("]\n");
			
			appendAttributes(grp, buf);
			sourceViewer.getTextWidget().setText(buf.toString());

		}
	}
	
	private void appendAttributes(HDF5Node set, StringBuilder buf) {
		buf.append("\n\nAttributes:\n");
		for (Iterator<HDF5Attribute> it = set.getAttributeIterator(); it.hasNext(); ) {
			final HDF5Attribute attribute = it.next();
			buf.append(attribute.getName());
			buf.append(" = ");
			buf.append(attribute.getValue().toString());
			buf.append("\n");
		}
	}

	private static IWorkbenchPage getActivePage() {
		final IWorkbench bench = PlatformUI.getWorkbench();
		if (bench == null) return null;
		final IWorkbenchWindow window = bench.getActiveWorkbenchWindow();
		if (window == null) return null;
		return window.getActivePage();
	}
	

}
