"""test_text.py
   Stand alone Python version of .bwif program
"""   
from BlockW import *

bW = BlockW()
bW.add(bW.axis)
bW.slider("tsize", 0, 5, 10)
bW.slider("t_x", 0, 1,10)
bW.slider("t_y", 0,1,10);
bW.slider("t_z", 0,1,10);
bW.slider("talign", bW.ALIGN_CENTER, bW.ALIGN_FIRST, bW.ALIGN_LAST);
bW.slider("tpath", bW.PATH_LEFT, bW.PATH_RIGHT, bW.PATH_DOWN)
bW.slider("leyex",-20,10,20)
bW.slider("leyey", -20, -10, 20)
bW.slider("leyez",-20,10,20)
bW.add(bW.lookAtEye, loc=("leyex", "leyey", "leyez"))
bW.add(bW.text, text=("text at loc=(t_x,t_y,t_z)", "talign", "tpath"), loc=("t_x","t_y","t_z"), size="tsize", color=(1,0,0))
bW.add(bW.text, text=("text at loc=(5,5,5)", "talign", "tpath"), loc=(5,5,5), size="tsize", color=(1,0,.4))
bW.add(bW.text, text=("loc=(2,2,2)","talign","tpath"), loc=(2,2,2), size="tsize", color=(1,0,0.5))
bW.add(bW.text, text=("loc=(0,-4,1)", "talign", "tpath"), loc=(0,-4,1), size="tsize", color=(1,0,0.6))
bW.add(bW.block, loc=(-4,2,2), size=.5, color=(1,0,0))
bW.add(bW.block, loc=(-3,2,2), size=.4, color=(1,0,.4))
bW.add(bW.block, loc=(-2,2,2), size=.3, color=(1,0,0.5))
bW.add(bW.block, loc=(-1,2,3), size=.2, color=(1,0,0.6))
bW.display()
