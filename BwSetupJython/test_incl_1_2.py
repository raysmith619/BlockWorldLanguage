#test_incl_1_2.py
print("In test_incl_1_2.py")
from BlockW import *
global bW
if bW == None:
    bW = BlockW(tr="input,execute")
    
bW.add(bW.block, loc=(1,2,1), size=.2, color=(0,0,1))
bW.add(bW.block, loc=(1,2,2), size=.3, color=(0,0,1))
bW.display()
