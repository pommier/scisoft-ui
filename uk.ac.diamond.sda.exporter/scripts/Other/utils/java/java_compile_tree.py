# 09/10/2003 Mike Miller on mcmvig8
#
# Compiles all Java files in a directory tree using OO and
# observer-observable plus command pattern approach.
# now skips compilation if a newer class file exists.
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
dirExcludeList = [r"..%sCVS" % os.sep,r"..%sRCS" % os.sep, \
                 r"..%s..%sCVS%sBase" % (os.sep, os.sep, os.sep)]
# list of directories Java nowarnings compilation is needed
javaNoWarnDirs = [r"..%sorbacus" % os.sep ,r"..%simpl" % os.sep]
#************************************************************************

# observer class here to find and compile java files found
class commandObserverJavaCompile:

   # generate command object, register as observer then make observable
   def __init__ (self, dirStart, dirExcludeList, fileSearch, javaNoWarnDirs):
      cmdObj = walktree.CommandBase ()
      cmdObj.addObserver (self, ["DIR_FOUND"])
      self.javaNoWarnDirs = javaNoWarnDirs
      walktree.WalkDirTreeObservable (cmdObj, dirStart, dirExcludeList, fileSearch)
      
   # callback function
   def commandExec (self, command, observable):
      # compile Java files with nowarnings if it's a nowarnings directory
      nowarnings = 0
      for noWarnDir in self.javaNoWarnDirs:
         try:
            if os.stat(observable.currentDir) == os.stat(noWarnDir):
               nowarnings = 1
               break
         except:
            if debug>1: print traceback.print_exc()
            pass

      for sourceFile in observable.newFilesFoundList:
      # 
         classFile = os.path.splitext(sourceFile)[0] + ".class"
         # recompile if class file is greatern or can't be found
         try:
            javaFileModTime = os.stat (sourceFile)[8]
            classFileModTime = os.stat (classFile)[8]
            if classFileModTime < javaFileModTime:
               raise "compilation needed"
            else:
               print "      %s - up to date" % sourceFile

         except:
            if nowarnings:
               print "javac -nowarn %s" % sourceFile
               os.system ("javac -nowarn %s" % sourceFile)
            else:
               print "javac %s" % sourceFile
               os.system ("javac %s" % sourceFile)

# walk the tree and compile Java files
startDir = "."
if len(sys.argv) > 1:
   startDir = sys.argv[1]
commandObserverJavaCompile (startDir, dirExcludeList, "*.java", javaNoWarnDirs)
