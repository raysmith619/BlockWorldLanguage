#test_incl_2.py
print("In test_incl_2.py")
from BlockW import *
global bW
if 'bW' not in globals() or bW == None:
    is_incl = True
    bW = BlockW(tr="input,execute")

bW.add(bW.block, loc=(2,1,0), size=.3, color=(0,1,0))
bW.include("test_incl_2_1")
bW.include("test_incl_2_2")
bW.add(bW.block,loc=(2,2,0), size=.3, color=(0,1,0))
if 'is_incl' in locals():       # Only display if standalone
    bW.display()

