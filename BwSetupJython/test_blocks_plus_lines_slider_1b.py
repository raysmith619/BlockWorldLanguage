# test_blocks_plus_lines_slider1a.py
# Use simple lines, each with two points

from BlockW import *
bW = BlockW()


bW.add(bW.block, bW.loc(1,2,3), bW.size(1,2,3), bW.color(0,0,3))

bW.display()
print("End of test")
