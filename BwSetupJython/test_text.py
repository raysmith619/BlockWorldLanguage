"""test_text.py
   Stand alone Python version of .bwif program
"""   
from BlockW import *

bW = BlockW()
bW.add(bW.text, bW.txt("test_txt"), bW.size(5), bW.loc(1,1,1),
       bW.color(0,1,0))
print("Commands to execute:")
bW.bExec.list()
bW.trace.setAll()
bW.display()


