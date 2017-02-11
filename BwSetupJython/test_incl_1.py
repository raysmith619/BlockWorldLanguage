#test_incl_1.py
print("In test_incl_1.py")
from BlockW import *
global bW

if 'bW' not in globals() or bW == None:
    is_incl = True
    bW = BlockW(tr="input,execute")
    
bW.add(bW.block, loc=(1,0,1), size=.3, color=(0,1,0))
bW.include("test_incl_1_1")
bW.include("test_incl_1_2")
bW.add(bW.block, loc=(1,0,2), size=.3, color=(0,1,0))
if 'is_incl' in locals():       # Only display if standalone
    bW.display()
