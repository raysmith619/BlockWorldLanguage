"""
lines_growing_cmds_100p.py
Use simple lines, each with two points
Display cmds array
"""
import random
import time
from BlockW import *
bW = BlockW()

tdly = .1         # Time between displays
npoints = 100    # Number of points to display
points = []     # array of points to create/display
maxval = 4.     # Maximum (x,y,z) dimensional value
minval = -maxval    # Minimum (x,y,z) dimensional value
for i in range(npoints):
    xval = random.uniform(minval, maxval)
    yval = random.uniform(minval, maxval)
    zval = random.uniform(minval, maxval)
    points.append([xval,yval,zval])
    
"""
save commands for fast display
"""
for i in range(0, npoints-1, 1):        # Loop over sub groups of all points
    pt1 = points[i]
    pt2 = points[i+1]
#    cmd = bW.add(bW.line, bW.color(0,1,0))
    cmd = bW.add(bW.line, bW.color(pt1[0],pt1[1],pt1[2]))
    cmd.addPoint(pt1[0], pt1[1], pt1[2])
    cmd.addPoint(pt2[0], pt2[1], pt2[2])
print("construct lines connecting {} points".format(i));

cmds = bW.bExec.getCmds()
bW.display(cmds=cmds)
"""
for i in range(len(cmds)-1, 0, -1):
    cmd = cmds[i]
    cmd.clear()
    time.sleep(tdly)
"""    
print("End of %d points" % npoints);
