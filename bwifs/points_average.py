# points.py
import random

npoints = 100    # Number of points to display
points_base = []
colors = [[1,0,0],
          [0,1,0],
          [0,0,1],
          [1,1,0],
          [0,1,1],
          [1,0,1]
          ]     # array of points to create/display
maxval = 4.     # Maximum (x,y,z) dimensional value
minval = -maxval    # Minimum (x,y,z) dimensional value
for i in range(npoints):
    xval = random.uniform(minval, maxval)
    yval = random.uniform(minval, maxval)
    zval = random.uniform(minval, maxval)
    points_base.append([xval,yval,zval])

with open("points_average.bwif", 'w') as fout:
    fout.write("add axis;\n")
    for i, color in enumerate(colors):
        if i == 0:
            points = points_base[:]
        else:
            points_prev = points[:]
            points = []
            lenpts = len(points_prev)
            for j in range(0,lenpts,2):
                if j >= len(points_prev)-1:
                    break
                pt1 = points_prev[j] 
                pt2 = points_prev[j+1]
                pt = []
                pt.append((pt1[0]+pt2[0])/2.)
                pt.append((pt1[1]+pt2[1])/2.)
                pt.append((pt1[2]+pt2[2])/2.)
                points.append(pt)
        color = colors[i]
        if len(points) < 2:
            break
        
        color_str = "color=({},{},{})".format(
                           color[0], color[1],
                           color[2])
        fout.write("add line {}\n".format(color_str))
        for pt in points:
            px = pt[0]
            py = pt[1]
            pz = pt[2]
            fout.write("  pt=%f,%f,%f\n" %(px,py,pz))
        fout.write("  ;\n")
    fout.close()       
print("End of %d points_average" % npoints);
