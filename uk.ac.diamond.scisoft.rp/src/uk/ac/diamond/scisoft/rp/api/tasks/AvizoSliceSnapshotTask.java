/**
 * 
 */
package uk.ac.diamond.scisoft.rp.api.tasks;

import java.util.ArrayList;
import java.util.List;

import uk.ac.diamond.scisoft.rp.api.ScriptUtils;

/**
 * @author vgb98675
 * 
 */
public class AvizoSliceSnapshotTask extends Task {

	private final boolean useXvfb;

	private final String inputDir;
	private final String axis;
	private String zoomAmount;
	private final String startSlice;
	private final String endSlice;
	private final String numberOfSnaps;
	private final String outputLocation;
	private final String format;
	private final String resX;
	private final String resY;
	private final String projection;

	private final boolean useMaxSlice;

	public AvizoSliceSnapshotTask(boolean useXvfb, String inputDir,
			String axis, String zoomAmount, String startSlice, String endSlice,
			String numberOfSnaps, String outputLocation, String format,
			String resX, String resY, String projection) {
		this.useXvfb = useXvfb;
		this.useMaxSlice = false;
		this.inputDir = inputDir;
		this.axis = axis;
		this.zoomAmount = zoomAmount;
		this.startSlice = startSlice;
		this.endSlice = endSlice;
		this.numberOfSnaps = numberOfSnaps;
		this.outputLocation = outputLocation;
		this.format = format;
		this.resX = resX;
		this.resY = resY;
		this.projection = projection;
	}

	public AvizoSliceSnapshotTask(boolean useXvfb, String inputDir,
			String axis, String zoomAmount, String startSlice,
			String numberOfSnaps, String outputLocation, String format,
			String resX, String resY, String projection) {
		this.useXvfb = useXvfb;
		this.useMaxSlice = true;
		this.inputDir = inputDir;
		this.axis = axis;
		this.zoomAmount = zoomAmount;
		this.startSlice = startSlice;
		this.endSlice = null;
		this.numberOfSnaps = numberOfSnaps;
		this.outputLocation = outputLocation;
		this.format = format;
		this.resX = resX;
		this.resY = resY;
		this.projection = projection;
	}

	@Override
	public List<String> getParameterList() {
		List<String> paramList = new ArrayList<String>();

		
		String scriptDir;
		if (this.useXvfb) {
			scriptDir = ScriptUtils.getAbsoluteScriptPath()
					+ "taskScripts/AvizoSliceSnapshotGLX.sh";
		} else {
			scriptDir = ScriptUtils.getAbsoluteScriptPath()
					+ "taskScripts/AvizoSliceSnapshot.sh";
		}

		paramList.add(scriptDir);
	
		paramList.add(this.inputDir);
		paramList.add(this.axis);
		paramList.add(this.zoomAmount);
		paramList.add(this.startSlice);
		if (!this.useMaxSlice) {
			paramList.add(this.endSlice);
		}
		paramList.add(this.numberOfSnaps);
		paramList.add(this.outputLocation);
		paramList.add(this.format);
		paramList.add(this.resX);
		paramList.add(this.resY);
		paramList.add(this.projection);

		return paramList;
	}

}
