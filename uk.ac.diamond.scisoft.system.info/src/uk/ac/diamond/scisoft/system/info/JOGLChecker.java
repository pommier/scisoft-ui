/*-
 * Copyright © 2010 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.system.info;

/**
 *
 */

import java.nio.IntBuffer;

import javax.media.opengl.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JOGLChecker {

	private static final Logger logger = LoggerFactory
	.getLogger(JOGLChecker.class);

	private static int maxXdim = 8192;
	private static int maxYdim = 8192;
	private static String glVendorStr = "";
	
	@SuppressWarnings("static-access")
	static public boolean canUseJOGL_OpenGL(String viewer, Composite parent) {
		GLCanvas dummyCanvas = null;		
		boolean hasJOGL = true;
		try {
			Class.forName(viewer).newInstance();
			GLData data = new GLData();
			data.doubleBuffer = true;
			data.depthSize = 16;
			data.redSize = 8;
			data.greenSize = 8;
			data.blueSize = 8;
			data.alphaSize = 8;
			dummyCanvas = new GLCanvas(parent,SWT.NO_BACKGROUND,data);
			dummyCanvas.setCurrent();
			GLContext context = GLDrawableFactory.getFactory().createExternalGLContext();
			context.makeCurrent();
			context.getGL().glMultTransposeMatrixd(new double[]{0.0,0.0,0.0,0.0,
																0.0,0.0,0.0,0.0,
																0.0,0.0,0.0,0.0,
																0.0,0.0,0.0,0.0,
																0.0,0.0,0.0,0.0},0);
			glVendorStr = context.getGL().glGetString(GL.GL_VENDOR);
			IntBuffer intB = IntBuffer.allocate(1);
			context.getGL().glGetIntegerv(context.getGL().GL_MAX_TEXTURE_SIZE, intB);
			
			maxXdim = maxYdim = intB.get(0);
			boolean isOkay = false;
			while (!isOkay) {
				context.getGL().glTexImage2D(context.getGL().GL_PROXY_TEXTURE_2D, 0, context.getGL().GL_RGBA, maxXdim, 
	                     maxYdim, 0, context.getGL().GL_RGBA, context.getGL().GL_BYTE, null);
				context.getGL().glGetTexLevelParameteriv(context.getGL().GL_PROXY_TEXTURE_2D,0,context.getGL().GL_TEXTURE_WIDTH,intB);
				if (intB.get(0) == maxXdim) {
					isOkay = true;
				} else {
					maxXdim = (maxXdim >> 1);
					maxYdim = (maxYdim >> 1);
				}
			}
			context.release();
			context.destroy();
		} catch (NoClassDefFoundError ndfe) {
			logger.warn("No class found for JOGL hardware acceleration");
			hasJOGL = false;
		} catch (UnsatisfiedLinkError le) {
			logger.warn("JOGL linking error");
			hasJOGL = false;
		} catch (Exception e) { //
			logger.warn("No JOGL using software render",e);
			hasJOGL = false;
		}
		if (dummyCanvas != null) dummyCanvas.dispose();
		return hasJOGL;
	}
	
	static public int getMaxTextureWidth() {
		return maxXdim;
	}
	
	static public int getMaxTextureHeight() {
		return maxYdim;
	}
	
	static public String getVendorName() {
		return glVendorStr;
	}
	
}
