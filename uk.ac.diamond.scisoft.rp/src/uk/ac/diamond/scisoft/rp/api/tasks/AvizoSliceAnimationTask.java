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
public class AvizoSliceAnimationTask extends Task {

	private final String inputDir;
	private final String axis;
	private final String zoomAmount;
	private final String projection;
	private final String numberOfFrames;
	private final String format;
	private final String videoType;
	private final String quality;	
	private final String resX;
	private final String resY;
	private final String outputLocation;	
	
	

	public AvizoSliceAnimationTask(String inputDir, String axis,
			String zoomAmount, String projection, String numberOfFrames,
			String format, String videoType, String quality, String resX,
			String resY, String outputLocation) {		
		this.inputDir = inputDir;
		this.axis = axis;
		this.zoomAmount = zoomAmount;
		this.projection = projection;
		this.numberOfFrames = numberOfFrames;
		this.format = format;
		this.videoType = videoType;
		this.quality = quality;
		this.resX = resX;
		this.resY = resY;
		this.outputLocation = outputLocation;
	}



	@Override
	public List<String> getParameterList() {
		List<String> paramList = new ArrayList<String>();

		String scriptDir = ScriptUtils.getAbsoluteScriptPath()
				+ "taskScripts/AvizoSliceAnimGLX.sh";
				
		paramList.add(scriptDir);

		paramList.add(this.inputDir);
		paramList.add(this.axis);
		paramList.add(this.zoomAmount);
		paramList.add(this.projection);
		paramList.add(this.numberOfFrames);	
		paramList.add(this.format);
		paramList.add(this.videoType);
		paramList.add(this.quality);	
		paramList.add(this.resX);
		paramList.add(this.resY);
		paramList.add(this.outputLocation);
		
		return paramList;
	}

}
