package uk.ac.diamond.sda.navigator.views;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jface.resource.ImageRegistry;

import com.sun.corba.se.spi.activation.Activator;

import uk.ac.diamond.sda.intro.navigator.NavigatorRCPActivator;
import uk.ac.gda.util.OSUtils;
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

    private static Image folderImage;
    
    private ImageRegistry imageRegistry;
        
    private Image getImage(File file) {
    	
    	if (file.isDirectory()) {
    		return getFolderImage(file);
    	}

    	final String ext = FileUtils.getFileExtension(file);
    	if (imageRegistry == null) imageRegistry = new ImageRegistry();
    	
    	Image returnImage = imageRegistry.get(ext);
    	if (returnImage != null) return returnImage;   	
    	
    	
    	// Not sure about the order of this. If always use the eclipse icon,
    	// eclipse too often provides default icons.
    	
    	// Eclipse icon
    	ECLISPE_BLOCK: if (returnImage==null) {
    		final IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getAbsolutePath());
    		if (desc==null) break ECLISPE_BLOCK;
    		final ImageDescriptor imageDescriptor = desc.getImageDescriptor();
	    	if (imageDescriptor!=null) {
	    		returnImage = imageDescriptor.createImage();
	    	}
    	}

    	
    	// Program icon from system
    	if (returnImage==null) {
	    	final Program program = Program.findProgram(ext);
	    	
	    	if (program!=null) {
		    	ImageData iconData=Program.findProgram(ext).getImageData();
		    	returnImage = new Image(Display.getCurrent(), iconData);
	    	}
    	}
    	    	
    	if (returnImage==null)	returnImage = getImageSWT(file);
    	
    	imageRegistry.put(ext, returnImage);
    	
    	return returnImage;
    }
    
    @Override
	public void dispose() {
    	super.dispose(); 	
    	if (imageRegistry!=null) imageRegistry.dispose();
    	imageRegistry = null;
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

	public static Image getFolderImage(File file) {
		
		if (folderImage==null) {
			
			if (file==null) file = OSUtils.isWindowsOS() ? new File("C:/Windows/") : new File("/");
			/**
			 * On windows, use windows icon for folder,
			 * on unix folder icon can be not very nice looking, use folder.png
			 */
	        if (OSUtils.isWindowsOS()) {
	        	folderImage = getImageSWT(file);
	        } else {
	        	folderImage = NavigatorRCPActivator.getImageDescriptor("icons/folder.png").createImage();
	        }
 			
		}
		return folderImage;
	}
}
