# Avizo Script

proc setUpViewModules {file renderDetail lighting edgeAdhance projection alphaScale} {

	#set projection	
	if { $projection == 0 } {
		viewer 0 setCameraType 0
	}

	#create Volume Rendering Settings module
	create HxVolumeRenderingSettings VolumeRenderingSettings

	#add this module to data
	VolumeRenderingSettings data setValue $file	
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
	Rotate time setMinMax 0 360
	Rotate fire
	
	viewer setBackgroundColor black
	viewer setBackgroundColor2 black
}


proc setRotatePointToCenter {} {
	global fileLoaded

	if {$fileLoaded == 0} {
		echo "No file loaded, use run procedure to load in file"
		return
	}	

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


proc setRotatePoint {x y z} {
	Rotate center setValue 0 $x
	Rotate center setValue 1 $y
	Rotate center setValue 2 $z
	Rotate fire
}


proc zoom {amount} {
	global fileLoaded
	if {$fileLoaded == 0} {
		echo "No file loaded, use run procedure to load in file"
		return
	}	

	#parallel
	set currentZoom [viewer 0 getCameraHeight]
	set newZoom [expr {$currentZoom - $amount}]
	viewer 0 setCameraHeight $newZoom

	#perspective
	set currentZoom [viewer 0 getCameraHeightAngle]
	set newZoom [expr {$currentZoom - $amount}]
	viewer 0 setCameraHeightAngle $newZoom
}

proc takeSnapshots {numOfShots startAngle endAngle targetDir imgFormat resX resY} {	
	set angleToTravel [expr {$endAngle - $startAngle}]	
	set inc [expr {$angleToTravel / [expr {$numOfShots - 1}] } ]			
	set i 1
	set loopcnt $startAngle
	while {$i <= $numOfShots} {		
		Rotate time setValue $loopcnt
		Rotate fire		
		set dir $targetDir			
		append dir $i
		append dir $imgFormat		
		set i [expr {$i + 1}]
		set loopcnt [expr {$loopcnt + $inc}]				
		viewer 0 snapshot $dir -offscreen $resX $resY
	}
}

#proc that is called from the outside
proc run {filePathDir zoomAmount cameraRotX cameraRotY cameraRotZ startAngle endAngle axis centerX centerY centerZ renderDetail lighting edgeAdhance alphaScale numberOfSnapshots outputLocation imgFormat resX resY projection} {
	if {$startAngle > $endAngle} {
		echo "startAngle: $startAngle cannot be greater than endAngle: $endAngle"
		echo "exiting"
		exit
	}	

	#load file
	[load $filePathDir] setLabel myData
	set fileName "myData"
	global fileNameG
	set fileNameG $fileName
	global fileLoaded
	set fileLoaded 1
	setUpViewModules $fileName $renderDetail $lighting $edgeAdhance $projection $alphaScale

	viewer 0 rotate $cameraRotX x
	viewer 0 rotate $cameraRotY y
	viewer 0 rotate $cameraRotZ z

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

	zoom $zoomAmount
	setRotatePoint $centerX $centerY $centerZ

	takeSnapshots $numberOfSnapshots $startAngle $endAngle $outputLocation $imgFormat $resX $resY

	remove $fileName
	exit
}

#proc that is called from the outside
proc run2 {filePathDir zoomAmount cameraRotX cameraRotY cameraRotZ startAngle endAngle axis renderDetail lighting edgeAdhance alphaScale numberOfSnapshots outputLocation imgFormat resX resY projection} {
	if {$startAngle > $endAngle} {
		echo "startAngle: $startAngle cannot be greater than endAngle: $endAngle"
		echo "exiting"
		exit
	}

	#load file
	[load $filePathDir] setLabel myData
	set fileName "myData"
	global fileNameG
	set fileNameG $fileName
	global fileLoaded
	set fileLoaded 1
	setUpViewModules $fileName $renderDetail $lighting $edgeAdhance $projection $alphaScale

	viewer 0 rotate $cameraRotX x
	viewer 0 rotate $cameraRotY y
	viewer 0 rotate $cameraRotZ z

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

	zoom $zoomAmount
	setRotatePointToCenter

	takeSnapshots $numberOfSnapshots $startAngle $endAngle $outputLocation $imgFormat $resX $resY

	remove $fileName
	exit
}



set fileNameG "Not yet loaded"
set fileLoaded 0
