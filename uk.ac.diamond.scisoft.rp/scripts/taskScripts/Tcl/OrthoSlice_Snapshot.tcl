# Avizo Script

proc setUpViewModules {file projection} {
	#set projection	
	if { $projection == 0 } {
		viewer 0 setCameraType 0
	}
	
	#create Ortha Slice object, defualt orientation is xy
	create HxOrthoSlice Slice

	#add Ortha Slice module to data
	Slice data setValue $file

	#compute action
	Slice fire	

	#background is made up of two colour starting with BackgroundColor at the top and ranging to BackgroundColor2 at the bottom 
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


proc takeSnapshots {numOfShots startSlice endSlice targetDir imgFormat resX resY} {
	set maxSlice [Slice sliceNumber getMaxValue]	
	if {$maxSlice < $endSlice} {
		set endSlice $maxSlice
	}
	if {$startSlice < 0} {
		set startSlice 0
	}
	set numOfSlice [expr {$endSlice - $startSlice}]	
	if {$numOfShots > 1} {	
		set inc [expr {$numOfSlice / [expr {$numOfShots - 1}] }]	
	} else {
		set inc 0
	}				
	set i 1
	set loopcnt $startSlice
	while {$i <= $numOfShots} {		
		Slice sliceNumber setValue $loopcnt
		Slice fire		
		set dir $targetDir		
		append dir $i
		append dir $imgFormat		
		set i [expr {$i + 1}]
		set loopcnt [expr {$loopcnt + $inc}]
		viewer 0 snapshot $dir -offscreen $resX $resY
	}
}

# sliceOrientation:
# 0 - xy
# 1 - xz
# 2 - yz
#proc that is called from the outside
proc run {filePathDir sliceOrientation zoomAmount startSlice endSlice numberOfSnapshots outputLocation imgFormat resX resY projection} {
	if {$endSlice < $startSlice} {
		echo "startSlice value: $startSlice cannot be greater than endSlice value: $endSlice"
		echo "exiting"
		exit
	}

	if { $sliceOrientation < 0 || $sliceOrientation > 2 } {
		echo "sliceOrientation: $sliceOrientation must be in range 0 to 2. Where 0 is xy, 1 is xz, 2 is yz."
		echo "exiting"		
		exit
	}

	#load file
	[load $filePathDir] setLabel myData
	set fileName "myData"

	setUpViewModules $fileName $projection

	Slice sliceOrientation setIndex $sliceOrientation
	Slice fire
	
	zoom $zoomAmount

	takeSnapshots $numberOfSnapshots $startSlice $endSlice $outputLocation $imgFormat $resX $resY

	remove $fileName
	exit
}

#proc that is called from the outside
proc run2 {filePathDir sliceOrientation zoomAmount startSlice numberOfSnapshots outputLocation imgFormat resX resY projection} {
	if { $sliceOrientation < 0 || $sliceOrientation > 2 } {
		echo "sliceOrientation: $sliceOrientation must be in range 0 to 2. Where 0 is xy, 1 is xz, 2 is yz."
		echo "exiting"		
		exit
	}

	[load $filePathDir] setLabel myData

	#load file
	load $filePathDir
	set fileName "myData"

	setUpViewModules $fileName $projection

	Slice sliceOrientation setIndex $sliceOrientation
	Slice fire

	#set endSlice to the max slice 
	set endSlice [Slice sliceNumber getMaxValue]
	
	zoom $zoomAmount

	takeSnapshots $numberOfSnapshots $startSlice $endSlice $outputLocation $imgFormat $resX $resY

	remove $fileName
	exit			
}

