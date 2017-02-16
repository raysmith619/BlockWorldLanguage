package BlockWorld;
import java.util.ArrayList;
import java.io.StringWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.*;
import java.nio.file.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

/*
BlockWorld - Java interface to Bw simple/learning API to java3d library
The goal here is to provide a simple programming interface with a bit more
capability than is provided by the Bw interface, keeping the user programming
interface as close as possible to that of Bw.
*/

public class BlockWJ {
    public static final String axis = "axis";
    public static final String block = "block";
    public static final String cone = "cone";
    public static final String line = "line";
    public static final String lightsource = "lightsource";
    public static final String lightSource = lightsource;
    public static final String lookateye = "lookateye";
    public static final String lookAtEye = lookateye;
    public static final String lookatcenter = "lookatCenter";
    public static final String lookAtCenter = lookatcenter;
    public static final String noop = "noop";
    public static final String pyramid = "pyramid";
    public static final String sphere = "sphere";;
    public static final String text = "text";
    public static final String text2d = "text2d";
    public static final String window = "window";
    public static final int ALIGN_CENTER = javax.media.j3d.Text3D.ALIGN_CENTER;
    public static final int ALIGN_FIRST = javax.media.j3d.Text3D.ALIGN_FIRST;
    public static final int ALIGN_LAST = javax.media.j3d.Text3D.ALIGN_LAST;
    public static final int PATH_LEFT = javax.media.j3d.Text3D.PATH_LEFT;
    public static final int PATH_RIGHT = javax.media.j3d.Text3D.PATH_RIGHT;
    public static final int PATH_UP = javax.media.j3d.Text3D.PATH_UP;
    public static final int PATH_DOWN = javax.media.j3d.Text3D.PATH_DOWN;

    public BlockWJ() throws Exception {
        this.bExec = new BwExec(new BwTrace());

        this.timeLimit = -1;         // No limit
        this.newCmd();
        this.timeBetween = 0;        // Time between files in runlist
    }
        
    /**
     * bExec - pre-existing BlockWorld execution object
     */
    
    public BlockWJ(BwExec bExec) throws Exception {
        this();
        this.bExec = bExec;     // Waste of initial bWexe
    }

    /**
     * tr - string of comma separated trace settings tr= [tr=(name=level[,name=level]*]
     */
    public BlockWJ(String tr) throws Exception {
        this();
        BwTrace trace = new BwTrace();
        String[] tracespecs = tr.split(",");
        for (String tracespec : tracespecs) {
            String name = tracespec;
            String levelstr = "1";
            int level = 1;  
            if (tracespec.contains("=")) {
                String[] parts= tracespec.split("=");
                level = Integer.parseInt(levelstr);
            } else {
                name = tracespec;
                level = 1;
            }
            trace.setLevel(name, level);  /// Coersion problem
        }
        this.bExec = new BwExec(trace);     // Waste of initial bWexe
    }
                                                              
                                                              
    /*
    General error message
    */
    public void error(String msg) {
        System.out.format("Error %s\n", msg);
        StackTraceElement[] stack_traces = Thread.currentThread().getStackTrace();
        String stack_trace = stack_traces.toString();
        System.out.format("At:\n%s\n",stack_trace);
        System.exit(1);    
    }
    /*
    name is the required graphic name
    *args are for parameter function calls
    **kwargs are for more bwif-like parameter=(values) which we may move to.
    */
    /*
    To replace Python-like name=values we revert
    to our original name(values), in which the name Function
    does the work.  Note we do not use the return values
    */
    public BwCmd add(String name, Boolean...params) {
        BwCmd cmd = this.setCmd();
        cmd.modCmd();       // Modify this command
        return cmd;
    }
    
    /*
    Modify a pre-existing command
    To replace Python-like name=values we revert
    to our original name(values), in which the name Function
    does the work.  Note we do not use the return values
    */
    public BwCmd mod(BwCmd cmd, Boolean...params) {
        cmd = this.setCmd(cmd);
        cmd.modCmd();       // Modify this command
        return cmd;
    }
        
    /*
    Set timelimit for all files in display lists
    */
    public void setTimeLimit(float timeLimit) {
        this.timeLimit = timeLimit;
        this.bExec.setTimeLimit(timeLimit);
    }
        
    /*
    Set time between displays
    Esentially the time displayed if display just stays there
    */
    public void setTimeBetween(float time) {
        this.timeBetween = time;
    }
        
            
    public void slider(String name, float...settings) throws BwException {
        BwCmd cmd = this.setCmd();
        String var_name = name;
        BwSliderSpec slider_spec = this.mkSliderSpec(name, settings);
        cmd.setSlider(slider_spec);
        this.addCmd(name="slider");
    }
    
    
    public void display() {
        this.bExec.display();
    }
    
    public void display(BwCmd cmd) {
        this.bExec.display(cmd);
    }
    
    public void display(BwCmd[] cmds) {
        this.bExec.display(cmds);
    }

    /*
    Utility functions named as key WordStart
    */
    public Boolean loc(float...values) throws Exception {
        BwCmd cmd = this.setCmd();
        float[] spec_array = values;
        BwLocationSpec loc_spec = this.mkLocationSpec(spec_array);
        cmd.setLocation(loc_spec);
        return true;
    }

    public Boolean loc(Point3f point) throws Exception {
        BwCmd cmd = this.setCmd();

        BwLocationSpec loc_spec = this.mkLocationSpec(point);
        cmd.setLocation(loc_spec);
        return true;
    }

	public Boolean color(float red, float green, float blue) {
        BwCmd cmd = this.setCmd();
        BwColorSpec color_spec = this.mkColorSpec();
        color_spec.setRed(red);
        color_spec.setGreen(green);
        color_spec.setBlue(blue);
        return true;
    }                

    public Boolean color(Color3f cpoint) {
        BwCmd cmd = this.setCmd();
        BwColorSpec color_spec = this.mkColorSpec();
        color_spec.setRed(cpoint.x);
        color_spec.setGreen(cpoint.y);
        color_spec.setBlue(cpoint.z);
        return true;
    }                
        
    
    public Boolean size(float...values) throws Exception {
        BwCmd cmd = this.setCmd();
        BwValue[] size_array = this.vals2specArray(values);
        BwSizeSpec size_spec = this.mkSizeSpec(size_array);
        cmd.setSize(size_spec);
        return true;
    }

    public Boolean partial() {
        BwCmd cmd = this.setCmd();
        Boolean setting = true;
        return true;
    }
    
    public Boolean partial(Boolean val) {
        BwCmd cmd = this.setCmd();
        Boolean setting = val;
        return true;
    }


    public void nop() {
        this.add(this.noop);
    }
    
    public Boolean pt(float...values) throws Exception {      // Abbreviation
        this.point(values);
        return true;
    }
     
    public Boolean point(float...values) throws Exception {
        BwCmd cmd = this.setCmd();
        BwValue[] spec_array = this.vals2specArray(values);        
        BwLocationSpec bwlocspec = this.mkLocationSpec(spec_array);
        cmd.addPointSpec(bwlocspec);
        return true;
    }
    
    public Boolean lines(float...values) {
        BwCmd cmd = this.setCmd();
        BwValue[] spec_array = this.vals2specArray(values);        
        if (spec_array.length != 1)
            this.error("lines not a single arg");
        cmd.setLineWidth(spec_array[0]);
        return true;
    }
            
    public Boolean txt(String text_string) {    // Can't use text
        BwCmd cmd = this.setCmd();
        cmd.setTextString(this.mkValue(text_string));
        return true;
    }
            
    public Boolean txt(String text_string, int alignment) {    // Can't use text
        BwCmd cmd = this.setCmd();
        cmd.setTextString(this.mkValue(text_string));
        cmd.setTextAlignment(this.mkValue(alignment));
        return true;        
    }
            
    public Boolean txt(String text_string, int alignment, int path) {    // Can't use text
        BwCmd cmd = this.setCmd();
        cmd.setTextString(this.mkValue(text_string));
        cmd.setTextAlignment(this.mkValue(alignment));        
        cmd.setTextPath(this.mkValue(path));
        return true;
    }

    
    public Boolean font(String font) {
        BwCmd cmd = this.setCmd();
        cmd.setTextFont(this.mkValue(font));
        return true;
    }

    
    public Boolean font(String font, String style) {
        BwCmd cmd = this.setCmd();
        cmd.setTextFont(this.mkValue(font));
        cmd.setTextStyle(this.mkValue(style));
        return true;
    }
    
    public Boolean font(String font, String style, float extrusion) {
        BwCmd cmd = this.setCmd();
        cmd.setTextFont(this.mkValue(font));
        cmd.setTextStyle(this.mkValue(style));
        cmd.setTextExtrusion(this.mkValue(extrusion));
        return true;
    }

    /*
    Support functions
    */

    public BwCmd addCmd(String name) {
        BwCmd cmd = this.setCmd();
        if (name != null) {
            BwCmdType cmd_type = cmd.name2type(name);
            if (cmd_type != BwCmdType.UNKNOWN)
                cmd.setCmdType(cmd_type);
            else
                cmd.setError("Unsupported cmd Type"
                                 + name);
        }    
        cmd.setComplete();
        if (this.trace.traceInput()) {
            String line = cmd.toString();
            System.out.println("Cmd: " + line);
        }
            
        this.bExec.addCmd(this.cmd);
        this.newCmd();       // Prepare for next cmd
        return cmd;
    }          // Return current command for possible augmenting


    public BwCmd addCmd() {
        return addCmd(null);
    }
    
       
    /*    
    Setup for next command
    */
    public BwCmd newCmd() {
        this.cmd = null;
        return this.setCmd();
    }
    
    
    /*
    Include file, assumes .py if no extension
    TBD: Can't seem to fetch globals from caller's frame
    */
    public Boolean include(String incFile) {
        /*** TBD - SUPPORT INCLUDE FOR JAVA FILES
        if globs == None:
            frames = inspect.stack()
            caller_frame = frames[-1][0]
            globs = caller_frame.f_globals
        pat_with_ext = re.compile(r'\.[^.]*$')
        if not pat_with_ext.match(incFile):
            incFile += ".py"       // Add .py extension
        incPath = this.trace.getIncludePath(incFile)
        if incPath == "":
            this.error("include file({}) was not found".format(incFile))
            return False
        execfile(incPath, globs)
        *************/
        return true;
    }
    


    /*
    Make command a empty
        1. keeping cmd entry
        2. Removing graphic, e.g. line
    */
    public void mkEmpty(BwCmd cmd) {
        cmd.setEmpty();
    }
    
    /*
    Process input files:
        .py ==> python script
        .bwif ==> BlockWorld scrip
    */
    public Boolean procFile(String inFile) {
        Pattern pattern = Pattern.compile("^(.*)[.]([^.]+)$");
        Matcher match_ftype = pattern.matcher(inFile);
        try {
            if (match_ftype.matches()) {
                String ext = match_ftype.group(2);
                if (ext.toLowerCase().equals("py"))
                    return this.procFilePy(inFile);
            }
                
            return this.parser.procFile(inFile);
        } catch (Exception e) {
            System.out.print("File processing error in " + inFile);
        }
        return true;
    }
    
    /*
    Process (Execute) standard python/Jython file
    */
    public Boolean procFilePy(String inFile) {
        String inPath = this.trace.getSourcePath(inFile);
        if (inPath.equals("")) {
            this.error("inFile({} was not found".format(inFile));
            return false;
        }
        Process p;
        try {
            p = Runtime.getRuntime().exec("python " + inPath);
            p.waitFor();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    /* 
    Run files listed in list file
    Currently only supports a sinle file type in list
    specified by the list file extension
        bwil - list of bwif files ( default)
        bwpyl - list of Jython files
    
    */    
    public Boolean runList(String listFile) throws Exception {
        Pattern pattern = Pattern.compile("^(.*)[.]([^.]+)$");
        Matcher match_ftype = pattern.matcher(listFile);

        if (match_ftype.matches()) {
            String ext = match_ftype.group(2);
            if (ext.toLowerCase().equals("pyl"))
                return this.runListPy(listFile);
        }
        
        return this.bExec.runList(listFile);
    }


    /*
    Run python files listed, one per line
    Ignore comments: text starting // to end of line
    Ignore lines consisting only of whitespace
    */
    public Boolean runListPy(String listFile) {
        String listPath = this.trace.getSourcePath(listFile);
        if (listPath.equals("")) {
            this.error("listFile({} was not found".format(listFile));
            return false;
        }
        Pattern pat_comment = Pattern.compile("^([^#]*)#");
        Matcher match_comment = pat_comment.matcher(listFile);

        BufferedReader br;
        File fin = new File(listPath);
        try {
            br = new BufferedReader(new FileReader(fin));
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            // e1.printStackTrace();
            System.out.printf("Can't open runListFile: %s\n",
                    listPath);
            return false;
        }
        String line = null;
        try {
            int filen = 0;                    // File number
            while ((line = br.readLine()) != null) {
                            // Look for // comments
                            // and blank lines
                Matcher matcher = pat_comment.matcher(line);
                if (matcher.matches()) {
                    line = matcher.group(1);
                }
                Pattern blank_line = Pattern.compile("^\b*$");
                Matcher is_blank_line = blank_line.matcher(line);
                if (is_blank_line.matches())
                    continue;
                String file = line.trim();    // Remove leading/trailing whitespace, incl newline
                filen++;
                System.out.printf("Running File %2d: %s\n", filen, file);
                if (!this.procFile(file)) {
                    System.out.printf("Run Failure File %2d: %s\n", filen, file);
                    return false;
                }
                System.out.printf("Run Success File %2d: %s\n", filen, file);
            }
        } catch (Exception e) {
            System.err.printf("IO execption in %s", listFile);
            e.printStackTrace();
            return false;
        } finally {
        }
        return true;
    }
    
    
    /*
    Setup new command for adding, if necessary
    Returns cmd
    */
    public BwCmd setCmd(BwCmd cmd) {
        this.cmd = cmd;          // Use given cmd, if one
        return this.cmd;
    }
    
    public BwCmd setCmd() {
        this.cmd = this.mkCmd();
        return this.cmd;
    }

    /*
    Erase cmd's graphic, otherwise leave the command unchanged
    */
    public void setEmpty(BwCmd cmd) {
        this.bExec.setEmpty(cmd);
    }

    public void setValue(String name, BwValue value) {
        this.symTab.setValue(name, value);
    }

    public void setValue(String name) {
        BwValue value = this.mkValue();
        this.symTab.setValue(name, value);
    }
           
    /*
    convert list of values to specification list
    */
    public ArrayList<BwValue> vals2specList(float...vals) {
        ArrayList<BwValue> speclist = new ArrayList<BwValue>();
        for ( float val : vals) {
            BwValue bwval = this.val2BwValue(val);
            speclist.add(bwval);
        }
        return speclist;
    }    
    
    /*
    convert list of values to specification Array
    */
    BwValue[] vals2specArray(float...vals) {
        BwValue[] spec_array = new BwValue[vals.length];
            
        for (int i = 0; i < vals.length; i++) {
            float val = vals[i];
            BwValue bwval = this.val2BwValue(val);
            spec_array[i] = bwval;
        }
        return spec_array;
    }
        
    
    BwValue val2BwValue(float val) {
            BwValue bwvalue = this.mkValue(val);
         return bwvalue;
    }
        
    /*
    Shortcuts to symbol table
    */
    public void addVariable(String name) {
        this.parser.addVariable(name);
    }
        
    public BwCmd mkCmd() {
        return this.parser.mkCmd();
    }
    
    public BwValue mkValue() {
        return this.parser.mkValue();
    }
    
    public BwValue mkValue(float val) {
        return this.parser.mkValue(val);
    }
    
    public BwValue mkValue(String val) {
        return this.parser.mkValue(val);
    }

    
    public BwValue mkValue(BwValue val)
            throws BwException {
        return this.parser.mkValue(val);
    }
    
    
    public BwLocationSpec mkLocationSpec(BwValue[] specArray)
            throws Exception {
        return this.parser.mkLocationSpec(specArray);
    }
    
    
    public BwLocationSpec mkLocationSpec(float[] specArray)
            throws Exception {
    	BwValue[] valArray = new BwValue[specArray.length];
    	for (int i = 0; i < specArray.length; i++) {
    		valArray[i] = this.mkValue(specArray[i]);
    	}
        return this.parser.mkLocationSpec(valArray);
    }

    private BwLocationSpec mkLocationSpec(Point3f point) throws Exception {
        return this.parser.mkLocationSpec(point);
	}

    
    public BwColorSpec mkColorSpec() {
        return this.parser.mkColorSpec();
    }
     
    public BwSizeSpec mkSizeSpec(BwValue[] values)
        throws Exception  {
        return this.parser.mkSizeSpec(values);
    }
    
    public BwSliderSpec mkSliderSpec(String name, BwValue[] valArray)
        throws BwException {
        return this.parser.mkSliderSpec(name, valArray);
    }
    
    public BwSliderSpec mkSliderSpec(String name, float[] valArray)
        throws BwException {
    	BwValue[] bwArray = new BwValue[valArray.length];
    	for (int i = 0; i < valArray.length; i++) {
    		bwArray[i] = this.mkValue(valArray[i]);
    	}
        return this.parser.mkSliderSpec(name, bwArray);
    }
    
    
    private BwCmd cmd;
    private BwExec bExec;
    private BwParser parser;
    private BwSymTable symTab;
    private BwTrace trace;
    private float timeLimit;
    private float timeBetween;
}
    