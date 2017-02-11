# test_blocks_plus_lines_slider1a.py
# Use simple lines, each with two points

from BlockW import *
bW = BlockW()


bW.add(bW.block, loc=(1,2,3), size=(1,2,3), color=(1,0,0))
bW.add(bW.block, loc=(2,2,3), size=(1,2,3), color=(0,1,0))
bW.add(bW.block, loc=(4,2,3), size=(1,2,3), color=(0,0,1))
print("Commands Stored:")
bW.bExec.list()
bW.display()
print("End of test")
