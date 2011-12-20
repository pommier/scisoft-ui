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

/**
 * Returns yea nor nay if the give file contents matches the description of this content type.
 * <p>
 * This is looking for a series of commented out lines using #'s, then a series of tab-separated
 * numerical data, then optionally some commented out lines at the end.
 * <p>
 * The commented out lines may optionally contain metadata. If so, they are in the format:
 *   &lt;metadata string&gt; : &lt;the value&gt;  \n
 */
public class XasAsciiDescriber implements IContentDescriber, ITextContentDescriber{
	
	public static final String ID = "uk.ac.diamond.scisoft.analysis.rcp.editors.describers.XasAsciiDescriber";

	@Override
	public int describe(Reader contents, IContentDescription description) throws IOException {
//		return IContentDescriber.VALID;
		final BufferedReader reader = new BufferedReader(contents);
		try {
			String dataStr;
			boolean readingHeader = true;
			boolean readingFooter = false;
			while ((dataStr = reader.readLine()) != null) {
				
				dataStr = dataStr.trim();
				
				if (dataStr.isEmpty()){
					continue;
				}
				
				if (dataStr.startsWith("#") && readingHeader){
					// ignore as that's OK
					continue;
				} 
				readingHeader = false;
				
				if (dataStr.startsWith("#") && !readingHeader){
					readingFooter = true;
					continue;
				}
				
				if (readingFooter){
					return IContentDescriber.INVALID;
				}
				
				// if get here then we are reading the 
				String[] parts = dataStr.split("[\t ]+");
				for (String part : parts){
					try {
						Double.parseDouble(part);
					} catch (NumberFormatException e) {
						return IContentDescriber.INVALID;
					}
				}
			}
			return IContentDescriber.VALID;
		} finally {
			reader.close();
		}
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
