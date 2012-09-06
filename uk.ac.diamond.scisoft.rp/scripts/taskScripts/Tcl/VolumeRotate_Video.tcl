# Avizo Script

proc setUpViewModules {file degreesToRotate renderDetail lighting edgeAdhance projection alphaScale} {

	#set projection	
	if { $projection == 0 } {
		viewer 0 setCameraType 0
	}

	#create Volume Rendering Settings module
	create HxVolumeRenderingSettings VolumeRenderingSettings

	#add this module to data
	VolumeRenderingSettings data setValue $file
	#compute action
	VolumeRenderingSettings fire
	VolumeRenderingSettings lighting setValue $lighting
	VolumeRenderingSettings	effects setValue 0 $edgeAdhance
	VolumeRenderingSettings quality setValue $renderDetail
	VolumeRenderingSettings fire

	#create volume rendering module
	create HxVolumeRender2 VolumeRendering

	#attatch this module to the VoumeRenderingSettings module
	VolumeRendering volumeRenderingSettings setValue VolumeRenderingSettings
	VolumeRendering fire
	VolumeRendering alphaScale setValue $alphaScale
	VolumeRendering fire


	create Rotate 
	Rotate data setValue $file
	Rotate script setValue /dls_sw/i12/software/avizo/64/share/script-objects/RotateObject.scro
	Rotate fire
	Rotate degrees setValue $degreesToRotate
	Rotate fire

	create HxMovieMaker MovieMaker
	MovieMaker time setValue Rotate
	MovieMaker fire

	viewer setBackgroundColor black
	viewer setBackgroundColor2 black
}


proc setRotatePointToCenter {} {
	#create ROI Box for finding center
	create HxSelectRoi Box
	global fileNameG
	Box data setValue $fileNameG
	Box fire

	set minX [Box minimum getValue 0]
	set minY [Box minimum getValue 1]
	set minZ [Box minimum getValue 2]

	set maxX [Box maximum getValue 0]
	set maxY [Box maximum getValue 1]
	set maxZ [Box maximum getValue 2]

	#Do not require the box anymore, so remove it
	remove Box
	
	set centerXTmp [expr {$minX + $maxX} ]
	set centerX [expr {$centerXTmp / 2.0} ]
	set centerYTmp [expr {$minY + $maxY} ]
	set centerY [expr {$centerYTmp / 2.0} ]
	set centerZTmp [expr {$minZ + $maxZ} ]
	set centerZ [expr {$centerZTmp / 2.0} ]

	#set center of rotation to the center of the object
	Rotate center setValue 0 $centerX
	Rotate center setValue 1 $centerY
	Rotate center setValue 2 $centerZ
	Rotate fire	
}

proc zoom {amount} {
	#parallel
	set currentZoom [viewer 0 getCameraHeight]
	set newZoom [expr {$currentZoom - $amount}]
	viewer 0 setCameraHeight $newZoom

	#perspective
	set currentZoom [viewer 0 getCameraHeightAngle]
	set newZoom [expr {$currentZoom - $amount}]
	viewer 0 setCameraHeightAngle $newZoom
}


proc getVideo {numberOfFrames format quality resX resY videoType outputLocation} {
	set startFrame [MovieMaker frames getMinValue]
	set numberOfFrames [expr {$startFrame + $numberOfFrames}]

	MovieMaker frames setValue $numberOfFrames	
	MovieMaker type setValue $videoType
	MovieMaker fileFormat setValue $format
	MovieMaker filename setValue $outputLocation
	MovieMaker compressionQuality setValue $quality
	MovieMaker size setValue 5
	MovieMaker resolution setValue 0 $resX
	MovieMaker resolution setValue 1 $resY
	MovieMaker fire	
	MovieMaker action setValue 0
	MovieMaker fire	
}

proc setRotatePoint {x y z} {
	Rotate center setValue 0 $x
	Rotate center setValue 1 $y
	Rotate center setValue 2 $z
	Rotate fire
}


#format:
#0 - MPEG movie
#1 - JPEG images
#2 - TIFF images
#3 - PNG images
#4 - RGB images

#videoType:
#0 - monoscopic
#1 - stereo side by side
#2 - stereo red/cyan
#3 - stereo blue/yellow
#4 - stereo green/magenta

proc run {dataFileDir axis cameraRotX cameraRotY cameraRotZ centerX centerY centerZ zoomAmount degreesToRotate renderDetail lighting edgeAdhance alphaScale numberOfFrames format videoType quality resX resY outputDir projection} {
	
	
	if { $format < 0 } {
		echo "format: $format must be in range 0  to 4"
		echo "exiting"
		exit
	}
	if { $format > 4 } {
		echo "format: $format must be in range 0  to 4"
		echo "exiting"
		exit
	}

	#load file
	[load $dataFileDir] setLabel myData
	set fileName "myData"

	setUpViewModules $fileName $degreesToRotate $renderDetail $lighting $edgeAdhance $projection $alphaScale

	viewer 0 rotate $cameraRotX x
	viewer 0 rotate $cameraRotY y
	viewer 0 rotate $cameraRotZ z

	global fileNameG
	set fileNameG $fileName

	if { [string compare $axis "x"] == 0 } {
		Rotate axis setValue 0 1
		Rotate axis setValue 1 0
		Rotate axis setValue 2 0
		Rotate fire
	} elseif { [string compare $axis "y"] == 0 } {
		Rotate axis setValue 0 0
		Rotate axis setValue 1 1
		Rotate axis setValue 2 0
		Rotate fire
	} elseif { [string compare $axis "z"] == 0 } {
		Rotate axis setValue 0 0
		Rotate axis setValue 1 0
		Rotate axis setValue 2 1
		Rotate fire
	} else {
		echo "axis: $axis must be x, y, or z"
		echo "exiting"
		exit
	}

	setRotatePoint centerX centerY centerZ

	zoom $zoomAmount

	getVideo $numberOfFrames $format $quality $resX $resY $videoType $outputDir

	remove $fileName
	exit
}


proc run2 {dataFileDir axis cameraRotX cameraRotY cameraRotZ zoomAmount degreesToRotate renderDetail lighting edgeAdhance alphaScale numberOfFrames format videoType quality resX resY outputDir projection} {
	
	if { $format < 0 } {
		echo "format: $format must be in range 0  to 4"
		echo "exiting"
		exit
	}
	if { $format > 4 } {
		echo "format: $format must be in range 0  to 4"
		echo "exiting"
		exit
	}
	

	#load file
	[load $dataFileDir] setLabel myData
	set fileName "myData"

	setUpViewModules $fileName $degreesToRotate $renderDetail $lighting $edgeAdhance $projection $alphaScale

	viewer 0 rotate $cameraRotX x
	viewer 0 rotate $cameraRotY y
	viewer 0 rotate $cameraRotZ z

	global fileNameG
	set fileNameG $fileName

	if { [string compare $axis "x"] == 0 } {
		Rotate axis setValue 0 1
		Rotate axis setValue 1 0
		Rotate axis setValue 2 0
		Rotate fire
	} elseif { [string compare $axis "y"] == 0 } {
		Rotate axis setValue 0 0
		Rotate axis setValue 1 1
		Rotate axis setValue 2 0
		Rotate fire
	} elseif { [string compare $axis "z"] == 0 } {
		Rotate axis setValue 0 0
		Rotate axis setValue 1 0
		Rotate axis setValue 2 1
		Rotate fire
	} else {
		echo "axis: $axis must be x, y, or z"
		echo "exiting"
		exit
	}

	setRotatePointToCenter

	zoom $zoomAmount

	getVideo $numberOfFrames $format $quality $resX $resY $videoType $outputDir

	remove $fileName
	exit
}


set fileNameG "Not yet loaded"

