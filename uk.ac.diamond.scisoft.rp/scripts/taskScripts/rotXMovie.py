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

RenderView14 = GetRenderView()
DataRepresentation21 = Show()
DataRepresentation21.EdgeColor = [0.0, 0.0, 0.5000076295109483]
DataRepresentation21.SelectionPointFieldDataArrayName = 'GlobalNodeId'
DataRepresentation21.SelectionCellFieldDataArrayName = 'GlobalElementId'
DataRepresentation21.ScalarOpacityUnitDistance = 1.3249258044319845
DataRepresentation21.ExtractedBlockIndex = 2
DataRepresentation21.ScaleFactor = 2.015999984741211

AnimationScene14 = GetAnimationScene()
a1_ObjectId_PVLookupTable = GetLookupTableForArray( "ObjectId", 1, NanColor=[0.25, 0.0, 0.0], RGBPoints=[1.0, 0.23, 0.299, 0.754, 1.0, 0.706, 0.016, 0.15], VectorMode='Magnitude', ColorSpace='Diverging', ScalarRangeInitialized=1.0 )

a1_ObjectId_PiecewiseFunction = CreatePiecewiseFunction( Points=[0.0, 0.0, 0.5, 0.0, 1.0, 1.0, 0.5, 0.0] )

CameraAnimationCue8 = GetCameraTrack()
CameraAnimationCue8.Mode = 'Path-based'


#start angle
DataRepresentation21.Orientation = [startX, startY, startZ]


TimeAnimationCue14 = GetTimeTrack()

KeyFrame2312 = CameraKeyFrame( FocalPathPoints=[0.0, 0.0, 0.0799999], Position=[0.0, 0.0, 50.119268546836174], PositionPathPoints=[0.0, 0.0, 50.1193, 0.0, -38.887839986848405, 31.570751806792178, 0.0, -48.94582133521389, -10.323755591128272, 0.0, -22.717366859016163, -44.50534295491966, 0.0, 20.352816944536556, -45.633175409969965, 0.0, 48.33425229601917, -12.871123969480482, 0.0, 40.48264416752803, 29.492362533817282], FocalPoint=[0.0, 0.0, 0.07999992370605469], ClosedPositionPath=1 )

KeyFrame2313 = CameraKeyFrame( Position=[0.0, 0.0, 50.119268546836174], KeyTime=1.0, FocalPoint=[0.0, 0.0, 0.07999992370605469] )

RenderView14.CameraPosition = [0.0, 0.0, 50.119268546836174]
RenderView14.CameraClippingRange = [29.47967608872377, 76.04025746136074]
RenderView14.CameraFocalPoint = [0.0, 0.0, 0.07999992370605469]
RenderView14.CameraParallelScale = 12.951115722667065
RenderView14.CenterOfRotation = [0.0, 0.0, 0.07999992370605469]

RenderView14.UseOffscreenRenderingForScreenshots = 1


if projection == 0:	
	RenderView14.CameraParallelProjection = 1  #parallelProjection


#resolution
RenderView14.ViewSize = [resX, resY]


if orientVis == 0:
	#disable orientation axex from view
	RenderView14.OrientationAxesVisibility = 0
if centerVis == 0:
	#disable center axes from view
	RenderView14.CenterAxesVisibility = 0

DataRepresentation21.ScalarOpacityFunction = a1_ObjectId_PiecewiseFunction
DataRepresentation21.ColorArrayName = 'ObjectId'
DataRepresentation21.LookupTable = a1_ObjectId_PVLookupTable
DataRepresentation21.ColorAttributeType = 'CELL_DATA'

AnimationScene14.NumberOfFrames = numberOfFrames

CameraAnimationCue8.KeyFrames = [ KeyFrame2312, KeyFrame2313 ]


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
	RenderView14.StereoRender = 0
elif stereoRenderID == 1:
	RenderView14.StereoRender = 1
	RenderView14.StereoType = 'Red-Blue'
elif stereoRenderID == 2:
	RenderView14.StereoRender = 1
	RenderView14.StereoType = 'Interlaced'
elif stereoRenderID == 3:
	RenderView14.StereoRender = 1
	RenderView14.StereoType = 'Checkerboard'
else:
	print "invalid stereoRenderID, using mono."


WriteAnimation(outputDir + ext, Magnification=mag, Quality=quality, FrameRate=fps)

