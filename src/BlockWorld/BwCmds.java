/**
 * Access to display commands
 * @author raysm
 *
 */
package BlockWorld;

import java.util.Vector;

public class BwCmds {

	/**
	 * Setup command access
	 */
	public BwCmds(BwTrace trace) {
		this();
		this.trace = trace;
	}

	/**
	 * Setup command access
	 */
	public BwCmds() {
		this.bCmds = new Vector<BwCmd>();
		this.error = false;
		this.errorDescription = null;
		this.nError = 0;
		this.firstErrorIndex = -1;
	}

	/**
	 * Add command to end of list
	 * Record first error
	 */
	public void addCmd(BwCmd cmd) {
		this.bCmds.add(cmd);
		if (cmd.isError()) {
			this.nError++;
			if (!this.error) {
				setError(cmd.errorDescription());	// Record first error
			}
		}
	}

	
	/**
	 * Clear out display
	 */
	public void clear() {
		for (BwCmd cmd : this.bCmds) {
			cmd.clear();
		}
		this.bCmds  = new Vector<BwCmd>();
		this.error = false;
		this.errorDescription = null;
		this.nError = 0;
		this.firstErrorIndex = -1;
	}
	
	/**
	 * Get cmd
	 */
	public BwCmd getCmd(int i) {
		return bCmds.get(i);
	}
	
	
	/**
	 * Get all stored commands
	 * @return array of cmds
	 */
	public BwCmd[] getCmds() {
		int ncmd = this.bCmds.size();
		BwCmd[] bCmds = new BwCmd[ncmd];
		for (int i = 0; i < ncmd; i++)
			bCmds[i] = this.bCmds.get(i);
		return bCmds;
	}

	
	public void setError(String description) {
		this.error = true;
		this.firstErrorIndex = this.size()-1;
		this.errorDescription = description;
	}
	
	public boolean isError() {
		return this.error;
	}

	
	public BwCmd firstError() throws BwException {
		if (!isError())
			throw new BwException("firstError: no error");
		return this.bCmds.get(this.firstErrorIndex);
	}
	/**
	 * Give primary error description - local if one, else first cmd's description
	 * @return
	 */
	public String errorDescription() {
		if (this.errorDescription != null) {
			return this.errorDescription;
		}
		for (BwCmd cmd : this.getCmds()) {
			if (cmd.isError()) {
				return cmd.errorDescription();	// Return first cmd error
			}
		}
		return "UNKNOWN";
	}
	
	/**
	 * check for program quit
	 */
	public boolean isContinue() {
		return !this.pgmQuit;
	}
	
	
	public int size() {
		return bCmds.size();
	}
	public BwTrace getTrace() {
		return trace;
	}

	public Vector<BwCmd> getbCmds() {
		return bCmds;
	}

	public boolean isPgmQuit() {
		return pgmQuit;
	}

	public int getnError() {
		return nError;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	
	private BwTrace trace;			// Diagnostic trace control
	private Vector<BwCmd> bCmds;	// List of commands
	private boolean pgmQuit;		// Program should quit
	private boolean error;			// true ==> error
	private int nError;				// Number of errors
	private int firstErrorIndex;	// Index of first error
	private String errorDescription;	// Base error description
}
