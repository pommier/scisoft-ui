import random
import time
import sys
import os

#SRS File generator
class srs_file_creator:
    
    def __init__(self) :
        print "Initialising file creator"
        self.filelocation = ""
        self.timelap = ""
        
    def create_files(self, filelocation, savingpath, filenumber, timelap, xaxis, yaxis):
        #create files
        count = 0
        while (count < filenumber):
            filename = savingpath + "srsFile" + str(count) + ".dat"
            FILE = open(filename, "w")
            FILE.write(" &SRS")
            FILE.write('\n')
            FILE.write(" &END")
            FILE.write('\n')
            FILE.write(xaxis + "\t" + yaxis)
            FILE.write('\n')
            
            xMax = random.randint(0, 1000)
            xMin = random.randint(0, 1000)
            if xMax < xMin:
                temp = xMax
                xMax = xMin
                xMin = temp
            elif xMax == xMin:
                xMax = xMin + 1
            
            yMax = random.randint(0, 1000)
            yMin = random.randint(0, 1000)
            if yMax < yMin:
                temp = yMax
                yMax = yMin
                yMin = temp
            elif yMax == yMin:
                yMax = yMin + 1
            
            count1 = 0
            datanumber = []
            while (count1 < 50):
                xValue = random.randrange(xMin, xMax, 1)
                yValue = random.randrange(yMin, yMax, 1)
                datanumber.append(str(xValue) + "\t" + str(yValue) + "\n")
                FILE.writelines(datanumber)
                count1 = count1 + 1
                
            FILE.close()
            print "File %d created" % count
                
                #update the file location text file
            #filenamelocation = "/scratch/polling/scatterplotlocation.txt"
            FILELOCATION = open(filelocation, "a")
            FILELOCATION.write(filename)
            FILELOCATION.write('\n')
            FILELOCATION.close()
                
            count = count + 1
            #wait ? sec
            time.sleep(timelap)

    
    def get_value_of_key(self, pollingjob, key):
        FILE = open(pollingjob, "r")
        lines = FILE.readlines()
        for line in lines:
            if str(line).startswith( key+'=' ):
                value = str(line).split('=')
                if str(line).endswith('\n'):
                    value = value[1].split('\n')
                    value = value[0]
                else:
                    value = value[1]
        FILE.close()
        return value


    
if __name__ == "__main__":
    print "SRS File creator"
    if len(sys.argv) != 4 :
        print "\n Usage :"
        print " createSRSFiles pollingjob.txt /out/put/pathname/ number_of_files_to_create\n"
        sys.exit(0)

    pollingjob = sys.argv[1]
    outpath = sys.argv[2]
    filenumber = sys.argv[3]
    filenumber = int(filenumber)
    
    filecreator = srs_file_creator()
    #get the values of polling job
    filelocation = filecreator.get_value_of_key(pollingjob, "FileName")
    timelap = filecreator.get_value_of_key(pollingjob, "PollTime")
    timelap = float(timelap)
    xaxis = filecreator.get_value_of_key(pollingjob, "XAxis")
    yaxis = filecreator.get_value_of_key(pollingjob, "YAxis")
    
    #if not os.path.exists(filelocation) :
    #    raise RuntimeError("Input file '%s' does not exist" % filelocation)

    if not os.path.exists(outpath) :
        raise RuntimeError("Out path '%s' does not exist" % outpath)
    
    if filenumber < 1:
        raise RuntimeError("The number of files must be > 0")
    
    if timelap<=0:
        raise RuntimeError("The time lap must be > 0")

    print "File location to update/create: %s" % filelocation
    print "Directory to create files to: %s" % outpath
    print "Number of files to create: %i" % filenumber
    print "A file will be created every: %f sec" % timelap
    
    time.sleep(2)
    
    print "SRS File creation starting..."
    #create the files
    filecreator.create_files(filelocation,outpath,filenumber,timelap, xaxis, yaxis)
    print str(filenumber) + " files have been created in " + str(timelap*filenumber) + " seconds."

