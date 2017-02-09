# points.py
import random

from BlockW import *
bW = BlockW()


npoints = 100    # Number of points to display
points = []     # array of points to create/display
maxval = 4.     # Maximum (x,y,z) dimensional value
minval = -maxval    # Minimum (x,y,z) dimensional value
for i in range(npoints):
    xval = random.uniform(minval, maxval)
    yval = random.uniform(minval, maxval)
    zval = random.uniform(minval, maxval)
    points.append([xval,yval,zval])
    

cmd = bW.add(bW.line, bW.loc(1,1,1),
       bW.color(0,1,0))

print("Setup display {} points".format(npoints))
for pt in points:
    cmd.addPoint(pt[0], pt[1], pt[2])
bW.add(bW.text, bW.txt("after points"), bW.size(5), bW.color(1,1,1), bW.loc(0,0,0))    
print("display");
bW.display()
print("End of %d points" % npoints);
