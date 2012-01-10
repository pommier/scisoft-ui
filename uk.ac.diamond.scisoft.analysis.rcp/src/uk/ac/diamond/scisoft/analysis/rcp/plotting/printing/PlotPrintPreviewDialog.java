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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.printing;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
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

import uk.ac.diamond.scisoft.analysis.rcp.plotting.Plot1DGraphTable;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.printing.PrintSettings.Orientation;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.printing.PrintMargin;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.printing.CreateImage;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.printing.PrintSettings.Scale;
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

	protected String printScaleText = ResourceProperties.getResourceString("PRINT_SCALE");
	protected String printButtonText = ResourceProperties.getResourceString("PRINT_BUTTON");
	protected String printToolTipText = ResourceProperties.getResourceString("PRINT_TOOLTIP");
	protected String printerSelectText = ResourceProperties.getResourceString("PRINTER_SELECT");
	protected String printPreviewText = ResourceProperties.getResourceString("PRINT_PREVIEW");
	protected String orientationText = ResourceProperties.getResourceString("ORIENTATION_TEXT");
	protected String resolutionText = ResourceProperties.getResourceString("RESOLUTION_TEXT");
	public static String portraitText = ResourceProperties.getResourceString("PORTRAIT_ORIENTATION");
	protected static String landscapeText = ResourceProperties.getResourceString("LANDSCAPE_ORIENTATION");
	protected String defaultPrinterText = ResourceProperties.getResourceString("DEFAULT_PRINTER");
	private AbstractViewerApp viewerApp = null;
	private Plot1DGraphTable legendTable;
	
	private PrintSettings settings;

	private Image image;
	private String fileName = "SDA plot";

	private static final Logger logger = LoggerFactory.getLogger(PlotPrintPreviewDialog.class);
	
	/**
	 * PlotPrintPreviewDialog constructor
	 * 
	 * @param viewerApp
	 *            the viewerApp object used to create the image
	 * @param device
	 *            the display device
	 * @param legendTable
	 *            the legend of the plot
	 * @param settings
	 *            The input PrintSettings. Will construct a default one if null.
	 */
	public PlotPrintPreviewDialog(AbstractViewerApp viewerApp, Display device, Plot1DGraphTable legendTable,
			PrintSettings settings) {
		super(device.getActiveShell());
		this.display = device;
		this.viewerApp = viewerApp;
		this.legendTable = legendTable;
		
		if (settings != null) {
			this.settings = settings.clone();
		} else {
			this.settings = new PrintSettings();
		}
		this.printer = new Printer(settings.getPrinterData());
		// We put the image creation into a thread and display a busy kind of indicator while the thread is
		// running
		Runnable createImage = new CreateImage(viewerApp, device, legendTable, this.settings.getPrinterData(), this.settings.getResolution());
		@SuppressWarnings("unused")
		Thread thread = new Thread(createImage);
		BusyIndicator.showWhile(this.display, createImage);

		this.image = CreateImage.getImage();
	}

	/**
	 * Creates and then opens the dialog. Note that setting or getting whether
	 * to use portrait or not must be handled separately.
	 * 
	 * @return The new value of the PrintSettings.
	 */
	public PrintSettings open() {
		setPrinter(printer, settings.getScale().getValue());
		
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
				PrinterData printerData = dialog.open();
				if (printerData == null) // the user cancels the dialog
					return;
				settings.setPrinterData(printerData);
				// Loads the printer.
				setPrinter(printer, settings.getScale().getValue());//Double.parseDouble(comboScale.getItem(comboScale.getSelectionIndex())));
				// print the plot
				print(printer, margin, settings);
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
					print(printer, margin, settings);
				}
				shell.dispose();
			}
		});

		Composite printerNameComposite = new Composite(previewComposite,SWT.TOP);
		printerNameComposite.setLayout(new RowLayout());
		new Label(printerNameComposite, SWT.RIGHT).setText(defaultPrinterText + ":");
		String[] tmp = settings.getPrinterData().toString().split("name =");
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
		comboScale.add("100%");
		comboScale.add("75%");
		comboScale.add("66%");
		comboScale.add("50%");
		comboScale.add("33%");
		comboScale.add("25%");
		comboScale.add("10%");
		for (int i = 0; i < comboScale.getItemCount(); i++) {
			if (settings.getScale().getName().equals(comboScale.getItem(i))) {
				comboScale.select(i);
				break;
			}
		}
		comboScale.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int scaleNumber = comboScale.getSelectionIndex();
				switch (scaleNumber){
					case 0:
						settings.setScale(Scale.DEFAULT);
						break;
					case 1:
						settings.setScale(Scale.PERCENT75);
						break;
					case 2:
						settings.setScale(Scale.PERCENT66);
						break;
					case 3:
						settings.setScale(Scale.PERCENT50);
						break;
					case 4:
						settings.setScale(Scale.PERCENT33);
						break;
					case 5:
						settings.setScale(Scale.PERCENT25);
						break;
					case 6:
						settings.setScale(Scale.PERCENT10);
						break;
				}	
				setPrinter(printer, settings.getScale().getValue());
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
			if (settings.getResolution() == Integer.parseInt(comboResolution.getItem(i))) {
				comboResolution.select(i);
				break;
			}
		}
		comboResolution.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				settings.setResolution(Integer.parseInt(comboResolution.getItem(comboResolution.getSelectionIndex())));
				Runnable createImage = new CreateImage(viewerApp, display, legendTable, settings.getPrinterData(), settings.getResolution());
				@SuppressWarnings("unused")
				Thread thread = new Thread(createImage);
				BusyIndicator.showWhile(display, createImage);
				image = CreateImage.getImage();
				setPrinter(printer, settings.getScale().getValue());
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
//			if (settings.getOrientation().equals(comboOrientation.getItem(i))) {
//				comboOrientation.select(i);
//				break;
//			}
//		}
//		comboOrientation.addListener(SWT.Selection, new Listener() {
//			@Override
//			public void handleEvent(Event e) {
//				settings.setOrientation(comboOrientation.getItem(comboOrientation.getSelectionIndex()));
//				//orientation = comboOrientation.getItem(comboOrientation.getSelectionIndex());
//				// setPrinter(printer, value);
//				canvas.redraw();
//			}
//		});

		canvas = new Canvas(shell, SWT.BORDER);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 4;
		canvas.setLayoutData(gridData);

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
					paint(e, settings.getOrientation());
					break;
				}
			}
		};
		canvas.addListener(SWT.Paint, listener);

		shell.setSize(800, 650);
		shell.open();
		setPrinter(printer, settings.getScale().getValue());

		// Set up the event loop.
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) {
				// If no more entries in event queue
				shell.getDisplay().sleep();
			}
		}
		return settings;
	}

	private void paint(Event e, Orientation orientation) {
		if (orientation.equals(Orientation.PORTRAIT)) {
		
			int canvasBorder = 20;

			if (printer == null || printer.isDisposed())
				return;
			Rectangle printerBounds = printer.getBounds();
			Point canvasSize = canvas.getSize();

			double viewScaleFactor = (canvasSize.x - canvasBorder * 2) * 1.0 / printerBounds.width;
			viewScaleFactor = Math.min(viewScaleFactor, (canvasSize.y - canvasBorder * 2) * 1.0 / printerBounds.height);

			int offsetX = (canvasSize.x - (int) (viewScaleFactor * printerBounds.width)) / 2;
			int offsetY = (canvasSize.y - (int) (viewScaleFactor * printerBounds.height)) / 2;

			e.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			// draws the page layout
			e.gc.fillRectangle(offsetX, offsetY, (int) (viewScaleFactor * printerBounds.width),
					(int) (viewScaleFactor * printerBounds.height));

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

		} 
			else if (orientation.equals(Orientation.LANDSCAPE)) {
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
		print(printer, null, settings);
	}

	/**
	 * Prints the image current displayed to the specified printer.
	 * 
	 * @param printer
	 */
	void print(final Printer printer, PrintMargin printMargin, final PrintSettings settings) {
		if (image == null) // If no image is loaded, do not print.
			return;

		final Point printerDPI = printer.getDPI();
		final Point displayDPI = display.getDPI();
		logger.info(displayDPI + " " + printerDPI);

		final PrintMargin margin = (printMargin == null ? PrintMargin.getPrintMargin(printer, 1.0) : printMargin);

		Thread printThread = new Thread() {
			@Override
			public void run() {
				if (!printer.startJob(fileName)) {
					logger.error("Failed to start print job!");
					printer.dispose();
					return;
				}

				GC gc = new GC(printer);

				if (!printer.startPage()) {
					logger.error("Failed to start a new page!");
					gc.dispose();
					return;
				} else {
					Rectangle trim = printer.computeTrim(0, 0, 0, 0);
					
					if (settings.getOrientation().equals(Orientation.PORTRAIT)) {
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
					if (settings.getOrientation().equals(Orientation.LANDSCAPE)) {
						// TODO orientation: need to have the image rotating to work...
						int imageWidth = image.getBounds().width;
						int imageHeight = image.getBounds().height;

						printerDPI.x = printer.getDPI().y;
						printerDPI.y = printer.getDPI().x;
						
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
				logger.info("Printing job done!");
			}
		};
		printThread.start();
	}
}
