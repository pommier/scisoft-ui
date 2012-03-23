# 01/04/2003 Mike Miller on mcmvig13
#
# Compiles all Java files in a directory tree using OO and
# observer-observable plus command pattern approach.
# Requires walktree.py module in same directory or Python path for import
# Syntax :
# 1) python java_compile_tree.py
#       compiles recursively starting from "." (current directory)
# 2) python java_compile_tree.py "\cvshome\java\dl\util"
#       compiles recursively starting from given input directory
#
# Python imports
import os
import sys
import traceback
#
# my imports
import walktree

#************************************************************************
# Change these lists to what you need (see info at base of file)
#************************************************************************
# debugging : set 1 to display search messages, 2 for all messages
debug = 1
# list of directories to skip over (uses os.sep for Unix/PC portability )
dirExcludeList = [r"..%sCVS" % os.sep,r"..%sRCS" % os.sep]
# full path to output dl.jar
thisDir=os.getcwd()
jarFile=thisDir + os.sep + "dl.jar"
#************************************************************************

# observer class here to find and compile java files found
class commandObserverJavaCompile:

   # generate command object, register as observer then make observable
   def __init__ (self, dirStart, dirExcludeList, fileSearch):
      cmdObj = walktree.CommandBase ()
      cmdObj.addObserver (self, ["DIR_FOUND"])
      walktree.WalkDirTreeObservable (cmdObj, dirStart, dirExcludeList, fileSearch)
      
   # callback function
   def commandExec (self, command, observable):
      # add to jar file
      for sourceFile in observable.newFilesFoundList:
         os.system ("jar -uvf %s %s" % (jarFile, sourceFile))

startDir = "\cvshome\java\dl"
# use one file to create the archive
os.system ("jar -cvf dl.jar %s/util/Debug.class" % startDir)
# now refresh the jar with class files from the walked dl tree
commandObserverJavaCompile (startDir, dirExcludeList, "*.class")
