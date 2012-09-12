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
public class AvizoRotationSnapshotTask extends Task {

	private final boolean useCenter;
	private final boolean rotateCamera;
	private final String const1;
	private final String const2;

	private final String dataFileDir;
	private final String zoomAmount;
	private final String cameraX;
	private final String cameraY;
	private final String cameraZ;
	private final String startAngle;
	private final String endAngle;
	private final String axis;
	private final String centerX;
	private final String centerY;
	private final String centerZ;
	private final String renderDetail;
	private final String lighting;
	private final String edgeAdhance;
	private final String alphaScale;
	private final String numberOfSnapshots;
	private final String outputFile;
	private final String format;
	private final String resX;
	private final String resY;
	private final String projection;

	private final boolean useXvfb;

	public AvizoRotationSnapshotTask(boolean useXvfb, String dataFileDir,
			String zoomAmount, String cameraX, String cameraY, String cameraZ,
			String startAngle, String endAngle, String axis, String centerX,
			String centerY, String centerZ, String renderDetail,
			String lighting, String edgeAdhance, String alphaScale,
			String numberOfSnapshots, String outputFile, String format,
			String resX, String resY, String projection) {

		this.rotateCamera = false;
		this.useCenter = false;
		this.useXvfb = useXvfb;

		this.const1 = null;
		this.const2 = null;

		this.dataFileDir = dataFileDir;
		this.zoomAmount = zoomAmount;
		this.cameraX = cameraX;
		this.cameraY = cameraY;
		this.cameraZ = cameraZ;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.axis = axis;
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;
		this.renderDetail = renderDetail;
		this.lighting = lighting;
		this.edgeAdhance = edgeAdhance;
		this.alphaScale = alphaScale;
		this.numberOfSnapshots = numberOfSnapshots;
		this.outputFile = outputFile;
		this.format = format;
		this.resX = resX;
		this.resY = resY;
		this.projection = projection;
	}

	public AvizoRotationSnapshotTask(boolean useXvfb, String dataFileDir,
			String zoomAmount, String cameraX, String cameraY, String cameraZ,
			String startAngle, String endAngle, String axis,
			String renderDetail, String lighting, String edgeAdhance,
			String alphaScale, String numberOfSnapshots, String outputFile,
			String format, String resX, String resY, String projection) {

		this.rotateCamera = false;

		this.const1 = null;
		this.const2 = null;

		this.useCenter = true;
		this.useXvfb = useXvfb;

		this.dataFileDir = dataFileDir;
		this.zoomAmount = zoomAmount;
		this.cameraX = cameraX;
		this.cameraY = cameraY;
		this.cameraZ = cameraZ;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.axis = axis;
		this.centerX = null;
		this.centerY = null;
		this.centerZ = null;
		this.renderDetail = renderDetail;
		this.lighting = lighting;
		this.edgeAdhance = edgeAdhance;
		this.alphaScale = alphaScale;
		this.numberOfSnapshots = numberOfSnapshots;
		this.outputFile = outputFile;
		this.format = format;
		this.resX = resX;
		this.resY = resY;
		this.projection = projection;
	}

	public AvizoRotationSnapshotTask(boolean useXvfb, String dataFileDir,
			String zoomAmount, String const1, String const2, String startAngle,
			String endAngle, String axis, String renderDetail, String lighting,
			String edgeAdhance, String alphaScale, String numberOfSnapshots,
			String outputFile, String format, String resX, String resY,
			String projection) {

		this.rotateCamera = true;

		this.const1 = const1;
		this.const2 = const2;

		this.useCenter = true;
		this.useXvfb = useXvfb;

		this.dataFileDir = dataFileDir;
		this.zoomAmount = zoomAmount;
		this.cameraX = null;
		this.cameraY = null;
		this.cameraZ = null;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.axis = axis;
		this.centerX = null;
		this.centerY = null;
		this.centerZ = null;
		this.renderDetail = renderDetail;
		this.lighting = lighting;
		this.edgeAdhance = edgeAdhance;
		this.alphaScale = alphaScale;
		this.numberOfSnapshots = numberOfSnapshots;
		this.outputFile = outputFile;
		this.format = format;
		this.resX = resX;
		this.resY = resY;
		this.projection = projection;
	}

	@Override
	public List<String> getParameterList() {
		List<String> paramList = new ArrayList<String>();
		
		String scriptDir;

		if (this.rotateCamera) {						
			if (this.useXvfb) {
				scriptDir = ScriptUtils.getAbsoluteScriptPath()
						+ "taskScripts/AvizoRotViewSnapshot.sh";
			} else {
				scriptDir = ScriptUtils.getAbsoluteScriptPath()
						+ "taskScripts/AvizoRotViewSnapshot.sh";
			}
		} else {		
			if (this.useXvfb) {
				scriptDir = ScriptUtils.getAbsoluteScriptPath()
						+ "taskScripts/AvizoRotSnapshotGLX.sh";
			} else {
				scriptDir = ScriptUtils.getAbsoluteScriptPath()
						+ "taskScripts/AvizoRotSnapshot.sh";
			}
		}

		paramList.add(scriptDir);
			
		paramList.add(this.dataFileDir);
		paramList.add(this.zoomAmount);
		if (this.rotateCamera) {
			paramList.add(this.const1);
			paramList.add(this.const2);
		} else {
			paramList.add(this.cameraX);
			paramList.add(this.cameraY);
			paramList.add(this.cameraZ);
		}
		paramList.add(this.startAngle);
		paramList.add(this.endAngle);
		paramList.add(this.axis);
		if (!this.rotateCamera && !this.useCenter) {
			paramList.add(this.centerX);
			paramList.add(this.centerY);
			paramList.add(this.centerZ);
		}
		paramList.add(this.renderDetail);
		paramList.add(this.lighting);
		paramList.add(this.edgeAdhance);
		paramList.add(this.alphaScale);
		paramList.add(this.numberOfSnapshots);
		paramList.add(this.outputFile);
		paramList.add(this.format);
		paramList.add(this.resX);
		paramList.add(this.resY);
		paramList.add(this.projection);

		return paramList;
	}
}
