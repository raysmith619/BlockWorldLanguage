"""test_slider.py
   Simple test - simple block
"""   
from BlockW import *

trace = BwTrace()
trace.setAll()			
bW = BlockW(trace=trace)
bW.add(BlockW.axis)

bW.slider("tsize",0,1,10)

bW.slider("t_x",0,2,10)
bW.slider("t_y",0,3,10)
bW.slider("t_z",0,4,10)
bW.slider("talign", BlockW.ALIGN_CENTER, BlockW.ALIGN_FIRST, BlockW.ALIGN_LAST)
bW.slider("tpath", BlockW.PATH_LEFT, BlockW.PATH_RIGHT, BlockW.PATH_DOWN)
bW.slider("leyex",-20,10,20)
bW.slider("leyey",-20,-10,20)
bW.slider("leyez",-20,10,20)

bW.add("lookateye", bW.loc("leyex", "leyey", "leyez"))
bW.add("block", bW.loc(-4,2,2), bW.size(.5), bW.color(1,0,0))
bW.add("block", bW.loc("t_x","t_y","t_z"), bW.size(.5), bW.color(1,1,0))

"""
bW.add("text", bW.txt("text at loc=-4,2,2", "talign", "tpath"),
           bW.loc("t_x", "t_y", "t_z"), bW.size("tsize"), bW.color(1,0,0))
"""
bW.display()


