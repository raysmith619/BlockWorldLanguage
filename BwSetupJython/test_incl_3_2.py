#test_incl_3_2.py
print("In test_incl_3_2.py")
from BlockW import *
global bW
if 'bW' not in globals() or bW == None:
    is_incl = True
    bW = BlockW(tr="input,execute")

bW.add(bW.block, loc=(3,2,1), size=.2, color=(1,0,1))
bW.add(bW.block, loc=(3,2,2), size=.3, color=(1,0,1))
if 'is_incl' in locals():       # Only display if standalone
    bW.display()

