package BlockWorld;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BwValue {
	enum BwValueType {
		UNDEFINED,
		CONSTANT,
		VARIABLE,
		CALCULATION,		// Not yet implemented
	};
	enum BwDataType {
		UNDEFINED,
		FLOAT,
		STRING,
	};

/**
 * Setup parser access
 * Parser will be the central control
 * and access to symbol table	
 */
	public static void setParser(BwParser pars) {
		parser = pars;
	}
	
/**
 * common error
 *  - throw	
 */
	private void error(String msg) throws BwException {
		throw new BwException(msg);
	}
	
	public BwValue() {
		this.type = BwValueType.UNDEFINED;
		this.dataType = BwDataType.FLOAT;
	}

	/**
	 * Copy constructor
	 * giving deep / unique copy
	 * 
	 * @param value one version
	 * @return unique version
	 */
	public BwValue(BwValue value) throws BwException {
		this();
		setValue(value);
	}
	
	/**
	 * @param type
	 * @param variableName
	 * @param value
	 */
	public BwValue(BwValueType type, String variableName, float value) {
		super();
		this.type = type;
		this.variableName = variableName;
		this.value = value;
	}

	public BwValue(int value) {
		this();
		setValue((float)value);
	}

	public BwValue(double value) {
		this.setValue((float)value);
	}

	public BwValue(float value) {
		this.setValue(value);
	}

	public BwValue(String value) {
		this();
		setValue(value);
	}

	/**
	 * Set variable type
	 */
	public void setType(BwValueType type) {
		this.type = type;
	}

	/**
	 * Set variable name
	 */
	public void setName(String name) {
		this.variableName = name;
	}

	/**
	 * Setup as variable
	 */
	public void setVariable(String name) {
		this.type = BwValueType.VARIABLE;
		setName(name);
	}
	
	/**
	 * set from parameter
	 * @throws BwException 
	 */
	public void setValue(BwValue value) throws BwException {
		this.type = value.type;
		if (value.dataType == BwDataType.FLOAT)
			this.value = value.floatValue();
		else if (value.dataType == BwDataType.STRING)
			this.stringValue = value.stringValue();
		else {
			throw new BwException("Unsupported data type");
		}
	}

	/**
	 * set from parameter
	 */
	public void setValue(float value) {
		this.dataType = BwDataType.FLOAT;
		this.value = value;
	}

	/**
	 * set from parameter
	 * @return
	 */
	public void setValue(String value) {
		this.dataType = BwDataType.STRING;
		this.stringValue = value;
	}
		
	public String varName() {
		if (type == BwValueType.VARIABLE) {
			String name = this.variableName;
			if (name == null)
				name = "UNDEFINED";
			return name;
		}
		return "NON-VARIABLE";
	}

	public BwValueType getType() {
		return type;
	}
	
	/**
	 * string value
	 * variable if one else value
	 * @return
	 */
	public String toString() {
		if (this.type == BwValueType.VARIABLE) {
			parser.ckit("BwValue:toString VARIABLE");
			return this.variableName;
		}
		return fmt(this.value);
	}
	
	/**
	 * string value
	 * value if one else name
	 * @return
	 */
	public String toStringEval()  throws BwException {
		BwValue value = this;
		if (this.type == BwValueType.VARIABLE) {
			String var_name = this.variableName;
			if (var_name == null) {
				error("No variable name for value of type VARIABLE");
			}
			parser.ckit("BwValue:toStringEval");
			if (!parser.inTable(var_name)) {
				return "UNDEFINED";
			}
			
			value = this.parser.getValueObject(this.variableName);
		}
		if (value.dataType == BwDataType.FLOAT) {
			return fmt(value.floatValue());
		} else if (value.dataType == BwDataType.STRING) {
			return value.stringValue();
		}
		error("Unrecognized dataType");
		return "UNRECOGNIZED";
	}

	/**
	 * Check for linked
	 * @return
	 * @throws BwException
	 */
	public boolean isLinked() {
		if (this.type == BwValueType.VARIABLE
				&&  this.variableName != null) {
			return true;
		}
		return false;
	}
	
	public float floatValue() throws BwException {
		if (this.type == BwValueType.VARIABLE) {
			float val = parser.getValue(this.variableName);
			return val;
		}
		return value;
	}

	
	public String stringValue() throws BwException {
		if (this.type == BwValueType.VARIABLE) {
			String val = parser.getStringValue(this.variableName);
			return val;
		}
		return this.stringValue;
	}
	
	
	/**
	 * From JasonD, ToolmakerSteve 
	 * @param d
	 * @return
	 */
	public static String fmt(double d)
	{
	    if(d == (long) d)
	        return String.format("%d",(long)d);
	    else {
	    	String str = String.format("%.3g",d);
			Pattern pattern = Pattern.compile("^([-0-9]*[.][0-9]*[^0])0+$");
			Matcher matcher = pattern.matcher(str);
			if (matcher.matches()) {
				str = matcher.group(1);
			}
	        return str;
	    }
	}
	
	
	public int intValue() throws BwException {
		if (this.type == BwValueType.VARIABLE) {
			float val = parser.getValue(this.variableName);
			return (int)val;
		}
		return (int)value;
	}
	private BwValueType type;
	private BwDataType dataType;
	private String variableName;		// Variable name
	private float value;
	private String stringValue;			// String value
	private static BwParser parser;		// Central access - including the symbol table
}
