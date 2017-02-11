#test_incl_3.py
print("In test_incl_3.py")
from BlockW import *
global bW
if 'bW' not in globals() or bW == None:
    is_incl = True
    bW = BlockW(tr="input,execute")

bW.add(bW.block, loc=(3,0,1), size=.2, color=(0,1,0))
bW.include("test_incl_3_1")
bW.include("test_incl_3_2")
bW.add(bW.block, loc=(3,0,2), size=.2, color=(0,1,0))
if 'is_incl' in locals():       # Only display if standalone
    bW.display()

