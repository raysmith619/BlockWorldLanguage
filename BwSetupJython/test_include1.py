#test_include1.py
# Smallest test
print("In test_include1.py")

from BlockW import *

global bW
bW = BlockW(tr="input,execute=2")
bW.add(bW.block, loc=(-4,2,2), size=.5, color=(1,0,0))
bW.include("test_incl_1", globals())
### TBD can't seem to get default to work: bW.include("test_incl_1")

bW.display()