import random
import time
import sys
import os

#SRS File generator
def create_files(filelocation, savingpath, filenumber, timelap):
    count = 0
    while (count < filenumber):
        filename = savingpath+"srsFile"+str(count)+".dat"
    
        FILE = open(filename,"w")
        FILE.write(" &SRS")
        FILE.write('\n')
        FILE.write(" &END")
        FILE.write('\n')
        FILE.write("x"+"\t"+"y")
        FILE.write('\n')
        xMax = random.randint(0, 1000)
        xMin = random.randint(0, 1000)
        if xMax < xMin:
            temp = xMax
            xMax = xMin
            xMin = temp
        elif xMax == xMin:
            xMax = xMin+1
    
        yMax = random.randint(0, 1000)
        yMin = random.randint(0, 1000)
        if yMax < yMin:
            temp = yMax
            yMax = yMin
            yMin = temp
        elif yMax == yMin:
            yMax = yMin+1
    
        datanumber = []
        count1 = 0
        while (count1 < 50):
            xValue = random.randrange(xMin, xMax, 1)
            yValue = random.randrange(yMin, yMax, 1)
            datanumber.append(str(xValue)+"\t"+str(yValue)+"\n")
            FILE.writelines(datanumber)
            count1 = count1+1
        
        FILE.close()
        print "File %d created" % count
        
        #update the file location text file
        #filenamelocation = "/scratch/polling/scatterplotlocation.txt"
        filenamelocation = filelocation
        filelocation
        FILELOCATION = open(filenamelocation,"a")
        FILELOCATION.write(filename)
        FILELOCATION.write('\n')
        FILELOCATION.close()
    
        count = count+1
        #wait ? sec
        time.sleep(timelap)
    
    
if __name__ == "__main__":
    print "SRS File creator"
    if len(sys.argv) != 5 :
        print "\n Usage :"
        print " createSRSFiles fileslocation.txt /out/put/pathname/ number_of_files_to_create time_lap\n"
        sys.exit(0)

    filelocation = sys.argv[1]
    outpath = sys.argv[2]
    filenumber = sys.argv[3]
    timelap = sys.argv[4]
    filenumber = int(filenumber)
    timelap = int(timelap)

    #if not os.path.exists(filelocation) :
    #    raise RuntimeError("input file '%s' does not exist" % filelocation)

    if not os.path.exists(outpath) :
        raise RuntimeError("out path '%s' does not exist" % outpath)
    
    if filenumber<1:
        raise RuntimeError("The number of files must be > 0")
    
    if timelap<1:
        raise RuntimeError("The time lap must be >= 1")

    print "File location to update/create: %s" % filelocation
    print "Directory to create files to: %s" % outpath
    print "Number of files to create: %d" % filenumber
    print "A file will be created every: %d sec" % timelap
    
    time.sleep(2)
    
    print "SRS File creation starting..."
    create_files(filelocation,outpath,filenumber,timelap)
    print str(filenumber)+" files have been created in "+str(timelap*filenumber)+" seconds."

