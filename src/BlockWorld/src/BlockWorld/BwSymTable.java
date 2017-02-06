package BlockWorld;

import java.util.ArrayList;
import java.util.Hashtable;

public class BwSymTable extends Hashtable<String, BwValue> {

	public BwSymTable(BwTrace trace) {
		this.trace = trace;
	}

	/**
	 * Add possible new variable
	 * @param var_name
	 * @return variable entry/value
	 */
	public BwValue addVariable(String var_name) {
		if (!inTable(var_name)) {
			BwValue entry = new BwValue(this,
					BwValue.BwValueType.VARIABLE,
					var_name, 0F);
			put(var_name.toLowerCase(), entry);
		}
		BwValue obj = this.get(var_name.toLowerCase());
		return obj;
	}

	/**
	 * General error message
	 * @param msg
	 * @throws BwException
	 */
	public void error(String msg) throws BwException {
		throw new BwException(msg);
	}
	
	public void setValue(String var_name, BwValue value) {
		try {
			if (trace.traceExecute())
				System.out.printf("\t\t\t\tsetValue(%s, %s)\n", var_name,
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
		setValue(name, new BwValue(this, (float)value));
	}

	public void setStringValue(String name, String value) {
		setValue(name, new BwValue(this, value));
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
			System.out.printf("getStringValue: variable %s is not in table", var_name);
			throw new BwException(String.format(
					"getStringValue(%s) fails - no variable entry",
					var_name));
		}
		String val = sym.immediateStringValue();
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
	 * list to array
	 * Convert lists generated in parser to arrays which
	 * can come from Jython code
	 */
	public BwValue[] list2array(ArrayList<BwValue> list) {
		BwValue[] va = new BwValue[list.size()];
		va = list.toArray(va);
		return va;
	}
	
	
	/**
	 * short format 
	 */
	public String fmt(double d)	{
		return BwValue.fmt(d);
	}
	
	private BwTrace trace;			// Trace diagnostic control

}
