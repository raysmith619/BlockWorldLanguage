"""test_text.py
   Stand alone Python version of .bwif program
"""   
from BlockWorld import *
from BlockW import *

class test_text(BlockW):
    def load(self):
        self.add(BlockW.axis)

trace = BwTrace()
trace.setAll()			
tt = test_text(trace=trace)

tt.load()
tt.display()


