# 14/03/2002 Mike Miller
#
# CPython script to replace Labwindows C tfilesync.py program to be used
# with tfile.py. It waits for tfile.py to write a semaphore specified by
# "semaphoreName" and then deletes the semaphore and exits. This allows
# e.g. Pincer to launch tfile first as an asynchronous command shell and
# tfilesync afterwards as a synchronous command shell. Thus Pincer will
# not carry on making new data files until the semaphore is written, which
# means that the file name lists for transferring have been fully built.
# A timeout prevents Pincer from blocking.
#
#************************************************************************
# Change these lists to what you need
#************************************************************************
# for future use if needed for extra messages
# maximum integer wait time for the semaphore to appear
# semaphore path name - must match that in tfile.py
semaphoreName = "\\mem\\bin\\TFILE_UPDATED"
timeOutSecs = 20
debug = 0
#
#************************************************************************
#
# Python imports
import sys
import os
import time
import string
#
# module level variables and functions

# main function to wait for semaphore with timeout
def waitForSemaphore ():
   writeMessage ("\n")
   writeMessage ("=== TFilesync utility (Python script)\n")
   writeMessage ("    waiting for tfile semaphore file : '%s' ..." % semaphoreName + "\n")

# stay in loop on exception
# tidy up and exit
# no exception if semaphore file opens
# check semaphore and sleep for 0.1 sec intervals
   for i in range(timeOutSecs*10):
      try:
         fp = open (semaphoreName, "r")
         fp.close()
         writeMessage ("=== successfully found semaphore\n")
         os.remove(semaphoreName)
         return
      except:
         pass
      time.sleep (0.1)
      
   writeMessage ("!!! TIMEOUT failing to find Tfile semaphore after ~ %d seconds\n" % timeOutSecs)
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

# code execution starts here (any exceptions seen here are fatal)
waitForSemaphore()
writeMessage ("=== tfileSync ends\n", 0, 1, 0)

