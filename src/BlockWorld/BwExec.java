package BlockWorld;
import java.lang.String;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.nio.file.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import BlockWorld.BwDisplay;

/**
 * 
 * @author raysm
 * Get command
 *   1. Default: STDIN
 *   2. String

 *   Internal format
 *      
 *         New - new display
 *         Add - add object
 *         Delete - delete object
 *         Replace - delete, then add object
 *         Quit - quit program
 *         
 *		Number - object number, starting with 1
 *		Color object color red, green, blue
 *		
 */
public class BwExec {
	public BwExec(BwTrace trace) throws Exception {
		setup(trace);
	}

	/**
	 * Setup for pgm compile/execution
	 * @param cmd
	 * @throws Exception 
	 */
	public void setup(BwTrace trace) throws Exception {
		this.trace = trace;
		this.cmdString = "";
		this.bCmds = new BwCmds(this.trace);
		this.parser = new BwParser(trace, this.bCmds);
		this.sT = this.parser.getSymTable();
		this.bD = new BwDisplay(trace, this);
		
	}
	/*
	 * Add cmd to list
	 *
	 */
	public void addCmd(BwCmd cmd) {
		if (this.trace.traceExecute(2))
			System.out.println("addCmd:" + cmd);
		if (cmd.getCmdType() == BwCmdType.SLIDER) {
			if (this.trace.traceExecute(2))
				System.out.println("addCmd:" + "SLIDER");
			setupControls();
		}
		this.bCmds.addCmd(cmd);
	}

	
	/**
	 * Setup controls window for runtime adjustments
	 */
	public void setupControls() {
		if (this.controls == null) {
			System.out.print("setupControls\n");
			this.controls = new BwControls(this.trace, this);
			this.controls.setVisible(true);
		}
	}
	/**
	 * Clear out display
	 * @throws Exception 
	 */
	public void clear() throws Exception {
		if (this.bD != null)
			bD.clear();
		setup(this.trace);
	}

	
	/**
	 * Erase display
	 * @throws Exception 
	 */
	public void erase() throws Exception {
		if (this.bCmds != null)
			this.bCmds.clear();
		if (this.bD != null)
			bD.erase();
	}
	
	
	/**
	 * Display/Execute one command
	 * @return true iff no errors
	 */
	public boolean display(BwCmd cmd) {
		BwCmd[] cmds = new BwCmd[] {cmd};
		return  display(cmds);
	}
	
	
	/**
	 * Display/Execute given commands in order
	 * @return true iff no errors
	 */
	public boolean display(BwCmd[] cmds) {
		try {
			this.bD.setTimeLimit(this.timeLimit);		// Set our time limit
			if (trace.traceExecute())
				System.out.printf("\nExecuting Commands\n");
			for (int j = 0; j < cmds.length; j++) {
				int n = j + 1;
				BwCmd cmd = cmds[j];
				if (trace.traceExecute()) {
					int lineno = cmd.getSrcLineNo();
					String cmd_str =  cmd.toString();
					System.out.printf("%3d: %s\n", lineno, cmd_str);
					try {
						System.out.printf("%s\n", cmd.toStringExtended());
					} catch (BwException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (trace.traceGraphics())
					System.out.printf("before display(cmd)\n");
				this.bD.display(cmd);
				if (trace.traceGraphics())
					System.out.printf("after display(cmd)\n");
			}
			if (trace.traceGraphics())
				System.out.printf("before setDisplay()\n");
			this.bD.setDisplay();
			if (trace.traceGraphics())
				System.out.printf("after setDisplay()\n");
		} catch (BwException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		if (this.controls != null)
			this.controls.setVisible(true);

		return true;
	}
	
	
	/**
	 * Display/Execute contained commands in order
	 * @return true iff no errors
	 */
	public boolean display() {
		BwCmd[] cmds = getCmds();
		if (this.trace.traceExecute(2))
			System.out.printf("display():%d cmds\n", cmds.length);
		return display(cmds);
	}
	
	/**
	 * List commands
	 */
	public void list() {
		for (int j = 0; j < size(); j++) {
			int n = j + 1;
			BwCmd cmd = getCmd(j);
			System.out.printf("%3d: %s\n", cmd.getSrcLineNo(), cmd.toString());
			try {
				System.out.printf("%s\n", cmd.toStringExtended());
			} catch (BwException e) {
				System.out.printf("Exception %s\n", e.getMessage());
				e.printStackTrace();
			}
		}
	}
	/**
	 * Get cmd
	 */
	public BwCmd getCmd(int i) {
		return this.bCmds.getCmd(i);
	}
	/**
	 * Get cmds
	 */
	public BwCmd[] getCmds() {
		return this.bCmds.getCmds();
	}

	/**
	 * provide access to display
	 * @return
	 */
	public BwDisplay getDisplay() {
		return this.bD;
	}

	/**
	 * Run file
	 * @throws Exception 
	 */
	public boolean runFile(String fileName) throws Exception {
		if (!this.parser.procFile(fileName)) {
			return false;
		}
		if (!display())
			return false;
		this.bD.setTimeLimit(this.timeLimit);		// Set current time limit
		if (isTimeLimited()) {
			while (!isEndTime()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			clear();
		}
		return true;
	}
		
	/**
	 * Check if end of display / execution
	 */
	
	public boolean isEndTime() {
		return  this.bD != null && this.bD.isEndTime();
	}
	
	/**
	 * Run list of files
	 *
	 * @param runListFile - file containing files to run
	 * @return true iff OK
	 * @throws Exception 
	 */
	public boolean runList(String runListFile) throws Exception{
		String runListPath = this.trace.getSourcePath(runListFile);
		System.out.printf("Running list File:%s\n", runListPath);
		try {
			File fin = new File(runListPath);
		} catch (Exception ex) {
			System.out.printf("Failed in File/get\n");
		}
		
		BufferedReader br;
		File fin = new File(runListPath);
		try {
			br = new BufferedReader(new FileReader(fin));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			// e1.printStackTrace();
			System.out.printf("Can't open runListFile: %s\n",
					runListPath);
			return false;
		}
		String line = null;
		try {
			int filen = 0;					// File number
			while ((line = br.readLine()) != null) {
							// Look for // comments
							// and blank lines
				Pattern pattern = Pattern.compile("^(.*)//(.*)");
				Matcher matcher = pattern.matcher(line);
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
				if (!runFile(file)) {
					System.out.printf("Run Failure File %2d: %s\n", filen, file);
					return false;
				}
				System.out.printf("Run Success File %2d: %s\n", filen, file);
				clear();
			}
		} catch (IOException e) {
			System.err.printf("IO execption in %s", runListFile);
			e.printStackTrace();
			return false;
		} finally {
		}
		return true;
	}
	public int size() {
		return bCmds.size();
	}

	/**
	 * provide current controls display / access
	 */
	public BwControls getControls() {
		return this.controls;
	}

	/**
	 * link execution with controls display / access
	 */
	public void setControls(BwControls controls) {
		this.controls = controls;
		this.bD.setControls(controls);
	}
							/**
							 * Access to symbol table
							 * @param description
							 */

	public BwParser getParser() {
		return this.parser;
	}
	
	public void setSymValue(String varName, double value) {
		try {
			this.parser.setValue(varName, mkValue(value));
		} catch (BwException e) {
			System.out.printf("setSymValue failed");
			e.printStackTrace();
		}
	}
	public void setError(String description) {
		this.error = true;
		this.errorDescription = description;
	}

	
	/**
	 * Erase cmd display
	 * Otherwise leave the command unchanged
	 */
	public boolean setEmpty(BwCmd cmd) {
		return this.bD.setEmpty(cmd);
	}

	
	public void update(String name, double value) {
		System.out.printf("%s has been updated to %g\n", name, value);
		setSymValue(name, value);
		display();		// Display - should we be much more clever here??? TBD
	}
	
	public boolean isError() {
		return this.bCmds.isError();
	}
	
	public int getnError() {
		return this.bCmds.getnError();
	}
	
	public BwCmd firstError() throws BwException {
		return this.bCmds.firstError();
	}

	/**
	 * Give primary error description - local if one, else first cmd's description
	 * @return
	 */
	public String errorDescription() {
		if (this.errorDescription != null) {
			return this.errorDescription;
		}
		for (BwCmd cmd : this.bCmds.getCmds()) {
			if (cmd.isError()) {
				return cmd.errorDescription();	// Return first cmd error
			}
		}
		return "UNKNOWN";
	}

	/**
	 * Get trace object
	 * @return trace object
	 */
	public BwTrace getTrace() {
		return this.trace;
	}

	
	/**
	 * Set time limit
	 */
	public void setTimeLimit(float limit) {
		this.timeLimit = limit;
		if (bD != null)
			this.bD.setTimeLimit(limit);
	}
	public float getTimeLimit() {
		return this.bD.getTimeLimit();
	}
	/**
	 * Check if time limited
	 */
	public boolean isTimeLimited() {
		return this.bD.isTimeLimited();
	}
	
	/**
	 * check for program quit
	 */
	public boolean isContinue() {
		return !pgmQuit;
	}
	
	/**
	 * Symbol Table short cuts
	 */
	public BwValue mkValue() {
		return new BwValue(this.sT);
	}
	public BwValue mkValue(double val) {
		return new BwValue(this.sT, val);
	}
	
	private BwParser parser;			// Parser
	private BwSymTable sT;				// Access to symbol table
	private BwTrace trace;				// Tracing control
	private String cmdString;			// Pending command string (left over from previous cmd)
	private BwCmds bCmds;				// Access to stored commands
	private int verbose = 0;
	private boolean pgmQuit = false;	// true => end processing
	private boolean error = false;		// true => error in processing
	private String errorDescription;	// Primary error description
	private BwControls controls;		// Sliders...
	private BwDisplay bD;						// Display Graphics
	private float timeLimit = -1;		// Time limit, neg == none
	
}
