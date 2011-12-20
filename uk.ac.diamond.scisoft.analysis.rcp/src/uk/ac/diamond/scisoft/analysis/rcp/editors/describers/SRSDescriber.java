/*
 * Copyright © 2011 Diamond Light Source Ltd.
 * Contact :  ScientificSoftware@diamond.ac.uk
 * 
 * This is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 * 
 * This software is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this software. If not, see <http://www.gnu.org/licenses/>.
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
