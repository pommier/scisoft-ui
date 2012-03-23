# 10/03/2003 Mike Miller
#
# Provides classes to walk down a given directory tree and notifying a
# command observer using the command pattern with the observer-observable.
# The classes are as follows :
#
# WalkDirTreeObservable (commandObserver, dirStart, dirExcludeList, fileSearch)
#    commandObserver is the command object (see CommandBase class below)
#    dirStart is the start directory of the tree e.g. "."
#    dirExcludeList is a list of directories to skip e.g. [r"..\RCS",r"..\CVS"]
#    fileSearch is a glob "unix" regular expression search string e.g. "*.java"
# CommandBase ()
#    creates a commandObserver object which notifies registered observers with
#    command events which they have elected to receive. It receives callbacks
#    to the method :
#    "notify (self, command, observable)"
#    Registered observers must provide the callback method :
#    "commandExec (self, command, observable)" 
#    and register using
#    "addObserver (self, commandObserverObj, commandExcludeList=[])"
#
#    This class can be extended if required
#    Thus the command string and the observable object is passed to the decoupled
#    observer.
# 
# This is used by java_compile_tree.py, delete_class_tree.py and others.
#
#************************************************************************
# Change these lists to what you need (see info at base of file)
#************************************************************************
# debugging : set 1 to display search messages, 2 for all messages
debug = 1
#************************************************************************

# Python imports
import glob
import os
import sys
import string
import traceback
#

# defines class to walk a directory tree, excludes specified directories and
# notifies the registered command object of directories and files matched.

class WalkDirTreeObservable:

   def __init__ (self, commandObserver, dirStart, dirExcludeList, fileSearch):
      self.commandObserver = commandObserver
      self.dirExcludeList = dirExcludeList[:]
      self.fileSearch = fileSearch
      self.currentDir = "."
      self.newFilesFoundList = []
      print "=== searching for '%s' in tree '%s', excluding : " % \
                                   (fileSearch, dirStart), dirExcludeList
      self.walkTree (dirStart)

   # callback when os.path.tree arrives in a new directory
   # exclude directory by making current and checking in exclude list
   def visitFunc (self, arg, dirName, fileNames):
      os.chdir (dirName)
      for excludeName in self.dirExcludeList:
         try:
            if os.stat(dirName) == os.stat(excludeName):
               if debug>=1: print "\n=== excluding directory : %s" % dirName
               return
         except:
            if debug>1: print traceback.print_exc()
            pass

      if debug>=1: print "\n=== dirName : ", dirName; print
      self.printList ("=== raw files : ", fileNames)

      # notify commandObservers that new directory has been found
      self.currentDir = dirName
      self.commandObserver.notify ("DIR_FOUND", self)

      # derive list of matched files using glob and notify if applicable
      self.newFilesFoundList = glob.glob (dirName + os.sep + self.fileSearch)
      if len(self.newFilesFoundList) > 0:
         self.printList ("=== new matched files : ", self.newFilesFoundList)
         self.commandObserver.notify ("FILES_FOUND", self)

   def walkTree (self, dirStart):
      os.path.walk (dirStart, self.visitFunc, "")

   def printList (self, title, lis):
      if debug>1:
         print title
         for name in lis:
            print name
         print

# defines a base command class which has the notify() callback invoked
# by the observable. It then notifies any commandObservers via commandExec()
# method with the command name and observable object ref.
#
class CommandBase:

   def __init__ (self):
      # dictionary of observable refs and their exclude list
      self.commandObserverDict = {}

   # adds observers
   def addObserver (self, commandObserverObj, commandExcludeList=[]):
      self.commandObserverDict[commandObserverObj] = commandExcludeList

   # notify callback from walkDirTree as it finds a new directory
   def notify (self, command, observable):
      if debug>1: print "command : command    : ", command
      if debug>1: print "command : observable : ", observable; print

      # notify all commandObservers by calling commandExec() skipping
      # commands which observers have excluded.
      for commandObserverObj in self.commandObserverDict.keys():
         if command not in self.commandObserverDict[commandObserverObj]:
            commandObserverObj.commandExec (command, observable)

# debug observer for use with the test() function
class commandObserverDebug:

   # generate command object, register as observer then make observable
   def __init__ (self, dirStart, dirExcludeList, fileSearch):
      cmdObj = CommandBase ()
      cmdObj.addObserver (self)
      WalkDirTreeObservable (cmdObj, dirStart, dirExcludeList, fileSearch)
      
   # callback function for diagnostics
   def commandExec (self, command, observable):
      print "commandObserverDebug : command    : ", command
      print "commandObserverDebug : observable : ", observable; print
      if command == "DIR_FOUND":
         print "commandObserverDebug found directory : ", observable.currentDir
      elif command == "FILES_FOUND":
         print observable.newFilesFoundList

# test function to walk the current tree finding all files
def test (startDir="."):
   currentDir = os.getcwd()
   try:
      commandObserverDebug (startDir, [], "*.*")
   finally:
      os.chdir (currentDir)
