# 11/03/2003 Mike Miller on mcmvig13
#
# CPython script to backup using glob, command line ZIP and TFILE32
# Each entry in "treeList" has all files matched recursively from "searchList"
# The zip file name has the current date and time string appended
# NB this will not fully work with JPython which has no "os.system"
#
#************************************************************************
# Change these lists to what you need (see info at base of file)
#************************************************************************
# no of tree and search lists, named treeList1, searchList1 etc.
lists = [1]

# search list 1 directory tree root (excludes "*.z*" i.e. zip files)
# exclude list 1 
treeList1=[r"e:\mem"]
searchList1=["*.[!z]*"]
excludeList1=[r"e:\mem\mpw6.2\data",r"e:\mem\mpw6.2\data\old",r"..\RCS"]

treeList2=[r"e:\WINNT\Profiles\mem"]
searchList2=["java.properties*"]

# a non-empty message will be sent via station_email.exe
# set 1 to display search messages, 2 for all messages
# set 0: keep zip name asis or 1: append date/time
dateStampZipName=0
debug = 2
emailMessage = ""
#
#************************************************************************
# Change these defaults either here or override as 2 command line arguments
#************************************************************************
#
# default station id is "" i.e. none for no transfer)
# default zip name is test (".zip" is added later)					
zipName="mcmvig13"
stationID=""
#************************************************************************
#
# Python imports
import glob
import os
import sys
import time
import string
import re
#
# module level variables
# current directory exclude list
# current search list as a copy of searchList1,2,etc
# current directory list as a copy of treeList1,2,etc
# list file containing files for zip to write
# list buffer containing files for zip write
matchedList=[]
fileBuffer=[]
startDir=os.getcwd()
zipListFile=startDir + os.sep + "zip.lis"
treeList = []
searchList = []
excludeList = []

# return as append to input string
# remove colons (invalid for Windows filename)
# replace spaces with underscores
# get lower case date and time string
# function to return filename + appended date/time
def appendDate (fileName):
   tstr = string.lower(time.ctime(time.time()))
   tstr = re.sub(' ', '_', tstr)
   tstr = re.sub(':', '', tstr)
   return (fileName + "_" + tstr)

# display current search settings
def showTargets ():
   writeMessage ("", 0, 0)
   writeMessage ("=== Backing up from       : " + repr(treeList), 0, 0)
   writeMessage ("    to zip and station ID : " + zipName + ".zip " + stationID, 0, 0)
   writeMessage ("    using match strings   : " + repr(searchList), 0, 0)
   writeMessage ("    this may take several minutes .......", 0, 0)
   return

# function to print string to screen. wait if pause
def writeMessage (msgStr, pause=0, exit=0):
   print msgStr
   if pause:
      print ">>> Press any key to continue : ",
      line = sys.stdin.readline()
   if exit:
      sys.exit()
   return

# function called by os.path.walk in each directory
def visitFunc (arg, dirName, names):
   global fileBuffer
   # set to current working directory for exclusion of e.g. "..\CVS"
   os.chdir (dirName)

# skip any directory which matches an excluded tree path
   for excludeName in excludeList:
      try:
         if os.stat(dirName) == os.stat(excludeName):
            if debug>1: print "excluding directory : %s" % dirName
            return
      except:
         pass

# save matched full file path to buffer
# match each directory file with search list
   if debug>1: print "searching in directory : %s" % dirName
   for searchStr in searchList:
      foundList = glob.glob (dirName + os.sep + searchStr)
      for foundName in foundList:
         fileBuffer.append (foundName + "\n")
         if debug: print "matched file : %s" % foundName
   return

# fatal error if write 
# get input zip name and station ID strings
# must be 0 or 2 command line arguments
# counts no. of user command arguments
nargs = len(sys.argv)-1
if nargs <= 0:
   pass
elif nargs == 2:
   zipName=sys.argv[1]
   stationID=sys.argv[2]
else:
   writeMessage ("!!! there must be 2 or no input arguments, " + repr(sys.argv), 1, 1)

# create unique name + date/time stamp
zipName = startDir + os.sep + zipName
if dateStampZipName:
   zipName = appendDate (zipName)

# default is empty exclude list
# copy next exclude list to default name if present
# copy next search list to default name
# copy next tree list to default name
# use each tree and search list in turn
for listNo in lists:
   exec "treeList = treeList%s" % (listNo)
   exec "searchList = searchList%s" % (listNo)
   try:
      exec "excludeList = excludeList%s" % (listNo)
   except:
      exec "excludeList%s = []" % (listNo)

# recurse each tree and search for searchList matches
# display next targets
   showTargets()
   for treeName in treeList:
      os.path.walk(treeName, visitFunc, "")

# write list file for zip
try:
   output = open (zipListFile, "w")
   output.writelines (fileBuffer)
   output.close()
except:
   writeMessage ("!!! error writing zip list file " + zipName, 1,1)

# execute the zip using list file as input stream and save file to
os.system ("zip -u %s -@ < %s" % (zipName, zipListFile))

# send email if message specified
# send zip to target station id using tfile
# write zip file into tfile general target file
if len(stationID)>0:
   writeMessage ("=== sending zip file '%s' to station id '%s'" % (zipName, stationID), 0, 0)
   os.system ("dir /b %s.zip > tfile.lis" % zipName)
   os.system ('tfile32 g srbac2 %s "" b overwrite' % stationID)
   if emailMessage != "":
      os.system ('station_email %s' % emailMessage)

# that's all folks
writeMessage ("=== backup finished processing '%s.zip'" % zipName, 0, 1)

# help for backup.py
# 
# by default "lists=[1]" specifies a single directory tree list (suffix 1) and single file name list (suffix 1)
# edit the internal variable "treeList1" to set your search directories 
# edit the internal variable "searchList1" to set your files to backup (with Unix wildcards - see below)
# for extra lists, change "lists" to e.g. [1,2] and add treeList2 and searchList2 etc.
# optionally set the internal variable "dateStampZipName=1" to time stamp the zip file name (makes unique)
# "debug" can be set to show just files found (=1) or directories searched as well (=2)
#
# if you don't want to use input command line arguments then :
#
# edit the internal variable "zipName" to set the default zip base file name (no ".zip" needed)
# edit the internal variable "stationID" to set the target SRS station ID for TFILE to send the zip to
# after a successfull tfile transfer, a station_email message can be set in the variable "emailMessage"
#
# with Python 1.52+ installed (NOT Jpython or Jython) invoke as follows :
# 1) command : python backup.py [<zip base name> <station ID for TFILE transfer>]
# 2) set shortcut to backup.py and add optional arguments if required
# 3) double click on backup.py to execute with no arguments (using internal defaults)
#
# e.g. python backup.py mike lb19
#      will generate "mike.zip" and try to TFILE it to station ID "lb19"
#
# Unix Wildcards supported in by "searchList" :
# 
#	*      matches everything
#	?      matches any single character
#	[seq]  matches any character in seq e.g. "r[1-9]*.dat" gets SRS runnumber files but not "runnum.dat"
#	[!seq] matches any char not in seq
#
# to exclude specific types use e.g. "*.[!z]*" will exclude any files with the extension ending ".z*"
# NB Do not use "*.*[!con]" as this excludes *.mac files or "*.*[!c][!o][!n]" which has more subtle problems.
#
# to exclude each directory which would otherwise be searched, add entries in an optional
# list called e.g. excludeList1 to correspond to the treeList1 tree search list. To exclude
# all instances of directories called e.g. RCS, specify name as r"..\RCS".
