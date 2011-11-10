/*-
 * Copyright © 2011 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rcp.rpc;

import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Display;

import uk.ac.diamond.scisoft.analysis.rpc.AnalysisRpcGenericInstanceDispatcher;

/**
 * Invokes methods on an instance class, but wraps the invoke in a {@link Display#syncExec(Runnable)} so they can run in
 * the UI thread.
 */
public class AnalysisRpcSyncExecDispatcher extends AnalysisRpcGenericInstanceDispatcher {

	/**
	 * @see AnalysisRpcGenericInstanceDispatcher#AnalysisRpcGenericInstanceDispatcher(Class, Object)
	 */
	public AnalysisRpcSyncExecDispatcher(Class<?> delegate, Object instance) {
		super(delegate, instance);
	}

	/**
	 * @see AnalysisRpcSyncExecDispatcher#getDispatcher(Object)
	 */
	public static AnalysisRpcSyncExecDispatcher getDispatcher(Object instance) {
		return new AnalysisRpcSyncExecDispatcher(instance.getClass(), instance);
	}

	/**
	 * Use the super class invoke, wrapped in a syncExec
	 */
	@Override
	protected Object invoke(final Method method, final Object instance, final Object[] args) throws Exception {
		final Object[] ret = new Object[1];
		final Exception[] exp = new Exception[1];
		syncExec(new Runnable() {

			@Override
			public void run() {
				try {
					ret[0] = method.invoke(instance, args);
				} catch (Exception e) {
					exp[0] = e;
				}
			}
		});

		if (exp[0] != null) {
			throw exp[0];
		}
		return ret[0];
	}
	
	protected void syncExec(Runnable r) {
		Display.getDefault().syncExec(r);
	}

}
