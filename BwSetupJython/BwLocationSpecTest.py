""" BwLocationSpecTest.py
Investigate Jython-to-Java interface
for BlockWorld
"""
import java.util.ArrayList;
from BlockWorld import *

trace = BwTrace()
bExec = BwExec(trace)
arr1 = [1.,2.,3.]
print("arr1:", arr1)
locspec1 = BwLocationSpec(arr1)
for i in range(len(arr1)):
    if i == 0:
        print("locspec1.getX()", locspec1.getX()) 
    if i == 0:
        print("locspec1.getY()", locspec1.getY()) 
    if i == 0:
        print("locspec1.getZ()", locspec1.getZ()) 

arr2 = [1.,2.]
print("")
print("arr2:", arr2)
locspec = BwLocationSpec(arr2)
for i in range(len(arr2)):
    if i == 0:
        print("locspec.getX()", locspec.getX()) 
    if i == 0:
        print("locspec.getY()", locspec.getY()) 
    if i == 0:
        print("locspec.getZ()", locspec.getZ()) 

arr3 = [1.]
print("")
print("arr3:", arr3)
locspec = BwLocationSpec(arr3)
for i in range(len(arr3)):
    if i == 0:
        print("locspec.getX()", locspec.getX()) 
    if i == 0:
        print("locspec.getY()", locspec.getY()) 
    if i == 0:
        print("locspec.getZ()", locspec.getZ()) 
        