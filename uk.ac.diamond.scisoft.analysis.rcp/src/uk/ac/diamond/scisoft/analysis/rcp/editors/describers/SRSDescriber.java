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

package uk.ac.diamond.scisoft.analysis.rcp.editors.describers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;

public class SRSDescriber implements IContentDescriber, ITextContentDescriber {

	@Override
	public int describe(Reader contents, IContentDescription description) throws IOException {
		final BufferedReader reader = new BufferedReader(contents);
		boolean foundSRS = false;
		boolean foundEND = false;
		try {
			String dataStr;
			while ((dataStr = reader.readLine()) != null) {

				dataStr = dataStr.trim();

				if (dataStr.equals("&SRS")) {
					foundSRS = true;
				}

				if (dataStr.equals("&END")) {
					foundEND = true;
					break;
				}
			}
		} finally {
			reader.close();
		}

		if (foundSRS && foundEND) {
			return IContentDescriber.VALID;
		}
		return IContentDescriber.INVALID;
	}

	@Override
	public int describe(InputStream contents, IContentDescription description) throws IOException {
		return describe(new InputStreamReader(contents, "UTF-8"), description);
	}

	@Override
	public QualifiedName[] getSupportedOptions() {
		return IContentDescription.ALL;
	}

}
