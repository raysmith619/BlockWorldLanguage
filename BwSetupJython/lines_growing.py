"""
lines_growing.py
Use simple lines, each with two points
"""
import random
import time
from BlockW import *
bW = BlockW()

tdly = 2         # Time between displays
npoints = 100    # Number of points to display
points = []     # array of points to create/display
maxval = 4.     # Maximum (x,y,z) dimensional value
minval = -maxval    # Minimum (x,y,z) dimensional value
for i in range(npoints):
    xval = random.uniform(minval, maxval)
    yval = random.uniform(minval, maxval)
    zval = random.uniform(minval, maxval)
    points.append([xval,yval,zval])
    

for i in range(2, npoints+1, 1):        # Loop over sub groups of all points
    for npt in range(2, i):
        for ip1 in range(0, npt-1):     # First point of line
            ip2 = ip1 + 1
            pt = points[ip1]
            pt2 = points[ip2]               # Second point of line
            cmd = bW.add(bW.line, bW.color(0,1,0))
            cmd.addPoint(pt[0], pt[1], pt[2])
            cmd.addPoint(pt2[0], pt2[1], pt2[2])
    print("display {} points".format(i));
    bW.display()
    time.sleep(tdly)
    #bW.bExec.erase()
    
print("End of %d points" % npoints);
