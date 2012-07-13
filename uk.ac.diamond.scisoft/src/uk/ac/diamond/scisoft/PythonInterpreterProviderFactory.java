package uk.ac.diamond.scisoft;

import org.python.pydev.core.REF;
import org.python.pydev.ui.pythonpathconf.AbstractInterpreterProviderFactory;
import org.python.pydev.ui.pythonpathconf.AlreadyInstalledInterpreterProvider;
import org.python.pydev.ui.pythonpathconf.IInterpreterProvider;
import org.python.pydev.ui.pythonpathconf.IInterpreterProviderFactory;

/**
 * Provide a python from PATH if not running MS Windows
 */
public class PythonInterpreterProviderFactory extends AbstractInterpreterProviderFactory {

	@Override
	public IInterpreterProvider[] getInterpreterProviders(InterpreterType type) {
		if (type != IInterpreterProviderFactory.InterpreterType.PYTHON) {
			return null;
		}

		if (REF.isWindowsPlatform()) {
			return null;
		}

		// This should be enough to find it from the PATH or any other way it's
		// defined.
		return AlreadyInstalledInterpreterProvider.create("python from PATH", "python");
	}
}
