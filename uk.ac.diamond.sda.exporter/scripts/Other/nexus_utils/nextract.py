'''
Tool which extracts the data out of nexus files and into ascii files
'''
import sys, os, getopt, glob, traceback
#import pprint
#pprint.pprint(sys.path)
import numpy, nxs
#import datetime
import re
#import nexus_utils.nxs
#import logging
#import shutil

class generic_data_extractor:

    def __init__(self, filename, verbosity, elements_list):
        self.filename = filename
        self.verbosity = verbosity
        self.elements_list = elements_list
        
        self.warnings_set = set()
        self.basename = os.path.basename(filename)
        self.nexusfile = nxs.open(self.filename)
        self.metadata = {}
        
        root_entries = self.nexusfile.getentries()
        if len(root_entries) != 1:
            warning = "unexpected extra root entries, reading first one only:" \
                "\n  %r" % root_entries
            print >>sys.stderr, "%s: Warning, %s" % (self.filename, warning)
            self.warnings_set.add(warning)
        
        root_group = root_entries.keys()[0]
        if root_group != "entry1":
            warning = "first root entry is not entry1, using: %s" % root_group
            print >>sys.stderr, "%s: Warning, %s" % (self.filename, warning)
            self.warnings_set.add(warning)

        self.nexusfile.opengroup(root_group)
        self.init_root()

    def init_root(self):
        entries = self.nexusfile.getentries()
        
        self.get_metadata("", "entry_identifier")
        self.get_metadata("", "investigation")
        self.get_metadata("", "program_name")
        self.get_metadata("", "proposal_identifier")
        self.get_metadata("", "scan_command")
        self.get_metadata("", "scan_dimensions")
        self.get_metadata("", "scan_identifier")
        self.get_metadata("", "title")
        
        self.get_metadatagroup("", "user01", ["username"])
            
        if not "scan_command" in self.metadata.keys():
            self.metadata["scan_command"] = "Unknown"
            warning = "scan_command not in root entry"
            print >>sys.stderr, "%s: Warning, %s" % (self.filename, warning)
            self.warnings_set.add(warning)

        if not "scan_dimensions" in self.metadata.keys():
            self.metadata["scan_dimensions"]=numpy.array([])
            warning = "scan_dimensions not in root entry"
            print >>sys.stderr, "%s: Warning, %s" % (self.filename, warning)
            self.warnings_set.add(warning)

        scan_dimensions = self.metadata["scan_dimensions"]
        # When doing one dimensional scans, nexus writer puts the dimension
        # data into a single integer value rather than an array of ints.
        if type(scan_dimensions) == type(numpy.int32()):
            self.metadata["scan_dimensions"] = numpy.array([scan_dimensions])

        if not "instrument" in entries:
            raise RuntimeError("No instrument definition")
        
        self.nexusfile.opengroup("instrument")
        self.init_instrument()
        
    def init_instrument(self):
        entries = self.nexusfile.getentries()

        self.get_metadata("instrument.", "name")
        
        self.get_metadatagroup('instrument.', 'source', ['current', 'frequency',
            'name', 'notes', 'power', 'probe', 'type', 'voltage'])
        
    def get_metadatagroup(self, prefix, group, names):
        if group in self.nexusfile.getentries():
            self.nexusfile.opengroup(group)
            for name in names:
                self.get_metadata(prefix + group+".", name)
            self.nexusfile.closegroup()
        
    def get_metadata(self, key_prefix, data_key):
        if data_key in self.nexusfile.getentries():
            self.nexusfile.opendata(data_key)
            self.metadata[key_prefix+data_key] = self.nexusfile.getdata()
            self.nexusfile.closedata()
        
    def get_data(self, data_not_shape, data_key, group_value):
        self.nexusfile.opendata(data_key)

        info = self.nexusfile.getinfo()
        if data_not_shape:
            data = self.nexusfile.getdata()
        else:
            data = (group_value,)+info

        self.nexusfile.closedata()
        return info, data

    def get_group_data(self, data_not_shape, group_key, group_value, data_key):
        self.nexusfile.opengroup(group_key)
        entries = self.nexusfile.getentries()

        arrays_dict, arrays_list = {}, []
        degenerate_arrays_dict, degenerate_arrays_list = {}, []

        for (k,v) in entries.iteritems():
            if k == data_key:
                key, value = group_key, group_value
            else:
                key, value = group_key+"."+k , group_value+"."+v

            (data_dims,data_type),data = self.get_data(data_not_shape, k, value)
            
            if len(data_dims) > 0 and data_type != 'char' :
                if data_dims[0] > 1:
                    arrays_dict[key] = data 
                    arrays_list += (key,)
                else:
                    degenerate_arrays_dict[key] = data 
                    degenerate_arrays_list += (key,)

        if self.verbosity >= 2:
            print >>sys.stderr, \
                "%s: " % self.filename, "%s:" % group_key, entries

        if len(arrays_list)==0:
            warning = "no data elements with more than one value, " + \
                "returning all single value elements"
            print >>sys.stderr, "%s: Warning, %s" % (self.filename, warning)
            self.warnings_set.add(warning)
            arrays_dict = degenerate_arrays_dict 
            arrays_list = degenerate_arrays_list 
        
        self.nexusfile.closegroup()
        return arrays_dict, arrays_list

    def get_all_instrument_data(self, data_not_shape):
        """ Get the data dictionaries for the nexus data, along with the list of
            dictionary keys for this data in a standard order - positioners,
            sorted by name, followed by detectors, sorted by name.
        """
        arrays_dict, positioners_list, detectors_list = {}, [], []

        entries = self.nexusfile.getentries()
        if self.verbosity >= 2:
            print >>sys.stderr, "%s:" % self.filename, entries

        for (k,v) in entries.iteritems():
            if v == 'NXpositioner':
                ad, pl = self.get_group_data(data_not_shape, k, v, k)
                arrays_dict.update(ad)
                positioners_list += pl
            
        for (k,v) in entries.iteritems():
            if v == 'NXdetector':
                ad, pl = self.get_group_data(data_not_shape, k, v, 'data')
                arrays_dict.update(ad)
                detectors_list += pl

        positioners_list.sort()
        detectors_list.sort()

        return arrays_dict, positioners_list + detectors_list

    def get_selected_instrument_data(self, data_not_shape):
        arrays_dict, arrays_list = self.get_all_instrument_data(data_not_shape)
        return arrays_dict, self.selected_keys(arrays_dict, arrays_list)

    def selected_keys(self, arrays_dict, arrays_list):
        """ The aim of this routine is to put all element columns on the output
               in the order specified, without duplicates.
            1) Populate the list with all of the specific named elements
            2) Populate unused sub elements if they have a wildcard element
            3) Populate other unused elements, if there is a '-' element
        """  
        if len(self.elements_list) == 0:
            return arrays_list
        
        available_keys = arrays_list[:]

        # ----------------------------------------------------------------------
        indexed_keys=[]
        remove_keys = set([])
        regex = re.compile('([a-z0-9]+)\[([0-9]+)\]')                    
        for key in self.elements_list:
            match = regex.match(key)
            if match != None:
                k = match.group(1)
                i = int(match.group(2))
                if k in available_keys:
                    if type(arrays_dict[k]) == numpy.ndarray: # Data
                        shape = arrays_dict[k].shape
                    elif type(arrays_dict[k]) == tuple: # Names
                        shape = arrays_dict[k][1]
                    else:
                        raise RuntimeError("Data element '%s' is not an array" % k)
                    
                    length = shape[len(self.metadata['scan_dimensions'])]
                    if i <= length:
                        arrays_dict[key]=arrays_dict[k]
                        indexed_keys += (key,) 
                        remove_keys.add(k) 
                    else:
                        raise RuntimeError("Data element '%s' index out of range" % key)
                else:
                    print "Key not exist, "
            else:
                print "Ok, "
        for key in indexed_keys:
            available_keys += (key,) 
        for k in remove_keys:
            available_keys.remove(k)
        # ----------------------------------------------------------------------

        if "-" in self.elements_list:
            i = self.elements_list.index("-", 0)  # Split elements at first "-" 
            before_keys = self.elements_list[0:i] # & get elements before it
            # Get elements after it, stripping out any additional "-"'s
            after_keys = [k for k in self.elements_list[i+1:] if k != "-"]
        else:
            before_keys, after_keys = self.elements_list, []
        
        # Strip before and after keys from list of remaining keys
        available_keys = [x for x in available_keys 
                          if not (x in before_keys or x in after_keys)]
        
        # Expand any sub-element wildcards and remove those keys
        before_keys = self.get_expanded_keys(before_keys, available_keys)
        after_keys = self.get_expanded_keys(after_keys, available_keys)
        
        if "-" in self.elements_list: # Add in all ramaining available keys
            keys = before_keys + available_keys + after_keys
        else:
            keys = before_keys + after_keys
        
        for k in keys: # Check requested elements_list are present
            if not k in arrays_dict.keys():
                raise RuntimeError("Data element '%s' not in: %s" \
                    % (k, ", ".join(arrays_dict.keys())))
        return keys

    def get_expanded_keys(self, unexpended_keys, available_keys):
        """ Note that this function side-effects the available keys, removing
            any expanded keys from the list of remaining available keys. """
        expanded_keys = []                    
        for key in unexpended_keys:
            new = [x for x in available_keys if x.startswith(key + '.')]
            expanded_keys += new if len(new) > 0 else [key]
            for new_key in new:
                available_keys.remove(new_key)
        return expanded_keys

    def instrument_names(self, names):
        (arrays_dict, arrays_list) = self.get_selected_instrument_data(False)

        if names < 2:
            return "%s: " % self.basename + ", ".join(arrays_list)
            
        widths_dict = {}
        data_dict = {        'node':{}, 'size':{}, 'type':{} }
        lines = { 'name':[], 'node':[], 'size':[], 'type':[] }

        for k in arrays_list:
            v = arrays_dict[k]
            node  = v[0]
            size  = "%d:%s" % (len(v[1]), v[1])
            type  = v[2]
            widths_dict[k] = max(len(k), len(node), len(size), len(type))
            data_dict['node'][k] = node
            data_dict['size'][k] = size
            data_dict['type'][k] = type

        for k in arrays_list:
            lines['name'] += [                 (k).rjust(widths_dict[k])]
            lines['node'] += [data_dict['node'][k].rjust(widths_dict[k])]
            lines['size'] += [data_dict['size'][k].rjust(widths_dict[k])]
            lines['type'] += [data_dict['type'][k].rjust(widths_dict[k])]
        
        return '\n'.join(['%s: %s' % (self.basename, self.get_header_Names()),
            "  Data names: " + ", ".join(lines['name']),
            "   Node type: " + "  ".join(lines['node']),
            "   Data dims: " + "  ".join(lines['size']),
            "   Data type: " + "  ".join(lines['type'])])

    def get_header_Names(self):
        return 'Scan dims: %d:%s' % (len(self.metadata['scan_dimensions']),
                                     self.metadata['scan_dimensions'])
    
    def get_header_SRS(self):
        lines = [" &SRS"]
        remaining_metadata = self.metadata.copy()

        SRS_line_order = ['instrument', 'facility', 'facilityType', 'probe',
                          'beamCurrent', 'facilityMode', 'fillMode', 'userid',
                          'federalid', 'defVisit', 'investigation', 'visit',
                          'proposal', 'title', 'user']
        SRS_metadata_keys = {'instrument':'instrument.name',
                    'facility':'instrument.source.name',
                    'facilityType':'instrument.source.type',
                    'probe':'instrument.source.probe',
                    'beamCurrent':'instrument.source.current',
                    'facilityMode':'',
                    'fillMode':'',
                    'userid':'user01.username',
                    'federalid':'user01.username',
                    'defVisit':'',
                    'investigation':'investigation',
                    'visit':'',
                    'proposal':'proposal_identifier',
                    'title':'title',
                    'user':''}
        for k in SRS_line_order:
            if k in SRS_metadata_keys.keys() and \
                    SRS_metadata_keys[k] in self.metadata.keys(): 
                lines += [k + "=%s" % self.metadata[SRS_metadata_keys[k]]]
                if SRS_metadata_keys[k] in remaining_metadata.keys():
                    del(remaining_metadata[SRS_metadata_keys[k]])
            else:
                lines += [k + "="]

        sorted_keys = sorted(remaining_metadata.keys())
        lines += ["%s=%s" % (k, remaining_metadata[k]) for k in sorted_keys]
        lines += [" &END"]
        
        return lines
    
    
    def instrument_SRS_lines(self):
        (arrays_dict, arrays_list) = self.get_selected_instrument_data(True)
        flattened_dict = self.arrays_flatten(arrays_dict,
                                             self.metadata['scan_dimensions'])
        widths_dict = {}
        for k in arrays_list:
            max_width = len(k)
            for value in flattened_dict[k]:
                strvalue = "%s" % value
                max_width = max(len(strvalue), max_width)
            widths_dict[k] = max_width

        lines = ["\t".join([(k).rjust(widths_dict[k]) for k in arrays_list])]
        for i in range(len(flattened_dict[arrays_list[0]])):
            lines += ["\t".join([("%s" % flattened_dict[k][i]).
                                rjust(widths_dict[k]) for k in arrays_list])]

        return self.get_header_SRS() + lines
        
    def instrument_write(self, lines, outpath):
        #for line in lines:
        #    print line
        output = "\n".join(lines) + "\n"
        if outpath == '-':
            sys.stdout.write(output)
        else:
            outfile = os.path.join(outpath, self.basename+".dat")
            if self.verbosity >= 1:
                print >>sys.stderr, "Writing %s" % outfile

            file = open(outfile, "w")
            file.write(output)
            file.close

    def arrays_flatten(self, arrays_dict, scan_dimensions):
        flattened_dict={}
        lengths = {}
        flat = False
        for (k,v) in arrays_dict.iteritems():
            if type(v) == numpy.ndarray:
                lengths['min'] = min(len(v), lengths.get('min', len(v)))
                lengths['max'] = max(len(v), lengths.get('max', len(v)))
                if self.verbosity >= 2:
                    print >>sys.stderr, scan_dimensions, k, len(v), lengths
            else:
                flat = True

        # ----------------------------------------------------------------------
        regex = re.compile('([a-z0-9]+)\[([0-9]+)\]')                    
        # ----------------------------------------------------------------------

        if flat or len(scan_dimensions) == 0:
            for (k,v) in arrays_dict.iteritems():
                if type(v) == numpy.ndarray:
                    # ----------------------------------------------------------
                    match = regex.match(k)
                    if match != None:
                        i = int(match.group(2))
                        if i <= len(v):
                            flattened_dict[k]=[v[i]]
                        else:
                            pass
                    else:
                        # ------------------------------------------------------
                        flattened_dict[k]=[v]
                else:
                    flattened_dict[k]=[v]
        else:
            scan_dimension = scan_dimensions[0]
            if lengths['min'] != lengths['max']:
                raise RuntimeError("Error, mismatch between dimensions" + \
                    "(min %d != max %d)" % (lengths['min'], lengths['max']))

            if lengths['min'] != scan_dimension:
                warning = "scan_dimensions inconsistent: %r not %r for %s" % ( 
                    lengths['min'],scan_dimension,self.metadata['scan_command'])
                print >>sys.stderr, "%s: Warning, %s" % (self.filename, warning)
                self.warnings_set.add(warning)
                scan_dimension = lengths['min'] 

            for i in range(scan_dimension):
                sub_arrays_dict={}
                for (k,v) in arrays_dict.iteritems():
                    sub_arrays_dict[k]=v[i]
                
                sub_flattened_dict = self.arrays_flatten( \
                    sub_arrays_dict, scan_dimensions[1:])

                for (k,v) in sub_flattened_dict.iteritems():
                    if flattened_dict.has_key(k):
                        flattened_dict[k] += sub_flattened_dict[k]
                    else:
                        flattened_dict[k] = sub_flattened_dict[k]
            
            if self.verbosity >= 2:
                print >>sys.stderr, scan_dimensions

        return flattened_dict

    #----------------------------------------------------------------------
"""
    def extract_all(self, path, filename) :
        print "Starting Extraction of all elements_list"
        entries = self.nexusfile.getentries()
        count = 0;
        for entry in [a for a in entries if (a.find("EDXD") >= 0)] :
            print "Extracting ", entry
            # make the direcorty
            elementpathname = os.path.join(path,entry)
            os.mkdir(elementpathname)
            self.extract(entry,elementpathname, filename)
            count += 1
        if count==0:
            print " No EDXD element to extract"

    def extract(self, element, path, filename) :
        self.nexusfile.opengroup(element)
        
        q = self.extract_q()
        print "q is ", q
        
        e = self.extract_energy()
        print "e is ", e
        
        shape = self.extract_data_shape()
        #print "shape is ", shape
        
        if len(shape) == 2 :
            print "Processing 1D scan"
            for x in range(shape[0]) :
                point = [x]
                #print "point ", point
                data = self.extract_data_point(point, shape[-1])[0,:]
                self.write_file(filename, path, point, e, q, data) 
        
        if len(shape) == 3 :
            print "Processing 2D scan"
            for x in range(shape[0]) :
                for y in range(shape[1]) :
                    point = [x,y]
                    #print "point ", point
                    data = self.extract_data_point(point, shape[-1])[0,:]
                    self.write_file(filename, path, point, e, q, data) 
        
        if len(shape) == 4 :
            print "Processing 3D scan"
            for x in range(shape[0]) :
                for y in range(shape[1]) :
                    for y in range(shape[2]) :
                        point = [x,y,z]
                        #print "point ", point
                        data = self.extract_data_point([x,y,z], shape[-1])[0,:]
                        self.write_file(filename, path, point, e, q, data) 
        
        self.nexusfile.closegroup()

    def write_file(self, filename, path, point, e, q, data) :
        fullname = "%s_%s.dat" % (filename, "_".join([("%04d"%x) for x in point]))
        
        file = open(os.path.join(path,fullname), "w")
        
        lines = []
        
        for i in range(len(e)) :
            lines.append("%5d %.8g %.8g %.8g" % (i,e[i],q[i],data[i]))
        
        file.write("\n".join(lines))
        
        file.close

    def extract_q(self) :
        # open the q group, get the data
        
        self.nexusfile.opendata("edxd_q")
        
        # return the result 
        q_axis = self.nexusfile.getdata()
        
        # close the path
        self.nexusfile.closedata()
        
        return q_axis

    def extract_energy(self) :
        # open the q group, get the data
        
        self.nexusfile.opendata("edxd_energy_approx")
        
        # return the result 
        energy_axis = self.nexusfile.getdata()
        
        # close the path
        self.nexusfile.closedata()
        
        return energy_axis

    def extract_data_shape(self) :
        # open the next group, get the data
        
        self.nexusfile.opendata("data")
        
        # return the result 
        info = self.nexusfile.getinfo()
        
        # close the path
        self.nexusfile.closedata()
        
        return info[0]

    def extract_data_point(self, point, length) :
        # open the next group, get the data
        #print "point is", point
        #print "length is", length
        
        self.nexusfile.opendata("data")
        
        startpoint = point+[0]
        #print "startpoint is ", startpoint
        datarange = []
        for i in range(len(point)):
            datarange.append(1)
        datarange.append(length)
        #print "datarange is ", datarange
        
        # put it in a dictionary, 
        #data = self.nexusfile.getdata()
        data = self.nexusfile.getslab(startpoint, datarange)
        
        # close the path
        self.nexusfile.closedata()
        
        return data
"""
    #----------------------------------------------------------------------

def process_file(infile, names, verbosity, outpath, elements):
    if not os.path.exists(infile) :
        print >>sys.stderr, "%s: Error, input file does not exist" % infile
    
    try:
        data_extractor = generic_data_extractor(infile, verbosity, elements)

        if names > 0:
            print data_extractor.instrument_names(names)
        else:
            lines = data_extractor.instrument_SRS_lines()
            data_extractor.instrument_write(lines, outpath)

    except RuntimeError:
        exc_type, exc_value, exc_traceback = sys.exc_info()
        print >>sys.stderr, "%s: Error, failed to extract data:" \
            % infile, exc_value

    except nxs.NeXusError:
        exc_type, exc_value, exc_traceback = sys.exc_info()
        print >>sys.stderr, "%s: NeXusError %s:" \
            % (infile, exc_type), exc_value

    #except Exception:
    #    exc_type, exc_value, exc_traceback = sys.exc_info()
    #    print >>sys.stderr, "%s: Error %s:" % (infile, exc_type), exc_value
    #    traceback.print_tb(exc_traceback, file=sys.stderr)

def usage(show_full=False):
    usage = """Usage: nextract [OPTIONS]... FILE|DIRECORY [DATA_ELEMENTS]
OPTIONS:
  -gPATTERN, --glob=PATTERN  For a directory, limit the files selected to those
                               matching this pattern, default is "*.nxs"
  -h, --help                 Display full help, with examples & DATA_ELEMENTS
  -l, --list                 List details of data elements in the file(s)
  -n, --names                List the names of data elements in the file(s)
  -oPATH, --outpath=PATH     Sets the path for output file(s), "-" sends output
                               to stdout, default is "."
  -v, --verbose              Show additional progress information
  -d, --debug                Show debugging information
"""
    usage2 = """
DATA_ELEMENTS:               These set the order of the data elements columns in
                               the output and limit processing to nexus files
                               containing these elements, default is "-"
  DATA_ELEMENT               Include a specific data element. If this element
                               has sub-elements, all will be included
  DATA_ELEMENT.SUB_ELEMENT   If a data element has sub elements (it has a dot in
                               it) then you can specify an individual sub
                               element. Any use of the full element will then
                               exclude the explicitly positioned sub element
  DATA_ELEMENT[INDEX]        If a data element is an array of values then you
                               can pull out specific indices and give them their
                               own column in the output
  -                          Include all data elements not explicitly listed

Examples:
  nextract -n -g*-?.nxs .    List the data elements of files in the current dir
                               which end with a dash, then any char, then ".nxs"
  nextract -ofubar /data     Extract data of all nexus files in "/data",
                               writing the ascii files to a sub-directory of
                               the current directory called "fubar"
  nextract . energy - ca21   For all nexus files in the current dir with data
                               elements named 'energy' and 'ca21', list all data
                               elements, with 'energy' first, 'ca' last and all
                               other data elements inbetween
  nextract . th mac119[0]    For all nexus files in the current dir with
                               data elements named 'th' and an array data
                               element named 'mac119', extract 'th' and the
                               first value (or sub array) in each 'mac119' array
"""

    return usage if not show_full else usage+usage2

ERR_OK = 0
ERR_NO_INPUT = 1
ERR_NO_OUTPUT = 2
ERR_OPTIONS = 3

def main(argv):
    try:
        longopts = ["debug", "glob=", "help", "list", "names", "outpath=",
                    "verbose"]
        opts, args = getopt.getopt(argv, "dg:hlno:v", longopts)
    except getopt.GetoptError as err:
        print >>sys.stderr, "Error,", err
        print >>sys.stderr, usage(), 
        sys.exit(ERR_OPTIONS)
    
    pathglob = "*.nxs"
    names = 0
    outpath = "."
    verbosity = 0
    
    for opt, arg in opts:
        if opt in ("-d", "--debug"):
            verbosity = max(2,verbosity)
        elif opt in ("-g", "--glob"):
            pathglob = arg
        elif opt in ("-h", "--help"):
            print >>sys.stderr, usage(True), 
            sys.exit(ERR_OK)
        elif opt in ("-l", "--list"):
            names = max(2,names)
        elif opt in ("-n", "--names"):
            names = max(1,names)
        elif opt in ("-o", "--outpath"):
            outpath = arg
        elif opt in ("-v", "--verbose"):
            verbosity = max(1,verbosity)

    if len(args) < 1:
        print >>sys.stderr, "Error, no file or directory given!\n"
        print >>sys.stderr, usage(), 
        sys.exit(ERR_NO_INPUT)

    infilepath = args[0]
    elements = args[1:]

    if verbosity >= 2:
        print >>sys.stderr, "Names=%d Verbosity=%d, " % (names, verbosity), \
            "In=%s, Glob=%s, Out=%s," % (infilepath, pathglob, outpath), \
            "Elements=%r" % elements

    if not (os.path.exists(outpath) or outpath=='-'):
        print >>sys.stderr, "Error, out path does not exist: %s" % outpath
        print >>sys.stderr, usage(), 
        sys.exit(ERR_NO_OUTPUT)

    if os.path.isdir(infilepath):
        infiles = glob.glob(os.path.join(infilepath, pathglob))
        infiles.sort()
        for infile in infiles:
            if os.path.isdir(infile):
                print >>sys.stderr, \
                    "%s: Warning, ignoring directory" % infile
                continue
            process_file(infile, names, verbosity, outpath, elements)
    else:       
        process_file(infilepath, names, verbosity, outpath, elements)

    sys.exit(ERR_OK)

if __name__ == '__main__':
    main(sys.argv[1:])