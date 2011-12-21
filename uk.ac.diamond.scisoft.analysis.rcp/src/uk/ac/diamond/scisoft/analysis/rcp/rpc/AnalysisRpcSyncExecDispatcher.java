/*
 * Copyright 2012 Diamond Light Source Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
