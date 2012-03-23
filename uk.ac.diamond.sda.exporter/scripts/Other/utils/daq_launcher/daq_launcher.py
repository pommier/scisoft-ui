# 27/11/2002 Mike Miller
#
# CPython script to launch DAQ object server and Java applications in
# the correct order and preventing duplicates.
# Requires freeware "pskill.exe" (from sysinternals.com) in the program
# "PATH" environment variable.
# This is best run from a desktop shortcut invoking "daq_launcher.py"
# if .py files are registered or else "python daq_launcher.py"
#
import os
import sys
import time
#
# define friendly delay function
#
def settle_time (secs):
   timeLeft = secs
   print
   for i in range(secs):
      print "=== settling for %3d more seconds\r" % timeLeft,
      time.sleep (1)
      timeLeft = timeLeft -1
   print "=== settling for %3d more seconds\r" % timeLeft
#
#************************************************************************
# Change these commands to your invoke Java batch files +/- time delays
#************************************************************************
#
print """
***********************************************************************
27/11/02         SRCG JAVA Application Launcher for mpw6.2 (mcmvig13)
***********************************************************************

"""
#
# prompt for orderly shutdown of Java applications
print "=== Please exit/quit all Java applications then press <return> : ",
sys.stdin.readline()
print

# kill all Java applications which may remain
print "=== brutally stopping any remaining Java applications and windows ..."
os.system ("pskill java")
os.system ("pskill cmd")

# wait for Parker controller timeout/reset then start object server
settle_time (10)

# change working directory for JClam
os.chdir(r"\mem\mpw6.2")

# start Object Server
print "=== starting object server ..."
os.system (r"start /min E:\mem\bin\execObjectServer.bat")
settle_time (5)

# start JClam
print "=== starting JClam ..."
os.system (r"start E:\mem\bin\execJClam.bat")

# start any GUIs
print "=== starting GIUs ..."
