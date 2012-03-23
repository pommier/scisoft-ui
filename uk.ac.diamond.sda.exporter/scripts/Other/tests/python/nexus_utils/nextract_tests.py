'''
Test the nextract script. 

@author: voo82358

To run a basic set of tests, use "Run As" > "Python unit-test" on this file.
For the full suite of tests, use "Run As" > "Python Run".

'''
#import sys, pprint
#pprint.pprint(sys.path)
    
import unittest
from nexus_utils.nextract import generic_data_extractor, main, usage
from nextract_testdata import testdata, tests, usagedata, usage2data

class TestNextractBase(unittest.TestCase):

    def setUpOne(self, inputs):
        (infile,elements)=inputs
        verbosity = 1
        #outpath = 'test_out'
        outpath = '-'
        #elements = testdata[infile]['e']
        return generic_data_extractor(infile, verbosity, list(elements))

    def warnings_check(self, test, warnings_set, expected_warnings_set):
        self.set_check(test, warnings_set, expected_warnings_set, "warnings")

    def set_check(self, test, data_set, expected_set, set_name):
        extra_warnings   = data_set.difference(expected_set)
        missing_warnings = expected_set.difference(data_set)
        self.assertEquals(test[0]+":missing "+set_name+":%r" % missing_warnings,
                          test[0]+":missing "+set_name+":set([])")
        self.assertEquals(test[0]+":extra "+set_name+":%r" % extra_warnings,
                          test[0]+":extra "+set_name+":set([])")
        
    def tearDown(self):
        pass

class TestNextractParameterised(TestNextractBase):

    def setUp(self):
        print "-"*80, self.currentTest

    def __init__(self, methodName='runTest'):
        ''' The __init__ '''
        self.currentTest = ('test_data/Incomplete+i10-1972.nxs',())
        ''' We must always call the base __init__ too though '''
        TestNextractBase.__init__(self, methodName)

    def __str__(self):
        ''' Override this so that we know which instance it is '''
        return "%s%s (%s)" % (self._testMethodName, self.currentTest, unittest._strclass(self.__class__))

    def testNames(self):
        test=self.currentTest
        data_extractor = self.setUpOne(test)
        data = data_extractor.instrument_names(1)
        #print data
        self.assertEquals(data, testdata[test]['n'])
        self.warnings_check(test, data_extractor.warnings_set,
                                  testdata[test]['w'])

    def testNamesVerbose(self):
        test=self.currentTest
        data_extractor = self.setUpOne(test)
        data_extractor.verbosity = 2
        data = data_extractor.instrument_names(1)
        #print data
        self.assertEquals(data, testdata[test]['n'])

    def testLongNames(self):
        test=self.currentTest
        data_extractor = self.setUpOne(test)
        data = data_extractor.instrument_names(2)
        #print data
        self.assertEquals(data, testdata[test]['l'])
        self.warnings_check(test, data_extractor.warnings_set,
                                  testdata[test]['w'])

    def testData(self):
        test=self.currentTest
        data_extractor = self.setUpOne(test)
        (arrays_dict, keys) = data_extractor.\
            get_selected_instrument_data(data_not_shape=True)
        
        for k in arrays_dict.iterkeys():
            if k in testdata[test]['d']:
                self.assertEquals(test[0]+":"+k+":"+repr(arrays_dict[k]),
                                  test[0]+":"+k+":"+testdata[test]['d'][k])
            else:
                self.assertEquals(test[0]+":"+k+":"+repr(arrays_dict[k]),
                                  test[0]+":"+k+":''")
        
        for k in testdata[test]['d'].iterkeys():
            if k in arrays_dict:
                self.assertEquals(test[0]+":"+k+":"+repr(arrays_dict[k]),
                                  test[0]+":"+k+":"+testdata[test]['d'][k])
            else:
                self.assertEquals(test[0]+":"+k+":''",
                                  test[0]+":"+k+":"+testdata[test]['d'][k])

        self.warnings_check(test, data_extractor.warnings_set,
                                  testdata[test]['w'])

    def testFlattened(self, verbosity=0):
        test=self.currentTest
        data_extractor = self.setUpOne(test)
        data_extractor.verbosity = verbosity
        (arrays_dict, keys) = data_extractor.\
            get_selected_instrument_data(data_not_shape=True)
        flattened_dict = data_extractor.\
            arrays_flatten(arrays_dict, data_extractor.metadata['scan_dimensions'])
            
        remaining_keys = flattened_dict.keys()
        for k in testdata[test]['f'].iterkeys():
            self.assertEquals(test[0]+":"+k+":"+repr(flattened_dict[k]),
                              test[0]+":"+k+":"+testdata[test]['f'][k])
            remaining_keys.remove(k)

        self.assertEquals(test[0]+":remaining_keys:%r" % remaining_keys,
                          test[0]+":remaining_keys:[]")
        self.warnings_check(test, data_extractor.warnings_set,
                            testdata[test]['w'].union(testdata[test]['wf']))

    def testFlattenedVerbose(self):
        self.testFlattened(2)

class TestNextract(TestNextractBase):

    def testNameNew(self): # Add new tests here to be sanity checked before
        pass               # adding them to the main testdata array above.
        
        #test = ('test_data/NXdetectors-1d+i20-4723.nxs',('counterTimer01.Iref',
        #                         'counterTimer01', 'counterTimer01.liveTime'))
        #testdata[test] = testdata[test[0],()]
        #testdata[test][XX] = YY
        #data_extractor = self.setUpOne(test)
        #data_extractor.verbosity = 1
        #data = data_extractor.instrument_names(1)
        #print data
        #self.assertEquals(data, testdata[test]['n'])
    
    def testSRSoutput(self):
        test = ('test_data/NXdetectors-1d+i10-3.nxs',())
        testdata[test]['s'] = set([
            ' &SRS',
            'instrument=i10',
            'facility=DLS',
            'facilityType=Synchrotron X-ray Source',
            'probe=x-ray',
            'beamCurrent=-1.0',
            'facilityMode=',
            'fillMode=',
            'userid=gfn74536',
            'federalid=gfn74536',
            'defVisit=',
            'investigation=98180733',
            'visit=',
            'proposal=CM2059',
            'title=Commissioning Directory for I10 2011',
            'user=',
            'entry_identifier=3',
            'instrument.source.frequency=-1.0',
            'instrument.source.notes=MCR Messages to go here...',
            'instrument.source.power=-1.0',
            'instrument.source.voltage=-1000.0',
            'program_name=GDA 8.11.0',
            'scan_command=scan th 0 1 1 mac119 1',
            'scan_dimensions=[2]',
            'scan_identifier=a65756f7-13a6-41f8-9fee-5831526ca707',
            ' &END',
            '               th\tmac119',
            '8.66599999999e-06\t 568.0',
            '      0.999998236\t 567.0'])
        data_extractor = self.setUpOne(test)

        lines = data_extractor.instrument_SRS_lines()
        lines_set = set(lines)
        self.set_check(test, lines_set, testdata[test]['s'], 'lines')
        
        
    def testMetaData(self):
        test = ('test_data/NXdetectors-1d+i10-3.nxs',())
        testdata[test]['m'] = {
            'entry_identifier':"'3'", 'investigation':"'98180733'",
            'program_name':"'GDA 8.11.0'", 'proposal_identifier':"'CM2059'",
            'scan_command':"'scan th 0 1 1 mac119 1'",
            'scan_dimensions':'array([2])',
            'scan_identifier':"'a65756f7-13a6-41f8-9fee-5831526ca707'",
            'title':"'Commissioning Directory for I10 2011'",
            'user01.username':"'gfn74536'",
            'instrument.name':"'i10'",
            'instrument.source.current':'-1.0',
            'instrument.source.frequency':'-1.0',
            'instrument.source.name':"'DLS'",
            'instrument.source.notes':"'MCR Messages to go here...'", 
            'instrument.source.power':'-1.0', 
            'instrument.source.probe':"'x-ray'",
            'instrument.source.type':"'Synchrotron X-ray Source'",
            'instrument.source.voltage':'-1000.0'}
        data_extractor = self.setUpOne(test)
        for k in data_extractor.metadata.iterkeys():
            if k in testdata[test]['m']:
                self.assertEquals(test[0]+":"+k+":"+repr(data_extractor.metadata[k]),
                                  test[0]+":"+k+":"+testdata[test]['m'][k])
            else:
                self.assertEquals(test[0]+":"+k+":"+repr(data_extractor.metadata[k]),
                                  test[0]+":"+k+":''")
        
    def testIndexTooBig(self):
        test = ('test_data/NXdetectors-2d(2x1)+i10-2.nxs',('-', 'mac119[99]'))
        data_extractor = self.setUpOne(test)
        self.assertRaises(RuntimeError, data_extractor.instrument_names, (1))

    def testNameNonexistant(self):
        test = ('test_data/NXdetectors-1d+i10-3.nxs',())
        data_extractor = self.setUpOne(test)
        #data_extractor.elements_list = ['mac119']
        data_extractor.elements_list = ['non_existant_element']
        self.assertRaises(RuntimeError, data_extractor.instrument_names, (1))

    def testDuplicateElement(self):
        test = ('test_data/NXdetectors-1d+i20-4723.nxs', ('counterTimer01.Iref',
                'counterTimer01', 'counterTimer01.liveTime', 'counterTimer01')) 
        testdata[test] = testdata \
                     [('test_data/NXdetectors-1d+i20-4723.nxs',())]
        data_extractor = self.setUpOne(test)
        self.assertRaises(RuntimeError, data_extractor.instrument_names, (1))

    def testCorrupt(self):
        test = ('test_data/synthetic/BadHeader+i10-3+Corrupted.nxs',())
        testdata = {
            'n':'BadHeader+i10-3+Corrupted.nxs: th, mac119',
            'w':set([
                 "unexpected extra root entries, reading first one only:\n"
                 "  {'entry2': 'NXentry', 'entry1': 'NXentry'}",
                 "first root entry is not entry1, using: entry2",
                 'scan_dimensions not in root entry',
                 "scan_command not in root entry"]),
            'c':'Synthetic test of a corrupted/ non GDA header'
            }
        data_extractor = self.setUpOne(test)
        data = data_extractor.instrument_names(1)
        #print data
        self.assertEquals(data, testdata['n'])
        self.warnings_check(test, data_extractor.warnings_set, testdata['w'])

    def testMissingInstrument(self):
        """ Synthetic test of a corrupted / non GDA nexus file with no
            Instrument node. """
        self.assertRaises(RuntimeError, self.setUpOne, 
            ('test_data/synthetic/NoInstrument+i10-3+Corrupted.nxs',()))

    def testUsage(self):
        self.assertEquals(usage(False), usagedata) 
        self.assertEquals(usage(True), usagedata + usage2data) 

class TestNextractCommandLineSim(unittest.TestCase):

    def testExamples(self):
        pass
    
    def testMain(self):
        #self.assertRaises(SystemExit, main, (["-otest_out", "test_data"]))
        self.assertRaises(SystemExit, main, ([]))

    def testHelp(self):
        self.assertRaises(SystemExit, main, (['-h']))

    def testBadOptions(self):
        self.assertRaises(SystemExit, main, (['-X']))

    def testBadOutput(self):
        #self.assertRaises(SystemExit, main, (["-otest_out", "test_data"]))
        self.assertRaises(SystemExit, main, (["-otest_OUT", "test_data"]))

    def testNamesDebug(self):
        self.assertRaises(SystemExit, main, (["-nd", "test_data"]))

    def testListVerbose(self):
        self.assertRaises(SystemExit, main, (["-lv", "test_data"]))

    def testBatch(self):
        self.assertRaises(SystemExit, main, (["-otest_out", "-g*", "test_data"]))

    def testBatchVerbose(self):
        self.assertRaises(SystemExit, main, (["-votest_out", "-g*1d.nxs", "test_data"]))

    def testStdOut(self):
        self.assertRaises(SystemExit, main, (["-vo-", "-g*1d.nxs", "test_data"]))

    def testNonExistantFile(self):
        self.assertRaises(SystemExit, main, (["-otest_out", "test_data/This_file_doesn't_exist"]))

    def testExamples(self):
        self.assertRaises(SystemExit, main, (['-n', '.']))

def test_suite():
    suite = unittest.TestSuite()
    for key in tests:
        loadedtests = unittest.TestLoader().loadTestsFromTestCase(TestNextractParameterised)
        for t in loadedtests:
            print t
            t.currentTest = key
        suite.addTests(loadedtests)

    #suite = TestNextractSuite() 
    suite.addTests(unittest.TestLoader().loadTestsFromTestCase(TestNextract))
    suite.addTests(unittest.TestLoader().loadTestsFromTestCase(TestNextractCommandLineSim))
    return suite

if __name__ == "__main__":
#    unittest.main()
    unittest.main(defaultTest="test_suite")
    #unittest.TextTestRunner(verbosity=2).run(suite())

#for key in tests:
#    loadedtests = unittest.TestLoader().loadTestsFromTestCase(TestNextractParameterised)
#    for t in loadedtests:
#        t.currentTest = key
     