# 12/03/2002 Mike Miller
#
# CPython script to replace Labwindows C Tfile program for transfer
# of SRS and other data types to central storage via ftp.
# The fileLists are non-recursive and represent a single ftp session
# NB this will not fully work with JPython which has no "os.system"
#
#************************************************************************
# Change these lists to what you need (see info at base of file)
#************************************************************************
# specify search lists, named fileList1, ftpList1 etc.
lists = [1,2]
fileList1=["data\\r[1-9]*.dat"]

ftpList1=["srbac2", "lb19", "", "", "", 2] 

# station runnumber file (staged)
fileList2=["\\srs\\pincer\\bin\\runnum.dat"]
ftpList2=["srbac2", "lb19", "", "", "overwrite", 0] 

# set 1 to display search messages, 2 for all messages
debug = 0

# test mode to get each file sent as #name for comparison
safeMode = 0

# semaphore path name written during execution and deleted after
# semaphore path name written after transfer lists built
# list of rename mode failures for retrying (file name & mode)
# single central log file path name
logFileName = "\\mem\\bin\\tfile.log"
backlogName = "\\mem\\bin\\tfile_backlog.log"
updateSemaphoreName = "\\mem\\bin\\TFILE_UPDATED"
runningSemaphoreName = "\\mem\\bin\\TFILE_RUNNING"

# timeout in secs for running semaphore to be assumed stale
runningTimeout = 60*30
#
#************************************************************************
#
# Python imports
import glob
import sys
import os
import time
import string
import ftplib
import shutil
#
# module level variables and functions
# new backlog mode list (integer)
# new backlog name list (file sent but failed renme_mode op)
# old backlog mode list (integer)
# old backlog name list (file sent but failed renme_mode op)
# object managing log file operations
# make some boolean constants
true=1
false=0
logFileObj = ""
backlogNameList = []
backlogModeList = []
newBacklogNameList = []
newBacklogModeList = []

# an exception here shows no running semaphore found
# get last modified date (if any) of running semaphore
# ensure this is the only active instance of tfile running
def checkUnique ():
   try:
      semDate = os.path.getmtime (runningSemaphoreName)
   except:
      return

# if age of running semaphore older than timout, ignore semaphore
   if time.time()-semDate > runningTimeout:
      return

# exit as already running actively
   writeMessage ("=== Tfile already running - finished\n", 0, 1)

# ignore errors and return an empty list
# extract name string and mode as integer from line
# split each line to list of string fields
# open and load backlog file names and modes into list
# updates lists of backlogName files and modes
def getBacklogFiles ():
   global backlogName, backlogNameList, backlogModeList
   backlogNameList = []
   backlogModeList = []
   try:
      fp = open (backlogName, "r")
      backlogList = fp.readlines()
      fp.close()
      for line in backlogList:
         lineLis = string.split(line)
         backlogNameList.append(lineLis[0])
         backlogModeList.append(string.atoi(lineLis[1]))
   except:
      try:
         fp.close()
      except:
         backlogNameList = []
         backlogModeList = []

   writeMessage ("=== backlogFile file entries found : " + repr(backlogNameList) + "\n", 0, 0, 1)
   writeMessage ("=== backlogFile mode entries found : " + repr(backlogModeList) + "\n", 0, 0, 1)
   return

# add value if there is one (i.e. non-string exception)
# build message of type in string form
# obtain exception type and value
# build message string from exception type and value
def getExceptionMsg ():
   type = sys.exc_type
   value = sys.exc_value
   msg = "!!! ERROR : " + str(type)
   if value == None:
      return msg + "\n"
   else:
      return msg + " : " + str(value) + "\n"

# return processed date/time stamp
# get date and time string
# function to return date/time stamp
def getDate ():
   tstr = time.ctime(time.time())
   return (tstr)

# add failed entries to new lists
# show errors but ignore them
# process local file using rename_mode
# obtain path and base name for file
# attempt to redo the rename_mode op
# flag if list has been modified
# rebuild lists during retry
# using backlog lists retry and write back failed entries
def retryBacklog ():
   global backlogNameList, backlogModeList
   newNameList = []
   newModeList = []
   listChanged = false
   for i in range(len(backlogNameList)):
      try:
         fileName = backlogNameList[i]
         path, baseName = os.path.split(fileName)
         tidyLocalFile(fileName, path, baseName, backlogModeList[i])
      except:
         writeMessage (getExceptionMsg())
         listChanged = true
         newNameList.append(backlogNameList[i])
         newModeList.append(backlogModeList[i])
         continue

# update backlog file if necessary
   if listChanged:
      writeBacklogFile (newNameList, newModeList)
   return

# display current search settings
def showTargets (fileList, foundList):
   writeMessage ("\n")
   writeMessage ("=== TFile file transfer utility (Python script)\n")
   writeMessage ("    using data file match list   : " + repr(fileList) + "\n")
   writeMessage ("    has found new files          : " + repr(foundList) + "\n")
   return

# process local file according to rename_mode (integer)
def tidyLocalFile (fileName, path, baseName, rename_mode):

# rename extension to .con
   if rename_mode==1:
      writeMessage ("=== renaming local %s extension to .con\n" % fileName)

# empty file path requires no leading separators
# move to old subdirectory (copy then delete)         
   elif rename_mode==2:
      writeMessage ("=== copying local %s to 'old' subdirectory\n" % fileName)
      if path == "":
         shutil.copy (fileName, "old")
      else:
         shutil.copy (fileName, path + os.sep + "old" + os.sep + baseName)

      writeMessage ("=== deleting local %s\n" % fileName)
      os.remove(fileName)

# rename to <full_name>.con
   elif rename_mode==3:
      writeMessage ("=== renaming local %s to %s.con\n" % (fileName, fileName))
      os.rename(fileName, fileName + ".con")
   return

# carry on with a warning
# overwrite file completely		
# append line list to write		
# only append if wrtStr specifies it
# build file structure line by line as list
# list of lines for file appending
# writes new names and mode lists to backlogName
def writeBacklogFile (backlogNameList, backlogModeList, wrtStr=""):
   global backlogName
   lineLis = []
   try:
      for i in range(len(newBacklogNameList)):
         line = backlogNameList[i] + " %d\n" % backlogModeList[i]
         lineLis.append(line)
      if wrtStr == "append":
         fp = open (backlogName, "a")
      else:
         fp = open (backlogName, "w")
      fp.writelines(lineLis)
      fp.close()
   except:
      try:
         fp.close()
      except:
         logFileObj.write ("!!! WARNING : error appending to backlogFile '%s'\n" % backlogName)
         return

   writeMessage ("=== added to backlogFile file : " + repr(lineLis) + "\n", 0, 0, 1)
   return

# sleep here in case no command window for user input
# no newline terminator for compatibility with log files
# function to print string to screen. wait if pause
def writeMessage (msgStr, pause=0, exit=0, debugLevel=0):
   global debug
   if (debug >= debugLevel):
      print msgStr,
      if pause:
         time.sleep(5)
      if exit:
         sys.exit()
   return

# write empty file and close
# write semaphore file using given name
def writeSemaphore (semaphoreName):
   writeMessage ("=== writing local semaphore file %s\n" % semaphoreName)
   filePtr = open (semaphoreName, "w")
   filePtr.close()
   return

# Object Classes defined

# connect and login
# make an ftplib FTP object
# file rename mode (see base of this file)
# reserved keyword to add
# transfer mode - "binary" or default ASCII
# password
# user id
# unpack input paremeter list - machine address
# constructor gains and stores ftp parameters
# wrapper for Python ftplib FTP class
class MYFTP:
   def __init__ (self, ftpList):
      self.machine = ftpList[0]
      self.id      = ftpList[1]
      self.passwd  = ftpList[2]
      self.mode    = ftpList[3]
      self.keyword = ftpList[4]
      self.rename_mode = ftpList[5]
      self.ftpObj = ftplib.FTP ()
      self.logon ()
      return

# this exception is raised if remote file doesn't exist
# non overwrite mode skips this file via exception
# overwrite mode needs target file deleting first
# no exception shows file exists so raise exception
# ensure target file does not exist (exception if does)
   def checkFileExists(self, baseName):
      try:
         self.ftpObj.rename (baseName, baseName)
         if self.keyword == "overwrite":
            self.ftpObj.delete (baseName)
            logFileObj.write ("=== remote file deleted %s (overwrite mode set)\n" % baseName)
         else:
            raise "file '%s' already exists on server" % baseName
      except ftplib.error_perm:
         pass
      return

# compare 2 files : return 1 if same, 0 if different
   def checkFilesSame(self, f1, f2):
      bufsize = 8192
      fp1 , fp2 = open(f1, 'rb'), open(f2, 'rb')
      while 1:
         b1, b2 = fp1.read(bufsize), fp2.read(bufsize)
         if b1!=b2: return 0
         if not b1: return 1

# wrap in Python data specifier for ftplib
# if password empty, generate SRS default
# keeps password if specified or builds SRS default
   def getPasswd (self):
      if self.passwd == "":
         pw = "oDknI"
         pw1 = ""
         for i in range(1,6):
            pw1 = pw1 + chr(ord(pw[i-1])-i-1)
            pw2 = chr(91) + pw1 + chr(93)
            self.passwd = pw2
      return

# fetch password
# perform ftp connect and login
   def logon (self):
      self.getPasswd()
      logFileObj.write ("=== connecting to %s with id %s\n" % (self.machine, self.id))
      self.ftpObj.connect (self.machine)
      self.ftpObj.login (self.id, self.passwd)
      return

# perform ftp logoff
   def logoff (self):
      self.ftpObj.close()
      return

# rename to final target name
   def remameTarget(self, tempName, baseName):
      writeMessage ("=== renaming remote file %s to %s\n" % (tempName, baseName))
      self.ftpObj.rename(tempName, baseName)      
      return

# write file to disk
# ASCII ftp "get" safe mode callback handler
   def safeModeAsciiCallBack (self, string):
      self.safeFp.write(string + "\n")
      return

# write binary data block to disk      
# binary ftp "get" safe mode callback handler
   def safeModeBinaryCallBack (self, string):
      self.safeFp.write(string)
      return

# create new local temporary compare file
# get ASCII lines (no <cr><lf> using my callback
# create new binary local temporary compare file
# construct temporary file name as <name>_tmp
# compare sent file if safe mode set
# carry out any safe mode checks here
   def safeModeCheck (self, fileName, baseName):
      if safeMode:
         self.safeName = fileName + "_tmp"
         try:
            writeMessage ("=== safe mode set - sucked back file %s for compare\n" % self.safeName)
            if self.mode == "binary":
               self.safeFp = open (self.safeName, "wb")
               self.ftpObj.retrbinary ("RETR %s" % baseName, self.safeModeBinaryCallBack) 
            else:
               self.safeFp = open (self.safeName, "w")
               self.ftpObj.retrlines  ("RETR %s" % baseName, self.safeModeAsciiCallBack) 

# pass exception up to give more error details
# ensure local file closed
# tidy up temporary file
# compare here
# ensure local file closed
            self.safeFp.close()
            writeMessage ("=== comparing files '%s' and '%s'\n" % (fileName, self.safeName))
            if not self.checkFilesSame (fileName, self.safeName):
               raise "files %s and %s failed safe mode comparison check, aborting" % (fileName, self.safeName)
            logFileObj.write ("=== files %s and %s compare ok in safe mode\n" % (fileName, self.safeName))
            os.remove(self.safeName)
            writeMessage ("=== compare file '%s' deleted\n" % self.safeName)
         except:
            self.safeFp.close()
            logFileObj.write ("!!! error in safe mode retrieve/comparison of '%s'\n" % fileName)
            raise
      return

# delete possible remote temporary file first
# ftp send given file as temporary .<name>
   def sendTemp(self, fileName, tempName):
      try:
         self.ftpObj.delete(tempName)
         logFileObj.write ("!!! warning : remote temporary file '%s' already existed\n" % tempName)
      except:
         pass

# always close ftp file
# open local file
# open local file in binary mode
# send as ASCII (default) or binary if specified
      writeMessage ("=== sending local %s to remote %s\n" % (fileName, tempName))
      try:
         if self.mode == "binary":
            fp = open (fileName, "rb")
            logFileObj.write ("=== binary transfer mode set\n")
            self.ftpObj.storbinary ("STOR %s" % tempName, fp, 512)
         else:
            fp = open (fileName, "r")
            self.ftpObj.storlines ("STOR %s" % tempName, fp)
      finally:
         fp.close()
      return

# close file for now
# check file will open ok
# number of writes so far      
# number of writes before buffer flush to file
# create a Queue to buffer the file writes
# store the quiet mode flag (no messages if true)
# store the file name
# constructor - needs file name and flag for no screen output
# class to manage log file using buffered output
class LOGHANDLER:
   def __init__ (self, fileName, quiet=0):
      self.fileName = fileName
      self.quiet = quiet
      self.buffer = QUEUE()
      self.nWriteFlush = 10
      self.nWrites = 0
      filePtr = open (self.fileName, "a")
      filePtr.close()
      return

# clear buffer write count
# delete the buffer
# now close file
# write all buffer onto file
# open file for append
# write buffer to file and reset counters
   def flush(self):
      filePtr = open (self.fileName, "a")
      filePtr.writelines(self.buffer.data)
      filePtr.close()
      self.buffer.delete()
      self.nWrites = 0
      return

# also send messages to screen to avoid duplicate calls
# if quiet mode not set,
# flush buffer if max write count reached
# increment write count
# write to the buffer
# write output line to buffer and flush every nWriteFlush
   def write (self, string):
      self.buffer.add(string)
      self.nWrites = self.nWrites + 1
      if self.nWrites >= self.nWriteFlush:
         self.flush()
      if not self.quiet:
         writeMessage (string)
      return

# return the no. of objects in the queue
# if no items, return an empty list
# extract the next queued item
# delete the whole queue
# returns copy of next item (i.e. can act then as a buffer)
# add a new item to the list
# an empty list
# class to manage a FIFO global queue (an object list)
class QUEUE:
   def __init__ (self):
      self.data = []
   def add (self, data):
      self.data.append(data)
   def copy (self):
      return self.data[0]
   def delete (self):
      self.data = []
   def extract (self):
      if len(self.data)==0:
         return []
      else:
         item = self.data[0]
         del self.data[0]
         return item
   def size (self):
      return len(self.data)

# object to handle each tfile file list/ftp session
class TFILE:

# obtain list of new files to send
# take deep copy of file and ftp lists
# generate transfer list for given file and ftp list
   def __init__ (self, fileList, ftpList):
      self.fileList = fileList[:]
      self.ftpList = ftpList[:]
      self.foundList = self.findFiles(self.fileList)
      return

# match each file with input list (wildcard support)
# returns a list of all files matching input list
# NB skip files which are of zero size
   def findFiles (self, fileList):
      global backlogNameList, backlogModeList
      foundList = []
      writeMessage ("=== searching for files in list : %s\n" % repr(fileList), 0, 0, 2)
      for file in fileList:
         foundNames = glob.glob (file)
         for name in foundNames:
            fileSize = os.path.getsize (name)
            if fileSize > 0:
               if self.ftpList[4] == "overwrite" or name not in backlogNameList:
                  foundList.append(name)
                  writeMessage ("=== matched file : %s\n" % name, 0, 0, 1)
      return foundList

# logoff the ftp session
# add file and rename_mode to new backlog lists
# build and display exception message
# process local file using rename_mode
# flag a sucessful transfer
# carry out safe mode comparison if specified
# rename temporary file to final target name
# send as temporary .<name>
# from here errors result in file not being sent
# ensure target file does not exist (exception if does)
# initialise flag for file sent successfully
# generate temporary target file name
# obtain path and base name for file
# logon to remote machine
# if no files to send skip ftp ops
# display files and peform the FTP transfer
   def transfer (self):
      global newBacklogNameList, newBacklogModeList
      showTargets(self.fileList, self.foundList)
      if len(self.foundList)==0: return
      ftpObj = MYFTP (self.ftpList)
      for fileName in self.foundList:
         path, baseName = os.path.split(fileName)
         tempName = ".%s" % baseName
         sentOk = true
         try:
            ftpObj.checkFileExists(baseName)
            sentOk = false
            ftpObj.sendTemp(fileName, tempName)
            logFileObj.write ("=== sent file '%s'\n" % fileName)
            ftpObj.remameTarget(tempName, baseName)
            ftpObj.safeModeCheck(fileName, baseName)
            sentOk = true
            tidyLocalFile(fileName, path, baseName, self.ftpList[5])
         except:
            logFileObj.write (getExceptionMsg())
            if sentOk:
               newBacklogNameList.append(fileName)
               newBacklogModeList.append(self.ftpList[5])
            continue
      ftpObj.logoff()
      return

# code execution starts here (any exceptions seen here are fatal)

# signal transfer lists complete via update semaphore
# exit here if tfile is already running
try:
   checkUnique()
except:
   writeSemaphore (updateSemaphoreName)
   sys.exit()

# write time stamp to log file
# write new session marker to log file
# prepare log file
# write running semaphore file for tfile duration
try:
   writeSemaphore (runningSemaphoreName)
   logFileObj = LOGHANDLER (logFileName)
   logFileObj.write ("*" * 60 +"\n")
   logFileObj.write ("=== Tfile started on " + getDate() + "\n")

# make object for each list
# store list of transfer objects
# read names and modes from backlog file
   getBacklogFiles()
   tfileObjList = []
   for listNo in lists:
      exec "objRef = TFILE (fileList%s, ftpList%s)" % (listNo, listNo)
      tfileObjList.append (objRef)

# transfer lists complete so write semaphore file
   writeSemaphore (updateSemaphoreName)

# request each object transfers its files
   for objRef in tfileObjList:
      objRef.transfer()

# that's all folks
# clear running semaphore
# retry any failed backlog rename_op entries
# update backlog file with new entries
# tidy up
   logFileObj.flush()
   writeBacklogFile (newBacklogNameList, newBacklogModeList, "append")
   retryBacklog ()
   os.remove(runningSemaphoreName)
   writeMessage ("=== Tfile finished\n", 0, 1)

# normal exit exception is caused by writeMessage (0, 1)!
except SystemExit:
   sys.exit()

# that's all folks
# clear running semaphore
# tidy up
# build and display exception message
except:
   logFileObj.write (getExceptionMsg())
   logFileObj.flush()
   os.remove(runningSemaphoreName)
   writeMessage ("=== Tfile finished\n", 0, 1)

# help for tfile.py
# 
# edit the internal variable "fileList1" to set your files to backup (with Unix wildcards - see below)
# for extra lists, change "lists" to e.g. [1,2] and add fileList2 etc.
# "debug" can be set to show just files found (=1) or all debug messages (=2)
#
# with Python 1.52+ installed (NOT Jpython or Jython) invoke as follows :
# 1) command : python tfile.py
# 2) set shortcut to correspond to the above command
# 3) double click on tfile.py where the system has .py files associated with Python.bat
#
# e.g. python tfile.py
#
# Unix Wildcards supported in by "fileList" :
# 
#	*      matches everything
#	?      matches any single character
#	[seq]  matches any character in seq e.g. "r[1-9]*.dat" gets SRS runnumber files but not "runnum.dat"
#	[!seq] matches any char not in seq
#
# to exclude specific types use e.g. "*.*[!c][!o][!n]" will exclude any files with the extension ending
# in exactly ".con" only. NB Do not use "*.*[!con]" as this will exclude *.mac files !!
# Do not use "~" (tilda) as this is not supported.
#
# ftp rename modes :
# This is an integer specifying what tfile will do with the file once transfered ok
# 0=no action - the file is left asis (it will be transferred again next time)
# 1=rename file exension to .con, e.g. "r1.dat" becomes "r1.con"
# 2=move file to a subdirectory called "old" below where the file is currently situated
# 3=rename the file to <full_name>.con e.g. s1.bin becomes s1.bin.con
#
# Semaphore files :
# The "updateSemaphore" is written to signal that the lists of files to transfer have
# been constructed and it's therefore safe to create the new data files without them
# being transferred before they are completed. It is the responsibility of the script
# "tfilesync.py" to wait for and then delete this file and should be started after
# the asynchronous tfile.py running in synchronous mode.
# The "runningSemaphore" is normally created at the start of the script execution and
# deleted at the end. It prevents other tfile scripts running which may conflict. It has
# a running timeout which is the age after which the semaphore is assumed to be stale i.e.
# not correspond to an active tfile. This period should be quite long (many minutes). If
# a running semaphore is found, tfile exits but writes the update semaphore first to
# allow a waiting client to quickly carry on with e.g. further scanning.
#
# backlog of rename mode failures (file name set in backlogName)
#
# During the rename mode a file may be open which prevents it being renamed or moved.
# This log file keeps a record of the file names and rename_modes so they can be retried
# the next time tfile is run when the file may be freed up.
# As the file has been successfully transferred, it will not be resent next time by
# tfile unless the overwrite mode flag is set.
