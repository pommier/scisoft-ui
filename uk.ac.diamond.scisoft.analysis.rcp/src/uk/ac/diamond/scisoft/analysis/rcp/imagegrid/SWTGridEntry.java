/*-
 * Copyright © 2009 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rcp.imagegrid;

import java.awt.Dimension;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.RGBDataset;
import uk.ac.diamond.scisoft.analysis.dataset.Stats;
import uk.ac.diamond.scisoft.analysis.rcp.histogram.mapfunctions.AbstractMapFunction;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.utils.GlobalColourMaps;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.utils.SWTImageUtils;


/**
 * SWT Image implementation of a ImageGridEntry
 */
public class SWTGridEntry extends AbstractGridEntry {

	// Adding in some logging to help with getting this running
	private static final Logger logger = LoggerFactory.getLogger(SWTGridEntry.class);
	private float minimumThreshold = 0.98f;
	private static Color green = null;
	private static Color red = null;
	private static Color blue = null;
	private static long lastREDRAWinMillis = 0; 
	
	private Image gridImage;
	private Dimension imageDim;
	private Canvas canvas;
	private String toolTipText = null;
	private int colourMapChoice = 0;
	
	public SWTGridEntry(String filename) {
		super(filename);
		gridImage = null;

	}

	public SWTGridEntry(String filename, Object additional) {
		super(filename,additional);
		gridImage = null;
	}

	public SWTGridEntry(String filename, Object additional, Canvas canvas,
			            int colourMapChoice, float threshold) {
		this(filename,additional);
		this.canvas = canvas;
		this.colourMapChoice = colourMapChoice;
		this.minimumThreshold = threshold;
	}
	
	@Override
	public void setNewfilename(String newFilename) {
		this.filename = newFilename;
		this.additionalInfo = null;
		if (gridImage != null)
			gridImage.dispose();
		if (thumbnailFilename != null)
		{
        	java.io.File imageFile = new java.io.File(thumbnailFilename);
        	imageFile.delete();
			thumbnailFilename = null;
		}
	}

	@Override
	public void setStatus(int newStatus) {
		status = newStatus;

	}

	@Override
	public void deActivate() {
		if (gridImage != null) {
			try {
				if (thumbnailFilename == null) {
					java.io.File file = java.io.File.createTempFile("tmp_thumb", ".png");
					String workspaceDir = org.eclipse.core.runtime.Platform.getInstanceLocation().getURL().getPath();
					if (SWT.getPlatform().equals("win32") ||
						SWT.getPlatform().equals("wpf"))
						workspaceDir = workspaceDir.substring(1);
					thumbnailFilename = workspaceDir +file.getName();
					file.delete();
					ImageLoader loader = new ImageLoader();
					loader.data = new ImageData[]{gridImage.getImageData()};
					loader.save(thumbnailFilename, SWT.IMAGE_PNG);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			gridImage.dispose();
			gridImage = null;
		}
	}

	public void loadThumbImage() {
		if (gridImage == null) {
			canvas.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					gridImage = new Image(canvas.getDisplay(),thumbnailFilename);
					// make sure system doesn't get flooded with redraw requests
					if (System.currentTimeMillis()-lastREDRAWinMillis > 20) {
						canvas.redraw();
						lastREDRAWinMillis = System.currentTimeMillis();
					}
				}				
			});
		} else {
			logger.warn("Something is wrong");
		}
	}
	
	@Override
	public boolean isDeactivated() {
		return gridImage == null;
	}

	@Override
	public void createImage(final AbstractDataset ds) {
		canvas.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				final int[] shape = ds.getShape();
				try {
					if (shape.length == 2) {
						double max;
						if (ds instanceof RGBDataset) {
							double temp;
							max = Stats.quantile(((RGBDataset) ds).createRedDataset(AbstractDataset.INT16),
									minimumThreshold);
							temp = Stats.quantile(((RGBDataset) ds).createGreenDataset(AbstractDataset.INT16),
									minimumThreshold);
							if (max < temp)
								max = temp;
							temp = Stats.quantile(((RGBDataset) ds).createBlueDataset(AbstractDataset.INT16),
									minimumThreshold);
							if (max < temp)
								max = temp;
						} else {
							max = Stats.quantile(ds, minimumThreshold);
						}
						int redSelect = GlobalColourMaps.colourSelectList.get(colourMapChoice * 4);
						int greenSelect = GlobalColourMaps.colourSelectList.get(colourMapChoice * 4 + 1);
						int blueSelect = GlobalColourMaps.colourSelectList.get(colourMapChoice * 4 + 2);
						AbstractMapFunction redFunc = GlobalColourMaps.mappingFunctions.get(Math.abs(redSelect));
						AbstractMapFunction greenFunc = GlobalColourMaps.mappingFunctions.get(Math.abs(greenSelect));
						AbstractMapFunction blueFunc = GlobalColourMaps.mappingFunctions.get(Math.abs(blueSelect));
						ImageData imgD = SWTImageUtils.createImageData(ds, max, redFunc, greenFunc, blueFunc,
								(redSelect < 0), (greenSelect < 0), (blueSelect < 0));
						gridImage = new Image(canvas.getDisplay(), imgD);
						imageDim = new Dimension(shape[1], shape[0]);
						canvas.redraw();
					} else {
						setStatus(INVALIDSTATUS);						
					}
				} catch (Exception e) {
					setStatus(INVALIDSTATUS);
				}
			}
			
		});
	}

	public void paint(GC gc, int posX, int posY, int xSize, int ySize)
	{
		if (gridImage != null &&
			!gridImage.isDisposed()) {
			int w, h, x, y;
			if (imageDim.width > imageDim.height) {
				w = xSize;
				x = 0;
				h = ySize * imageDim.height / imageDim.width;
				y = (ySize - h)/2;
			} else {
				h = ySize;
				y = 0;
				w = xSize * imageDim.width / imageDim.height;
				x = (xSize - w)/2;
			}
			gc.drawImage(gridImage, 0, 0, imageDim.width, imageDim.height, posX+x, posY+y, w, h);
			switch(status) {
				case 1:
				{
					if (green == null) {
						green = new Color(canvas.getDisplay(),new RGB(0,255,0));						
					}
					gc.setForeground(green);
					gc.drawRectangle(posX, posY, xSize, ySize);
				}
				break;
				case 2:
				{
					if (red == null) {
						red = new Color(canvas.getDisplay(),new RGB(255,0,0));						
					}
					gc.setForeground(red);
					gc.drawRectangle(posX, posY, xSize, ySize);
				}
				break;
				case SELECTEDSTATUS:
				{
					if (blue == null) {
						blue = new Color(canvas.getDisplay(),new RGB(64,64,255));						
					}
					gc.setAlpha(128);
					gc.setBackground(blue);
					gc.fillRectangle(posX, posY, xSize, ySize);
					gc.setAlpha(255);
				}
				break;
			}
		} else {
			if (red == null) {
				red = new Color(canvas.getDisplay(),new RGB(255,0,0));						
			}			
			gc.setForeground(red);
			gc.drawLine(posX+8,posY+8, posX+xSize-8, posY+ySize-8);
			gc.drawLine(posX+xSize-8, posY+8, posX+8, posY+ySize-8);
		}
	}
	
	public Dimension getImageDimension() {
		return imageDim;
	}

	public boolean hasThumbnailImage() {
		return thumbnailFilename != null;
	}
	
	public boolean hasImage() {
		return (gridImage != null && !gridImage.isDisposed());
	}

	@Override
	public void dispose() {
		if (gridImage != null &&
			!gridImage.isDisposed())
			gridImage.dispose();
		
        if (thumbnailFilename != null)	
        {
        	java.io.File imageFile = new java.io.File(thumbnailFilename);
        	imageFile.delete();
        }
	}

	@Override
	public String getToolTipText() {
		if (additionalInfo == null) {
			if (toolTipText == null) {
				toolTipText = (new java.io.File(filename)).getName();
			}
		} else {
			// TODO
		}
		return toolTipText;
	}
}
