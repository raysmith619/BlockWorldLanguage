package BlockWorld;

import java.awt.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Facilitate execution traceing / logging
 * of execution.
 * @author raysm
 *
 */
public class BwTrace {

	/**
	 * @return the mark
	 */
	public int getMark() {
		return mark;
	}

	/**
	 * Default setup
	 */
	public BwTrace() {
		this("BlockWorld.properties");
	}

	
	/**
	 * Set up based on properties file
	 * @throws IOException 
	 */
	public BwTrace(String propFile) {
		// create and load default properties
		clearAll();
		this.defaultProps = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream(propFile);
		} catch (FileNotFoundException e) {
			File inf = new File(propFile);
			String abs_path;
			try {
				abs_path = inf.getCanonicalPath();
			} catch (IOException e1) {
				abs_path = "NO PATH for '" + propFile
						+ "'";
				e1.printStackTrace();
			}

			
			System.err.println("Properties file " + abs_path
					+ " not found");
			// e.printStackTrace();
			return;
		}
		try {
			this.defaultProps.load(in);
		} catch (IOException e) {
			System.err.println("Can't load Properties file "
					+ propFile);
			e.printStackTrace();
			return;
		}
		try {
			in.close();
		} catch (IOException e) {
			System.err.println("Can't load Properties file "
					+ propFile);
			e.printStackTrace();
		}
		
	}
	public void clearAll() {
		this.parse = 0;
		this.execute = 0;
		this.state = 0;
		this.input = 0;
		this.token = 0;
		this.tokenAccept = 0;
		this.tokenReject = 0;
		this.mark = 0;
		this.accept = 0;
		this.backup = 0;
		this.debug = 0;
		this.graphics = 0;
		this.tokStack = 0;
		this.tokQueue = 0;
		this.verbose = 0;
	}

	/**
	 * Set all levels, except debug
	 * @param levels
	 */
	public void setAll(int ...levels) {
		int level = 1;
		if (levels.length > 0)
			level = levels[0];
		this.parse = level;
		this.execute = level;
		this.state = level;
		this.input = level;
		this.token = level;
		this.tokenAccept = level;
		this.tokenReject = level;
		this.mark = level;
		this.accept = level;
		this.backup = level;
		this.graphics = level;
		this.tokStack = level;
		this.tokQueue = level;
		this.verbose = level;
		
	}
	
	public void setLevel(String trace_name, int...levels) {
		int level = 1;
		if (levels.length > 0) {
			level = levels[0];
		}

		switch (trace_name.toLowerCase()) {
			case "execute":
				this.execute = level;
				break;
			case "graphics":
				this.graphics = level;
				break;
			case "debug":
				this.debug = level;
				break;
			case "parse":
				this.parse = level;
				break;
			case "state":
				this.state = level;
				break;
			case "input":	
				this.input = level;
				break;
			case "token":
				this.token = level;
				break;
			case "tokenAccept":
			case "tokenaccept":
				this.tokenAccept = level;
				break;
			case "tokenReject":
			case "tokenreject":
				this.tokenReject = level;
			case "mark":
				this.mark = level;
			case "accept":
				this.accept = level;
				break;
			case "backup":
				this.backup = level;
				break;
			case "tokStack":
			case "tokstack":
				this.tokStack = level;
				break;
			case "tokQueue":
			case "tokqueue":
				this.tokQueue = level;
				break;
			case "all":
				setAll(level);
				break;
			case "none":
				clearAll();
				break;
				
			default:
				System.out.printf("Illegal trace name: %s - ignored\n", trace_name);
		}
	}
	
	public boolean traceParse() {
		return this.parse > 0;
	}
	
	public boolean traceExecute() {
		return this.execute > 0;
	}
	
	public boolean traceGraphics() {
		return this.graphics > 0;
	}
	
	public boolean traceInput() {
		return this.input > 0;
	}
	
	public boolean traceState() {
		return this.state > 0;
	}	
	public boolean traceToken() {
		return this.token > 0;
	}
	public boolean traceTokenAccept() {
		return this.tokenAccept > 0;
	}
	public boolean traceTokenReject() {
		return this.tokenReject > 0;
	}
	
	public boolean traceMark() {
		return this.mark > 0;
	}
	
	public boolean traceAccept() {
		return this.accept > 0;
	}
	
	public boolean traceBackup() {
		return this.backup > 0;
	}
	
	public boolean traceDebug() {
		return this.debug > 0;
	}
	
	public boolean traceTokStack() {
		return this.tokStack > 0;
	}
	
	public boolean traceQueue() {
		return this.tokQueue > 0;
	}
	
	public boolean traceVerbose(int ...levels) {
		int level = 1;
		if (levels.length > 0) 
			level = levels[0];
		return this.verbose > level;
	}
	
	/**
	 * @param parse the parse to set
	 */
	public void setParse(int parse) {
		this.parse = parse;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(int input) {
		this.input = input;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(int token) {
		this.token = token;
	}

	/**
	 * @param token the token to set
	 */
	public void setTokenAccept(int token) {
		this.tokenAccept = token;
	}

	/**
	 * @param token the token to set
	 */
	public void setTokenReject(int token) {
		this.tokenReject = token;
	}

	/**
	 * @param accept the accept to set
	 */
	public void setMark(int mark) {
		this.mark = mark;
	}

	/**
	 * @param debug to set
	 */
	public void setDebug(int level) {
		this.debug = level;
	}

	/**
	 * @param accept the accept to set
	 */
	public void setAccept(int accept) {
		this.accept = accept;
	}

	/**
	 * @param backup the backup to set
	 */
	public void setBackup(int backup) {
		this.backup = backup;
	}

	/**
	 * @param tokStack the tokStack to set
	 */
	public void setTokStack(int tokStack) {
		this.tokStack = tokStack;
	}

	/**
	 * @param tokQueue the tokQueue to set
	 */
	public void setTokQueue(int tokQueue) {
		this.tokQueue = tokQueue;
	}

	/**
	 * @param level
	 */
	public void setVerbose(int level) {
		this.verbose = level;
	}

	/**
	 * @return the parse
	 */
	public int getParse() {
		return parse;
	}

	/**
	 * 
	 * @param key - property key
	 * @return property value, "" if none
	 */
	public String getProperty(String key) {
		return this.defaultProps.getProperty(key, "");
	}

	
	public void setProperty(String key, String value) {
		this.defaultProps.setProperty(key, value);
	}

	/**
	 * Get source absolute path
	 * Get absolute if fileName is not absolute
	 * TBD handle chain of paths like C include paths
	 * @param fileName
	 * @return absolute file path
	 */
	public String getSourcePath(String fileName) {
		Path p = Paths.get(fileName); 
		if ( !p.isAbsolute() ) {
			String[] dirs = getAsStringArray("source_files");
			ArrayList<String> searched = new ArrayList<String>();
			for (String dir : dirs) {
				File inf = new File(dir, fileName);
				if (inf.exists() && !inf.isDirectory()) {
					try {
						return inf.getCanonicalPath();
					} catch (IOException e) {
						System.err.printf("Problem with path %s,%s",
								dir, fileName);
					}
				}
				try {
					searched.add(inf.getCanonicalPath());
				} catch (IOException e) {
						// Ignore
				}
			}
			System.err.printf("%s was not found\n", fileName);
			if (dirs.length > 0) {
				System.err.printf("Searched in:\n");
				for (String dir : dirs) {
					File dirf = new File(dir);
					try {
						String dirpath = dirf.getCanonicalPath();
						System.err.printf("\t%s\n", dirpath);
					} catch (IOException e) {
						System.err.printf("\tpath error for %s\n", dir);
					}
				}
			}
			return fileName;	// Return unchanged
		}
		return fileName;		// Already absolute path
	}

	/**
	 * Get include absolute path
	 * Get absolute if fileName is not absolute
	 * TBD handle chain of paths like C include paths
	 * @param fileName
	 * @return absolute file path
	 */
	public String getIncludePath(String fileName) {
		Path p = Paths.get(fileName); 
		if ( !p.isAbsolute() ) {
			String dir = this.defaultProps.getProperty("include_files");
			if (fileName.equals("")) {
				System.err.print("%s is not absolute and no include_files in properties file\n");
				return fileName;
			}
			String joinedPath = new File(dir, fileName).toString();
			return joinedPath;
		}
		return fileName;		// Already absolute path
	}

	/**
	 * Get default properties key with value
	 * stored as comma-separated values
	 * as an array of those values
	 * If propKey not found, return an empty array
	 * @param propKey
	 * @return array of string values
	 */
	public String[] getAsStringArray(String propKey) {
		String[] vals = getProperty(propKey).split(",");
		return vals;
	}
	
	/**
	 * @return the input
	 */
	public int getInput() {
		return input;
	}

	/**
	 * @return the token
	 */
	public int getToken() {
		return token;
	}

	/**
	 * @return the accept
	 */
	public int getAccept() {
		return accept;
	}

	/**
	 * @return the backup
	 */
	public int getBackup() {
		return backup;
	}

	/**
	 * @return the tokStack
	 */
	public int getTokStack() {
		return tokStack;
	}

	/**
	 * @return the tokQueue
	 */
	public int getTokQueue() {
		return tokQueue;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}

	private int verbose;		// General tracking / logging level
	/**
	 * @return the verbose
	 */
	public int getVerbose() {
		return verbose;
	}

	/**
	 * @return the tokenAccept
	 */
	public int getTokenAccept() {
		return tokenAccept;
	}

	public int getTokenReject() {
		return tokenReject;
	}

	public void setExecute(int execute) {
		this.execute = execute;
	}

	private Properties defaultProps;	// program properties
	private int parse;
	private int graphics;		// graphics tracing
	private int input;
	private int state;
	private int execute;		// Execution trace
	private int tokenAccept;	// If token is accepted
	private int tokenReject;	// If token is rejected
	private int token;
	private int mark;
	private int accept;
	private int backup;
	private int debug;			// DEBUG setting - temporary use
	private int tokStack;
	private int tokQueue;
}
