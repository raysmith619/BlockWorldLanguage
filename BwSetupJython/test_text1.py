"""test_text.py
   Stand alone Python version of .bwif program
"""   
from BlockW import *

bW = BlockW()
bW.add(bW.axis)
bW.add(BlockW.lookAtEye, loc=(10,-10,10))
bW.add(bW.text,
       bW.txt("test_txt", bW.ALIGN_FIRST, bW.PATH_RIGHT),
       bW.size(5), bW.loc(1,1,1),
       bW.color(0,1,0))
bW.display()


