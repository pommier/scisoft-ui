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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.utils;

import junit.framework.Assert;

import org.junit.Test;
/**
 *
 */
public class ShaderBuildTest {

	private static final String FRAGPROG = 
		"uniform sampler2D sampler;\n"+
        "uniform sampler2D tableSampler;\n"+
        "uniform sampler2D overlaySampler;\n"+
        "uniform float maxValue;\n"+
        "uniform float minValue;\n"+
        "void main(void)\n"+
        "{\n"+
        " float dataValue = texture2D(sampler,gl_TexCoord[0].st).x;\n"+
        " float nDataValue = min(1.0,(dataValue - minValue) / (maxValue-minValue));\n"+
        " vec4 image = texture2D(tableSampler,vec2(nDataValue,nDataValue));\n"+
        " vec4 overlay = texture2D(overlaySampler, gl_TexCoord[0].st);\n"+
        " image = image * (1.0-overlay.w) + overlay * overlay.w;\n"+
        " gl_FragColor = image;\n"+
		"}\n";

    private static final String FRAGCOLORPASSTHROUGH =
    	"uniform sampler2D sampler;\n"+
    	"uniform sampler2D overlaySampler;\n"+
    	"void main(void)\n"+
    	"{\n"+
    	"vec4 image = texture2D(sampler,gl_TexCoord[0].st);\n"+
    	"vec4 overlay = texture2D(overlaySampler,gl_TexCoord[0].st);\n"+
    	"gl_FragColor = image;\n"+
    	"}\n";

	private static final String DIFFRAGPROG = 
		"uniform sampler2D sampler;\n"+
	    "uniform sampler2D tableSampler;\n"+
	    "uniform sampler2D overlaySampler;\n"+
	    "uniform float maxValue;\n"+
	    "uniform float minValue;\n"+
	    "uniform float threshold;\n"+
	    "void main(void)\n"+
	    "{\n"+
	    " float dataValue = texture2D(sampler,gl_TexCoord[0].st).x;\n"+
	    " float nDataValue = min(1.0,(dataValue - minValue) / (maxValue-minValue));\n"+
	    " vec4 image = texture2D(tableSampler,vec2(nDataValue,nDataValue));\n"+
	    " if (dataValue < -1.0) image = vec4(0.3,1.0,0.15,1.0);\n" +
	    " if (dataValue >= threshold) image = vec4(1,1,0,1);\n"+
        " vec4 overlay = texture2D(overlaySampler, gl_TexCoord[0].st);\n"+
        " image = image * (1.0-overlay.w) + overlay * overlay.w;\n"+
	    " gl_FragColor = image;\n"+
		"}\n";
	
	@Test
	public void buildNormalShader() {
		String shaderStr = JOGLGLSLShaderGenerator.generateShader(false, false, false,false);
		Assert.assertEquals(FRAGPROG, shaderStr);
		System.err.println(shaderStr);
	}
	
	@Test
	public void buildColourShader() {
		String shaderStr = JOGLGLSLShaderGenerator.generateShader(false, true, false,false);
		Assert.assertEquals(shaderStr, FRAGCOLORPASSTHROUGH);
	}
	
	@Test
	public void buildDiffractionShader() {
		String shaderStr = JOGLGLSLShaderGenerator.generateShader(false, false, true, false);
		Assert.assertEquals(shaderStr, DIFFRAGPROG);
		System.err.println(shaderStr);
	}
	
}
