package BlockWorld;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import BlockWorld.BwValue.BwValueType;
/**
 * Basic recursive decent parsing package for block world
 * Routines restore input/stack upon failure
 * Failure may be coming to the end of input before completion
 * Intermediate failures return token stack to beginning of search
 * to facilitate rescanning for alternate targets
 * @author raysm
 * 
 * 
 *   Input Syntax:
 *   Examples:
 *   	add cube loc=1,2,3 size=2,4,6 color=red
 *   	delete ALL
 *   	delete LAST
 *   	mod loc=(4,,)	// change loc x of last
 *      variable = slider(cur,min,max)
 *      slider(name,cur,min,max)
 *
 *	 comment :=					// Handled by Tokenizer
 *		"//" to end of line
 *		;
 *   command :=
 *   	setCmd ";"
 *      sliderCmd ";"
 *   	| command_base command_args ";"
 *   	;
 *   command_base :=
 *   	"set" variable_name "=" number	// Simple to start
 *   	| command_name 
 *   		(
 *   		  // EMPTY
 *   		 | range
 *   		 | Graphic_type
 *  		)
 *   	;
 *   
 *   command_args :=
 *   	command_arg
 *   	| command_args command_arg
 *   	;
 *   command_arg :=
 *    	Graphic_type
 *      |   location_spec
 *      |	color_spec
 *      |	size_spec
 *      ;
 *   command_name :=
 *   	"display" // display current list
 *   	"add"		// add Graphic to display list
 *   	"delete"	// delete Graphic(s)
 *   	"move"		// move Graphic(s)
 *   	"modify"	// modify Graphic(s)
 *   	;
 *   range_spec :=		// IFF cmd_name is NOT "add"
 *   	number
 *   	| number "_" number
 *   	| "ALL" 			// All Graphics
 *   	| "LAST"			// Last Graphic
 *   	;
 *   loc_spec :=
 *   	"loc" "=" value_list
 *   	;
 *   valueList :=
 *   	"(" valueList ")"
 *   	| value
 *   	| value "," value_list
 *   	;
 *   Graphic_type :=
 *   	"cube"
 *   	| "sphere"
 *   	| "cone"
 *   	| "viewer"
 *   	| "vp"
 *   	;
 *   color_spec :=
 *   	
 *   	"color" "=" "red"
 *   	"color" "=" "green"
 *   	"color" "=" "blue"
 *   	"color" "=" "number"
 *   	"color" "=" valueList	// color red, green, blue
 *   						//  if less than 3, replicate last
 *   	;
 *
 *   size_spec :=
 *   	"size" "=" valueList		// size x, y, z
 *   							// if less than needed replicate last
 *   	;
 *   
 *   slider :=
 *   	"slider" "(" nameValueList ")"
 *   	;
 *   value :=
 *   	number
 *   	| variable_name
 *   	| slider
 *   	;
 *   
 *   variable_name :=
 *   	[a-zA-Z_][0-9a-zA-z_]*
 *   	;
 */

/**
 * 
 * @author raysm
 * @param trace - diagnostic/debugging trace level
 * @param bwcmds - destination for found commands
 */
public class BwParser {

	public BwParser(BwTrace trace, BwCmds bcmds) {
		this.tokenizer = new BwTokenizer(trace);
		this.trace = trace;
		this.bCmds = bcmds;
		this.pgmQuit = false;
		this.errorCount = 0;
		this.symTable = new BwSymTable(trace);
		BwValue.setParser(this);
		
	}

	/**
	 * Add more input string to end
	 * @param str
	 */
	public void addStr(String str) {
		this.tokenizer.addStr(str);
	}
	
/**
 * Debugging tool
 * Only used to track special cases
 * TBD - add traceCkit level
 * @param tag to identify location of trace
 */
	public void ckit(String tag) {
		String var_name = "height";
		if (trace.traceVerbose()) {
			if (inTable(var_name)) {
				System.out.printf(String.format("ckit:%s: %s is in table\n", tag, var_name));
			} else {
				System.out.printf(String.format("ckit:%s: %s NOT in table\n", tag, var_name));
			}
		}
	}
/**
 * Parsing routines
 * Recursive decent
 * We may put these in there own Graphic some day
 */
	/**
	 *   command :=
	 *   	  cmdInclude
	 *   	| cmdNoOp
	 *   	| setCmd
	 *   	| sliderCmd
	 *   	| command_base command_args ";"
	 *   	;
	 * 
	 */
	public boolean cmd(BwCmd cmd) throws BwException {
		cmd.setFirstSrcFileName(this.tokenizer.srcFileName());
		cmd.setFirstSrcLineNo(this.tokenizer.srcLineNo());
		cmd.setFirstSrcLine(this.tokenizer.srcLine());		
		markState("cmd:= commandBase commandArgs");
		ckit("cmd - beginning");
		this.currentCmd = cmd;			// Store current command for diagnostics
		if (cmdInclude(cmd)) {
			cmd.setSrcFileName(this.tokenizer.srcFileName());
			cmd.setSrcLineNo(this.tokenizer.srcLineNo());
			cmd.setSrcLine(this.tokenizer.srcLine());
			acceptProduction("include cmd");
		} else if (cmdNoOp(cmd)) {
			cmd.setSrcFileName(this.tokenizer.srcFileName());
			cmd.setSrcLineNo(this.tokenizer.srcLineNo());
			cmd.setSrcLine(this.tokenizer.srcLine());
			acceptProduction("noOp cmd");
		} else if (setCmd(cmd)) {
			cmd.setSrcFileName(this.tokenizer.srcFileName());
			cmd.setSrcLineNo(this.tokenizer.srcLineNo());
			cmd.setSrcLine(this.tokenizer.srcLine());
			acceptProduction("set cmd");
		} else if (sliderCmd(cmd)) {
				cmd.setSrcFileName(this.tokenizer.srcFileName());
				cmd.setSrcLineNo(this.tokenizer.srcLineNo());
				cmd.setSrcLine(this.tokenizer.srcLine());
				acceptProduction("set cmd");
		} else {
			if (! commandBase(cmd)) {
				backupState();	
				return false;
			}
			cmd.setSrcFileName(this.tokenizer.srcFileName());
			cmd.setSrcLineNo(this.tokenizer.srcLineNo());
			cmd.setSrcLine(this.tokenizer.srcLine());
			try {
				if (! commandArgs(cmd)) {
					backupState();	
					return  false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				backupState();
				return false;
			}

			if (!tokenType(";")) {
				syntaxError("Unterminated command");
				acceptProduction("unterminated cmd");	// bypass tokens
				return true;		// cmd is over (accepted) but in error
			}
			
			acceptProduction("complete cmd");
		}
		cmd.setComplete();
		if (trace.traceInput()) {
			System.out.printf("%s\n", cmd.toStringExtended());
			if (cmd.getCmd_type() == BwCmdType.INCLUDE_FILE_END)
				System.out.printf("\n");
		}
		return true;
	}
	
/**
 * implement include file facility
 * by inserting commands into the stream
 * Terminating ";" is optional
 * @throws BwException 
 */
	boolean cmdInclude(BwCmd cmd) throws BwException {
		markState("cmd:= include filename" );
		if (!tokenType("include")) {
			backupState();
			return false;
		}
		if (!tokenType('"') && !tokenType("'")) {
			syntaxError("include file spec is missing");
			matchProduction();
			return true;
		}
		matchProduction();

		String incl_file = this.tokenizer.tokenStr();
					/**
					 * Mark beginning of include
					 */
		BwCmd cmd_start = new BwCmd(BwCmdType.INCLUDE_FILE);
		cmd_start.setIncludeFile(incl_file);
		cmd_start.setComplete();
		if (trace.traceInput()) {
			System.out.printf("%s\n\n", cmd_start.toStringExtended());
		}
		bCmds.addCmd(cmd_start);

						/**
						 * Mark end of include
						 */
		cmd.setCmdType(BwCmdType.INCLUDE_FILE_END);
		cmd.setIncludeFile(incl_file);
		
		Path pathck = Paths.get(incl_file);
		if (!pathck.isAbsolute()) {
			Path pathinc = Paths.get("bwifs", incl_file);
			String fileinc = pathinc.toString();
			incl_file = fileinc;
		}
		
		BwParser inc_parser =
				new BwParser(this.trace, this.bCmds);
		if (!inc_parser.procFile(incl_file)) {
			inc_parser.syntaxError(
					String.format("include file %s"
					+ " parsing error", incl_file));
			return true;
		}
						/**
						 * trailing ";" is optional to
						 * look like C preprocessor include statement
						 */
		if (tokenType(";")) {
						// Ignored
		}
		return true;	// accept even if file error
	}
/**
 * NO-OP command- most likely ";"
 */
	boolean cmdNoOp(BwCmd cmd) {
		markState("NoOp;");
		if (tokenType(";")) {
			cmd.setCmdType(BwCmdType.NO_OP);
			acceptProduction(";");
			return true;
		}
		backupState();
		return false;
	}
	
	
	/**
	 * set cmd - arithmetic command
	 * Initially just set variable = value
	 * "SET" is required to ease the parsing task
	 *  setCmd :=
	 *  	"set" variable_name "=" value ";"
	 *  	;
	 */
	public boolean setCmd(BwCmd cmd) throws BwException {
		markState("setCmd := set name = value");
		if (!tokenType("set")) {
			backupState();
			return false;
		}
		BwValue name_var = new BwValue();
		if (!variableName(name_var)) {
			syntaxError("setCmd has no variable");
			return false;
		}
		String var_name = name_var.varName();
		
		if (!tokenType("=")) {
			syntaxError("setCmd has no '=' after variable name:"
					+ var_name);
			return false;
		}
		BwValue value = new BwValue();
		if (!value(value)) {
			syntaxError("setCmd has no value after '='");
			return false;
		}
		if (!tokenType(";")) {
			syntaxError("setCmd has no terminating ';'");
			return false;
					
		}
		
		setValue(var_name, value);		// Immediate value set
		cmd.setCmdType(BwCmdType.SET_CMD);			// Create comand type
		cmd.setVariable(var_name);
		cmd.setValue(value);
		cmd.setComplete();
		
		acceptProduction("setCmd");
		if (!inTable(var_name)) {
			System.out.printf(String.format("setCmd: variable %s not in table",  var_name));
		}
		return true;
	}

	
	/**
	 * sliderCmd - create slider command
	 * just set variable = slider setting
	 *  sliderCmd :=
	 *  	slider ";"
	 *  	;
	 */
	public boolean sliderCmd(BwCmd cmd) throws BwException {
		markState("slider ;");
		BwSliderSpec spec = new BwSliderSpec();
		if (!slider(spec)) {
			backupState();
			return false;
		}
		cmd.setCmdType(BwCmdType.SLIDER);
		cmd.setSlider(spec);

		if (!tokenType(";")) {
			syntaxError("setCmd has no terminating ';'");
			return false;
					
		}
		String var_name = spec.getVarName();
		BwValue value = spec.getCurVal();
		setValue(var_name, value);					// Immediate value set
		cmd.setCmdType(BwCmdType.SLIDER);			// Create command type
		cmd.setVariable(var_name);
		cmd.setValue(value);
		cmd.setComplete();
		
		acceptProduction("sliderCmd");
		if (!inTable(var_name)) {
			System.out.printf(String.format("setCmd: variable %s not in table",  var_name));
		}
		return true;
	}

	
	/**
	 * slider - create slider expression
	 *
	 *  slider :=
	 *  	"slider" name
	 *  	| "slider" name value
	 *  	| "slider" name value, value
	 *  	| "slider" name value, value, value
	 *  	;
	 *  
	 *  
	 */
	public boolean slider(BwSliderSpec spec) throws BwException {
		markState("slider");
		if (!tokenType("slider")) {
			backupState();
			return false;
		}
		ArrayList<BwValue> vlist = new ArrayList<BwValue>();
		if (!listSpec(vlist)) {
			backupState("no list");
			return false;
		}
		BwValue firstpart = vlist.get(0);
		String var_name;		// Set if variable name
		if (firstpart.getType() == BwValueType.VARIABLE) {
			var_name = firstpart.varName();
			spec.setTitle(var_name);
			spec.setVarName(var_name);
			vlist.remove(0);	// Remove name part
		}
								// slider ... x => min 0, cur x, max 5*x
		if (vlist.size() == 1) {
			spec.setMinVal(new BwValue(0));
			spec.setCurVal(vlist.get(0));
			spec.setMaxVal(new BwValue(vlist.get(0).floatValue()*5));	// Must be const
		} else if (vlist.size() == 2) {	// slider ... x y => min: 0, cur x, max y
			spec.setMinVal(new BwValue(0));
			spec.setCurVal(vlist.get(0));
			spec.setMaxVal(vlist.get(1));
		} else if (vlist.size() == 3) {
			spec.setMinVal(vlist.get(0));
			spec.setCurVal(vlist.get(1));
			spec.setMaxVal(vlist.get(2));
		} else {
			syntaxError(String.format("%d is an unsupported number of slider values",
					vlist.size()));
			return true;		// accept but an error
		}

		acceptProduction("slider");
		return true;
	}

	
	/**
	 * variableName :=
	 * 	word
	 * @param value with name returned iff found
	 * @return true iff found
	 */
	public boolean variableName(BwValue value) throws BwException {	// use name part
		markState("variableName");
		BwToken token = nextToken();
		if (token.type != StreamTokenizer.TT_WORD) {
			backupState("not WORD");
			return false;
		}
		String token_name = token.str;
		if (BwGraphic.str2type(token_name)
				!= BwGraphic.Type.UNKNOWN) {
			backupState("a graphic name");
			return false;
		}
		String var_name = token_name;
		value.setVariable(var_name);

		if (!inTable(var_name)) {
			if (trace.traceVerbose()) {
				System.out.printf("New variable: %s\n", var_name);
			}
			setValue(var_name, new BwValue());		// Add variable to table
		}
		matchProduction("name");
		return true;
	}

	
	/**
	 * Check table for variable
	 * no exception
	 */
	public boolean inTable(String var_name) {
		return symTable.inTable(var_name);
	}
	
	/**
	 * get value from symbol table
	 * @param variable name
	 * @return current value, null if not in table
	 */
	float getValue(String name) throws BwException {
		float value = symTable.getValue(name);
		return value;
	}
	
	/**
	 * get string value from symbol table
	 * @param variable name
	 * @return current value, null if not in table
	 */
	String getStringValue(String name) throws BwException {
		String value = symTable.getStringValue(name);
		return value;
	}
	
	/**
	 * get value object from symbol table
	 * @param variable name
	 * @return current value, null if not in table
	 */
	public BwValue getValueObject(String name) {
		BwValue valobj = symTable.getValueObject(name.toLowerCase());
		return valobj;
	}

	
	/**
	 * set value in symbol table
	 * tracks value through chain of variables
	 * @param variable name
	 */
	public void setValue(String name, BwValue val) throws BwException {

		BwValue val2 = val;
		int chain_count = 0;
		int chain_max = 10;
		while (val2.isLinked()) {
			String var_name = val2.varName();
			if (++chain_count > chain_max) {
				throw new BwException(String.format(
						"setValue %s chain max %d exceeded to %s not in sym table",
						name, chain_max, var_name));
			}	
			val2 = getValueObject(var_name.toLowerCase());		// Lowercase all variable names
			if (val2 == null) {
				throw new BwException(String.format(
						"setValue %s tracks to %s not in sym table",
						name, var_name));

			}
		}
		
		this.symTable.setValue(name, val2);
	}
	
	
	/**
	 * stringConstant:=
	 * 	"\"" string "\""
	 *  | "'" string "'"
	 *  @param - returned value
	 */
	boolean stringConstant(BwValue value) throws BwException {
		markState("stringConst");
		if ((tokenType('"') || tokenType("'"))) {
			value.setValue(this.tokenizer.tokenStr());
			matchProduction("stringConst");
			return true;
		}
		backupState("not a string constant");
		return false;
	}
	
	
	/**
	 * stringValue:=
	 * 	variableName | string_constant
	 *  ;
	 *  @param - returned value
	 */
	boolean stringValue(BwValue value) throws BwException {
		markState("stringValue:= stringConstant | variableName");
		if (stringConstant(value)) {
			matchProduction("stringConst");
			return true;
		}
	
		if (variableName(value)) {
			matchProduction("stringVariable");
			return true;
		}
		backupState();
		return false;
	}
	
	
	/**
	 * value:=
	 * 	variableName | number
	 *  ;
	 *  @param - returned value
	 */
	boolean value(BwValue value) throws BwException {
		markState("value:= number | variableName");
		if (number(value)) {
			matchProduction("number");
			return true;
		}
	
		if (variableName(value)) {
			matchProduction("number");
			return true;
		}
		backupState();
		return false;
	}
	
	/**
	 * 
	 *   command_base :=
	 *   	command_name
	 *   		(
	 *   		  range
	 *   		  | graphic_type
	 *   		)
	 *   	;
	 */
	private boolean commandBase(BwCmd cmd) throws BwException {
		markState("commandBase:=commandName commandGraphic");
		if (!commandName(cmd)) {
			backupState();
			return false;
		}

		if  (commandGraphicType(cmd)) {	// variable names cannot be graphic types
			matchProduction("name graphic");
			return true;
		}
		
		if (commandRange(cmd)) {
			matchProduction("commandName range");
			return true;
		}
		
		matchProduction("commandBase:= commandName");
		return true;
	}
	
	
/**
 * Check for command name	
 */
	public boolean commandName(BwCmd cmd) {
		markState("commandName");
		BwToken token = nextToken();
		
		if (token.type != StreamTokenizer.TT_WORD) {
			backupState("not WORD");
			return false;
		}
		String namestr = token.str;
		BwCmdType type = cmd.name2type(namestr);
		if (type == BwCmdType.UNKNOWN) {
			backupState("not recognized cmd name");
			return false;
		}
		cmd.setCmdType(type);
		matchProduction("");
		return true;
	}

	
	public boolean commandGraphicType(BwCmd cmd) {
		markState("commandGraphic");
		BwToken token = nextToken();
		if (token.type != StreamTokenizer.TT_WORD) {
			backupState("not WORD");
			return false;
		}
		String type_str = token.str;
		BwGraphic.Type type = BwGraphic.str2type(type_str);
		if (type == BwGraphic.Type.UNKNOWN) {
			backupState("not recognized graphic name");
			return false;
		}
		cmd.setGraphicType(type);
		matchProduction();
		return true;
	}
	
/**
 * Check for command range	
 *   range_spec :=
 *   	number
 *   	| number "_" number
 *   	| "FIRST"			// First Graphic
 *   	| "ALL" 			// All Graphics
 *   	| "LAST"			// Last Graphic
 */
	public boolean commandRange(BwCmd cmd) throws BwException {
		markState("commandRange:= VALUE - VALUE");
		BwValue value1 = new BwValue();
		BwValue value2 = new BwValue();
		if (value(value1) && tokenType("-") && value(value2)) {
			BwRange range = new BwRange(BwRange.BwRangeType.LIST,
					value1, value2);
			cmd.setRange(range);
			matchProduction("number - number");
			return true;
		}
		backupState();
		
		markState("commandRange:= VALUE");
		if (value(value1)) {
			BwRange range = new BwRange(BwRange.BwRangeType.SINGLE,
					value1, new BwValue());
			cmd.setRange(range);
			matchProduction("-number");
			return true;
		}
		// TBD - check for range FIRST, LAST...
		backupState("not VALUE");
		return false;
	}

	
	/**
	 * Process command arg
	 *      location_spec
	 *      |	color_spec
	 *      |	size_spec
	 *      |	point_spec
	 *   	;
	 * @throws Exception 
	 */
	public boolean commandArg(BwCmd cmd) throws Exception {
		markState("commandArg:=...arg types");
		if (locationSpec(cmd)) {
			matchProduction("locationSpec");
			return true;
		}
		if (colorSpec(cmd)) {
			matchProduction("colorSpec");
			return true;
		}
		if (sizeSpec(cmd)) {
			matchProduction("sizeSpec");
			return true;
		}
		if (pointSpec(cmd)) {
			matchProduction("pointSpec");
			return true;
		}
		if (lineSpec(cmd)) {
			matchProduction("lineSpec");
			return true;
		}
		backupState();
		return false;
	}

	
	/**
	 * Process command args
	 *   command_args :=
	 *   	// EMPTY
	 *   	command_arg
	 *   	| command_args command_arg
	 *   	;
	 * @throws Exception 
	 */
	public boolean commandArgs(BwCmd cmd) throws Exception {
		markState("commandArgs:=commandArg | commandArgs commandArg");
							// Process zero or more args
		int narg = 0;
		while (commandArg(cmd)) {
			narg++;
		}
		
		matchProduction("narg = " + narg);
		return true;
	}

	
	/**
	 * colorSpec :+
	 *   	"color" "=" 
	 *   			( 
	 *   				colorName
	 *   				| "(" number_list ")"
	 *   				| number_list
	 *   			)
	 *   	;
	 */
	public boolean colorSpec(BwCmd cmd)
		throws Exception {
		markState("colorSpec");
		if (!tokenType("color")) {
			backupState();
			return false;
		}
		if (!tokenType("=")) {
			backupState();
			return false;
		}
		markState("color value name");			// After =
		BwToken token = nextToken();
		if (token.type == StreamTokenizer.TT_WORD) {
			String color_name = token.str.toLowerCase();
			if (color_name.equals("red")
					|| color_name.equals("green")
					|| color_name.equals("blue")) {
				cmd.setColor(new BwColorSpec(color_name));
				matchProduction("color name");
				matchProduction("full spec");
				return true;
			}
			backupState("not explicit color");
		}
		backupState("not color value name");
		
		markState("color rgb strengths");
		ArrayList<BwValue> dlist = new ArrayList<BwValue>();
		if (listSpec(dlist)) {
			cmd.setColor(new BwColorSpec(dlist));
			matchProduction("rgb list");
			matchProduction("spec");
			return true;
		}
		backupState();
		return false;
	}


	/**
	 * Line specification
	 * line=width....
	 * Currently only width
	 * @param cmd
	 * @return
	 * @throws Exception
	 */
	public boolean lineSpec(BwCmd cmd)
		throws Exception {
		markState("lineSpec");
		if (!tokenType("lines")) {
			backupState();
			return false;
		}
		if (!tokenType("=")) {
			backupState("no \"=\"");
			return false;
		}
		ArrayList<BwValue> dlist = new ArrayList<BwValue>();
		if (listSpec(dlist)) {
			if (dlist.size() != 1) {
				syntaxError("line width and only width must be present");
				matchProduction();
				return true;
			}
			cmd.setLineWidth(dlist.get(0));
			matchProduction();
			return true;
		}

		backupState();
		return false;
	}

	
	public boolean locationSpec(BwCmd cmd)
		throws Exception {
		markState("locationSpec");
		if (!tokenType("loc")) {
			backupState();
			return false;
		}
		if (!tokenType("=")) {
			backupState("no \"=\"");
			return false;
		}
		ArrayList<BwValue> dlist = new ArrayList<BwValue>();
		if (listSpec(dlist)) {
			cmd.setLocation(new BwLocationSpec(dlist));
			matchProduction();
			return true;
		}

		backupState();
		return false;
	}

	/**
	 * Add one or more points to cmd
	 * pt_spec :=
	 *   'pt' '='
	 *   	(
	 *   	listSpec
	 *   	| fileSpec
	 *   	)
	 *   
	 * @param cmd
	 * @return
	 * @throws Exception
	 */
	public boolean pointSpec(BwCmd cmd)
		throws Exception {
		markState("pointSpec");
		if (!tokenType("pt")) {
			backupState();
			return false;
		}
		if (!tokenType("=")) {
			backupState("no \"=\"");
			return false;
		}
		ArrayList<BwValue> dlist = new ArrayList<BwValue>();
		if (listSpec(dlist)) {
			cmd.addPointSpec(new BwLocationSpec(dlist));
			matchProduction();
			return true;
		}

		backupState();
		return false;
	}

		
		public boolean sizeSpec(BwCmd cmd)
			throws Exception {
			markState("sizeSpec");
			if (!tokenType("size")) {
				backupState();
				return false;
			}
			if (!tokenType("=")) {
				backupState("no \"=\"");
				return false;
			}

			ArrayList<BwValue> dlist = new ArrayList<BwValue>();
			if (listSpec(dlist)) {
				cmd.setSize(new BwSizeSpec(dlist));
				matchProduction("list");
				return true;
			}

			backupState();
			return false;
		}
	
	/**
	 * listSpec
	 *  listSpec :=
	 *  	"(" csvList ")"
	 *  	| csvList
	 *  	;
	 * 
	 *  
	 */
	public boolean listSpec(ArrayList<BwValue> list) throws BwException {
		markState("listSpec:= ( cvsList ) | cvsList");
		list.clear();
		int paren_depth = 0;
		if (tokenType("(")) {
			paren_depth++;
		}
		if (csvList(list)) {
			if (paren_depth > 0) {
				for (int i = 0; i < paren_depth; i++) {
					if (!tokenType(")")) {
						syntaxError("No closing right paren");
						return false;
					}
				}
			}
			matchProduction();
			return true;
		}
		backupState();
		return false;
	}

	
	/**
	 * comma separated list
	 * csvList :=
	 * 		value
	 * 		|
	 * 		value "," csvList
	 */
	public boolean csvList(ArrayList<BwValue> list) throws BwException {
		markState("csvList:= value | value csvList");
		list.clear();
		BwValue val = new BwValue();
		while (value(val)) {
			list.add(val);
			if (tokenType(",")) {
				val = new BwValue();	// New value object
				continue;
			}
			break;
		}
		if (list.size() > 0) {
			matchProduction();
			return true;
		}
		backupState("empty list");
		return false;
	}
	/**
	 * Recognize number
	 * We use the hack of an array to pass back numeric value
	 */
	public boolean number(BwValue val) {
		markState("number");
		BwToken token = nextToken();
		if (token.type == StreamTokenizer.TT_NUMBER) {
			val.setValue((float) token.number);
			matchProduction();
			return true;
		}
		backupState();
		return false;
	}
	
	
	/**
	 * Check if this token
	 */
	public boolean tokenType(int type) {
		return this.tokenizer.tokenType(type);
	}
	
	
	/**
	 * Check if this token string
	 * case insensitive
	 */
	public boolean tokenType(String str) {
		return this.tokenizer.tokenType(str);
	}

	/**
	 * push back toke for retry
	 *
	 */
	public void pushBackToken(BwToken token) {
		this.tokenizer.pushBack(token);
	}
	
	
	/**
	 * state marking/backup to facilitate alternate production targets
	 *   Exampl  sequence => first_try | second_try | third_try
	 */
	public void markState() {
		markState("");
	}
	public void markState(String str) {
		this.tokenizer.markState(str);
	}
	
	public void backupState() {
		backupState("");
	}
	
	public void backupState(String str) {
		this.tokenizer.backupState(str);
	}

	public  void acceptProduction(String str) {
		this.tokenizer.matchProduction(str, true);
	}
	
	public void matchProduction() {
		matchProduction("");
	}

	public  void matchProduction(String str, boolean...accepts) {
		this.tokenizer.matchProduction(str, accepts);;
	}

	
	public BwToken nextToken() {
		return this.tokenizer.nextToken();
	}

	/**
	 * Printing full parsing state
	 * 
	 */
	public void printFullState(String heading) {
		this.tokenizer.printFullState(heading);
	}

	
	/**
	 * Add to display
	 */
	public void buildDisplay(BwCmd cmd) {
		cmd.setProcessed();
	}
	
	
	/**
	 * Process command if complete and not yet processed
	 *  @return true continue processing, false - quit processing cmds 
	 */
	public boolean procCmd(BwCmd cmd) {
		if (cmd.isError()) {
			return false;
		}
		if (!cmd.isComplete())
			return false;		// Stop at first incomplete command

		if (cmd.getCmd_type() == BwCmdType.QUIT_PROGRAM) {
			return quitProgram(cmd);
		}			
			
		buildDisplay(cmd);
		return true;
	}

	
	/**
	 * Setup for quit program
	 * If return is true - program will continue
	 * 
	 */
	public boolean quitProgram(BwCmd cmd) {
		this.pgmQuit = true;
		return false;
	}

	
	/**
	 * Process file adding commands
	 * @param fileName - file name to process
	 * @return true if success
	 */
	public boolean procFile(String fileName) {
		if (!this.tokenizer.setFile(fileName)) {
			Path path = Paths.get(fileName);
			System.err.printf("Input file %s failed setup\n",  path);
			return false;
		}
		
		try {
			while (true) {
				BwCmd cmd = new BwCmd();
				if (cmd(cmd)) {
					this.bCmds.addCmd(cmd);
				} else {
					break;
				}
			}
		} catch (BwException ex) {
			String errorMessage = ex.getMessage();
			System.err.printf("BwException in file %s: %s\n", fileName,
					errorMessage);
			if (tokenizer.srcLineNo() > 0) {
	    		System.out.printf("Parsing exception in file %s"
						+ " At line %d: %s\n",
						fileName, tokenizer.srcLineNo(), tokenizer.srcLine());
				
			}
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	
	
/**
 * Get process input from string
 * @return true if not quit
 * @param str command input
 */
	public boolean procInput(String str) throws BwException {		// true - continue processing cmds
		addStr(str);
										// Process commands on this string
		while (true) {
			BwCmd cmd = new BwCmd();
			if (cmd(cmd)) {
				this.bCmds.addCmd(cmd);
			} else {
				break;
			}
		}
												// Process completed commands
												// quitting on quit or error
		for (BwCmd cmd : this.bCmds.getCmds()) {
			if (!cmd.isComplete() || cmd.isError())
				break;		// Go no further
			if (!cmd.isProcessed())
				procCmd(cmd);
		}
		return this.bCmds.isContinue() && !this.bCmds.isError();
	}
	
	/**
	 * Set verbose level
	 * @return previous verbose level
	 */
	public int setVerbose() {
		return this.setVerbose(1);
	}
	
	/*
	 * Set verbose level
	 * @return previous verbose level
	 */
	public int setVerbose(int level) {
		int lev = this.verbose;
		this.verbose = level;
		return lev;
	}

	/**
	 * Provide access to symbol table to facilitate:
	 *  1. Use in execution
	 *  2. Setting by control operation
	 */
	public BwSymTable getSymTable() {
		return this.symTable;
	}
	
	/**
	 * Indicate and report syntax error
	 * 
	 * @param msg - error message
	 */
	public void syntaxError(String msg) {
		this.errorCount++;
		System.out.printf("\nError %d: %s\n",  this.errorCount, msg);
		String errloc = "";
		if (this.tokenizer.isSrcFile()) {
			errloc += "File: " + this.tokenizer.srcFileName() + " ";
			if (this.tokenizer.srcLineNo() > 0) {
				errloc += String.format("Line %d: ",  this.tokenizer.srcLineNo());
				errloc += this.tokenizer.srcLine();
			}
		} else {
			errloc = this.tokenizer.srcLine(); 
		}
		System.out.printf("  At %s\n", errloc);
			
								/**
								 * On error skip past next ';'
								 */
		System.out.printf("  Skipping tokens to next ';' >>>");
		BwToken tok;
		while (true) {
			tok = nextToken();
			System.out.printf(" %s",  tok);
			if (tok.type == ';' || tok.type == StreamTokenizer.TT_EOF)
				break;
		}
		System.out.printf("\n\n");
		this.currentCmd.setError(msg);		// Tag cmd as in error
	}

	private boolean pgmQuit;		// true ==> quit	
	private BwTokenizer tokenizer;			// Token access
	private int verbose;
	private BwTrace trace;
	private int errorCount;
	private BwSymTable symTable;
	private BwCmd currentCmd;		// Current command for diagnostics
	private BwCmds bCmds;			// command storage access
									// File info for diagnostics is in tokenizer
	
	
	/**
	 * Parsing self test
	 * Parameters starting with "-" name are program options
	 *  -v or -verbose - set verbose level
	 *  -trace (comma-separated list of names specify tracing levels see BwTrace.java)
	 * Subsequent paramaters, if any, are command string passed to parsing
	 */
	public static void main(String args[]) {
		int i = 0;
		BwTrace trace = new BwTrace();
		BwCmds bCmds = new BwCmds(trace);
		trace.setAccept(1);
		trace.setTokenAccept(1);
		trace.setTokQueue(1);
		trace.setAll();
		trace.clearAll();
		trace.setInput(1);
		BwParser parser = new BwParser(trace, bCmds);
		int nfile = 0;						// Count files processed
		
											// Process flags -<...>
		for (; i < args.length; i++) {
			String arg = args[i];
			Pattern pattern = Pattern.compile("^-{1,2}(.*)");
			Matcher matcher = pattern.matcher(arg);
			if (!matcher.matches())
				break;		// No more flags
			String opt = matcher.group(1);
			if (opt.matches("^(verbose|v)$")) {
				trace.setVerbose(1);
			} else if (opt.matches("^(ifile|if)$")) {
				String in_file = args[++i];
				nfile++;					// Count files processed
				if (trace.traceInput()) {
					System.out.printf("File: %d: %s\n", nfile, in_file);
				}
				if (!parser.procFile(in_file)) {
					System.err.printf("Quitting\n");
					System.exit(1);
				}
			} else if (opt.equalsIgnoreCase("trace")) {
				String trace_spec = args[++i];
				String [] trace_levels = trace_spec.split(",");
				for (String trace_level : trace_levels) {
					trace.setLevel(trace_level);
				}
			} else {
				System.err.printf("Unrecognized option:'%s' - Quitting", arg);
				System.exit(1);
			}
		}
							// Use sample if no data commands
		if (nfile == 0 && i >= args.length) {
			i = 0;				// Start at beginning of sample
			args = new String[] {
				"add cone; delete 1",
				"add block color=red loc=.1 size=.5",
				"add sphere color=blue loc=.1,.2,.3 size=.5",
				"add cone color=green loc=.2,.3,.4 size=.1",
				"display",
				};
		};
		
		for (; i < args.length; i++) {
			String arg = args[i];
			Pattern pat_ends_with_semi = Pattern.compile(";[ \b\t]*$");
			Matcher matcher = pat_ends_with_semi.matcher(arg);
			if (!matcher.matches()) {
				arg += ";";			// Terminate last command on line
			}
									// Process all commands on line
			parser.addStr(arg);
			parser.tokenizer.tokQueue(trace.traceQueue(), "addStr");
			BwCmd cmd = new BwCmd();
			try {
				do {
					cmd = new BwCmd();
				} while (parser.cmd(cmd));
			} catch (BwException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (cmd.isError()) {
				System.out.printf("Error: %s\n",
						cmd.errorDescription());
				break;
			}
		}
		System.out.printf("End of Test\n");
	}
}
