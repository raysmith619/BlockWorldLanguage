#test_include.py
print("In test_include.py")

from BlockW import *
global bW
bW = BlockW(tr="input")

bW.add(bW.block, loc=(-4,2,2), size=.5, color=(1,0,0))
bW.include("test_incl_1")
bW.add(bW.block, loc=(-3,2,2), size=.4, color=(1,0,.4))
bW.include("test_incl_2")
bW.add(bW.block, loc=(-2,2,2), size=.3, color=(1,0,0.5))
bW.include("test_incl_3")
bW.add(bW.block, loc=(-4,4,4), size=.2, color=(1,0,0.6))

bW.display()