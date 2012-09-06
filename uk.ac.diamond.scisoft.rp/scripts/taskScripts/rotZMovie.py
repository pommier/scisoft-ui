try: paraview.simple
except: from paraview.simple import *
import sys

inputFile = sys.argv[1]
startX = float(sys.argv[2])
startY = float(sys.argv[3])
startZ = float(sys.argv[4])
numberOfFrames = int(sys.argv[5])
fps = float(sys.argv[6])
mag = float(sys.argv[7])
projection = int(sys.argv[8])
resX = int(sys.argv[9])
resY = int(sys.argv[10])
quality = float(sys.argv[11])
orientVis = int(sys.argv[12])
centerVis = int(sys.argv[13])
extensionID = int(sys.argv[14])
stereoRenderID = int(sys.argv[15])
outputDir = sys.argv[16]

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

RenderView4 = GetRenderView()
DataRepresentation4 = Show()
DataRepresentation4.EdgeColor = [0.0, 0.0, 0.5000076295109483]
DataRepresentation4.SelectionPointFieldDataArrayName = 'GlobalNodeId'
DataRepresentation4.SelectionCellFieldDataArrayName = 'GlobalElementId'
DataRepresentation4.ScalarOpacityUnitDistance = 1.3249258044319845
DataRepresentation4.ExtractedBlockIndex = 2
DataRepresentation4.ScaleFactor = 2.015999984741211

AnimationScene4 = GetAnimationScene()
a1_ObjectId_PVLookupTable = GetLookupTableForArray( "ObjectId", 1, NanColor=[0.25, 0.0, 0.0], RGBPoints=[1.0, 0.23, 0.299, 0.754, 1.0, 0.706, 0.016, 0.15], VectorMode='Magnitude', ColorSpace='Diverging', ScalarRangeInitialized=1.0 )

a1_ObjectId_PiecewiseFunction = CreatePiecewiseFunction( Points=[0.0, 0.0, 0.5, 0.0, 1.0, 1.0, 0.5, 0.0] )

CameraAnimationCue3 = GetCameraTrack()
CameraAnimationCue3.Mode = 'Path-based'


#start angle
DataRepresentation4.Orientation = [startX, startY, startZ]


TimeAnimationCue4 = GetTimeTrack()

KeyFrame2312 = CameraKeyFrame( FocalPathPoints=[0.0, 0.0, 0.0799999], Position=[0.0, 0.0, 50.119268546836174], PositionPathPoints=[0.0, 0.0, 50.1193, 0.0, 0.0, 50.1193, 0.0, 0.0, 50.1193, 0.0, 0.0, 50.1193, 0.0, 0.0, 50.1193, 0.0, 0.0, 50.1193, 0.0, 0.0, 50.1193], FocalPoint=[0.0, 0.0, 0.07999992370605469], ClosedPositionPath=1 )

KeyFrame2313 = CameraKeyFrame( Position=[0.0, 0.0, 50.119268546836174], KeyTime=1.0, FocalPoint=[0.0, 0.0, 0.07999992370605469] )

RenderView4.CameraPosition = [0.0, 0.0, 50.119268546836174]
RenderView4.CameraClippingRange = [29.47967608872377, 76.04025746136074]
RenderView4.CameraFocalPoint = [0.0, 0.0, 0.07999992370605469]
RenderView4.CameraParallelScale = 12.951115722667065
RenderView4.CenterOfRotation = [0.0, 0.0, 0.07999992370605469]

RenderView4.UseOffscreenRenderingForScreenshots = 1

if projection == 0:	
	RenderView4.CameraParallelProjection = 1  #parallelProjection

#resolution
RenderView4.ViewSize = [resX, resY]

if orientVis == 0:
	#disable orientation axex from view
	RenderView4.OrientationAxesVisibility = 0
if centerVis == 0:
	#disable center axes from view
	RenderView4.CenterAxesVisibility = 0

DataRepresentation4.ScalarOpacityFunction = a1_ObjectId_PiecewiseFunction
DataRepresentation4.ColorArrayName = 'ObjectId'
DataRepresentation4.LookupTable = a1_ObjectId_PVLookupTable
DataRepresentation4.ColorAttributeType = 'CELL_DATA'

AnimationScene4.NumberOfFrames = numberOfFrames

CameraAnimationCue3.KeyFrames = [ KeyFrame2312, KeyFrame2313 ]

ext = ".avi"

if extensionID == 0:
	ext = ".avi"
elif extensionID == 1:
	ext = ".jpg"
elif extensionID == 2:
	ext = ".tif"
elif extensionID == 3:
	ext = ".png"
else:
	print "invalid extensionID, using .avi"

if stereoRenderID == 0:
	RenderView4.StereoRender = 0
elif stereoRenderID == 1:
	RenderView4.StereoRender = 1
	RenderView4.StereoType = 'Red-Blue'
elif stereoRenderID == 2:
	RenderView4.StereoRender = 1
	RenderView4.StereoType = 'Interlaced'
elif stereoRenderID == 3:
	RenderView4.StereoRender = 1
	RenderView4.StereoType = 'Checkerboard'
else:
	print "invalid stereoRenderID, using mono."


WriteAnimation(outputDir + ext, Magnification=mag, Quality=quality, FrameRate=fps)
