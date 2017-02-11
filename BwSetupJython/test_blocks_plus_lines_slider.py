# test_blocks_plus_lines_slider.py
# Use simple lines, each with two points

from BlockW import *
bW = BlockW()

bW.slider("winH", 0, 900, 2000)
bW.slider("winW", 0, 1400, 2000)
bW.add(bW.window, size=("winW", "winH"))

""" Still unrecognized bW.nop()
"""
bW.slider("lx",-20,10,20)
bW.slider("ly",-20,10,20)
bW.slider("lz",-20,10,20)
bW.add(bW.lookAtEye, loc=("lx","ly","lz"))

                        ## Add focal point
bW.slider("px",-8,0,8)
bW.slider("py",-8,1,8)
bW.slider("pz",-8,2,8)
bW.add(bW.block, bW.color(1,0,0), bW.loc("px","py","pz"), bW.size(.1))
bW.add(bW.block, color=(1,0,0), loc=("px","py","pz"), size=.1)

bW.add(bW.block, color=(0,1,0),              loc=(1,0,0), size=.05)
bW.add(bW.line, color=(0,1,0),  pt=("px","py","pz"), pt2=(1,0,0))

bW.add(bW.block, color=(0,0,1),            loc=(1,1,0), size=(.05))
bW.add(bW.line,  color=(0,0,1), pt=("px","py","pz"),  pt2=(1,1,0))

bW.add(bW.block, color=(1,1,0),            loc=(0,1,0), size=(.05))
bW.add(bW.line,  color=(1,1,0), pt=("px","py","pz"),  pt2=(0,1,0))

bW.add(bW.block, color=(0,1,1),            loc=(-1,-2,-3), size=(.05))
bW.add(bW.line , color=(0,1,1), pt=("px","py","pz"),  pt2=(-1,-2,-3))

bW.add(bW.block, color=(1,1,1),            loc=(-3.5, -.5, 1), size=(.05))
bW.add(bW.line, color=(1,1,1), pt=("px","py","pz"),  pt2=(-3.5, -.5, 1))

bW.add(bW.block, color=(1,0,1),            loc=(-.5,-1, 1), size=(.05))
bW.add(bW.line, color=(1,0,1), pt=("px","py","pz"),  pt2=(-.5,-1, 1))
bW.add(bW.axis)

bW.display()
