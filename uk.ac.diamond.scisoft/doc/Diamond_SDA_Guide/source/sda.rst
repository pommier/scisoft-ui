SDA - Scientific Data Analysis
==============================

SDA is bundled with Pydev - an Eclipse-based Python development environment. This document
describes how to install SDA and setup Pydev and also how to use "scisoftpy" - the Diamond
Scientific Software Python package.

Minimum requirements
--------------------

The SDA workbench needs the following to run properly:

* 2 gigahertz (GHz) 32-bit (x86) or 64-bit (x64) processor (dual core preferable)
* 4 gigabyte (GB) of system memory
* 1 GB of available disk space
* NVidia GPU with at least 128MB of graphic memory
* DVD-ROM drive
* Internet access (fees may apply)

Setup on Ubuntu 10.04 LTS
-------------------------

You first need to make sure that the GPU is the default hardware device (instead of the integrated 
video unit provided by the motherboard) used by the OS. 

This can be set up in the BIOS settings : the BIOS settings menu can be accessed by pressing F12 during 
the startup screen of the machine. In the video menu, check that all the options are set to their maximum.

Once the OS is started, make sure that the repos are up to date ::

	sudo apt-get update

Then add in all the packages which are required for SDA ::

	sudo apt-get install python-dev python-numpy python-scipy 
		python-matplotlib python-setuptools ipython 
		openjdk-6-jdk subversion

Insert the SDA disk, and copy the program into your home directory (or wherever you want it),
and unzip it ::

	cp /media/sda/Scisoft-client-r12345-Linux32.zip .
	unzip Scisoft-client-r12345-Linux32.zip

Now you are ready to launch SDA ::

	client/scisoft

To set up Python, go to 'Window->Preferences->Pydev->Interpreter - Python' and click on
'Auto Config', then click OK and OK.

To make a Pydev project work well with the scisoft functionality, there is a small addition to
make, on the project right click and select properties. Then select 'PyDev - PYTHONPATH' and, in the
'External Libraries' tab, click 'Add source folder' and browse to the directory
'/home/name/client/plugins/uk.ac.diamond.scisoft.python_1.0.0' and click OK.

If you have a Pydev project the Python console should pick up the configuration correctly. However,
if it does not, you can make the console work in the following way: Open the console view
('Window->Show View->Console') and in the far right drop-down menu in the console view ('Open Console')
click on Pydev Console. Then select Python console from the available list and click OK. Finally
there is a single line of code which needs to be run (this can be made easier by enabling a new
feature for path completion, 'Window->Preferences->Pydev->Interactive Console->Completions' and check
both 'Enable completions' and 'Request completions'). The command to enable python is ::

	sys.path.append('/home/name/client/plugins/uk.ac.diamond.scisoft.python_1.0.0')

Then you should be able to use ::

	import scisoftpy as dnp

Finally, an optional step to optimize the loading of TIFF files that have been compressed with LZW
or PackBits is to compile the tifffile.c file in 'uk.ac.diamond.scisoft.python_1.0.0/scisoftpy/_external'.
Instructions are given in that file.
 
Setup on DLS Redhat 5 machines
------------------------------

Like on Ubuntu machines, you need to make sure that the advanced GPU is used for the display. This 
is done by setting it up in the BIOS settings (F12 during startup screen).

Once the OS is started, use module to load up everything you need ::

	module load scipy
	module load sda/0.8

You will need some information later which is easiest to get hold of now so do the following and
copy the result apart from the "sda" on the end ::

	which sda

you will also need to get the following path ::
	
	which python

Now you are ready to launch SDA ::

	sda

To set up Python, go to 'Window->Preferences->Pydev->Interpreter - Python' and click on new. In the
Interpreter name field enter the name 'Python' and in the 'Interpreter Executable' field paste the
result of your 'which python' command from earlier. Finally click apply and then OK once it has
finished. 

To make a Pydev project work well with the scisoft functionality, there is a small addition to
make, on the project right click and select properties. Then select 'PyDev - PYTHONPATH' and, in the
'External Libraries' tab, click 'Add source folder' and browse to the directory
'/copied/from/which/command/release/plugins/uk.ac.diamond.scisoft.python_1.0.0' and click OK.

If you have a Pydev project the Python console should pick up the configuration correctly. However,
if it does not, you can make the console work in the following way: Open the console view
('Window->Show View->Console') and in the far right drop-down menu in the console view ('Open Console')
click on Pydev Console. Then select Python console from the available list and click OK. Finally
there is a single line of code which needs to be run (this can be made easier by enabling a new
feature for path completion, 'Window->Preferences->Pydev->Interactive Console->Completions' and check
both 'Enable completions' and 'Request completions'). The command to enable python is ::

	sys.path.append('/copied/from/which/command/
		release/plugins/uk.ac.diamond.scisoft.python_1.0.0')

Then you should be able to use ::

	import scisoftpy as dnp


Setup on Windows machines
-------------------------

First make sure the GPU is the default option used for the display. Go to ::

	Control Panel > Appearance and Personalization > GPU Control Panel

'GPU Control Panel', if the GPU is an NVidia, would be called 'NVidia Control Panel'. Go in the 3D 
global settings, check that the preferred graphics processor is set on the 'GPU processor' instead 
than on 'integrated graphics' or 'auto-select'. Click on 'Apply'.

Once this is done, get a good install of python, we suggest getting a full install from  ::

	https://www.enthought.com/products/epd.php

And Install this package. Then copy SDA off the CD and unzip it wherever you would like to use it.
You should then be able to run the application by double-clicking on the scisoft.exe inside the
unzipped client directory.

To set up Python, go to 'Window->Preferences->Pydev->Interpreter-Python' and click on new. In the
Interpreter name field enter the name 'Python' and in the 'Interpreter Executable' field browse to
the location of your epd python install (something like c:/Python27). Finally click apply and then
OK once it has finished. 

To make a Pydev project work well with the scisoft functionality, there is a small addition to
make, on the project right click and select properties. Then select 'PyDev - PYTHONPATH' and, in the
'External Libraries' tab, click 'Add source folder' and browse to the directory
'/where/you/extracted/the/zip/client/plugins/uk.ac.diamond.scisoft.python_1.0.0' and click ok.

If you have a Pydev project the Python console should pick up the configuration correctly. However,
if it does not, you can make the console work in the following way: Open the console view
('Window->Show View->Console') and in the far right drop-down menu in the console view ('Open Console')
click on Pydev Console. Then select Python console from the available list and click OK. Finally
there is a single line of code which needs to be run (this can be made easier by enabling a new
feature for path completion, 'Window->Preferences->Pydev->Interactive Console->Completions' and check
both 'Enable completions' and 'Request completions'). The command to enable python is ::

	sys.path.append('/where/you/extracted/the/zip/
		client/plugins/uk.ac.diamond.scisoft.python_1.0.0')

Then you should be able to use ::

	import scisoftpy as dnp


Graphics issues
---------------

As SDA tries to use hardware acceleration as much as possible, sometimes there can be issues when
your graphics card incorrectly identify itself to the program. This will become apparent when
using the program if any of the plots are empty or incredibly slow and unresponsive. If this is the
case then there are 2 other levels of graphics card capability which can be tried to make sure that
you can still use the majority of SDA's functionality. To activate either levels you need to
start SDA with one of the following commands ::

	scisoft -vmargs -Duk.ac.diamond.analysis.rcp.plotting.useGL13=True
	scisoft -vmargs -Duk.ac.diamond.analysis.rcp.plotting.useSoftware=True

The first will stop SDA using more modern graphics card features, this will disable some
features and reduce the performance of some others. The second will move to software rendering, all
plotting will be slower, but should still work with no other issues.


Using Pydev
-----------

Now we have a working python environment, we can make use of some of the nice python functionality.
Let us first create a python project: In the Pydev Package Explorer view right click and select
'New->Project' then select 'Pydev->Pydev Project'. Fill in the information that is needed, and then
click finish. Now that the package is created, we can write some code, let's start with something
simple. Right click on the "src" directory of the new project and click on 'New->Pydev Module'. In
the wizard dialogue give this the name "hello" and select 'Module: Main' from the template list,
then click finish (leave the Package field blank). The new file is then opened in the editor area
and you're ready to go, so replace pass with ::

	print("Hello World")

Now we have a script that's ready to go, so, to run it, find its name in the Project Explorer,
right click and select 'Run As->Python Run'. The console should then pop to the front and show you
the text "Hello World".

Now that we have a basic script, let's look at using some python packages to keep your code nice
and tidy, and also introduce some of the "scisoftpy" functionality. Right click on the "src" folder
and select 'New->Python Package' and enter the name "plotting". In the Project Explorer, right
click on the package "plotting" and select 'New->Pydev Module', then give the name "plottest" and
select 'Module: Class' from the drop-down menu. The new file should have opened, and there will be
several sections to change in the template. Change the class name from "MyClass" to "PlotClass" and
then fill out the class to look like the following ::

	import scisoftpy as dnp

	class PlotClass(object):
		'''
		Class to do some plotting
		'''
		
		def __init__(self):
			'''
			Null constructor (does nothing)
			'''
			pass
			
		def plot_1d(self):
			x = dnp.arange(0,10,0.1)
			y = dnp.sin(x)
			z = dnp.cos(x)
			dnp.plot.line(x,[y,z])
			
		def plot_stack(self):
			x = dnp.arange(0,10,0.1)
			y = dnp.sin(x)
			z = dnp.cos(x)
			dnp.plot.stack(x,[y,z])
			
		def plot_2d(self):
			im = dnp.random.rand(100,100)
			dnp.plot.image(im)
			
		def plot_surf(self):
			im = dnp.random.rand(100,100)
			dnp.plot.surface(im)
			
Now that this class is set up to do some plotting for us, let's make use of it in our hello script,
so open this and change the code to the following ::

	from time import sleep
	from plotting.plottest import PlotClass   # This is where we bring in the class we have just made
	
	if __name__ == '__main__':
		print("hello world")
		pc = PlotClass()
		pc.plot_1d()
		sleep(2)
		pc.plot_2d()
		sleep(2)
		pc.plot_stack()
		sleep(2)
		pc.plot_surf()

Now open the 'Plot 1' view, which is where all this output will go by clicking
'Window->Show Plot View->Plot 1'. Then run the hello script and watch the output, if any of the
screens show nothing then it might be wise to check the "Graphics Issues" section above.


Debugging
---------

A very powerful feature of Pydev is its debugging functionality, we will use the example above to
demonstrate this. In the hello.py script, right click in the left margin next to the line ::

	print("hello world")

And select 'Add Breakpoint' from the drop-down menu. Now instead of using the run command, right
click on the file and select 'Debug As->Python Run'. This should then show a screen suggesting that
you move to the Debug perspective, click yes to this. You should see that the script has now paused
on the line you specified earlier, and you can now look at what's going on inside your script. The
Variables window lets you look at all the classes and variables that are alive at this point in
the script and you can then use the buttons in the debug view to move through the code a step at
a time or go into or out of functions. There are also key shortcuts: for example, if you press
(function key) F6, this will step you on one, and you should see the console output appear. If you
then press F5 you will follow the code into the constructor of your "PlotClass", F6 will then
return you back to the script as the construction is completed. Open the 'Plot 1' window on this
perspective and you can watch the plots appear as you step over them with F6, or F5 to look inside
the functions you wrote in the class. If you get in too deep and want to come out, F7 will return
you up a level, so you can keep pressing this until you get back to somewhere you recognise.
Finally if you want to just keep going till the next breakpoint, press F8.

SVN
---

Version control is an important part of working as a team whilst developing software, it can
promote cooperation and help with code sharing. For this example we will check out some code from
the scientific software repository. In Eclipse change to the SVN perspective 'Window->Open 
Perspective->Other->SVN Repository Exploring'. In the 'SVN Repositories' view right click and
select 'New->Repository Location'. Enter the following into the url field ::

	https://svn.diamond.ac.uk/svn/scientific_software/scripts/

You may be prompted for your username (FedID) and password. This should then populate a tree, if
you expand this you should arrive at a directory called "training". Right click on "training" and
select checkout, in the following window click OK. Go back to the Pydev perspective, and you should
now see newly checked-out "Training" project. The PYTHONPATH setting may need changing to point to
your local installation of 'uk.ac.diamond.scisoft.python'.

