# 21/11/2002 Mike Miller
#
# CPython script "comment_fix" to tidy Python source comments and remove "%Id"
# lines. The 
# 
# Each entry in "treeList" has all files matched recursively from "searchList"
# and these are set from entries in turn from treeList1..n and searchList1..n
# and indexed by the mask list "lists"
#
#************************************************************************
# Change these variables to what you need
#************************************************************************
# no of each tree and search lists, i.e. 1 for treeList1, searchList1 etc.
lists = [1]

# list of directory tree roots to search in for files in searchListn
treeList1=["E:\\cvshome\\python"]

# Unix style file match string for each directory in the treeListn 
searchList1=["*.py"]

# set 1 to display some debug messages, 2 for all messages
debug = 0
#************************************************************************
#
# Python imports
import glob
import os
import sys
import string
import traceback
#
# module level variables
# current search list as a copy of searchList1,2,etc
# current directory list as a copy of treeList1,2,etc
treeList = []
searchList = []
hash = "#"


# returns index of substring matched in nonquoted line text
def findUnquoted (line, substring):
   str = line[:]
   nextIndex=0
   totalIndex=0
   foundIndex = -1

   try:
      if (string.find(line, "'") == -1) and (string.find(line, '"') == -1):
         return string.find(line, substring) 

# calculate position of match
# find substring in current unquoted string
# update next unquoted string search
# end of extracted unquoted strings
# if debugging on follow progress
      while (1):
         unquoteStr, nextIndex = getUnquotedStr(str[totalIndex:])
         if debug>1:
            print "findUnquoted: totalIndex, unquoteStr, nextIndex ", totalIndex, unquoteStr, nextIndex
         if (unquoteStr == ""):
            break
         totalIndex = totalIndex + nextIndex
         foundIndex = string.find(unquoteStr, substring)
         if (foundIndex != -1):
            foundIndex = totalIndex - len(unquoteStr) + foundIndex
            break
   except:
      if debug>1: print traceback.print_exc()
      return (-1)

   return foundIndex

# temporary quote string (single or double)
# single quotes string
# double quotes string
# temporary index
# store end of raw line
# end index of non-quoted string slice
# start index of non-quoted string slice
# return next non-quoted substring and next index
def getUnquotedStr(line):
   startSlice=0
   endSlice=0
   lineLen=len(line)
   index=0
   dblQuote='"'
   sglQuote="'"
   quote=dblQuote

# trap empty input line string
   if (lineLen == 0):
      return ("", lineLen)

# find string start as double or single quote
   while 1:
      if (line[endSlice] == dblQuote) or (line[endSlice] == sglQuote):

# if at end of string, that's all
# increment to next character
# slice already available so return it now
# if at end of string, that's all
# set substring search to after the quoted section
# no terminating quote found, return rest of line
# try to match closing quote
# if at end of string, that's all
# increment to char following 1st matched quote
# if quote is matched and no substring yet found
# set quote character to the non-default single one
         if (line[endSlice] == sglQuote):
            quote = sglQuote
         if (endSlice == startSlice):
            endSlice = endSlice + 1
            if (endSlice >= lineLen):
               return ("", endSlice)
            index = string.find (line[endSlice:], quote)
            if (index == -1):
               return (line[startSlice:], lineLen)
            else:
               startSlice = index + endSlice + 1
               endSlice = index + endSlice + 1
               if (endSlice >= lineLen):
                  return ("", endSlice)
         else:
            return (line[startSlice:endSlice], endSlice)
      else:
         endSlice=endSlice+1
         if (endSlice >= lineLen):
            return (line[startSlice:], endSlice)

# none found so keep the line
# find any hash not in col1
# whitespace line is target for comments
# col1 comment is target for comments
# target for moves of EOL comments
# buffer for processed file
# move EOL comment lines i.e. not starting in col 1
def moveComments (lineList):
   outList = []
   lastCommentLine = 0
   nCommentsMoved = 0
   for i in range(len(lineList)):
      line = lineList[i]
      if debug>1: print line,
      if line[0] == hash:
         if debug>1: print "comment_fix : input line %d is a comment line " % i
         outList.append(line)
         lastCommentLine = len(outList)
         continue
      stripLine = string.strip(line)
      if len(stripLine) == 0:
         if debug>1: print "comment_fix : input line %d is a blank line " % i
         outList.append(line)
         lastCommentLine = len(outList)
         continue
      startHash = findUnquoted(line, hash)
      if startHash == -1:
         if debug>1: print "comment_fix : input line %d is a code only line " % i
         outList.append(line)
         continue
      if debug>1: print "comment_fix : input line %d is a code + EOL comment line " % i
      if debug: print "hash found at %d" % startHash

# ensure comments start in col1
# move code to to comment line above keeping indent
# append stripped code line to output buffer
# search backwards from has for last code char
      nCommentsMoved = nCommentsMoved + 1      
      for j in range(startHash-1, -1, -1):
         if line[j] not in string.whitespace:
            if debug>1: print "EOL comment on line %d starts at index %d" % (i, j)
            lastCodeChar = j+1          
            outList.append(line[:lastCodeChar] + "\n")
            comment = line [lastCodeChar:]
            comment = string.lstrip(comment)
            outList.insert (lastCommentLine, comment)
            break
   print "%d end of line comments moved up and started in col 1" % nCommentsMoved
   return outList

# prints elements of a sequence
def printSeq (list):
   for name in list:
      print name

# save file
# move comments from line ends
# remove cvs "$Id:" lines
# read script/module into a list
# process line to new layout
def processFile (foundName):
   if debug>1: print "comment_fix : matched file : %s" % foundName
   try:
      fptr = open (foundName, "r")
      lineList = fptr.readlines()
      lineList= removeDollar (lineList)
      lineList = moveComments (lineList)
      fptr.close()
   except:
      print traceback.print_exc()
      try:
         fptr.close()
      except:
         pass
   saveFile (foundName, lineList)

# chop lines matching cvs "$Id" string
def removeDollar (lineList):
   outList = []
   for i in range(len(lineList)):
      line = lineList[i]
      offset = findUnquoted(line, "$Id")
      if offset == -1:
         outList.append(line)
      else:
         print "removed         : " + string.rstrip(line)
   return outList

# save the modifications back to file
def saveFile (fileName, lineList):
   try:
      fptr = open (fileName, "w")
      fptr.writelines (lineList)
   except:
      print traceback.print_exc()
      try:
         fptr.close()
      except:
         pass

# function called by os.path.walk in each directory
def visitFunc (arg, dirName, names):

# carry out action on each named file
# match each directory file with search list
   if debug: print "searching in directory : %s" % dirName
   for searchStr in searchList:
      foundList = glob.glob (dirName + os.sep + searchStr)
      for foundName in foundList:
         print "\nprocessing file : %s" % foundName
         processFile (foundName)
   return

# function to print string to screen. wait if pause
def writeMessage (msgStr, pause=0, exit=0):
   print msgStr
   if pause:
      print ">>> Press return to continue : ",
      line = sys.stdin.readline()
   if exit:
      sys.exit()
   return

# copy next search list to default name
# copy next tree list to default name
# use each tree and search list in turn
for listNo in lists:
   exec "treeList = treeList%s" % (listNo)
   exec "searchList = searchList%s" % (listNo)

# recurse each tree and search for searchList matches
   for treeName in treeList:
      os.path.walk(treeName, visitFunc, "")


# that's all folks
writeMessage ("=== processing finished", 1, 1)
