"""
points_growing.py
"""
import random
import time
from BlockW import *
bW = BlockW()

tdly = 1         # Time between displays
npoints = 100    # Number of points to display
points = []     # array of points to create/display
maxval = 4.     # Maximum (x,y,z) dimensional value
minval = -maxval    # Minimum (x,y,z) dimensional value
for i in range(npoints):
    xval = random.uniform(minval, maxval)
    yval = random.uniform(minval, maxval)
    zval = random.uniform(minval, maxval)
    points.append([xval,yval,zval])
    

for i in range(2, npoints+1, 1):
    for npt in range(2, i):
        cmd = bW.add(bW.line,
                     bW.color(0,1,0))
        for pt in points[0:npt]:
            cmd.addPoint(pt[0], pt[1], pt[2])

    print("display");
    bW.display()
    time.sleep(tdly)
    #bW.bExec.erase()
    
print("End of %d points" % npoints);
