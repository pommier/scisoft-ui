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

import org.dawnsci.plotting.jreality.impl.Plot1DGraphTable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.printing.PrintSettings.Orientation;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.printing.PrintSettings.Resolution;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.printing.PrintSettings.Scale;
import uk.ac.diamond.scisoft.analysis.rcp.preference.PreferenceConstants;
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
	private Combo comboPrinterName;
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
	private PrinterData[] printerNames;
	
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
		this.printer = new Printer(this.settings.getPrinterData());
		// We put the image creation into a thread and display a busy kind of indicator while the thread is
		// running
		Runnable createImage = new CreateImage(viewerApp, device, legendTable, this.settings.getPrinterData(), this.settings.getResolution().getValue());
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
				setPrinter(printer, settings.getScale().getValue());
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
				Printer printer = new Printer(settings.getPrinterData());
				print(printer, margin, settings);
				shell.dispose();
			}
		});

		Composite printerNameComposite = new Composite(previewComposite, SWT.BORDER);
		RowLayout printerNameLayout=new RowLayout();
		printerNameLayout.center=true;
		printerNameComposite.setLayout(printerNameLayout);
		new Label(printerNameComposite, SWT.BOTTOM).setText(defaultPrinterText + ":");
		comboPrinterName = new Combo(printerNameComposite, SWT.READ_ONLY);
		PrinterData[] printerList = Printer.getPrinterList();
		for (int i = 0; i < printerList.length; i++) {
			comboPrinterName.add(printerList[i].name);
		}
		comboPrinterName.select(getPreferencePrinterName());
		comboPrinterName.addSelectionListener(printerNameSelection);
		
		Composite scaleComposite = new Composite(previewComposite, SWT.BORDER);
		RowLayout scaleLayout=new RowLayout();
		scaleLayout.center=true;
		scaleComposite.setLayout(scaleLayout);
		new Label(scaleComposite, SWT.BOTTOM).setText(printScaleText + ":");
		comboScale = new Combo(scaleComposite, SWT.READ_ONLY);
		Scale[] scaleList = Scale.values();
		for (int i = 0; i < scaleList.length; i++) {
			comboScale.add(scaleList[i].getName());
		}
		comboScale.select(getPreferencePrintScale());
		comboScale.addSelectionListener(scaleSelection);

		Composite resolutionComposite = new Composite(previewComposite, SWT.BORDER);
		RowLayout resolutionLayout = new RowLayout();
		resolutionLayout.center=true;
		resolutionComposite.setLayout(resolutionLayout);
		Label resolutionLabel = new Label(resolutionComposite, SWT.SINGLE);
		resolutionLabel.setText(resolutionText + ":");
		comboResolution = new Combo(resolutionComposite, SWT.READ_ONLY);
		Resolution[] resolutionList = Resolution.values();
		for (int i = 0; i < resolutionList.length; i++) {
			comboResolution.add(resolutionList[i].getName());
		}
		comboResolution.select(getPreferencePrintResolution());
		comboResolution.addSelectionListener(resolutionSelection);

		// TODO orientation button disabled: works for preview not for data sent to printer
//		Composite orientationComposite = new Composite(previewComposite, SWT.BORDER);
//		RowLayout orientationLayout=new RowLayout();
//		orientationLayout.center=true;
//		orientationComposite.setLayout(orientationLayout);
//		new Label(orientationComposite, SWT.NULL).setText(orientationText + ":");
//		comboOrientation = new Combo(orientationComposite, SWT.READ_ONLY);
//		comboOrientation.add(portraitText);
//		comboOrientation.add(landscapeText);
//		comboOrientation.select(getPreferencePrintOrientation());
//		comboOrientation.addSelectionListener(orientationSelection);

		canvas = new Canvas(shell, SWT.BORDER);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 4;
		canvas.setLayoutData(gridData);

		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event e) {
				@SuppressWarnings("unused")
				Canvas canvas = null;
				switch (e.type) {
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

		addPropertyListeners();
		
		// Set up the event loop.
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) {
				// If no more entries in event queue
				shell.getDisplay().sleep();
			}
		}
		return settings;
	}
	
	private SelectionAdapter printerNameSelection = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			selectPrinter(comboPrinterName.getSelectionIndex());
			setPrinterNamePreference(comboPrinterName.getSelectionIndex());
		}
	};

	private void selectPrinter(int printerNameNum) {
		PrinterData[] printerList = Printer.getPrinterList();
		settings.setPrinterData(printerList[printerNameNum]);
		setPrinter(printer, settings.getScale().getValue());
	}

	private SelectionAdapter scaleSelection = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			selectScale(comboScale.getSelectionIndex());
			setScalePreference(comboScale.getSelectionIndex());
		}
	};

	private void selectScale(int scaleNum) {
		// ("100%", 0.5), ("75%", 2.0), ("66%", 3.0), ("50%", 4.0), ("33%", 5.0), ("25%", 6.0), ("10%", 7.0)
		switch (scaleNum){
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

	private SelectionAdapter resolutionSelection = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			selectResolution(comboResolution.getSelectionIndex());
			setResolutionPreference(comboResolution.getSelectionIndex());
		}
	};

	private void selectResolution(int resolutionNum) {
		// ("Low", 1), ("Medium", 2), ("Medium High", 3), ("High", 4) 	
		switch (resolutionNum){
		case 0:
			settings.setResolution(Resolution.LOW);
			break;
		case 1:
			settings.setResolution(Resolution.MEDIUM);
			break;
		case 2:
			settings.setResolution(Resolution.MEDIUMHIGH);
			break;
		case 3:
			settings.setResolution(Resolution.HIGH);
			break;
		}
		Runnable createImage = new CreateImage(viewerApp, display, legendTable, settings.getPrinterData(), settings.getResolution().getValue());
		@SuppressWarnings("unused")
		Thread thread = new Thread(createImage);
		BusyIndicator.showWhile(display, createImage);
		image = CreateImage.getImage();
		setPrinter(printer, settings.getScale().getValue());
	}

	@SuppressWarnings("unused")
	private SelectionAdapter orientationSelection = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			selectOrientation(comboOrientation.getSelectionIndex());
			setOrientationPreference(comboOrientation.getSelectionIndex());
		}
	};

	private void selectOrientation(int orientationNum) {
		// "Portrait", "Landscape"	
		switch (orientationNum){
		case 0:
			settings.setOrientation(Orientation.PORTRAIT);
			settings.getPrinterData().orientation = Orientation.PORTRAIT.getValue();
			break;
		case 1:
			settings.setOrientation(Orientation.LANDSCAPE);
			settings.getPrinterData().orientation = Orientation.LANDSCAPE.getValue();
			break;
		}
		canvas.redraw();
	}

	/**
	 * PlotPrintPreviewDialog is listening to eventual property changes done through the Preference Page
	 */
	private void addPropertyListeners() {
		AnalysisRCPActivator.getDefault().getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				String property = event.getProperty();
				IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
				if (property.equals(PreferenceConstants.PRINTSETTINGS_PRINTER_NAME)
						|| property.equals(PreferenceConstants.PRINTSETTINGS_SCALE)
						|| property.equals(PreferenceConstants.PRINTSETTINGS_RESOLUTION)
						|| property.equals(PreferenceConstants.PRINTSETTINGS_ORIENTATION)) {

					int printerName;
					if (preferenceStore.isDefault(PreferenceConstants.PRINTSETTINGS_PRINTER_NAME)) {
						printerName = preferenceStore.getDefaultInt(PreferenceConstants.PRINTSETTINGS_PRINTER_NAME);
					} else {
						printerName = preferenceStore.getInt(PreferenceConstants.PRINTSETTINGS_PRINTER_NAME);
					}
					printerNames = Printer.getPrinterList();
					for (int i = 0; i < printerNames.length; i++) {
						if(i==printerName){
							settings.setPrinterData(printerNames[i]);
							break;
						}
					}

					int scale;
					if (preferenceStore.isDefault(PreferenceConstants.PRINTSETTINGS_SCALE)) {
						scale = preferenceStore.getDefaultInt(PreferenceConstants.PRINTSETTINGS_SCALE);
					} else {
						scale = preferenceStore.getInt(PreferenceConstants.PRINTSETTINGS_SCALE);
					}
					Scale[] scales = Scale.values();
					for (int i = 0; i < scales.length; i++) {
						if(i==scale){
							settings.setScale(scales[i]);
							break;
						}
					}

					int resolution;
					if (preferenceStore.isDefault(PreferenceConstants.PRINTSETTINGS_RESOLUTION)) {
						resolution = preferenceStore.getDefaultInt(PreferenceConstants.PRINTSETTINGS_RESOLUTION);
					} else {
						resolution = preferenceStore.getInt(PreferenceConstants.PRINTSETTINGS_RESOLUTION);
					}
					Resolution[] resolutions = Resolution.values();
					for (int i = 0; i < resolutions.length; i++) {
						if(i==resolution){
							settings.setResolution(resolutions[i]);
							break;
						}
					}

					int orientation;
					if (preferenceStore.isDefault(PreferenceConstants.PRINTSETTINGS_ORIENTATION)) {
						orientation = preferenceStore.getDefaultInt(PreferenceConstants.PRINTSETTINGS_ORIENTATION);
					} else {
						orientation = preferenceStore.getInt(PreferenceConstants.PRINTSETTINGS_ORIENTATION);
					}
					Orientation[] orientations = Orientation.values();
					for (int i = 0; i < orientations.length; i++) {
						if(i==orientation){
							settings.setOrientation(orientations[i]);
							break;
						}
					}
				}
			}
		});
	}
	
	private int getPreferencePrinterName() {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		return preferenceStore.isDefault(PreferenceConstants.PRINTSETTINGS_PRINTER_NAME)
				? preferenceStore.getDefaultInt(PreferenceConstants.PRINTSETTINGS_PRINTER_NAME)
				: preferenceStore.getInt(PreferenceConstants.PRINTSETTINGS_PRINTER_NAME);
	}
	
	private int getPreferencePrintScale() {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		return preferenceStore.isDefault(PreferenceConstants.PRINTSETTINGS_SCALE)
				? preferenceStore.getDefaultInt(PreferenceConstants.PRINTSETTINGS_SCALE)
				: preferenceStore.getInt(PreferenceConstants.PRINTSETTINGS_SCALE);
	}
	
	private int getPreferencePrintResolution() {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		return preferenceStore.isDefault(PreferenceConstants.PRINTSETTINGS_RESOLUTION)
				? preferenceStore.getDefaultInt(PreferenceConstants.PRINTSETTINGS_RESOLUTION)
				: preferenceStore.getInt(PreferenceConstants.PRINTSETTINGS_RESOLUTION);
	}
	
	private void setPrinterNamePreference(int value) {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		settings.setPrinterData(Printer.getPrinterList()[value]);
		preferenceStore.setValue(PreferenceConstants.PRINTSETTINGS_PRINTER_NAME, value);
	}
	
	private void setScalePreference(int value) {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		settings.setScale(Scale.values()[value]);
		preferenceStore.setValue(PreferenceConstants.PRINTSETTINGS_SCALE, value);
	}
	
	private void setResolutionPreference(int value) {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		settings.setResolution(Resolution.values()[value]);
		preferenceStore.setValue(PreferenceConstants.PRINTSETTINGS_RESOLUTION, value);
	}
	
	private void setOrientationPreference(int value) {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		settings.setOrientation(Orientation.values()[value]);
		preferenceStore.setValue(PreferenceConstants.PRINTSETTINGS_ORIENTATION, value);
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

				//float imageFactor = imageWidth / rectangle.width;
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
				} else if(printer.startPage()) {
					//Rectangle trim = printer.computeTrim(0, 0, 0, 0);
					
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
