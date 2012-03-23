'''
This test runs the nextract script over the whole of the i10 commissioning
folder. All we are testing for here is unhandled exceptions.

This will takes several minutes to run, and will only get longer, so it is
not included in the normal test suite. 

Both "Run As" > "Python unit-test" & "Python Run" will do the same test.

@author: voo82358
'''
#import sys, pprint
#pprint.pprint(sys.path)
    
import unittest
#import nexus_utils.nextract
from nexus_utils.nextract import generic_data_extractor, main

class TestNextractI10(unittest.TestCase):

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def testCmd(self):
        main(['-otest_out', '/dls/i10/data/2011/cm2059-1'])

if __name__ == "__main__":
    unittest.main()
