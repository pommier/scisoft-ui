# 10/03/2003 Mike Miller
#
# deletes all Java class files in a directory tree using OO and
# observer-observable plus command pattern approach.
# requires walktree.py module in same directory or Python path.
# Syntax :
# 1) python delete_class_tree.py
#       deletes .class files recursively from "." (current directory)
# 2) python delete_class_tree.py "\cvshome\java\dl"
#       deletes .class files starting from given input directory
#
# Python imports
import sys
import os
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
#************************************************************************

# observer class here to find and compile java files found
class commandObserverDelClass:

   # generate command object, register as observer then make observable
   def __init__ (self, dirStart, dirExcludeList, fileSearch):
      cmdObj = walktree.CommandBase ()
      # exclude "DIR_FOUND" callback events
      cmdObj.addObserver (self, ["DIR_FOUND"])
      walktree.WalkDirTreeObservable (cmdObj, dirStart, dirExcludeList, fileSearch)
      
   # callback function for diagnostics
   def commandExec (self, command, observable):
      for file in observable.newFilesFoundList:
         print "=== deleting '%s' ..." % file
         os.remove (file)

# walk the tree and delete class files
startDir = "."
if len(sys.argv) > 1:
   startDir = sys.argv[1]
commandObserverDelClass (startDir, dirExcludeList, "*.class")
