try: paraview.simple
except: from paraview.simple import *
import sys, math

inputFile = sys.argv[1]
startAngle = float(sys.argv[2])
endAngle = float(sys.argv[3])
originX = float(sys.argv[4])
originY = float(sys.argv[5])
originZ = float(sys.argv[6])
const1 = float(sys.argv[7])
const2 = float(sys.argv[8])
axis = sys.argv[9]
transX = float(sys.argv[10])
transY = float(sys.argv[11])
transZ = float(sys.argv[12])
numOfSnaps = int(sys.argv[13])
orientVis = int(sys.argv[14])
centerVis = int(sys.argv[15])
mag = float(sys.argv[16])
projection = int(sys.argv[17])
resX = int(sys.argv[18])
resY = int(sys.argv[19])
stereoRenderID = int(sys.argv[20])
extensionID = int(sys.argv[21])
outputDir = sys.argv[22]



paraview.simple._DisableFirstRenderCameraReset()

disk_out_ref_ex2 = ExodusIIReader( FileName=[inputFile] )

disk_out_ref_ex2.NodeMapArrayStatus = []
disk_out_ref_ex2.FaceVariables = []
disk_out_ref_ex2.ElementVariables = []
disk_out_ref_ex2.XMLFileName = 'Invalid result'
disk_out_ref_ex2.FaceSetResultArrayStatus = []
disk_out_ref_ex2.PointVariables = []
disk_out_ref_ex2.FaceSetArrayStatus = []
disk_out_ref_ex2.FaceMapArrayStatus = []
disk_out_ref_ex2.FileRange = [0, 0]
disk_out_ref_ex2.SideSetResultArrayStatus = []
disk_out_ref_ex2.ElementSetArrayStatus = []
disk_out_ref_ex2.EdgeVariables = []
disk_out_ref_ex2.FilePrefix = inputFile
disk_out_ref_ex2.FilePattern = '%s'
disk_out_ref_ex2.EdgeSetArrayStatus = []
disk_out_ref_ex2.SideSetArrayStatus = []
disk_out_ref_ex2.GlobalVariables = []
disk_out_ref_ex2.NodeSetArrayStatus = []
disk_out_ref_ex2.NodeSetResultArrayStatus = []
disk_out_ref_ex2.ElementMapArrayStatus = []
disk_out_ref_ex2.EdgeSetResultArrayStatus = []
disk_out_ref_ex2.EdgeMapArrayStatus = []
disk_out_ref_ex2.ElementSetResultArrayStatus = []

disk_out_ref_ex2.ModeShape = 0
disk_out_ref_ex2.ElementBlocks = ['Unnamed block ID: 1 Type: HEX8 Size: 7472']

RenderView1 = GetRenderView()
DataRepresentation1 = Show()
DataRepresentation1.EdgeColor = [0.0, 0.0, 0.5000076295109483]
DataRepresentation1.SelectionPointFieldDataArrayName = 'GlobalNodeId'
DataRepresentation1.SelectionCellFieldDataArrayName = 'GlobalElementId'
DataRepresentation1.ScalarOpacityUnitDistance = 1.3249258044319845
DataRepresentation1.ExtractedBlockIndex = 2
DataRepresentation1.ScaleFactor = 2.015999984741211



RenderView1.UseOffscreenRenderingForScreenshots = 1

if orientVis == 0:
	#disable orientation axex from view
	RenderView1.OrientationAxesVisibility = 0
if centerVis == 0:
	#disable center axes from view
	RenderView1.CenterAxesVisibility = 0

a1_GlobalElementId_PiecewiseFunction = CreatePiecewiseFunction( Points=[0.0, 0.0, 0.5, 0.0, 1.0, 1.0, 0.5, 0.0] )

a1_GlobalElementId_PVLookupTable = GetLookupTableForArray( "GlobalElementId", 1 )

RenderView1.CameraPosition = [0.0, 0.0, 50.119268546836174]
RenderView1.CameraFocalPoint = [0.0, 0.0, 0.07999992370605469]
RenderView1.CameraClippingRange = [27.287007476158514, 78.85335879121243]
RenderView1.CenterOfRotation = [0.0, 0.0, 0.07999992370605469]
RenderView1.CameraParallelScale = 12.951115722667065

if projection == 0:	
	RenderView1.CameraParallelProjection = 1  #parallelProjection


#resolution
RenderView1.ViewSize = [resX, resY]


DataRepresentation1.ScalarOpacityFunction = a1_GlobalElementId_PiecewiseFunction
DataRepresentation1.ColorArrayName = 'GlobalElementId'
DataRepresentation1.LookupTable = a1_GlobalElementId_PVLookupTable
DataRepresentation1.ColorAttributeType = 'CELL_DATA'

angleToTravel = endAngle- startAngle	
inc = angleToTravel / (numOfSnaps - 1)			
i = 0
loopcnt = startAngle

ext = ".png"

if extensionID == 0:
	ext = ".png"
elif extensionID == 1:
	ext = ".bmp"
elif extensionID == 2:
	ext = ".tif"
elif extensionID == 3:
	ext = ".ppm"
elif extensionID == 4:
	ext = ".jpg"
else:
	print "invalid extensionID, using .png"


#translation
DataRepresentation1.Position = [transX, transY, transZ]

#set origin for rotation
DataRepresentation1.Origin = [originX, originY, originZ]

if stereoRenderID == 0:
	RenderView1.StereoRender = 0
elif stereoRenderID == 1:
	RenderView1.StereoRender = 1
	RenderView1.StereoType = 'Red-Blue'
elif stereoRenderID == 2:
	RenderView1.StereoRender = 1
	RenderView1.StereoType = 'Interlaced'
elif stereoRenderID == 3:
	RenderView1.StereoRender = 1
	RenderView1.StereoType = 'Checkerboard'
else:
	print "invalid stereoRenderID, using mono."


if axis == "x":
	while (loopcnt <= endAngle) :				
		DataRepresentation1.Orientation = [loopcnt, const1, const2]				
		dir = outputDir + str(i) + ext	
		i = i + 1		
		loopcnt = loopcnt + inc					
		WriteImage(dir, Magnification=mag)
		

if axis == "y":
	while (loopcnt <= endAngle) :				
		DataRepresentation1.Orientation = [const1, loopcnt, const2]				
		dir = outputDir + str(i) + ext	
		i = i + 1		
		loopcnt = loopcnt + inc				
		WriteImage(dir, Magnification=mag)

if axis == "z":
	while (loopcnt <= endAngle) :				
		DataRepresentation1.Orientation = [const1, const2, loopcnt]				
		dir = outputDir + str(i) + ext	
		i = i + 1		
		loopcnt = loopcnt + inc				
		WriteImage(dir, Magnification=mag)


