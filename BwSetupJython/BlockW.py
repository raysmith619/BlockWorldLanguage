# BlockWorld.py
"""
BlockWorld - Python interface to Bw simple/learning API to java3d library
The goal here is to provide a simple programming interface with a bit more
capability than is provided by the Bw interface, keeping the user programming
interface as close as possible to that of Bw.
"""
import sys, traceback
import re
import java.util.ArrayList

from BlockWorld import *
import javax.media.j3d.Text3D

class BlockW():
    axis = "axis"
    block = "block"
    cone = "cone"
    line = "line"
    lightSource = "lightsource"
    lookateye = "lookateye"
    lookatcenter = "lookatCenter"
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
    
    def __init__(self, bExec=None, trace=None):
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
        """
        Setup controls window, if not already in place
        """
        if bExec == None:
            self.controls = BwControls(trace, self.bExec)
            self.controls.setVisible(True)
        else:
            self.controls = bExec.getControls()     # Obtain current controls

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
        
    def add(self, name, *args):
        cmd = self.setCmd()
        graphic_type = BwGraphic.str2type(name)
        if graphic_type == BwGraphic.Type.UNKNOWN:
            self.error("Unrecognized command type " + name)
        cmd.setGraphicType(graphic_type)
        self.addCmd(name="add")
            

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

    
    
    def display(self, trace='', **kwargs):
        try:
            self.bExec.display()
        except:
            exc = sys.exc_info()
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


    
    def pt(self, *values):      # Abbreviation
        self.point(self, *values)
     
    def point(self, *values):
        cmd = self.setCmd()
        spec_list = self.vals2specList(*values)        
        bwlocspec = self.mkLocationSpec(spec_list)
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
            
        self.setCmdOpts()
        cmd.setComplete()
        if self.trace.traceInput():
            line = cmd.toString()
            print("Cmd: " + line)
            
        self.bExec.addCmd(self.cmd)
        self.newCmd()       # Prepare for next cmd
        
    
    """    
    Setup for next command
    """
    def newCmd(self):
        self.cmd = None
        self.optList = []
        return self.setCmd()
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
        execfile(inFile)
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
    def setCmd(self):
        if self.cmd == None:
            self.cmd = self.mkCmd()
            self.optList = []   # list of settings
        return self.cmd
    
    """
    Set options collected for this command
    """
    def setCmdOpts(self):
        cmd = self.cmd
        for op in self.optList:
            if op == "loc":
                pass
            elif op == "size":
                pass
            elif op == "pt" or "point":
                pass
            elif op == "lines":
                pass
            elif op == "txt":
                pass
            else:
                cmd.error("Don't recognize option" + op)


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
    
