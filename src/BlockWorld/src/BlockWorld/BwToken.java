package BlockWorld;

import java.io.StreamTokenizer;

public class BwToken {

	public BwToken(int type, String str, double number) {
		this.type = type;
		this.str = str;
		this.number = number;
	}
	
	public String toString() {
        int token_type = this.type;
        String str;
        switch (token_type) {
        	case StreamTokenizer.TT_EOF:
        	   str = "EOF";
        	   break;
           case StreamTokenizer.TT_EOL:
        	   str = "EOL";
              break;
           case StreamTokenizer.TT_WORD:
              str = "WORD[" + this.str + "]";
              break;
              
           case StreamTokenizer.TT_NUMBER:
        	   str = "NUMBER[" + this.number + "]";
              break;
           default:
        	   char c = (char)token_type;
              str = String.valueOf(c);
        }
        return str;
	}
	
	
	public int type;
	public String str;
	public double number;
	int lineno;						// Line number
}
