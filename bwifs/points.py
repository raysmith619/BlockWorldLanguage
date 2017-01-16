# points.py
import random

from doctest import _SpoofOut
npoints = 100    # Number of points to display
points = []     # array of points to create/display
maxval = 4.     # Maximum (x,y,z) dimensional value
minval = -maxval    # Minimum (x,y,z) dimensional value
for i in range(npoints):
    xval = random.uniform(minval, maxval)
    yval = random.uniform(minval, maxval)
    zval = random.uniform(minval, maxval)
    points.append([xval,yval,zval])

with open("points.bwif", 'w') as fout:
#    fout.write("add axis;\n")
    fout.write("add line color=(1,0,1) \n")
    for pt in points:
        px = pt[0]
        py = pt[1]
        pz = pt[2]
        fout.write("  pt=%f,%f,%f\n" %(px,py,pz))
    fout.write("  ;\n")
    fout.close()       
print("End of %d points" % npoints);
