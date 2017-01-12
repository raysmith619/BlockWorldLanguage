package BlockWorld;

import java.util.Hashtable;

public class BwSymTable extends Hashtable<String, BwValue> {

	public BwSymTable(BwTrace trace) {
		this.trace = trace;
	}

	public void setValue(String var_name, BwValue value) {
		try {
			if (trace.traceExecute())
				System.out.printf("setValue(%s, %s)\n", var_name,
						fmt(value.floatValue()));
		} catch (BwException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		put(var_name.toLowerCase(), value);
		if (!inTable(var_name)) {
			System.out.printf(String.format(
					"setValue: %s not in table after put\n", var_name));
		}
	}

	public void setValue(String name, double value) {
		setValue(name, new BwValue((float)value));
	}

	public void setStringValue(String name, String value) {
		setValue(name, new BwValue(value));
	}

	
	/**
	 * Check symbol table for entry
	 */
	public boolean inTable(String var_name) {
		BwValue sym = this.get(var_name.toLowerCase());
		if (sym == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Access to symbol table
	 * @param var_name
	 * @return value, null if not found
	 */
	public String getStringValue(String var_name) throws BwException {
		
		BwValue sym = this.get(var_name.toLowerCase());
		if (sym == null) {
			throw new BwException(String.format(
					"getStringValue(%s) fails - no variable entry",
					var_name));
		}
		String val = sym.stringValue();
		if (trace.traceExecute())
			System.out.printf("getValue(%s = %s)\n", var_name, val);
		return val;
	}
	
	/**
	 * Access to symbol table
	 * @param var_name
	 * @return value, null if not found
	 */
	public float getValue(String var_name) throws BwException {
		
		BwValue sym = this.get(var_name.toLowerCase());
		if (sym == null) {
			throw new BwException(String.format(
					"getValue(%s) fails - no variable entry",
					var_name));
		}
		float val = sym.floatValue();
		if (trace.traceExecute())
			System.out.printf("getValue(%s = %s)\n", var_name, fmt(val));
		return val;
	}

	
	/**
	 * Access to symbol table
	 * @param var_name
	 * @return value, null if not found
	 */
	public BwValue getValueObject(String var_name) {
		
		BwValue obj = this.get(var_name.toLowerCase());
		return obj;
	}
	
	
	/**
	 * short format 
	 */
	public String fmt(double d)	{
		return BwValue.fmt(d);
	}
	
	private BwTrace trace;			// Trace diagnostic control

}
