# Avizo Script
											
#functions
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
	
	viewer setBackgroundColor black
	viewer setBackgroundColor2 black
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


proc takeSnapshots {numOfShots startAngle endAngle targetDir imgFormat resX resY axis} {	
	set angleToTravel [expr {$endAngle - $startAngle}]	
	if {$numOfShots > 1} {	
		set inc [expr {$angleToTravel / [expr {$numOfShots - 1}] } ]		
	} else {
		set inc 0
	}			
	set i 1	
	while {$i <= $numOfShots} {				
		set dir $targetDir			
		append dir $i
		append dir $imgFormat		
		set i [expr {$i + 1}]
		viewer 0 rotate $inc $axis					
		viewer 0 snapshot $dir -offscreen $resX $resY
	}
}


#17 args
proc run {filePathDir zoomAmount const1 const2 startAngle endAngle axis renderDetail lighting edgeAdhance alphaScale numberOfSnapshots outputLocation imgFormat resX resY projection} {
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

	if { [string compare $axis "x"] == 0 } {
		viewer 0 rotate $startAngle x
		viewer 0 rotate $const1 y
		viewer 0 rotate $const2 z		
	} elseif { [string compare $axis "y"] == 0 } {
		viewer 0 rotate $startAngle y
		viewer 0 rotate $const1 z
		viewer 0 rotate $const2 x		
	} elseif { [string compare $axis "z"] == 0 } {
		viewer 0 rotate $startAngle z
		viewer 0 rotate $const1 x
		viewer 0 rotate $const2 y		
	} else {
		echo "axis: $axis must be x, y, or z"
		echo "exiting"
		exit
	}

	zoom $zoomAmount

	takeSnapshots $numberOfSnapshots $startAngle $endAngle $outputLocation $imgFormat $resX $resY $axis

	remove $fileName
	exit
}


set fileNameG "Not yet loaded"
set fileLoaded 0
