package uk.ac.diamond.scisoft.rp.api.tasks;

import java.util.ArrayList;
import java.util.List;

import uk.ac.diamond.scisoft.rp.api.ScriptUtils;

public class VPRotationAnimationTask extends Task {

	private final String axis;

	private final String inputFile;
	private final String startX;
	private final String startY;
	private final String startZ;
	private final String numberOfFrames;
	private final String fps;
	private final String mag;
	private final String projection;
	private final String resX;
	private final String resY;
	private final String quality;
	private final String orientVis;
	private final String centerVis;
	private final String extensionID;
	private final String stereoRenderID;
	private final String outputDir;

	public VPRotationAnimationTask(String axis, String inputFile,
			String startX, String startY, String startZ, String numberOfFrames,
			String fps, String mag,  String projection, String resX, String resY, String quality, String orientVis,
			String centertVis, String extensionID, String stereoRenderID,
			String outputDir) {
		this.axis = axis;
		this.inputFile = inputFile;
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.numberOfFrames = numberOfFrames;
		this.fps = fps;
		this.mag = mag;
		this.projection = projection;
		this.resX = resX;
		this.resY = resY;
		this.quality = quality;
		this.orientVis = orientVis;
		this.centerVis = centertVis;
		this.extensionID = extensionID;
		this.stereoRenderID = stereoRenderID;
		this.outputDir = outputDir;
	}

	@Override
	public List<String> getParameterList() {
		List<String> paramList = new ArrayList<String>();

		String shScriptDir = ScriptUtils.getAbsoluteScriptPath()
				+ "taskScripts/pvTask.sh";
		paramList.add(shScriptDir);

		String pyScriptDir = null;

		if (axis == "x") {
			pyScriptDir = ScriptUtils.getAbsoluteScriptPath()
					+ "taskScripts/rotXMovie.py";
		} else if (axis == "y") {
			pyScriptDir = ScriptUtils.getAbsoluteScriptPath()
					+ "taskScripts/rotYMovie.py";
		} else if (axis == "z") {
			pyScriptDir = ScriptUtils.getAbsoluteScriptPath()
					+ ("taskScripts/rotZMovie.py");
		}

		paramList.add(pyScriptDir);

		paramList.add(this.inputFile);
		paramList.add(this.startX);
		paramList.add(this.startY);
		paramList.add(this.startZ);
		paramList.add(this.numberOfFrames);
		paramList.add(this.fps);
		paramList.add(this.mag);
		paramList.add(this.projection);
		paramList.add(this.resX);
		paramList.add(this.resY);
		paramList.add(this.quality);
		paramList.add(this.orientVis);
		paramList.add(this.centerVis);
		paramList.add(this.extensionID);
		paramList.add(this.stereoRenderID);
		paramList.add(this.outputDir);

		return paramList;
	}
}
