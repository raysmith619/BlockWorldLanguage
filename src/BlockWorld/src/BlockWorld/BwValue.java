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

	
	public String valueType2string(BwValueType vtype) {
		switch (vtype) {
			case UNDEFINED:
				return "UNDEFINED";
			case CONSTANT:
				return "CONSTANT";
			case VARIABLE:
				return "VARIABLE";
			case CALCULATION:
				return "CALCULATION";
			default:
				return "UNKNOWN";
		}
	}
	
/**
 * common error
 *  - throw	
 */
	private void error(String msg) throws BwException {
		System.out.print("error:" + msg);
		throw new BwException(msg);
	}
	
	public BwValue(BwSymTable sT) {
		this.type = BwValueType.CONSTANT;
		this.dataType = BwDataType.FLOAT;
		this.value = 0;
		this.stringValue = "";
		this.variableName = "";
		this.sT = sT;
	}

	/**
	 * Copy constructor
	 * giving deep / unique copy
	 * 
	 * @param value one version
	 * @return unique version
	 */
	public BwValue(BwSymTable sT, BwValue value) throws BwException {
		this(value.sT);
		setValue(value);
	}
	
	/**
	 * @param type
	 * @param variableName
	 * @param value
	 */
	public BwValue(BwSymTable sT,
			BwValueType type, String variableName, float value) {
		this(sT);
		this.type = type;
		this.variableName = variableName;
		this.value = value;
	}

	public BwValue(BwSymTable sT, int value) {
		this(sT);
		setValue((float)value);
	}

	public BwValue(BwSymTable sT, double value) {
		this(sT);
		this.setValue((float)value);
	}

	public BwValue(BwSymTable sT, float value) {
		this(sT);
		this.setValue(value);
	}

	public BwValue(BwSymTable sT, String value) {
		this(sT);
		setValue(value);
	}

	/**
	 * Set data type
	 */
	public void setDataType(BwDataType type) {
		this.dataType = type;
	}
	
	/**
	 * Set value type
	 */
	public void setType(BwValueType type) {
		this.type = type;
	}
	
	/**
	 * Set value type to VARIABLE
	 */
	public void setTypeVariable() {
		this.type = BwValueType.VARIABLE;
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
	public BwValue setVariable(String name) {
		if (!this.sT.inTable(name)) {
			this.sT.addVariable(name);
		}
		setName(name);
		setType(BwValue.BwValueType.VARIABLE);
		return this.sT.getValueObject(name);
	}
	
	/**
	 * set from parameter
	 * @throws BwException 
	 */
	public void setValue(BwValue value) throws BwException {
		this.type = value.type;
		if (value.type == BwValueType.VARIABLE)
			this.variableName = value.variableName;
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
		if (this.type == BwValueType.VARIABLE) {
			String name = this.variableName;
			if (name == null)
				name = "UNDEFINED";
			return name;
		}
		String type_name = valueType2string(this.type);
		String eval = this.toString();
		return type_name + "(" + eval + ")";
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
			String variableName = "???";
			if (this.variableName != null)
				variableName = this.variableName;
			else
				variableName = "NULL_VARIABLE_NAME";
			return variableName;
		}
		BwDataType dataType = this.dataType;
		if (dataType == BwDataType.FLOAT)
			return fmt(this.value);
		else if (dataType == BwDataType.STRING) {
				stringValue = this.stringValue;
				if (stringValue == null)
					return "NULL string";
				else 
					return stringValue;
		}
		return "UNRECOGNIZED Data Type";
	}
	
	/**
	 * string value
	 * value if one else name
	 * @return
	 */
	public String toStringEval()  throws BwException {
		BwValue value = this;
		if (this == null)
			return "NULLVALUE";
		
		if (this.type == BwValueType.VARIABLE) {
			String var_name = this.variableName;
			if (var_name == null) {
				System.err.printf("toStringEval var_name = null");
				error("No variable name for value of type VARIABLE");
			}
			if (!this.sT.inTable(var_name)) {
				System.err.printf("toStringEval var_name not in table");
				return "UNDEFINED";
			}
			
			value = this.sT.getValueObject(this.variableName);
			value.setType(BwValue.BwValueType.CONSTANT);		// current value
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
			float val = this.sT.getValue(this.variableName);
			return val;
		}
		return value;
	}

	
	public String stringValue() throws BwException {
		if (this.type == BwValueType.VARIABLE) {
			String val = this.sT.getStringValue(this.variableName);
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

	/**
	 * Indicate and report syntax error
	 * 
	 * @param msg - error message
	 * @throws BwException 
	 */
	public void syntaxError(String msg) throws BwException {
		error(msg);
	}

	/**
	 * Get immediate string value
	 * @return string value
	 */
	public String immediateStringValue() {
		return this.stringValue;
	}
	
	
	public int intValue() throws BwException {
		if (this.type == BwValueType.VARIABLE) {
			float val = this.sT.getValue(this.variableName);
			return (int)val;
		}
		return (int)value;
	}
	private BwValueType type;
	private BwDataType dataType;
	private String variableName;		// Variable name
	private float value;
	private String stringValue;			// String value
	private BwSymTable sT;				// Symbol table
}
