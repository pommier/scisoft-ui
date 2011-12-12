package uk.ac.diamond.sda.navigator.views;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.gda.util.io.FileUtils;

public class FileLabelProvider extends ColumnLabelProvider {

	private static final Logger logger = LoggerFactory.getLogger(FileLabelProvider.class);
	private int columnIndex;
	private SimpleDateFormat dateFormat;

	public FileLabelProvider(final int column) {
		this.columnIndex = column;
		this.dateFormat  = new SimpleDateFormat("dd/MM/yyyy hh:mm");
	}
	
	@Override
	public Color getForeground(Object element) {
		if (columnIndex==0) return null;
		return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
	}
	
	@Override
	public Image getImage(Object element) {
		final File node   = (File)element;
	
		switch(columnIndex) {
		case 0:
			return getImage(node);
        default:
        	return null;
		}
	}

	/**
	 * { "Name", "Class", "Dims", "Type", "Size" };
	 */
	@Override
	public String getText(Object element) {
		
		final File node   = (File)element;
	
		switch(columnIndex) {
		case 0:
			return node.getName();
		case 1:
			return dateFormat.format(new Date(node.lastModified()));
		case 2:
			return node.isDirectory() ? "Directory" : FileUtils.getFileExtension(node);
		case 3:
			return formatSize(node.length());
		default:
			return null;
		}
	}

    private static final double BASE = 1024, KB = BASE, MB = KB*BASE, GB = MB*BASE;
    private static final DecimalFormat df = new DecimalFormat("#.##");

    public static String formatSize(double size) {
        if(size >= GB) {
            return df.format(size/GB) + " GB";
        }
        if(size >= MB) {
            return df.format(size/MB) + " MB";
        }
        if(size >= KB) {
            return df.format(size/KB) + " KB";
        }
        return "" + (int)size + " bytes";
    }

    private Image folderImage;
    
    /**
     * Caching seems to be needed for icons on large dirs.
     * NOTE Caching usually a bad idea check if can be done another way.
     */
    private Map<String,Image> extensionCache;
    
    private Image getImage(File file) {
    	
    	if (file.isDirectory()) {
    		if (folderImage==null) {
     			folderImage = getImageSWT(file);
    		}
    		return folderImage;
    	}
    	if (extensionCache==null) extensionCache = new HashMap<String,Image>(31);
   	
    	final String ext = FileUtils.getFileExtension(file);
    	if (extensionCache.containsKey(ext)) return extensionCache.get(ext);
    	
    	Image returnImage = null;
    	
    	// Not sure about the order of this. If always use the eclipse icon,
    	// eclipse too often provides default icons.
    	
    	// Program icon from system
    	if (returnImage==null) {
	    	final Program program = Program.findProgram(ext);
	    	
	    	if (program!=null) {
		    	ImageData iconData=Program.findProgram(ext).getImageData();
		    	returnImage = new Image(Display.getCurrent(), iconData);
	    	}
    	}
    	
    	// Eclipse icon
    	if (returnImage==null) {
		    ImageDescriptor imageDescriptor = PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(file.getName());
	    	if (imageDescriptor!=null) {
	    		returnImage = imageDescriptor.createImage();
	    	}
    	}
    	
    	if (returnImage==null)	returnImage = getImageSWT(file);
    	
    	extensionCache.put(ext, returnImage);
    	
    	return returnImage;
    }
    
    @Override
	public void dispose() {
    	super.dispose();
    	if (folderImage!=null) folderImage.dispose();
    	
    	for (String ext : extensionCache.keySet()) {
    		extensionCache.get(ext).dispose();
		}
    }
    
    static ImageData convertToSWT(BufferedImage bufferedImage) {
        if (bufferedImage.getColorModel() instanceof DirectColorModel) {
            DirectColorModel colorModel = (DirectColorModel)bufferedImage.getColorModel();
            PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
            ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
            for (int y = 0; y < data.height; y++) {
                    for (int x = 0; x < data.width; x++) {
                            int rgb = bufferedImage.getRGB(x, y);
                            int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF)); 
                            data.setPixel(x, y, pixel);
                            if (colorModel.hasAlpha()) {
                                    data.setAlpha(x, y, (rgb >> 24) & 0xFF);
                            }
                    }
            }
            return data;            
        } else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
            IndexColorModel colorModel = (IndexColorModel)bufferedImage.getColorModel();
            int size = colorModel.getMapSize();
            byte[] reds = new byte[size];
            byte[] greens = new byte[size];
            byte[] blues = new byte[size];
            colorModel.getReds(reds);
            colorModel.getGreens(greens);
            colorModel.getBlues(blues);
            RGB[] rgbs = new RGB[size];
            for (int i = 0; i < rgbs.length; i++) {
                    rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
            }
            PaletteData palette = new PaletteData(rgbs);
            ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
            data.transparentPixel = colorModel.getTransparentPixel();
            WritableRaster raster = bufferedImage.getRaster();
            int[] pixelArray = new int[1];
            for (int y = 0; y < data.height; y++) {
                    for (int x = 0; x < data.width; x++) {
                            raster.getPixel(x, y, pixelArray);
                            data.setPixel(x, y, pixelArray[0]);
                    }
            }
            return data;
        }
        return null;
    }
    
    static Image getImageSWT(File file) {
        ImageIcon systemIcon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file);
        java.awt.Image image = systemIcon.getImage();
        if (image instanceof BufferedImage) {
            return new Image(Display.getDefault(), convertToSWT((BufferedImage)image));
        }
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return new Image(Display.getDefault(), convertToSWT(bufferedImage));
    }
}
