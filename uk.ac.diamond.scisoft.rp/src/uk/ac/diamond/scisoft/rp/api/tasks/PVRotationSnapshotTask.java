package uk.ac.diamond.scisoft.rp.api.tasks;

import java.util.ArrayList;
import java.util.List;

import uk.ac.diamond.scisoft.rp.api.ScriptUtils;

public class PVRotationSnapshotTask extends Task {

	private final String inputFile;
	private final String startAngle;
	private final String endAngle;
	private final String originX;
	private final String originY;
	private final String originZ;
	private final String const1;
	private final String const2;
	private final String axis;
	private final String transX;
	private final String transY;
	private final String transZ;
	private final String numOfSnaps;
	private final String orientVis;
	private final String centerVis;
	private final String mag;
	private final String projection;
	private final String resX;
	private final String resY;
	private final String stereoRenderID;
	private final String extensionID;
	private final String outputDir;

	public PVRotationSnapshotTask(String inputFile, String startAngle,
			String endAngle, String originX, String originY, String originZ,
			String const1, String const2, String axis, String transX,
			String transY, String transZ, String numOfSnaps, String orientVis,
			String centerVis, String mag, String projection, String resX, String resY, String stereoRenderID,
			String extensionID, String outputDir) {
		this.inputFile = inputFile;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.originX = originX;
		this.originY = originY;
		this.originZ = originZ;
		this.const1 = const1;
		this.const2 = const2;
		this.axis = axis;
		this.transX = transX;
		this.transY = transY;
		this.transZ = transZ;
		this.numOfSnaps = numOfSnaps;
		this.orientVis = orientVis;
		this.centerVis = centerVis;
		this.mag = mag;
		this.projection = projection;
		this.resX = resX;
		this.resY = resY;
		this.stereoRenderID = stereoRenderID;
		this.extensionID = extensionID;
		this.outputDir = outputDir;
	}

	@Override
	public List<String> getParameterList() {
		List<String> paramList = new ArrayList<String>();

		String shScriptDir = ScriptUtils.getAbsoluteScriptPath()
				+ "taskScripts/pvTask.sh";
		paramList.add(shScriptDir);

		String pyScriptDir = ScriptUtils.getAbsoluteScriptPath()
				+ "taskScripts/rot.py";
		paramList.add(pyScriptDir);

		paramList.add(this.inputFile);
		paramList.add(this.startAngle);
		paramList.add(this.endAngle);
		paramList.add(this.originX);
		paramList.add(this.originY);
		paramList.add(this.originZ);
		paramList.add(this.const1);
		paramList.add(this.const2);
		paramList.add(this.axis);
		paramList.add(this.transX);
		paramList.add(this.transY);
		paramList.add(this.transZ);
		paramList.add(this.numOfSnaps);
		paramList.add(this.orientVis);
		paramList.add(this.centerVis);
		paramList.add(this.mag);
		paramList.add(this.projection);
		paramList.add(this.resX);
		paramList.add(this.resY);
		paramList.add(this.stereoRenderID);
		paramList.add(this.extensionID);
		paramList.add(this.outputDir);

		return paramList;
	}

}
