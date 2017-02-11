# BlockWorld.py
"""
BlockWorld - Python interface to Bw simple/learning API to java3d library
The goal here is to provide a simple programming interface with a bit more
capability than is provided by the Bw interface, keeping the user programming
interface as close as possible to that of Bw.
"""
import sys, traceback
import re
import inspect
import java.util.ArrayList

from BlockWorld import *
import javax.media.j3d.Text3D
import cmd

class BlockW():
    axis = "axis"
    block = "block"
    cone = "cone"
    line = "line"
    lightsource = lightSource = "lightsource"
    lookateye = lookAtEye = "lookateye"
    lookatcenter = lookAtCenter = "lookatCenter"
    noop = "noop"
    pyramid = "pyramid"
    sphere = "sphere"
    text = "text"
    text2d = "text2d"
    window = "window"
    ALIGN_CENTER = javax.media.j3d.Text3D.ALIGN_CENTER
    ALIGN_FIRST = javax.media.j3d.Text3D.ALIGN_FIRST
    ALIGN_LAST = javax.media.j3d.Text3D.ALIGN_LAST
    PATH_LEFT = javax.media.j3d.Text3D.PATH_LEFT
    PATH_RIGHT = javax.media.j3d.Text3D.PATH_RIGHT
    PATH_UP = javax.media.j3d.Text3D.PATH_UP
    PATH_DOWN = javax.media.j3d.Text3D.PATH_DOWN
    
    """
    bExec - pre-existing BlockWorld execution object
    trace - pre-existing trace object
    tr - string of comma separated trace settings tr= [tr=(name=level[,name=level]*]
    """
    
    def __init__(self, bExec=None, trace=None, tr=None):
        if bExec != None:
            self.bExec = bExec
        else:
            if trace == None:
                trace = BwTrace()
            self.bExec = BwExec(trace)
    
        if trace != None:
            self.trace = trace
        else:
            self.trace = BwTrace()

        if tr != None:
            tracespecs = tr.split(",")
            for tracespec in tracespecs:
                if "=" in tracespec:
                    (name, levelstr) = tracespec.split("=")
                    level = int(levelstr)
                else:
                    name = tracespec
                    level = 1
                
                self.trace.setLevel(name, level)  ### Coersion problem
                
        self.parser = self.bExec.getParser()
        if self.parser == None:
            self.error("No parser")
        self.symTab = self.parser.getSymTable()
        if self.symTab == None:
            self.errof("No Symbol Table")
            
        self.timeLimit = -1         # No limit
        self.newCmd()


    """
    General error message
    """
    def error(self, msg):
        print("Error {}".format(msg))
        stack_trace = traceback.format_exc()
        print("At:\n" + stack_trace)
        exit(1)    

    """
    name is the required graphic name
    *args are for parameter function calls
    **kwargs are for more bwif-like parameter=(values) which we may move to.
    """
            
    def add(self, name, *args, **kwargs):
        cmd = self.setCmd()
        graphic_type = BwGraphic.str2type(name)
        if graphic_type == BwGraphic.Type.UNKNOWN:
            self.error("Unrecognized command type " + name)
        cmd.setGraphicType(graphic_type)
        """ Support point or pt### for points """
        pat_pt = re.compile(r'^(pt\d*|point)$', re.IGNORECASE)    
        for key in kwargs:
            key = key.lower()
            if key == "loc":
                self.loc(*kwargs[key])
            elif key == "color":
                self.color(*kwargs[key])
            elif key == "font":
                self.font(*kwargs[key])
            elif key == "lines":
                self.lines(*kwargs[key])
            elif key == "size":
                args = kwargs[key]
                if type(args) is tuple:
                    self.size(*args)
                else:
                    self.size(args)
            elif pat_pt.match(key):
                self.point(*kwargs[key])
            elif key == "txt":
                self.txt(*kwargs[key])
        self.addCmd(name="add")
        return cmd
    
    """
    Modify a pre-existing command
    """        
    def mod(self, cmd, **kwargs):
        cmd = self.setCmd(cmd)
            
        if "pt" in kwargs or "point" in kwargs:
            cmd.clearPoints()   # All points change
            
        for key in kwargs:
            key = key.lower()
            if key == "loc":
                self.loc(*kwargs[key])
            elif key == "color":
                self.color(*kwargs[key])
            elif key == "font":
                self.font(*kwargs[key])
            elif key == "lines":
                self.lines(*kwargs[key])
            elif key == "size":
                self.size(*kwargs[key])
            elif key == "pt" or key == "point":
                self.point(*kwargs[key])
            elif key == "txt":
                self.txt(*kwargs[key])
                
        cmd.modCmd()       # Modify this command
        return cmd
        
    """
    Set timelimit for all files in display lists
    """
    def setTimeLimit(self, timeLimit):
        self.timeLimit = timeLimit
        self.bExec.setTimeLimit(timeLimit);
        
            
    def slider(self, *values):
        cmd = self.setCmd()
        var_name = values[0]
        #self.addVariable(var_name)
        settings = values[1:]
        spec_array = self.vals2specArray(*settings)
        slider_spec = self.mkSliderSpec(var_name, spec_array)
        cmd.setSlider(slider_spec)
        self.addCmd(name="slider")

    
    
    def display(self, trace='', cmd=None, cmds=None):
        try:
            if cmd != None:
                self.bExec.display(cmd)
            elif cmds != None:
                self.bExec.display(cmds)
            else:
                """ From internal list """
                self.bExec.display()    
        except:
            exc = sys.exc_info()
            if exc[1].message != None:
                print("display error:" + exc[1].message)
            if isinstance( sys.exc_info()[1], java.lang.Throwable ):
                sys.stderr.write( "AS JAVA:\n" )
                sys.exc_info()[1].printStackTrace() # java part
            else:
                sys.stderr.write( "NO JAVA TRACE:\n" )
                sys.stderr.write( "AS PYTHON:\n" )
                traceback.print_exc()
            raise
    """
    Utility functions named as key WordStart
    """
    def loc(self, *values):
        cmd = self.setCmd()
        spec_array = self.vals2specArray(*values)
        loc_spec = self.mkLocationSpec(spec_array)
        cmd.setLocation(loc_spec)

    def color(self, *values):
        cmd = self.setCmd()
        spec_list = self.vals2specList(*values)
        """ Propagate final value to end """
        while len(values) < 3:
            values.append(values[values.size()-1])
            
        spec_list = self.vals2specList(*values)
        color_spec = self.mkColorSpec()      # Why doesn't BwSizeSpec(0,.0.,0.) work
        for i,sp in enumerate(spec_list):
            if i == 0:
                color_spec.setRed(sp)
            elif i == 1:
                color_spec.setGreen(sp)
            elif i == 2:
                color_spec.setBlue(sp)
                
        cmd.setColor(color_spec)
        
    
    def size(self, *values):
        cmd = self.setCmd()
        size_array = self.vals2specArray(*values)
        size_spec = self.mkSizeSpec(size_array)
        cmd.setSize(size_spec)

    def partial(self, *values):
        cmd = self.setCmd()
        setting = True
        if len(values) == 1:
            setting = values[0]
        elif len(values) > 1:
            self.error("Invalid partial setting")
        #cmd.setIsPartial(setting)

    def nop(self):
        return self.add(BlockW.noop)            # TBD
    
    def pt(self, *values):      # Abbreviation
        self.point(self, *values)
     
    def point(self, *values):
        cmd = self.setCmd()
        spec_array = self.vals2specArray(*values)        
        bwlocspec = self.mkLocationSpec(spec_array)
        cmd.addPointSpec(bwlocspec)
    
    def lines(self, *values):
        cmd = self.setCmd()
        spec_list = self.vals2specList(*values)        
        if spec_list.len != 1:
            self.error("lines no single arg")
        cmd.setLineWidth(spec_list[0])
            
    def txt(self, *values):    # Can't use text
        cmd = self.setCmd()
        if len(values) < 1:
            self.error("txt: No text")
        text_string = values[0]
        cmd.setTextString(self.mkValue(text_string))
        settings = values[1:]
        spec_list = self.vals2specList(*settings)

        if spec_list.size() >= 1:
            cmd.setTextAlignment(spec_list[0])        
        if spec_list.size() >= 2:
            cmd.setTextPath(spec_list[1])
        if spec_list.size() > 2:
            self.error("txt: " + str(str(spec_list.size())
                        + " args:"
                        + " is too many args for text"))

    
    def font(self, *values):    # name, style
        cmd = self.setCmd()
        spec_list = self.vals2specList(values)
        if spec_list.len() < 1:
            self.error("No args for font")
        cmd.setTextFont(spec_list[0])
        if spec_list.len() > 1:
            cmd.setTextStyle(spec_list[1])
        if spec_list.len() > 2:
            cmd.setTextExtrusion(spec_list[2])
        if spec_list.len() > 3:
            self.error(self, "Too many text args")
        self.fontName = values[0]
        if len(values) > 1:
            self.fontStyle = values[1]

    """
    Support functions
    """
    def addCmd(self, name=None):
        cmd = self.setCmd()
        if name is not None:
            cmd_type = cmd.name2type(name)
            if cmd_type != BwCmdType.UNKNOWN:
                cmd.setCmdType(cmd_type)
            else:
                cmd.setError("Unsupported cmd Type"
                             + name)
            
        cmd.setComplete()
        if self.trace.traceInput():
            line = cmd.toString()
            print("Cmd: " + line)
            
        self.bExec.addCmd(self.cmd)
        self.newCmd()       # Prepare for next cmd
        return cmd          # Return current command for possible augmenting
    
    """    
    Setup for next command
    """
    def newCmd(self):
        self.cmd = None
        self.optList = []
        return self.setCmd()
    
    
    """
    Include file, assumes .py if no extension
    TBD: Can't seem to fetch globals from caller's frame
    """
    def include(self, incFile, globs=None):
        if globs == None:
            frames = inspect.stack()
            caller_frame = frames[-1][0]
            globs = caller_frame.f_globals
        pat_with_ext = re.compile(r'\.[^.]*$')
        if not pat_with_ext.match(incFile):
            incFile += ".py"       # Add .py extension
        incPath = self.trace.getIncludePath(incFile)
        if incPath == "":
            self.error("include file({}) was not found".format(incFile))
            return False
        execfile(incPath, globs)
        return True

            
    """
    Process input files:
        .py ==> python script
        .bwif ==> BlockWorld scrip
    """
    def procFile(self, inFile):
        pat_ftype = re.compile(r'^(.*)\.([^.]+)$')
        match_ftype = pat_ftype.match(inFile)
        try:
            if match_ftype:
                ext = match_ftype.group(2)
                if ext.lower() == "py":
                    return self.procFilePy(inFile)
                
            return self.parser.procFile(inFile)
        except:
            print("File processing error in " + inFile)
            if isinstance( sys.exc_info()[1], java.lang.Throwable ):
                sys.stderr.write("AS JAVA:")
                sys.exc_info()[1].printStackTrace() # java part
            else:
                sys.stdout.write( "NO JAVA TRACE:\n" )
                print("AS PYTHON:")                
                traceback.print_exc()
            
    
    """
    Process (Execute) standard python/Jython file
    """
    def procFilePy(self, inFile):
        inPath = self.trace.getSourcePath(inFile)
        if inPath == "":
            self.error("inFile({} was not found".format(inFile))
            return False
        execfile(inPath)
        return True
    
    """ 
    Run files listed in list file
    Currently only supports a sinle file type in list
    specified by the list file extension
        bwil - list of bwif files ( default)
        bwpyl - list of Jython files
    
    """    
    def runList(self, listFile):
        pat_ftype = re.compile(r'^(.*)\.([^.]+)$')
        match_ftype = pat_ftype.match(listFile)
        if match_ftype:
            ext = match_ftype.group(2)
            if ext.lower() == "pyl":
                return self.runListPy(listFile)
        
        return self.bExec.runList(listFile)


    """
    Run python files listed, one per line
    Ignore comments: text starting # to end of line
    Ignore lines consisting only of whitespace
    """
    def runListPy(self, listFile):
        listPath = self.trace.getSourcePath(listFile)
        if listPath == "":
            self.error("listFile({} was not found".format(listFile))
            return False
        pat_comment = re.compile(r'^([^#]*)#')
        pat_blanks = re.compile(r'^\s*$')
        
        fileno = 0
        with open(listPath) as inf:
            for line in inf:
                m = pat_comment.match(line)
                if m:
                    line = m.group(1)   # Remove comment
                mb = pat_blanks.match(line)
                if mb:
                    continue        #Ignore blank lines
                line = line.strip()
                fileno += 1
                print("Running File {}: {}".format(fileno, line)) # Removing leading and trailing whitespace    
                if not self.procFilePy(line):
                    return False
        return True
    
    
    """
    Setup new command for adding, if necessary
    Returns cmd
    """
    def setCmd(self, cmd=None):
        if cmd != None:
            self.cmd = cmd      #Use given cmd, if one
        if self.cmd == None:
            self.cmd = self.mkCmd()
        return self.cmd


    def setValue(self, name, value=None):
        if value == None:
            value = self.mkValue()
            
        self.symTab.setValue(name, value)
           
    """
    convert list of values to specification list
    """
    def vals2specList(self, *vals):
        speclist = java.util.ArrayList()
        for val in vals:
            bwval = self.val2BwValue(val)
            speclist.append(bwval)
        return speclist    
    
    """
    convert list of values to specification Array
    """
    def vals2specArray(self, *vals):
        spec_array = []
            
        for val in vals:
            bwval = self.val2BwValue(val)
            spec_array.append(bwval)
        return spec_array    
    
    def val2BwValue(self, val):
        bwvalue = self.mkValue()
        if type(val) is str:
            bwvalue = bwvalue.setVariable(val)
            bwvalue.setTypeVariable()
            bwvalue.setName(val)
        elif type(val) is int:
            bwvalue.setValue(val)
        elif type(val) is float:
            bwvalue.setValue(val)
        else:
            self.error("Unrecognized value type" + str(type(val)))
            
        return bwvalue
        
    """
    Shortcuts to symbol table
    """
    def addVariable(self, name):
        self.parser.addVariable(name)
        
    def mkCmd(self):
        return self.parser.mkCmd()
    
    def mkValue(self, *val):
        if len(val) == 0:
            return self.parser.mkValue()
        if len(val) == 1:            
            return self.parser.mkValue(val[0])
        else:
            self.error("mkValue: " + str(val.len()) + "args is not supported")
    
    def mkValueSet(self, val):
        return self.parser.mkValueSet(val)
    
    def mkLocationSpec(self,specArray):
        return self.parser.mkLocationSpec(specArray)

    
    def mkColorSpec(self):
        return self.parser.mkColorSpec()
     
    def mkSizeSpec(self, values):
        return self.parser.mkSizeSpec(values)
    
    def mkSliderSpec(self, name, valArray):
        return self.parser.mkSliderSpec(name, valArray)
    
