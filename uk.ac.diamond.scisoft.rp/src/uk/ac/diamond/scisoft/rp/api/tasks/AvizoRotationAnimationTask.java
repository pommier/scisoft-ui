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
public class AvizoRotationAnimationTask extends Task {

	private final boolean useXvfb;

	private final boolean useCenter;

	private final String dataFileDir;
	private final String axis;
	private final String cameraX;
	private final String cameraY;
	private final String cameraZ;
	private final String centerX;
	private final String centerY;
	private final String centerZ;
	private final String zoomAmount;
	private final String degreesToRotate;
	private final String renderDetail;
	private final String lighting;
	private final String edgeAdhance;
	private final String alphaScale;
	private final String numberOfFrames;
	private final String format;
	private final String videoType;
	private final String quality;
	private final String resX;
	private final String resY;
	private final String outputFile;
	private final String projection;

	public AvizoRotationAnimationTask(boolean useXvfb, String dataFileDir,
			String axis, String cameraX, String cameraY, String cameraZ,
			String centerX, String centerY, String centerZ, String zoomAmount,
			String degreesToRotate, String renderDetail, String lighting,
			String edgeAdhance, String alphaScale, String numberOfFrames,
			String format, String videoType, String quality, String resX,
			String resY, String outputFile, String projection) {

		this.useCenter = false;
		this.useXvfb = useXvfb;

		this.dataFileDir = dataFileDir;
		this.axis = axis;
		this.cameraX = cameraX;
		this.cameraY = cameraY;
		this.cameraZ = cameraZ;
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;
		this.zoomAmount = zoomAmount;
		this.degreesToRotate = degreesToRotate;
		this.renderDetail = renderDetail;
		this.lighting = lighting;
		this.edgeAdhance = edgeAdhance;
		this.alphaScale = alphaScale;
		this.numberOfFrames = numberOfFrames;
		this.format = format;
		this.videoType = videoType;
		this.quality = quality;
		this.resX = resX;
		this.resY = resY;
		this.outputFile = outputFile;
		this.projection = projection;
	}

	public AvizoRotationAnimationTask(boolean useXvfb, String dataFileDir,
			String axis, String cameraX, String cameraY, String cameraZ,
			String zoomAmount, String degreesToRotate, String renderDetail,
			String lighting, String edgeAdhance, String alphaScale,
			String numberOfFrames, String format, String videoType,
			String quality, String resX, String resY, String outputFile,
			String projection) {

		this.useCenter = true;
		this.useXvfb = useXvfb;

		this.dataFileDir = dataFileDir;
		this.axis = axis;
		this.cameraX = cameraX;
		this.cameraY = cameraY;
		this.cameraZ = cameraZ;
		this.centerX = null;
		this.centerY = null;
		this.centerZ = null;
		this.zoomAmount = zoomAmount;
		this.degreesToRotate = degreesToRotate;
		this.renderDetail = renderDetail;
		this.lighting = lighting;
		this.edgeAdhance = edgeAdhance;
		this.alphaScale = alphaScale;
		this.numberOfFrames = numberOfFrames;
		this.format = format;
		this.videoType = videoType;
		this.quality = quality;
		this.resX = resX;
		this.resY = resY;
		this.outputFile = outputFile;
		this.projection = projection;
	}

	@Override
	public List<String> getParameterList() {
		List<String> paramList = new ArrayList<String>();

		String scriptDir;
		if (this.useXvfb) {
			scriptDir = ScriptUtils.getAbsoluteScriptPath()
					+ "taskScripts/AvizoRotAnimGLX.sh";
		} else {
			scriptDir = ScriptUtils.getAbsoluteScriptPath()
					+ "taskScripts/AvizoRotAnim.sh";
		}

		paramList.add(scriptDir);		
		paramList.add(this.dataFileDir);
		paramList.add(this.axis);
		paramList.add(this.cameraX);
		paramList.add(this.cameraY);
		paramList.add(this.cameraZ);
		if (!this.useCenter) {
			paramList.add(this.centerX);
			paramList.add(this.centerY);
			paramList.add(this.centerZ);
		}
		paramList.add(this.zoomAmount);
		paramList.add(this.degreesToRotate);
		paramList.add(this.renderDetail);
		paramList.add(this.lighting);
		paramList.add(this.edgeAdhance);
		paramList.add(this.alphaScale);
		paramList.add(this.numberOfFrames);
		paramList.add(this.format);
		paramList.add(this.videoType);
		paramList.add(this.quality);
		paramList.add(this.resX);
		paramList.add(this.resY);
		paramList.add(this.outputFile);
		paramList.add(this.projection);

		return paramList;
	}

}
