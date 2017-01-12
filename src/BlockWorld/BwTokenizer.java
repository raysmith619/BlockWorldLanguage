package BlockWorld;
import java.lang.String;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.io.*;
/**
 * Tokinize input
 *  1. Supports parsing functions:
 *    a. markState to mark position which can be retreated to for rescan of tokens
 *    b. backupState - to force rescan to most recent markState
 * 	2. Uses StreamTokenizer for basic tokenizing
 *  3. Keeps tokens locally after input string segment has been scanned
 *    a. tokQueue: in order list of tokens before first scanned
 *    b. tokStack: tokens pushed back for rescanning
 * @author raysm
 *
 */
public class BwTokenizer {
			/**
			 * Source input type for parsing
			 */
	enum srctype {
		FILE,
		STRING,
		ARRAY,		// of strings
		UNSET,		// Input not set
	};
	
	/**
	 * Empty setup
	 */
	public BwTokenizer(BwTrace trace) {
		this("", trace);
	}
	
	/**
	 * Setup to tokenize string
	 * @param str
	 */
	public BwTokenizer(String str,
			BwTrace trace) {
		this.trace = trace;
		this.srcType = srctype.UNSET;
		this.tokQueue = new LinkedList<BwToken>();
		this.tokStack = new Stack<BwToken>();
		this.marksStack = new Stack<Integer>();
		this.markStack = new Stack<BwToken>();
		this.markDescriptionStack = new Stack<String>();
		this.srcLineNo = 0;
		scanString(str);
	}
	
	public void addStr(String str) {
		this.srcType = srctype.STRING;
		this.srcLineNo++;
		Pattern pat_ends_with_newline = Pattern.compile("\n$");
		Matcher matcher = pat_ends_with_newline.matcher(str);
		if (!matcher.matches()) {
			str += "\n";			// Terminate last command on line
		}
		if (trace.traceInput())
			System.out.printf("%5d: %s", this.srcLineNo, str);
		scanString(str);		// Parse string and move tokens
	}

	/**
	 * Setup file access
	 * Initially look in assumed JAR file, if found, use it else look for file
	 */
	public boolean setFile(String file_name) {
		this.srcFileName = file_name;
		this.srcType = srctype.FILE;
		
									// Look first at embedded files
		InputStream is = getClass().getResourceAsStream(file_name);
		if (is != null) {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			this.srcReader = br;
			System.out.printf("Using data file %s from JAR file",
					file_name);
			return true;
		}

		Path path = Paths.get(file_name);
		if (!Files.exists(path)) {
			System.err.printf("Input file %s not found\n", path);
			return false;
		}
		if (!Files.isRegularFile(path)) {
			System.err.printf("Input file %s is not a regular file\n", path);
			return false;
		}
		
		try {
			this.srcReader = new BufferedReader(new FileReader(file_name));
		} catch (IOException ex) {
			String errorMessage = ex.getMessage();
			System.err.printf("setFile: IO Error in file %s: %s\n", file_name,
					errorMessage);
			return false;
		}
		return true;
	}
	
	
	/**
	 * Check on next token
	 */
	public boolean tokenType(int type) {
		markState("tokenType");
		BwToken token = nextToken();
		if (token.type != type) {
			if (trace.traceTokenReject())
				System.out.printf("Token reject %c\n", type);
			backupState("");
			return false;
		}
		if (trace.traceTokenAccept())
			System.out.printf("Token accept %c\n", type);
		matchProduction("");
		return true;
	}
	
	
	/**
	 * Check if this token string
	 * case insensitive
	 */
	public boolean tokenType(String str) {
		markState("tokenType");
		BwToken token = nextToken();
		String tok_lc;
		if (token.type != StreamTokenizer.TT_WORD) {
			char toc_c = (char)token.type;
			tok_lc = String.valueOf(toc_c);
		} else {
			tok_lc = token.str.toLowerCase();
		}
		String target_lc = str.toLowerCase();
		if (!tok_lc.equals(target_lc)){
			if (trace.traceTokenReject())
				System.out.printf("Token reject %s\n", token.str);
			backupState("not equal");
			return false;
		}
		if (trace.traceTokenAccept())
			System.out.printf("Token accept %s\n", tok_lc);
		matchProduction(str);
		return true;
	}

	/**
	 * Standard - non-silent
	 * @return
	 */
	public BwToken nextToken() {
		tokQueue(trace.traceQueue());
		markTrace(trace.traceMark());
		BwToken token =  nextTokenBase(false);
		if (this.marksStack.size() > 0)
			this.markStack.push(token);
		printFullState("nextToken");
		return token;
	}

	/**
	 * Silent special get token - no trace
	 * @return
	 */
	public BwToken nextTokenSilent() {
		return nextTokenBase(true);
	}
	
	/**
	 * Primitive token access
	 * Depends on source input type
	 * @param silent
	 * @return token value, EOF on failures
	 */
	public BwToken nextTokenBase(boolean silent) {
		BwToken token;
		
		token = new BwToken(StreamTokenizer.TT_EOF, "", 0);		// Default - end of line
		if (!this.tokStack.isEmpty())
			token = tokStack.pop();
		else if (!this.tokQueue.isEmpty())
			token = this.tokQueue.remove();
		else if (this.srcType == srctype.FILE)
			token = nextTokenBaseFile();
		else if (this.srcType == srctype.STRING)
			token =  nextTokenBaseString();
		else if (this.srcType == srctype.UNSET) {
			System.err.printf("Token access is unset");
			token = new BwToken(StreamTokenizer.TT_EOF, "", 0);		// End it
		} else {
			System.err.printf("Token access method is unsupported:" + this.srcType);
			token = new BwToken(StreamTokenizer.TT_EOF, "", 0);		// End it
		}
		
		if (!silent)
			if (trace.traceToken())
				System.out.println("TOKEN: " + token);
		return token;
	}

	
	/**
	 * Get next token(s) from file
	 */
	public BwToken nextTokenBaseFile() {
		String line;
		try {
			line = this.srcReader.readLine();
		} catch (IOException e) {
			System.out.printf("nextToken file %s read error\n", this.srcFileName);
			return new BwToken(StreamTokenizer.TT_EOF, "", 0.);
		}

		if (line == null) {
			return new BwToken(StreamTokenizer.TT_EOF, "", 0.);
		}
		this.srcLineNo++;
		this.srcLine = line;
		if (trace.traceInput())
			System.out.printf("%3d: %s\n", this.srcLineNo, line);
		scanString(line);
		return nextTokenBase(true);		// Get token if any, else next line
	}
	/**
	 * Token input string access
	 * Not used except for end of string EOF
	 * @param silent
	 * @return
	 */
	public BwToken nextTokenBaseString() {
		BwToken token = new BwToken(StreamTokenizer.TT_EOF, "", 0.);
		return token;
	}

	/**
	 * 
	 * @param print - true print
	 * @param heading - if present, add heading
	 * @return input queue string
	 * 		The prefix " stack < " is included iff tok stack not empty
	 */
	public String markTrace(boolean print, String...headings) {
		String mtrace = "";
		if (headings.length > 0)
			mtrace = headings[0];
	
		String mstr = "markDescriptionStack\n";
		Iterator<String> msdi = this.markDescriptionStack.iterator();
		while(msdi.hasNext()) {
			mstr += "  " + msdi.next() + "\n";
			
		}
		mstr += "\nmarksStack\n";
		Iterator<Integer> msi = this.marksStack.iterator();
		while(msi.hasNext()) {
			mstr += " " + msi.next();		
		}

		mstr += "\nmarkStack\n";
		Iterator<BwToken> mti = this.markStack.iterator();
		while(mti.hasNext()) {
			mstr += " " + mti.next();			
		}
		mstr += "\ntokQueue: " + tokQueue(print) + "\n";
		if (print)
			System.out.printf("\n%s\n%s\n", mtrace, mstr);

		return mstr;
	}

	
	/**
	 * 
	 * @param print - true print
	 * @param heading - if present, add heading
	 * @return input queue string
	 * 		The prefix " stack < " is included iff tok stack not empty
	 */
	public String tokQueue(boolean print, String...heading) {
		String qstr = "";
		if (this.tokStack.size() > 0) {
			Iterator<BwToken> tki = this.tokStack.iterator();
			while(tki.hasNext()) {
				qstr += " " + tki.next();
			}
			qstr += " <<<";			// Visual delimiter
		}
		Iterator<BwToken> tkq = this.tokQueue.iterator();
		while(tkq.hasNext()) {
			qstr += " " + tkq.next();			
		}
		if (print) {
			String str = "";
			if (heading.length > 0)
				str += heading[0];
			
			str += " tokStack<tokQueue:";
			System.out.printf("%s %s\n", str, qstr);
		}
		return qstr;
	}

	/**
	 * Check next token
	 * pop;push sequence is used because of the complexity of looking
	 * @return
	 */
	public BwToken peek() {

		if (!this.tokStack.isEmpty()) {
			BwToken token = tokStack.peek();
			return token;
		}
		if (!this.tokQueue.isEmpty()) {
			BwToken token = this.tokQueue.peek();
			return token;
		}

						// If we have to go to input stream/string
		BwToken token = nextTokenSilent();
		pushBackBase(token);
		return token;
	}

	/**
	 * Push back token
	 */
	public void pushBack(BwToken token) {
		pushBackBase(token);
		printFullState("pushBack " + token);
	}

	/**
	 * Push back token - base - no tracking
	 */
	public void pushBackBase(BwToken token) {
		this.tokStack.push(token);
	}
	
	/**
	 * Move in-process StreamTokenizer tokens to 
	 * to our token stack
	 * to facilitate markState, backupState.
	 * so no mark, backup will loose tokens
	 */
	public void moveTokensLocal() {
		BwToken token;
		int tok = 0;
		if (!this.tokStack.isEmpty()) {
			if (this.tokStack.peek().type == StreamTokenizer.TT_EOF) {
				this.tokStack.pop();		// Remove extraneous EOF
			}
		}
					   /**
						 * Move all in-process tokens to beginning of our
						 * stack so they will be taken, after any current
						 * pushback, in the order they appear in the input 
						 */
		try {
			while ((tok = this.st.nextToken()) != StreamTokenizer.TT_EOF) {
				token = new BwToken(tok, st.sval, st.nval);
				this.tokQueue.add(token);
			}
		} catch (IOException ex) {
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			ex.printStackTrace(printWriter);
			String sts = writer.toString();
			System.out.printf("moveTokensLocal Error: %s", sts);
		}

	}

	
	/**
	 * scan next input string, moving tokens to local queue
	 */
	private void scanString(String str) {
		this.sr = new StringReader(str);
		this.st = new StreamTokenizer(this.sr);
		moveTokensLocal();
	}
	
	/**
	 * state marking/backup to facilitate alternate production targets
	 *   Exampl  sequence => first_try | second_try | third_try
	 */
	public void markState(String str) {
		markTrace(trace.traceMark(), "before markState");
		if (this.markDescriptionStack.size() != this.marksStack.size()) {
			System.out.printf("STACKs differ desc:%d != marks:%d,  mark:%d\n" ,
					this.markDescriptionStack.size(),
					this.marksStack.size(),
					this.markStack.size());
			System.out.printf("%s\n", str);
		}
		this.markDescriptionStack.push(str);		// Save target description
		this.marksStack.push(this.markStack.size());
		if (trace.traceMark()) {
			System.out.printf("mark: %s", str);
			BwToken next_token = peek();
			System.out.printf("    next token: %s",next_token);
			String sstr = "\nmarkDescriptionStack\n";
			Iterator<String> msdi = this.markDescriptionStack.iterator();
			while(msdi.hasNext()) {
				sstr += "  " + msdi.next() + "\n";
				
			}
			sstr += "\nmarksStack\n";
			Iterator<Integer> msi = this.marksStack.iterator();
			while(msi.hasNext()) {
				sstr += " " + msi.next();		
			}
	
			sstr += "\nmarkStack\n";
			Iterator<BwToken> mti = this.markStack.iterator();
			while(mti.hasNext()) {
				sstr += " " + mti.next();			
			}
			System.out.printf("%s\n", sstr);
		}
		markTrace(trace.traceMark(), "after markState");

		printFullState("markState");
	}
	
	public void backupState(String str) {
		markTrace(trace.traceMark(), " before backupState");
		String description = this.markDescriptionStack.pop();
		if (trace.traceBackup())
			System.out.printf("backup: %s %s\n", str, description);
		int backup_till = this.marksStack.pop();
		
		Stack<BwToken> pbts = new Stack<BwToken>();
		while (this.markStack.size() > backup_till) {
			BwToken pbt = this.markStack.pop();
			pbts.push(pbt);
			pushBack(pbt);
		}

		if (trace.traceBackup()) {
			System.out.printf("   rescan tokens:");
			while (pbts.size() > 0) {
				System.out.printf(" %s", pbts.pop());
			}
			System.out.printf("\n");
		}
		markTrace(trace.traceMark(), "after backupState");
		
		printFullState("backupState");
	}

	/**
	 * Matching state
	 * @param str - additional description of match
	 * @param accept - true - accept production as final
	 */
	public void matchProduction(String str, boolean...accepts) {
		boolean accept = false;		// Default no accept
		if (accepts.length > 0) {
			accept = accepts[0];
		}
		String action = accept ? "Accept" : "Match";
		if (this.marksStack.size() == 0) {
			System.out.printf(" NO PRODUCTION MATCH"
					+ ":" + action
					+ " " + str + "\n");
			return;					// No backup
		}
		if (this.markDescriptionStack.size() > 0) {
			String mark_desc = this.markDescriptionStack.pop();
			if (trace.traceAccept())
				System.out.printf("%s: %s", action, mark_desc);
		}
		int backup_till = this.marksStack.pop();
		Stack<BwToken> accepted_tokens = new Stack<BwToken>();
		markTrace(trace.traceMark(), "before " + action);
		while (this.markStack.size() > backup_till) {
			BwToken acc_tok = this.markStack.pop();		// Throw away tokens
			accepted_tokens.push(acc_tok);
		}

		if (trace.traceAccept()) {
			if (str != "") {
				System.out.printf(" %s ", str);
			}
			System.out.printf("    %s tokens:", action);
			while (accepted_tokens.size() > 0) {
				BwToken acctok = accepted_tokens.pop();
				System.out.printf(" " + acctok);
						// TBD - do better on match vs accept
				this.markStack.push(acctok);
			}
			System.out.printf("\n");
			if (this.markDescriptionStack.size() == 0) {
				System.out.printf("\n");	// Top levelo
			}
		}
		markTrace(trace.traceMark(), "after " + action);
		printFullState("acceptState");
	}

	
	/**
	 * ck if file input
	 */
	public boolean  isSrcFile() {
		return this.srcType == srctype.FILE;
	}
	
	
	public String srcFileName() {
		return this.srcFileName;
	}
	
	/**
	 * Access to source line
	 */
	public String srcLine() {
		return this.srcLine;
	}
	
	/**
	 * Access to source line no
	 */
	public int srcLineNo() {
		return this.srcLineNo;
	}
	
	
	/**
	 * Create description of current parsing stack
	 * @param heading
	 * @return
	 */
	public String fullState(String heading) {
		String str = "";
		if (heading != "") {
			str = "\n" + heading + "\n";
		}
		if (trace.traceMark()) {
			str += "\nmarkDescriptionStack\n";
			Iterator<String> msdi = this.markDescriptionStack.iterator();
			while(msdi.hasNext()) {
				str += "  " + msdi.next() + "\n";
				
			}
			str += "\nmarksStack\n";
			Iterator<Integer> msi = this.marksStack.iterator();
			while(msi.hasNext()) {
				str += " " + msi.next();		
			}
	
			str += "\nmarkStack\n";
			Iterator<BwToken> mti = this.markStack.iterator();
			while(mti.hasNext()) {
				str += " " + mti.next();			
			}
		}

		if (trace.traceQueue()) {
			str += "\ntokQueue\n";
			Iterator<BwToken> tkq = this.tokQueue.iterator();
			while(tkq.hasNext()) {
				str += " " + tkq.next();			
			}
		}

		if (trace.traceTokStack()) {
			str += "\ntokStack\n";
			Iterator<BwToken> tki = this.tokStack.iterator();
			while(tki.hasNext()) {
				str += " " + tki.next();			
			}
		}
		
		
		return str;
	}

	
	/**
	 * print current parsing state
	 */
	public void printFullState(String heading) {
		if (!trace.traceState())
			return;
		String fs = fullState("");
		System.out.println("\nSTATE>" + heading + fs);
	}
	
	private BwTrace trace;				// Tracing control
	private srctype srcType;			// Input source type
	private String srcFileName;			// source input file name
	private BufferedReader srcReader;	// Input src file reader
	public int srcLineNo;					// Source line number
	public String srcLine;				// Source line text
										// mark stack state facilitates arbitrary token
										// rescanning
	private Stack<Integer> marksStack;	// marks: stack size numbers
	private Stack<BwToken> markStack;	// saved backup tokens for backupState/acceptState
	private Stack<String> markDescriptionStack;		// Target description
	private Queue<BwToken> tokQueue;	// Local token queue
	private Stack<BwToken> tokStack;
	private StreamTokenizer st;			// Tokenizer
	private StringReader sr;			// String Reader for tokenizer
	public String sval;					// Token String value
	public double nval;					// Token Numeric value
	
	
	/**
	 * Self-test
	 * Tokenize each parameter
	 * 
	 */
	public static void main(String args[]) {
		if (args.length == 0) {
			args =  new String[] {"First: line of text",
					"Second: numbers: 1 2 345"};
		}
		BwTrace trace = new BwTrace();
		trace.setAll();
		for (int i = 0; i < args.length; i++){
			String arg = args[i];
			System.out.printf("arg(%s)\n", arg);
			BwTokenizer st = new BwTokenizer(arg, trace);
	         // print the stream tokens
	         boolean eof = false;
	         do {

	            BwToken token = st.nextToken();
	            int token_type = token.type;
	            switch (token.type) {
	               case StreamTokenizer.TT_EOF:
	                  System.out.println("End of File encountered.");
	                  eof = true;
	                  break;
	               case StreamTokenizer.TT_EOL:
	                  System.out.println("End of Line encountered.");
	                  break;
	               case StreamTokenizer.TT_WORD:
	                  System.out.println("Word: " + st.sval);
	                  break;
	               case StreamTokenizer.TT_NUMBER:
	                  System.out.println("Number: " + st.nval);
	                  break;
	               default:
	                  System.out.println((char) token_type + " encountered.");
	                  if (token_type == '!') {
	                     eof = true;
	                  }
	            }
	         } while (!eof);			
		}
	}
}
