"""test_axis2.py
   Simple test - only axis, open (not using subclass)
"""   
from BlockW import *

trace = BwTrace()
trace.setAll()			
bW = BlockW(trace=trace)
bW.add(BlockW.axis)
bW.display()


