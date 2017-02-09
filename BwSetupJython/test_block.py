"""test_block.py
   Simple test - simple block
"""   
from BlockW import *

trace = BwTrace()
trace.setAll()			
bW = BlockW(trace=trace)
block = "block"
bW.add(BlockW.axis)
bW.add(block, bW.loc(-4,2,2), bW.size(.5), bW.color(1,0,0))
bW.add(block, bW.loc(3,2,2), bW.size(.4), bW.color(1,0,.4))
bW.add(block, bW.loc(-2,2,2), bW.size(.3), bW.color(1,0,0.5))
bW.add(block, bW.loc(-4,4,4), bW.size(.2), bW.color(1,0,0.6))

bW.display()


