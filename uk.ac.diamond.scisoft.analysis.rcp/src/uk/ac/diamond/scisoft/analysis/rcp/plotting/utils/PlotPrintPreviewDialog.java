/*-
 * Copyright © 2011 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.utils;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.Plot1DGraphTable;
import uk.ac.diamond.scisoft.analysis.rcp.util.ResourceProperties;

import de.jreality.ui.viewerapp.AbstractViewerApp;

public class PlotPrintPreviewDialog extends Dialog {
	private Shell shell;
	private Display display;
	private Canvas canvas;
	private Printer printer;
	private PrintMargin margin;
	private Combo combo;
	private Combo comboOrientation;

	protected String[] listPrintScaleText = { "10", "25", "33", "50", "66", "75", "100" };
	protected String printScaleText = ResourceProperties.getResourceString("PRINT_SCALE");
	protected String printButtonText = ResourceProperties.getResourceString("PRINT_BUTTON");
	protected String printToolTipText = ResourceProperties.getResourceString("PRINT_TOOLTIP");
	protected String printerSelectText = ResourceProperties.getResourceString("PRINTER_SELECT");
	protected String printPreviewText = ResourceProperties.getResourceString("PRINT_PREVIEW");
	protected String orientationText = ResourceProperties.getResourceString("ORIENTATION_TEXT");
	protected static String portraitText = ResourceProperties.getResourceString("PORTRAIT_ORIENTATION");
	protected static String landscapeText = ResourceProperties.getResourceString("LANDSCAPE_ORIENTATION");
	private static String orientation = portraitText;

	private Image image;
	private String fileName = "SDA plot";

	private static final Logger logger = LoggerFactory.getLogger(PlotPrintPreviewDialog.class);

	public PlotPrintPreviewDialog(AbstractViewerApp viewerApp, Display device, Plot1DGraphTable legendTable) {
		super(device.getActiveShell());
		this.display = device;
		// this.image = PlotExportUtil.createImage(viewerApp, device, legendTable, getPrinterData());

		// We put the image creation into a thread and display a busy kind of indicator while the thread is
		// running
		Runnable createImage = new CreateImage(viewerApp, device, legendTable, getPrinterData());
		@SuppressWarnings("unused")
		Thread thread = new Thread(createImage);
		BusyIndicator.showWhile(display, createImage);

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

		public CreateImage(AbstractViewerApp viewerApp, Display device, Plot1DGraphTable legendTable,
				PrinterData printerData) {
			this.viewerApp = viewerApp;
			this.device = device;
			this.legendTable = legendTable;
			this.printerData = printerData;
		}

		@Override
		public void run() {
			setImage(PlotExportUtil.createImage(viewerApp, device, legendTable, printerData));
		}

		public static Image getImage() {
			return image;
		}

		public void setImage(Image image) {
			CreateImage.image = image;
		}
	}

	private PrinterData printData;

	public PrinterData getPrinterData() {
		setPrinter(printer, 0.5);
		return printer.getPrinterData();
	}

	public PrinterData open() {
		shell = new Shell(display.getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		shell.setText(printPreviewText);
		shell.setLayout(new GridLayout(4, false));

		final Composite previewComposite = new Composite(shell, SWT.TOP);
		previewComposite.setLayout(new RowLayout());
		final Button buttonSelectPrinter = new Button(previewComposite, SWT.PUSH);
		buttonSelectPrinter.setText(printerSelectText);
		buttonSelectPrinter.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				PrintDialog dialog = new PrintDialog(shell);
				// Prompts the printer dialog to let the user select a printer.
				PrinterData printerData = dialog.open();
				printData = printerData;
				if (printerData == null) // the user cancels the dialog
					return;
				// Loads the printer.
				final Printer printer = new Printer(printerData);
				setPrinter(printer, Double.parseDouble(combo.getItem(combo.getSelectionIndex())));
				// print the plot
				print(printer, margin, orientation);
				shell.dispose();
			}
		});

		Composite scaleComposite = new Composite(previewComposite, SWT.BORDER);
		scaleComposite.setLayout(new RowLayout());
		new Label(scaleComposite, SWT.NULL).setText(printScaleText+":");
		combo = new Combo(scaleComposite, SWT.READ_ONLY);
		combo.add("0.5");
		combo.add("2.0");
		combo.add("3.0");
		combo.add("4.0");
		combo.add("5.0");
		combo.add("6.0");
		combo.add("7.0");
		combo.select(0);// default 100% scaling
		combo.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				double value = Double.parseDouble(combo.getItem(combo.getSelectionIndex()));
				setPrinter(printer, value);
			}
		});

		// TODO orientation button disabled: works for preview not for data sent to printer
//		Composite orientationComposite = new Composite(previewComposite, SWT.BORDER);
//		orientationComposite.setLayout(new RowLayout());
//		new Label(orientationComposite, SWT.NULL).setText(orientationText+":");
//		comboOrientation = new Combo(orientationComposite, SWT.READ_ONLY);
//		comboOrientation.add(portraitText);
//		comboOrientation.add(landscapeText);
//		comboOrientation.select(0);
//		orientation = portraitText;
//		comboOrientation.addListener(SWT.Selection, new Listener() {
//			@Override
//			public void handleEvent(Event e) {
//				orientation = comboOrientation.getItem(comboOrientation.getSelectionIndex());
//				// setPrinter(printer, value);
//				canvas.redraw();
//			}
//		});
		
		final Button buttonPrint = new Button(previewComposite, SWT.PUSH);
		buttonPrint.setText(printButtonText);
		buttonPrint.setToolTipText(printToolTipText);
		buttonPrint.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (printer == null)
					print();
				else
					print(printer, margin, orientation);
				shell.dispose();
			}
		});

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
		setPrinter(null, 0.5);

		// Set up the event loop.
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) {
				// If no more entries in event queue
				shell.getDisplay().sleep();
			}
		}
		return printData;
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
			//e.gc.drawRectangle(marginOffsetX, marginOffsetY, (int) (viewScaleFactor * (margin.right - margin.left)),
			//		(int) (viewScaleFactor * (margin.bottom - margin.top)));

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

			int offsetX = (canvasSize.x - (int) (viewScaleFactor * rectangle.width)) / 2;
			int offsetY = (canvasSize.y - (int) (viewScaleFactor * rectangle.height)) / 2;

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
				// e.gc.drawImage(image, 0, 0, imageWidth, imageHeight, marginOffsetX, marginOffsetY,
				// (int) (dpiScaleFactorX * imageSizeFactor * imageWidth * viewScaleFactor),
				// (int) (dpiScaleFactorY * imageSizeFactor * imageHeight * viewScaleFactor));
				e.gc.drawImage(image, 0, 0, imageWidth, imageHeight, marginOffsetX, marginOffsetY,
						(int) (rectangle.height * imageFactor * dpiScaleFactorX * viewScaleFactor * imageSizeFactor),
						(int) (rectangle.width * imageFactor * dpiScaleFactorY * viewScaleFactor * imageSizeFactor));

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
					Rectangle trim = printer.computeTrim(0,0,0,0);
					
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
						gc.drawImage(image, 0, 0, imageWidth, imageHeight, margin.left, margin.top,
								(int) (dpiScaleFactorX * imageSizeFactor * imageWidth), 
								(int) (dpiScaleFactorY * imageSizeFactor * imageHeight));
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
						t.translate(0, (float)-(dpiScaleFactorY * imageSizeFactor * imageWidth*1.1));	 
						gc.setTransform(t);
						gc.drawImage(image, 0, 0, imageWidth, imageHeight, margin.left, margin.top,
								(int) (dpiScaleFactorX * imageSizeFactor * imageWidth*1.35), 
								(int) (dpiScaleFactorY * imageSizeFactor * imageHeight*1.35));
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
