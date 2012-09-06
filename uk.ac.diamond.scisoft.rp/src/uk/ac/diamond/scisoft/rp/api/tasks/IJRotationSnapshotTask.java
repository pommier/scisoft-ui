package uk.ac.diamond.scisoft.rp.api.tasks;

import java.util.ArrayList;
import java.util.List;

import uk.ac.diamond.scisoft.rp.api.ScriptUtils;

public class IJRotationSnapshotTask extends Task {

	private final boolean useCenter;

	private final String inputFile;
	private final String axis;
	private final String startAngle;
	private final String endAngle;
	private final String const1;
	private final String const2;
	private final String originX;
	private final String originY;
	private final String originZ;
	private final String numOfSnaps;
	private final String rendering;
	private final String resX;
	private final String resY;
	private final String extensionID;
	private final String outputDir;

	public IJRotationSnapshotTask(String inputFile, String axis,
			String startAngle, String endAngle, String const1, String const2,
			String originX, String originY, String originZ, String numOfSnaps,
			String rendering, String resX, String resY, String extensionID,
			String outputDir) {

		this.useCenter = false;
		this.inputFile = inputFile;
		this.axis = axis;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.const1 = const1;
		this.const2 = const2;
		this.originX = originX;
		this.originY = originY;
		this.originZ = originZ;
		this.numOfSnaps = numOfSnaps;
		this.rendering = rendering;
		this.resX = resX;
		this.resY = resY;
		this.extensionID = extensionID;
		this.outputDir = outputDir;
	}

	public IJRotationSnapshotTask(String inputFile, String axis,
			String startAngle, String endAngle, String const1, String const2,
			String numOfSnaps, String rendering, String resX, String resY,
			String extensionID, String outputDir) {

		this.useCenter = true;
		this.inputFile = inputFile;
		this.axis = axis;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.const1 = const1;
		this.const2 = const2;
		this.originX = null;
		this.originY = null;
		this.originZ = null;
		this.numOfSnaps = numOfSnaps;
		this.rendering = rendering;
		this.resX = resX;
		this.resY = resY;
		this.extensionID = extensionID;
		this.outputDir = outputDir;
	}

	@Override
	public List<String> getParameterList() {
		List<String> paramList = new ArrayList<String>();

		String shScriptDir = ScriptUtils.getAbsoluteScriptPath()
				+ "taskScripts/IJRotSnapshot.sh";
		paramList.add(shScriptDir);

		paramList.add(this.inputFile);
		paramList.add(this.axis);
		paramList.add(this.startAngle);
		paramList.add(this.endAngle);
		paramList.add(this.const1);
		paramList.add(this.const2);
		if (!this.useCenter) {
			paramList.add(this.originX);
			paramList.add(this.originY);
			paramList.add(this.originZ);
		}
		paramList.add(this.numOfSnaps);
		paramList.add(this.rendering);
		paramList.add(this.resX);
		paramList.add(this.resY);
		paramList.add(this.extensionID);
		paramList.add(this.outputDir);

		return paramList;
	}

}
