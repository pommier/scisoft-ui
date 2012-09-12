package uk.ac.diamond.scisoft.qstatmonitor;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class QStatMonitorPreferenceIntializer extends
		AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {		
		final IPreferenceStore store = new ScopedPreferenceStore(
				InstanceScope.INSTANCE, "uk.ac.diamond.scisoft.qstatMonitor");
		store.setDefault(QStatMonitorPreferencePage.SLEEP, "4.5");
		store.setDefault(QStatMonitorPreferencePage.DISABLE_AUTO_REFRESH, false);
		store.setDefault(QStatMonitorPreferencePage.QUERY, "qstat");
		store.setDefault(QStatMonitorPreferencePage.USER, "*");		
	}

}
