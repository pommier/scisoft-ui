# Avizo Script


proc setUpViewModules {file projection} {
	#set projection	
	if { $projection == 0 } {			
		viewer 0 setCameraType 0
	}

	#create Ortha Slice object, defualt orientation is xy
	create HxOrthoSlice Slice

	#add Ortha Slice module to data file
	Slice data setValue $file

	#compute action
	Slice fire

	create HxDynamicParameter Animate	
	Animate object setValue Slice
	Animate fire	

	create HxMovieMaker MovieMaker
	MovieMaker time setValue Animate
	MovieMaker fire

	viewer setBackgroundColor black
	viewer setBackgroundColor2 black
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


proc getVideo {numberOfFrames quality resX resY format videoType outputLocation} {
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

#videoType:
#0 - monoscopic
#1 - stereo side by side
#2 - stereo red/cyan
#3 - stereo blue/yellow
#4 - stereo green/magenta
#proc that is called from the outside
proc run {dataFileDir sliceOrientation zoomAmount projection numberOfFrames format videoType quality resX resY outputDir} {

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

	if { $sliceOrientation < 0 || $sliceOrientation > 2 } {
		echo "sliceOrientation: $sliceOrientation must be in range 0 to 2. Where 0 is xy, 1 is xz, 2 is yz."
		echo "exiting"		
		exit
	}

	#load file
	[load $dataFileDir] setLabel myData
	set dataFileName "myData"

	setUpViewModules $dataFileName $projection

	zoom $zoomAmount

	Slice sliceOrientation setIndex $sliceOrientation
	Slice fire
	
	getVideo $numberOfFrames $quality $resX $resY $format $videoType $outputDir

	remove $dataFileName
	exit
}
