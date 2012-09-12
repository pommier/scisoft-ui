package uk.ac.diamond.scisoft.rp;


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class Render3DInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {						
		final IPreferenceStore store = new ScopedPreferenceStore(
				InstanceScope.INSTANCE, "uk.ac.diamond.scisoft.rp");
		store.setDefault(Render3DPreferencePage.snapshotResX, 400);		
		store.setDefault(Render3DPreferencePage.snapshotResY,  400);		
		store.setDefault(Render3DPreferencePage.snapshotNumberOfSnaps, 15);
		store.setDefault(Render3DPreferencePage.movieResX,  300);
		store.setDefault(Render3DPreferencePage.movieResY,  300);
		store.setDefault(Render3DPreferencePage.movieNumberOfFrames,  5);		
		store.setDefault(Render3DPreferencePage.useCenter,  true);	
		store.setDefault(Render3DPreferencePage.centX,  "0");
		store.setDefault(Render3DPreferencePage.centY,  "0");
		store.setDefault(Render3DPreferencePage.centZ,  "0");				
		store.setDefault(Render3DPreferencePage.openInIm,  true);	
		store.setDefault(Render3DPreferencePage.openInIe,  false);					
	}

}
