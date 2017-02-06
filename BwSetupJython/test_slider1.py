"""test_slider1.py
   Simple slider debugging file
"""   
from BlockW import *

trace = BwTrace()
trace.setAll()			
bW = BlockW(trace=trace)
bW.slider("tsize",0,1,10)
bW.add("block", bW.size("tsize","tsize","tsize"), bW.loc(0,0,0), bW.color(0,1,0))


bW.display()


