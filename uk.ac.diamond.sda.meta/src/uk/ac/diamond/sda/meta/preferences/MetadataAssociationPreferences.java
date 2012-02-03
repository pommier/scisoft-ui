package uk.ac.diamond.sda.meta.preferences;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.progress.UIJob;

import uk.ac.diamond.sda.meta.Activator;
import uk.ac.diamond.sda.meta.utils.MapUtils;



public class MetadataAssociationPreferences
	extends PreferencePage
	implements IWorkbenchPreferencePage {

	private List fileAssociations;
	private Text txtFileType;
	private Text txtFileAssociation;


	public MetadataAssociationPreferences() {
		//setDescription("These Preferences show the association between metadata loaders and different ways of viewing it");
	}
	


	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());	
	}


	@Override
	protected Control createContents(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		
		Group gpdefaultType = new Group(comp, SWT.NONE);
		gpdefaultType.setText("Default Metadata Page");
		gpdefaultType.setLayout(new GridLayout(2, true));
		
		new Label(gpdefaultType, SWT.NONE).setText("The default viewer is ");
		Text defaultType = new Text(gpdefaultType, SWT.READ_ONLY);
		defaultType.setText(getPreferenceStore().getDefaultString(PreferenceConstants.defaultPage));
		defaultType.setBackground(gpdefaultType.getBackground());
		
		Group gpfileAssociationTable = new Group(comp, SWT.NONE);
		gpfileAssociationTable.setText("File Associations");
		gpfileAssociationTable.setLayout(new GridLayout(1, false));
		gpfileAssociationTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		GridData data = new GridData(GridData.FILL_BOTH);
		fileAssociations = new List(gpfileAssociationTable, SWT.BORDER);
		fileAssociations.setLayoutData(data);
		fileAssociations.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				updateAssociationDetails(fileAssociations.getSelection());
			}
		}
		);
		
		Group gpFileAssociationDetails = new Group(comp, SWT.NONE);
		gpFileAssociationDetails.setText("Association Details");
		gpFileAssociationDetails.setLayout(new GridLayout(2, false));
		gpFileAssociationDetails.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		
		new Label(gpFileAssociationDetails, SWT.NONE).setText("File: ");
		txtFileType = new Text(gpFileAssociationDetails, SWT.READ_ONLY);
		txtFileType.setBackground(gpFileAssociationDetails.getBackground());
		txtFileType.setLayoutData(data);
		
		new Label(gpFileAssociationDetails, SWT.NONE).setText("Association: ");
		txtFileAssociation =  new Text(gpFileAssociationDetails, SWT.NONE);
		txtFileAssociation.setBackground(gpFileAssociationDetails.getBackground());
		txtFileAssociation.setLayoutData(data);
		
		updateFileAssociation();
		return comp;
	}
	
	

	protected void updateAssociationDetails(final String[] selection) {
	UIJob updateDetails = new UIJob("Updata details") {
		
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			//there should only be a single selection.
			Map<String, String> map = getHashMapFromPreferences();
			txtFileType.setText(selection[0]);
			txtFileAssociation.setText(map.get(selection[0]));
			return Status.OK_STATUS;
		}
	};
	updateDetails.schedule();
	}



	private void updateFileAssociation() {
		final Set<String> set = getHashMapFromPreferences().keySet();
		UIJob updateLists = new UIJob("Update the file lists") {
			
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				for (String string : set) {
					fileAssociations.add(string);
				}return Status.OK_STATUS;
			}
		};
		updateLists.schedule();
	}

	private Map<String, String> getHashMapFromPreferences(){
		return MapUtils.getMap(getPreferenceStore().getString(PreferenceConstants.defaultMetadataAssociation));
	}



	@Override
	protected void performDefaults() {
		super.performDefaults();
		// TODO There should be some code to do this here and in the view
	}

		
}