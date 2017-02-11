#test_incl_2_1.py
print("In test_incl_2_1.py")
from BlockW import *
global bW
if 'bW' not in globals() or bW == None:
    is_incl = True
    bW = BlockW(tr="input,execute")

bW.add(bW.block, loc=(2,1,1), size=.2, color=(0,0,1))
bW.add(bW.block, loc=(2,1,2), size=.3, color=(0,0,1))
if 'is_incl' in locals():       # Only display if standalone
    bW.display()

