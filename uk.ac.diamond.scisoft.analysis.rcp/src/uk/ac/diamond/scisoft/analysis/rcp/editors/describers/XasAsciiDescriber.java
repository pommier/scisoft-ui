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
