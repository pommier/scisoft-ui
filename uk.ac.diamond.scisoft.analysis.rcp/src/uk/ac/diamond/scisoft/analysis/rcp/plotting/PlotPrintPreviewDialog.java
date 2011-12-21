/*
 * Copyright 2012 Diamond Light Source Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.diamond.scisoft.analysis.rcp.plotting;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.utils.PlotExportUtil;
import uk.ac.diamond.scisoft.analysis.rcp.util.ResourceProperties;

import de.jreality.ui.viewerapp.AbstractViewerApp;

/**
 * Class based on the preview SWT dialog example found in "Professional Java Interfaces with SWT/JFace" Jackwind Li
 * Guojie John Wiley & Sons 2005
 */
public class PlotPrintPreviewDialog extends Dialog {
	private Shell shell;
	private Display display;
	private Canvas canvas;
	private Printer printer;
	private PrintMargin margin;
	private Combo comboScale;
	private Combo comboOrientation;
	private Combo comboResolution;
	private Combo comboPrinterList;

	protected String[] listPrintScaleText = { "10", "25", "33", "50", "66", "75", "100" };
	protected String printScaleText = ResourceProperties.getResourceString("PRINT_SCALE");
	protected String printButtonText = ResourceProperties.getResourceString("PRINT_BUTTON");
	protected String printToolTipText = ResourceProperties.getResourceString("PRINT_TOOLTIP");
	protected String printerSelectText = ResourceProperties.getResourceString("PRINTER_SELECT");
	protected String printPreviewText = ResourceProperties.getResourceString("PRINT_PREVIEW");
	protected String orientationText = ResourceProperties.getResourceString("ORIENTATION_TEXT");
	protected String resolutionText = ResourceProperties.getResourceString("RESOLUTION_TEXT");
	protected static String portraitText = ResourceProperties.getResourceString("PORTRAIT_ORIENTATION");
	protected static String landscapeText = ResourceProperties.getResourceString("LANDSCAPE_ORIENTATION");
	protected String defaultPrinterText = ResourceProperties.getResourceString("DEFAULT_PRINTER");
	private PrinterData currentPrinterData;
	private AbstractViewerApp viewerApp = null;
	private Plot1DGraphTable legendTable;
	private String orientation;
	private double scale;
	private int resolution;

	private Image image;
	private String fileName = "SDA plot";

	private static final Logger logger = LoggerFactory.getLogger(PlotPrintPreviewDialog.class);

	public PlotPrintPreviewDialog(AbstractViewerApp viewerApp, Display device, Plot1DGraphTable legendTable,
			PrinterData defaultPrinterData, String printOrientation, double printScale, int printResolution) {
		super(device.getActiveShell());
		this.display = device;
		this.viewerApp = viewerApp;
		this.legendTable = legendTable;
		this.currentPrinterData = defaultPrinterData;
		this.orientation = printOrientation;
		this.scale = printScale;
		this.resolution = printResolution;
		this.printer = new Printer(currentPrinterData);
		// We put the image creation into a thread and display a busy kind of indicator while the thread is
		// running
		System.out.println(currentPrinterData.toString());
		Runnable createImage = new CreateImage(viewerApp, device, legendTable, this.currentPrinterData, this.resolution);
		@SuppressWarnings("unused")
		Thread thread = new Thread(createImage);
		BusyIndicator.showWhile(this.display, createImage);

		this.image = CreateImage.getImage();
	}

	/**
	 * CreateImage Class used to create a Thread because of the amount of time it eventually takes to create an image
	 */
	public static class CreateImage implements Runnable {
		private AbstractViewerApp viewerApp;
		private Display device;
		private Plot1DGraphTable legendTable;
		private PrinterData printerData;
		private static Image image;
		private int resolution;

		public CreateImage(AbstractViewerApp viewerApp, Display device, Plot1DGraphTable legendTable,
				PrinterData printerData, int resolution) {
			this.viewerApp = viewerApp;
			this.device = device;
			this.legendTable = legendTable;
			this.printerData = printerData;
			this.resolution = resolution;
		}

		@Override
		public void run() {
			setImage(PlotExportUtil.createImage(this.viewerApp, this.device, this.legendTable, this.printerData,
					this.resolution));
		}

		public static Image getImage() {
			return image;
		}

		public void setImage(Image image) {
			CreateImage.image = image;
		}
	}

	public PrinterData open() {
		setPrinter(printer, scale);

		shell = new Shell(display.getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		shell.setText(printPreviewText);
		GridLayout previewGridLayout =new GridLayout(4, false);
		shell.setLayout(previewGridLayout);

		final Composite previewComposite = new Composite(shell, SWT.TOP);
		RowLayout previewLayout = new RowLayout();
		previewLayout.wrap=true;
		previewLayout.center=true;
		previewComposite.setLayout(previewLayout);


		final Button buttonSelectPrinter = new Button(previewComposite, SWT.PUSH);
		buttonSelectPrinter.setText(printerSelectText);
		buttonSelectPrinter.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				PrintDialog dialog = new PrintDialog(shell);
				// Prompts the printer dialog to let the user select a printer.
				currentPrinterData = dialog.open();
				// printData = currentPrinterData;
				if (currentPrinterData == null) // the user cancels the dialog
					return;
				// Loads the printer.
				// final Printer printer = new Printer(currentPrinterData);
				setPrinter(printer, Double.parseDouble(comboScale.getItem(comboScale.getSelectionIndex())));
				// print the plot
				print(printer, margin, orientation);
				shell.dispose();
			}
		});
		
		final Button buttonPrint = new Button(previewComposite, SWT.PUSH);
		buttonPrint.setText(printButtonText);
		buttonPrint.setToolTipText(printToolTipText);
		buttonPrint.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (printer == null)
					print();
				else {
					print(printer, margin, orientation);
				}
				shell.dispose();
			}
		});

		Composite printerNameComposite = new Composite(previewComposite,SWT.TOP);
		printerNameComposite.setLayout(new RowLayout());
		new Label(printerNameComposite, SWT.RIGHT).setText(defaultPrinterText + ":");
		String[] tmp = currentPrinterData.toString().split("name =");
		String currentPrinterName = "";
		if (tmp.length > 1)
			currentPrinterName = tmp[1].split("}")[0];
		new Text(printerNameComposite, SWT.RIGHT|SWT.READ_ONLY).setText(currentPrinterName);
		
		Composite scaleComposite = new Composite(previewComposite, SWT.BORDER);
		RowLayout scaleLayout=new RowLayout();
		scaleLayout.center=true;
		scaleComposite.setLayout(scaleLayout);
		new Label(scaleComposite, SWT.BOTTOM).setText(printScaleText + ":");
		comboScale = new Combo(scaleComposite, SWT.READ_ONLY);
		comboScale.add("0.5");
		comboScale.add("2.0");
		comboScale.add("3.0");
		comboScale.add("4.0");
		comboScale.add("5.0");
		comboScale.add("6.0");
		comboScale.add("7.0");
		for (int i = 0; i < comboScale.getItemCount(); i++) {
			if (scale == Double.parseDouble(comboScale.getItem(i))) {
				comboScale.select(i);
				break;
			}
		}
		comboScale.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				scale = Double.parseDouble(comboScale.getItem(comboScale.getSelectionIndex()));
				setPrinter(printer, scale);
			}
		});

		Composite resolutionComposite = new Composite(previewComposite, SWT.BORDER);
		RowLayout resolutionLayout = new RowLayout();
		resolutionLayout.center=true;
		resolutionComposite.setLayout(resolutionLayout);
		Label resolutionLabel = new Label(resolutionComposite, SWT.SINGLE);
		resolutionLabel.setText(resolutionText + ":");
		comboResolution = new Combo(resolutionComposite, SWT.READ_ONLY);
		comboResolution.add("1");
		comboResolution.add("2");
		comboResolution.add("3");
		comboResolution.add("4");
		for (int i = 0; i < comboResolution.getItemCount(); i++) {
			if (resolution == Integer.parseInt(comboResolution.getItem(i))) {
				comboResolution.select(i);
				break;
			}
		}
		comboResolution.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				resolution = Integer.parseInt(comboResolution.getItem(comboResolution.getSelectionIndex()));
				Runnable createImage = new CreateImage(viewerApp, display, legendTable, currentPrinterData, resolution);
				@SuppressWarnings("unused")
				Thread thread = new Thread(createImage);
				BusyIndicator.showWhile(display, createImage);
				image = CreateImage.getImage();
				setPrinter(printer, scale);
			}
		});

		// TODO orientation button disabled: works for preview not for data sent to printer
//		Composite orientationComposite = new Composite(previewComposite, SWT.BORDER);
//		RowLayout orientationLayout=new RowLayout();
//		orientationLayout.center=true;
//		orientationComposite.setLayout(orientationLayout);
//		new Label(orientationComposite, SWT.NULL).setText(orientationText + ":");
//		comboOrientation = new Combo(orientationComposite, SWT.READ_ONLY);
//		comboOrientation.add(portraitText);
//		comboOrientation.add(landscapeText);
//		for (int i = 0; i < comboOrientation.getItemCount(); i++) {
//			if (orientation.equals(comboOrientation.getItem(i))) {
//				comboOrientation.select(i);
//				break;
//			}
//		}
//		comboOrientation.addListener(SWT.Selection, new Listener() {
//			@Override
//			public void handleEvent(Event e) {
//				orientation = comboOrientation.getItem(comboOrientation.getSelectionIndex());
//				// setPrinter(printer, value);
//				canvas.redraw();
//			}
//		});

//		final Button buttonOldPrint = new Button(previewComposite, SWT.PUSH);
//		buttonOldPrint.setText("Old Print");
//		buttonOldPrint.setToolTipText(printToolTipText);
//		buttonOldPrint.addListener(SWT.Selection, new Listener() {
//			@Override
//			public void handleEvent(Event event) {
//				PrintDialog dialog = new PrintDialog(shell, SWT.NULL);
//				PrinterData printerData = dialog.open();
//				PlotExportUtil.printGraph(printerData, viewerApp, display, legendTable, 1);
//
//				shell.dispose();
//			}
//		});

		canvas = new Canvas(shell, SWT.BORDER);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 4;
		canvas.setLayoutData(gridData);
		// canvas.addPaintListener(new PaintListener() {
		// @Override
		// public void paintControl(PaintEvent e) {
		// paint(e,orientation);
		// }
		// });

		Listener listener = new Listener() {
			int zoomFactor = 1;

			@Override
			public void handleEvent(Event e) {
				Canvas canvas = null;
				switch (e.type) {
				case SWT.MouseWheel:
					canvas = (Canvas) e.widget;
					zoomFactor = (Math.max(0, zoomFactor + e.count) * 2);
					canvas.redraw();

					break;
				case SWT.Paint:
					canvas = (Canvas) e.widget;
					paint(e, orientation);
					break;
				}
			}
		};
		// canvas.addListener(SWT.MouseWheel, listener);
		// canvas.addListener(SWT.MouseUp, listener);
		// canvas.addListener(SWT.MouseDown, listener);
		canvas.addListener(SWT.Paint, listener);
		// comboOrientation.addListener(SWT.Selection, listener);
		// canvas.addListener(SWT.KeyDown, listener);

		shell.setSize(800, 650);
		shell.open();
		setPrinter(printer, scale);

		// Set up the event loop.
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) {
				// If no more entries in event queue
				shell.getDisplay().sleep();
			}
		}
		return currentPrinterData;
	}

	private void paint(Event e, String orientation) {
		if (orientation.equals(portraitText)) {

			int canvasBorder = 20;

			if (printer == null || printer.isDisposed())
				return;
			Rectangle rectangle = printer.getBounds();
			Point canvasSize = canvas.getSize();

			double viewScaleFactor = (canvasSize.x - canvasBorder * 2) * 1.0 / rectangle.width;
			viewScaleFactor = Math.min(viewScaleFactor, (canvasSize.y - canvasBorder * 2) * 1.0 / rectangle.height);

			int offsetX = (canvasSize.x - (int) (viewScaleFactor * rectangle.width)) / 2;
			int offsetY = (canvasSize.y - (int) (viewScaleFactor * rectangle.height)) / 2;

			e.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			// draws the page layout
			e.gc.fillRectangle(offsetX, offsetY, (int) (viewScaleFactor * rectangle.width),
					(int) (viewScaleFactor * rectangle.height));

			// draws the margin.
			e.gc.setLineStyle(SWT.LINE_DASH);
			e.gc.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));

			int marginOffsetX = offsetX + (int) (viewScaleFactor * margin.left);
			int marginOffsetY = offsetY + (int) (viewScaleFactor * margin.top);
			// e.gc.drawRectangle(marginOffsetX, marginOffsetY, (int) (viewScaleFactor * (margin.right - margin.left)),
			// (int) (viewScaleFactor * (margin.bottom - margin.top)));

			if (image != null) {
				int imageWidth = image.getBounds().width;
				int imageHeight = image.getBounds().height;

				double dpiScaleFactorX = printer.getDPI().x * 1.0 / shell.getDisplay().getDPI().x;
				double dpiScaleFactorY = printer.getDPI().y * 1.0 / shell.getDisplay().getDPI().y;

				double imageSizeFactor = Math.min(1, (margin.right - margin.left) * 1.0
						/ (dpiScaleFactorX * imageWidth));
				imageSizeFactor = Math.min(imageSizeFactor, (margin.bottom - margin.top) * 1.0
						/ (dpiScaleFactorY * imageHeight));

				e.gc.drawImage(image, 0, 0, imageWidth, imageHeight, marginOffsetX, marginOffsetY,
						(int) (dpiScaleFactorX * imageSizeFactor * imageWidth * viewScaleFactor),
						(int) (dpiScaleFactorY * imageSizeFactor * imageHeight * viewScaleFactor));

			}

		} else if (orientation.equals(landscapeText)) {
			int canvasBorder = 20;

			if (printer == null || printer.isDisposed())
				return;
			Rectangle rectangle = printer.getBounds();
			Point canvasSize = canvas.getSize();

			double viewScaleFactor = (canvasSize.x - canvasBorder * 2) * 1.0 / rectangle.width;
			viewScaleFactor = Math.min(viewScaleFactor, (canvasSize.y - canvasBorder * 2) * 1.0 / rectangle.height);

			int offsetY = (canvasSize.y - (int) (viewScaleFactor * rectangle.width)) / 2;
			int offsetX = (canvasSize.x - (int) (viewScaleFactor * rectangle.height)) / 2;

			e.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			// draws the page layout
			e.gc.fillRectangle(offsetX, offsetY, (int) (viewScaleFactor * rectangle.height),
					(int) (viewScaleFactor * rectangle.width));

			// draws the margin.
			e.gc.setLineStyle(SWT.LINE_DASH);
			e.gc.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));

			int marginOffsetX = offsetX + (int) (viewScaleFactor * margin.left);
			int marginOffsetY = offsetY + (int) (viewScaleFactor * margin.top);
			// e.gc.drawRectangle(marginOffsetX, marginOffsetY, (int) (viewScaleFactor * (margin.right - margin.left)),
			// (int) (viewScaleFactor * (margin.bottom - margin.top)));

			if (image != null) {
				int imageWidth = image.getBounds().width;
				int imageHeight = image.getBounds().height;

				double dpiScaleFactorX = printer.getDPI().x * 1.0 / shell.getDisplay().getDPI().x;
				double dpiScaleFactorY = printer.getDPI().y * 1.0 / shell.getDisplay().getDPI().y;

				double imageSizeFactor = Math.min(1, (margin.right - margin.left) * 1.0
						/ (dpiScaleFactorX * imageWidth));
				imageSizeFactor = Math.min(imageSizeFactor, (margin.bottom - margin.top) * 1.0
						/ (dpiScaleFactorY * imageHeight));

				float imageFactor = imageWidth / rectangle.width;
				e.gc.drawImage(image, 0, 0, imageWidth, imageHeight, marginOffsetX, marginOffsetY,
				 (int) (dpiScaleFactorX * imageSizeFactor * imageHeight * viewScaleFactor),
				 (int) (dpiScaleFactorY * imageSizeFactor * imageWidth * viewScaleFactor));
				//e.gc.drawImage(image, 0, 0, imageWidth, imageHeight, marginOffsetX, marginOffsetY,
				//		(int) (rectangle.height * imageFactor * dpiScaleFactorX * viewScaleFactor * imageSizeFactor),
				//		(int) (rectangle.width * imageFactor * dpiScaleFactorY * viewScaleFactor * imageSizeFactor));

			}
		}

	}

	/**
	 * Sets target printer.
	 * 
	 * @param printer
	 */
	void setPrinter(Printer printer, double marginSize) {
		if (printer == null) {
			printer = new Printer(Printer.getDefaultPrinterData());
		}
		this.printer = printer;
		margin = PrintMargin.getPrintMargin(printer, marginSize);
		if (canvas != null)
			canvas.redraw();
	}

	/**
	 * Lets the user to select a printer and prints the image on it.
	 */
	void print() {
		PrintDialog dialog = new PrintDialog(shell);
		// Prompts the printer dialog to let the user select a printer.
		PrinterData printerData = dialog.open();

		if (printerData == null) // the user cancels the dialog
			return;
		// Loads the printer.
		Printer printer = new Printer(printerData);
		print(printer, null, orientation);
	}

	/**
	 * Prints the image current displayed to the specified printer.
	 * 
	 * @param printer
	 */
	void print(final Printer printer, PrintMargin printMargin, final String orientation) {
		if (image == null) // If no image is loaded, do not print.
			return;

		final Point printerDPI = printer.getDPI();
		final Point displayDPI = display.getDPI();
		System.out.println(displayDPI + " " + printerDPI);

		final PrintMargin margin = (printMargin == null ? PrintMargin.getPrintMargin(printer, 1.0) : printMargin);

		Thread printThread = new Thread() {
			@Override
			public void run() {
				if (!printer.startJob(fileName)) {
					System.err.println("Failed to start print job!");
					printer.dispose();
					return;
				}

				GC gc = new GC(printer);

				if (!printer.startPage()) {
					System.err.println("Failed to start a new page!");
					gc.dispose();
					return;
				} else {
					Rectangle trim = printer.computeTrim(0, 0, 0, 0);

					if (orientation.equals(portraitText)) {
						int imageWidth = image.getBounds().width;
						int imageHeight = image.getBounds().height;

						// Handles DPI conversion.
						double dpiScaleFactorX = printerDPI.x * 1.0 / displayDPI.x;
						double dpiScaleFactorY = printerDPI.y * 1.0 / displayDPI.y;

						// If the image is too large to draw on a page, reduces its
						// width and height proportionally.
						double imageSizeFactor = Math.min(1, (margin.right - margin.left) * 1.0
								/ (dpiScaleFactorX * imageWidth));
						imageSizeFactor = Math.min(imageSizeFactor, (margin.bottom - margin.top) * 1.0
								/ (dpiScaleFactorY * imageHeight));

						// Draws the image to the printer.
						gc.drawImage(image, 0, 0, imageWidth, imageHeight, margin.left - 20, margin.top - 20,
								(int) (dpiScaleFactorX * imageSizeFactor * imageWidth), (int) (dpiScaleFactorY
										* imageSizeFactor * imageHeight));
						gc.dispose();

					}
					if (orientation.equals(landscapeText)) {
						// TODO orientation: need to have the image rotating to work...
						int imageWidth = image.getBounds().width;
						int imageHeight = image.getBounds().height;

						// Handles DPI conversion.
						double dpiScaleFactorX = printerDPI.x * 1.0 / displayDPI.x;
						double dpiScaleFactorY = printerDPI.y * 1.0 / displayDPI.y;

						// If the image is too large to draw on a page, reduces its
						// width and height proportionally.
						double imageSizeFactor = Math.min(1, (margin.right - margin.left) * 1.0
								/ (dpiScaleFactorX * imageWidth));
						imageSizeFactor = Math.min(imageSizeFactor, (margin.bottom - margin.top) * 1.0
								/ (dpiScaleFactorY * imageHeight));

						// Draws the image to the printer.
						gc.setAdvanced(true);
						Transform t = new Transform(printer);
						t.rotate(90);
						t.translate(0, (float) -(dpiScaleFactorY * imageSizeFactor * imageWidth * 1.1));
						gc.setTransform(t);
						gc.drawImage(image, 0, 0, imageWidth, imageHeight, margin.left, margin.top,
								(int) (dpiScaleFactorX * imageSizeFactor * imageWidth * 1.35), (int) (dpiScaleFactorY
										* imageSizeFactor * imageHeight * 1.35));
						gc.dispose();
						t.dispose();
					}
				}

				image.dispose();
				printer.endPage();
				printer.endJob();

				printer.dispose();
				System.out.println("Printing job done!");
			}
		};
		printThread.start();
	}

	public String getOrientation() {
		return orientation;
	}

	public double getScale() {
		return scale;
	}

	public int getResolution() {
		return resolution;
	}
}

class PrintMargin {
	// Margin to the left side, in pixels
	public int left;
	// Margins to the right side, in pixels
	public int right;
	// Margins to the top side, in pixels
	public int top;
	// Margins to the bottom side, in pixels
	public int bottom;

	private PrintMargin(int left, int right, int top, int bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	/**
	 * Returns a PrintMargin object containing the true border margins for the specified printer with the given margin
	 * in inches. Note: all four sides share the same margin width.
	 * 
	 * @param printer
	 * @param margin
	 * @return PrintMargin
	 */
	static PrintMargin getPrintMargin(Printer printer, double margin) {
		return getPrintMargin(printer, 0, margin, 0, margin);
	}

	/**
	 * Returns a PrintMargin object containing the true border margins for the specified printer with the given margin
	 * width (in inches) for each side.
	 */
	static PrintMargin getPrintMargin(Printer printer, double marginLeft, double marginRight, double marginTop,
			double marginBottom) {
		Rectangle clientArea = printer.getClientArea();
		Rectangle trim = printer.computeTrim(0, 0, 0, 0);

		Point dpi = printer.getDPI();

		int leftMargin = (int) (marginLeft * dpi.x) - trim.x;
		int rightMargin = clientArea.width + trim.width - (int) (marginRight * dpi.x) - trim.x;
		int topMargin = (int) (marginTop * dpi.y) - trim.y;
		int bottomMargin = clientArea.height + trim.height - (int) (marginBottom * dpi.y) - trim.y;

		return new PrintMargin(leftMargin, rightMargin, topMargin, bottomMargin);
	}

	public String toString() {
		return "Margin { " + left + ", " + right + "; " + top + ", " + bottom + " }";
	}
}
