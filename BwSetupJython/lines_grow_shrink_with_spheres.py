"""
lines_grow_shrink_with_spheres.py
Use simple lines, each with two points
Add small spheres at the line ends
"""
import random
import time
from BlockW import *

bW = BlockW(tr="input,execute,graphics")

max_line_width = .1
min_line_width = max_line_width/10.
tdly_max = .5         # Time between displays
tdly_min = tdly_max/10.
npoints = 200    # Number of points to display
npoints = 5     # TFD - shorten test
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

"""
An unsuccessful attempt to avoid black lines
"""
def colmag(num):
    #num = abs(num)
    #if num1 < .01:
    #    num1 += .1
    return num

cmds = []           # Lines
cmd_spheres = []    # spheres at line ends
for i in range(0, npoints-1, 1):        # Loop over sub groups of all points
    pt1 = points[i]
    pt2 = points[i+1]
#    cmd = bW.add(bW.line, bW.color(0,1,0))
    cl = (colmag(pt1[0]), colmag(pt1[1]), colmag(pt1[2]))
    line_width = (min_line_width * i/npoints + max_line_width * (npoints-i)/npoints)/2
    cmd = bW.add(bW.line, color=(cl[0], cl[1], cl[2]),
                        lines=line_width
                )
    cmd.addPoint(pt1[0], pt1[1], pt1[2])
    cmd.addPoint(pt2[0], pt2[1], pt2[2])
    cmd_sphere = bW.add(bW.sphere, color=(cl[0], cl[1], cl[2]), loc=(pt2[0], pt2[1], pt2[2]), size=line_width)
    cmd_spheres.append(cmd_sphere)
    cmds.append(cmd)
print("construct lines connecting {} points".format(i));

"""
Lines growing
"""
for i,cmd in enumerate(cmds):
    bW.display(cmd=cmd)
    bW.display(cmd=cmd_spheres[i])
    tdly = (tdly_max * i/npoints + tdly_min * (npoints-i)/npoints)/2
    time.sleep(tdly)
    
"""
lines shrinking back
"""
print("Erasing lines")
for i in range(len(cmds)-1, -1, -1):
    cmd = cmds[i]
    ###bW.mod(cmd=cmd, color=(0,0,0))
    bW.setEmpty(cmd)
    cmd_sphere = cmd_spheres[i];
    ###bW.setEmpty(cmd_sphere)
    
    tdly = (tdly_min * i/npoints + tdly_max * (npoints-i)/npoints)/2.
    time.sleep(tdly)

print("Blanking points")
for i in range(len(cmds)-1, -1, -1):
    cmd_sphere = cmd_spheres[i];
    ###bW.setEmpty(cmd_sphere)
    bW.mod(cmd=cmd_sphere, color=(0,0,0))
    bW.display(cmd=cmd_sphere)
    
    tdly = (tdly_min * i/npoints + tdly_max * (npoints-i)/npoints)/2.
    time.sleep(tdly)
    
print("End of %d points" % npoints);
