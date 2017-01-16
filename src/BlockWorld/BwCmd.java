/**
 * 
 */
package BlockWorld;

import java.util.Vector;

import javax.media.j3d.BranchGroup;

/**
 * @author raysm
 * One display command
 * May be a object, display, or manipulation
 */


public class BwCmd {
	static final int GRID_NONE = -1;		// No graphic placed
	
	/**
	 * @return the color
	 */
	public BwColorSpec getColor() {
		return color;
	}

	/**
	 * 
	 */
	public BwCmd() {
		this(BwCmdType.UNKNOWN);
	}
	
	public BwCmd(BwCmdType type) {
		this.cmdType = type;
		this.complete = false;			// true - command is complete
		this.processed = false;			// true command has been processed
		this.grid = GRID_NONE;			// Set to no graphics 
	}

	/**
	 * Setup branch group
	 */
	public void setBranchGroup(BranchGroup branch_group) {
		this.branchGroup = branch_group;
	}

	
	/**
	 * clear / erase command
	 * @return
	 */
	public void clear() {
	    if (this.grid != BwCmd.GRID_NONE) {
	    	if (this.branchGroup != null)
	    		this.branchGroup.removeChild(this.grid);
	    	else
	    		System.out.printf("Missing branchGroup\n");
	    }
		this.complete = false;			// true - command is complete
		this.processed = false;			// true command has been processed
	    this.grid = BwCmd.GRID_NONE;
	}
	
	public int getGrid() {
		return this.grid;
	}

	public void setGrid(int grid) {
		this.grid = grid;
	}

	/**
	 * @return the cmd_type
	 */
	public BwCmdType getCmd_type() {
		return cmdType;
	}

	/**
	 * @param cmd_type the cmd_type to set
	 */
	public void setCmdType(BwCmdType cmd_type) {
		this.cmdType = cmd_type;
	}

	
	public void setComplete() {
		this.complete = true;
	}

	/**
	 * Set command as error, storing description
	 */
	public void setError(String description) {
		this.error = true;
		this.errorDescription = description;
	}
	/**
	 * Check if command is complete
	 * @return true if complete
	 */
	
	public boolean isComplete() {
		return this.complete;
	}
	
	
	public boolean isProcessed() {
		return this.processed;
	}
	
	public void setProcessed() {
		this.processed = true;
	}
	
	public boolean isError() {
		return error;
	}

	public String errorDescription() {
		return this.errorDescription;
	}

	public BwCmdType name2type(String name_str) {
		String name = name_str.toLowerCase();
		switch (name) {
			case"display":
				return BwCmdType.DISPLAY_SCENE;
			case"add":
				return BwCmdType.ADD_OBJECT;
			case "delete":
				return BwCmdType.DELETE_OBJECT;
			case "move":
				return BwCmdType.MOVE_OBJECT;
			case "modify":
				return BwCmdType.MODIFY_OBJECT;
			case "quit":
				return BwCmdType.QUIT_PROGRAM;
			case "list":
				return BwCmdType.LIST_CMD;
			case "duplicate":
				return BwCmdType.DUPLICATE_CMD;
			case "include":
				return BwCmdType.INCLUDE_FILE;
			case "includeEnd":
				return BwCmdType.INCLUDE_FILE_END;
			case "noop":
				return BwCmdType.NO_OP;
			case "set":
				return BwCmdType.SET_CMD;
			default:
				return BwCmdType.UNKNOWN;
		}
	}

	public String cmdName() {
		BwCmdType type = this.cmdType;
		switch (type) {
			case DISPLAY_SCENE:
				return "display";
			case ADD_OBJECT:
				return "add";
			case DELETE_OBJECT:
				return "delete";
			case MOVE_OBJECT:
				return "move";
			case MODIFY_OBJECT:
				return "modify";
			case QUIT_PROGRAM:
				return "quit";
			case LIST_CMD:
				return "list";
			case DUPLICATE_CMD:
				return "duplicate";
			case INCLUDE_FILE:
				return "include";
			case INCLUDE_FILE_END:
				return "includeEnd";
			case NO_OP:
				return "noop";
			case SET_CMD:
				return "set";
			case SLIDER:
				return "slider";
				
			case UNKNOWN:
			default:
				return "UNKNOWN";
		}
	}

	/**
	 * Add first or additional point specification
	 * Use location specification
	 * @param loc_spec
	 */
	
	public void addPointSpec(BwLocationSpec loc_spec) {
		if (this.points == null) {
			this.points = new Vector<BwLocationSpec>();
		}
					// TBD support abreviation:
					// leaving one or more coordinates
					// unchanged.
		points.addElement(loc_spec);
	}

	public Vector<BwLocationSpec> getPoints() {
		return this.points;		// null -> no points
	}
	
	
	public void setLocation(BwLocationSpec loc_spec) {
		this.loc = loc_spec;
	}
	
	public void setColor(BwColorSpec color) {
		this.color = color;
	}
	
	
	public void setGraphicType(BwGraphic.Type type) {
		this.graphicType = type;
	}
	
	
	public void setRange(BwRange range) {
		this.range = range;
	}

	public void setSize(BwSizeSpec size) {
		this.size = size;
	}
	
	public String toString() {
		String str = "";
		if (!this.complete) {
			if (str != "") str += " ";
			str += "INCOMPLETE";
		}
		if (this.cmdType == BwCmdType.INCLUDE_FILE) {
			if (str != "") str += " ";
			str += cmdName();
			str += " " + this.getIncludeFile();
		} else if (this.cmdType == BwCmdType.INCLUDE_FILE_END) {
				if (str != "") str += " ";
				str += cmdName();
				str += " " + this.getIncludeFile();
		} else if (this.cmdType == BwCmdType.SET_CMD) {
			if (str != "") str += " ";
			if (this.setCmdName) {
				str += cmdName() + " ";		// Include "SET"
			}
			str += this.setVariableName;
			str += " = ";
			str += this.setValue;
		} else if (this.cmdType == BwCmdType.SLIDER) {
			BwSliderSpec slider = getSliderSpec();
			if (str != "") str += " ";
			str += cmdName();
			str += slider;
		} else {
			if (str != "") str += " ";
			str += cmdName();
			BwRange range = this.range;		
			if (range != null) {
				str += " ";
				str += range;
			}
			BwGraphic.Type graphic = this.graphicType;
			if (graphic != null) {
				str += " ";
				str += graphic;
			}
			BwColorSpec color = this.color;
			if (color != null) {
				str += " ";
				str += "color=" + color;
			}
			BwLocationSpec loc = this.loc;
			if (loc != null) {
				str += " ";
				str += "loc=" + loc;
			}
			BwSizeSpec size = this.size;
			if (size != null) {
				str += " ";
				str += "size=" + size;
			}
			
			BwValue lineWidth = this.lineWidth;
			if (lineWidth != null) {
				str += " ";
				str += "lines=" + lineWidth;
			}
			
			Vector<BwLocationSpec> points = this.points;
			if (points != null) {
				str += " ";
				for (BwLocationSpec point : points) {
					str += " pt=" + point;
				}
			}
		}
		str += ";";				// Terminate command
		return str;
	}

	/**
	 * Command string with any values evaluated
	 */
	public String toStringEval() throws BwException {
		String str = "";
		if (!this.complete) {
			if (str != "") str += " ";
			str += "INCOMPLETE";
		}
		if (this.cmdType == BwCmdType.INCLUDE_FILE) {
			if (str != "") str += " ";
			str += cmdName();
			str += " " + this.getIncludeFile();
		} else if (this.cmdType == BwCmdType.INCLUDE_FILE_END) {
				if (str != "") str += " ";
				str += cmdName();
				str += " " + this.getIncludeFile();
		} else if (this.cmdType == BwCmdType.SET_CMD) {
			if (str != "") str += " ";
			if (this.setCmdName) {
				str += cmdName() + " ";		// Include "SET"
			}
			str += this.setVariableName;
			str += " = ";
			str += this.setValue.toStringEval();			// We don't count this
		} else if (this.cmdType == BwCmdType.SLIDER) {
			BwSliderSpec slider = getSliderSpec();
			if (str != "") str += " ";
			str += cmdName();
			str += slider.toStringEval();
		} else {
			if (str != "") str += " ";
			str += cmdName();
			BwRange range = this.range;		
			if (range != null) {
				str += " ";
				str += range;
			}
			BwGraphic.Type graphic = this.graphicType;
			if (graphic != null) {
				str += " ";
				str += graphic;
			}
			BwColorSpec color = this.color;
			if (color != null) {
				str += " ";
				str += "color=" + color.toStringEval();
			}
			BwLocationSpec loc = this.loc;
			if (loc != null) {
				str += " ";
				str += "loc=" + loc.toStringEval();
			}
			BwSizeSpec size = this.size;
			if (size != null) {
				str += " ";
				str += "size=" + size.toStringEval();
			}
			
			BwValue lineWidth = this.lineWidth;
			if (lineWidth != null) {
				str += " ";
				str += "lines=" + lineWidth.toStringEval();
			}

			Vector<BwLocationSpec> points = this.points;
			if (points != null) {
				str += " ";
				for (BwLocationSpec point : points) {
					str += " pt=" + point.toStringEval();
				}
			}
		}
		str += ";";				// Terminate command
		return str;
	}

	
	/**
	 * Default cmd extended display
	 * No trailing newline - caller should add trailing newline
	 */
	public String toStringExtended() throws BwException {
		String cmdstr = "";
		String cmd_str = this.toString();
		String cmd_eval_str = this.toStringEval();
		cmdstr = String.format("     Cmd: %s", cmd_str);
		if (!cmd_eval_str.equals(cmd_str))
			cmdstr += String.format("\n    +Cmd: %s", cmd_eval_str);

		return cmdstr;
	}
	
	
	/**
	 * Set variable, value
	 * For possible reexecution
	 */
	public void setValue(BwValue value) {
		this.setValue = value;
	}

	public String getSetVariableName() {
		return this.setVariableName;
	}

	public void setVariable(String name) {
		this.setVariableName = name;
	}
	
	public BwValue getSetValue() {
		return this.setValue;
	}

	/**
	 * set up slider info
	 */
	public void setSlider(BwSliderSpec spec) {
		this.sliderSpec = spec;
	}

	
	/**
	 * get slider specification
	 */
	public BwSliderSpec getSliderSpec() {
		return this.sliderSpec;
	}
	
	
	private boolean complete;				// Completely specified
	/**
	 * @return the loc
	 */
	public BwLocationSpec getLoc() {
		return loc;
	}

	/**
	 * @param loc the loc to set
	 */
	public void setLoc(BwLocationSpec loc) {
		this.loc = loc;
	}

	/**
	 * @return the srcLineNo
	 */
	public int getSrcLineNo() {
		return this.srcLineNo;
	}

	/**
	 * @param srcLineNo the srcLineNo to set
	 */
	public void setSrcLineNo(int srcLineNo) {
		this.srcLineNo = srcLineNo;
	}

	/**
	 * Set first, in case we cross
	 * @param srcLineNo the srcLineNo to set
	 */
	public void setFirstSrcLineNo(int srcLineNo) {
		this.firstSrcLineNo = srcLineNo;
	}

	/**
	 * @return the srcLine
	 */
	public String getSrcLine() {
		return srcLine;
	}

	/**
	 * @param srcLine the srcLine to set
	 */
	public void setSrcLine(String srcLine) {
		this.srcLine = srcLine;
	}

	/**
	 * @param srcLine the srcLine to set
	 */
	public void setFirstSrcLine(String srcLine) {
		this.firstSrcLine = srcLine;
	}

	public int getFirstSrcLineNo() {
		return this.firstSrcLineNo;
	}

	public String getFirstSrcLine() {
		return this.firstSrcLine;
	}

	public String getFirstSrcFileName() {
		return this.firstSrcFileName;
	}

	/**
	 * @return the srcFileName
	 */
	public String getSrcFileName() {
		return srcFileName;
	}

	/**
	 * @param srcFileName the srcFileName to set
	 */
	public void setSrcFileName(String srcFileName) {
		this.srcFileName = srcFileName;
	}

	public BwValue getLineWidth() {
		return this.lineWidth;
	}

	public void setLineWidth(BwValue lineWidth) {
		this.lineWidth = lineWidth;
	}

	/**
	 * @param srcFileName the srcFileName to set
	 */
	public void setFirstSrcFileName(String srcFileName) {
		this.firstSrcFileName = srcFileName;
	}

	/**
	 * @return the size
	 */
	public BwSizeSpec getSize() {
		return size;
	}

	private BwRange range;					// Operation range
	private BwLocationSpec loc;				// location specification
	/**
	 * @return the graphicType
	 */
	public BwGraphic.Type getGraphicType() {
		return graphicType;
	}

	
	/**
	 * @return the includeFile
	 */
	public String getIncludeFile() {
		return includeFile;
	}

	/**
	 * @param includeFile the includeFile to set
	 */
	public void setIncludeFile(String includeFile) {
		this.includeFile = includeFile;
	}



	
	private boolean processed;				// Processed (e.g. added to display)
	private boolean error;

	private String errorDescription;		// Error cause
	private int srcLineNo;					// Source line number as of cmd recognition
	private String srcLine;					// Source Line
	private String srcFileName;				// Source File Name
	
											// First: In case we cross boundaries
	private int firstSrcLineNo;				// Source line number as of cmd recognition
	private String firstSrcLine;			// Source Line
	private String firstSrcFileName;		// Source File Name
	
	private BwCmdType cmdType;				// Command
	private boolean setCmdName = true;		// true - "SET" present in cmd
	private String includeFile;				// Include file name
	private String setVariableName;			// variable name for SET cmd
	private BwValue setValue;				// value for SET cmd
	private BwSliderSpec sliderSpec;
	private Vector<BwLocationSpec> points;	// Points for object
											// such as line
	private BwGraphic.Type graphicType;		// Graphical object type
	private BwValue lineWidth;				// Line width if given
	private BwColorSpec color;				// Graphical object Color
	private BwSizeSpec size;				//
	private BranchGroup branchGroup;
	private int grid;						// Graphic id if any else (-1)

}
