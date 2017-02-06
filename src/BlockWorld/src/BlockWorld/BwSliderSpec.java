package BlockWorld;

import java.util.ArrayList;

import BlockWorld.BwValue.BwValueType;

public class BwSliderSpec {
		public BwSliderSpec(BwSymTable sT) {
			this.sT = sT;
		}

		public BwSliderSpec(BwSymTable sT, String varName, BwValue[] values) throws BwException {
			this(sT);
			setArray(varName, values);
		}
		
		/**
		 * Set slider from variable name, array of values
		 * @return
		 * @throws BwException 
		 */

		public Boolean setArray(String var_name, BwValue vlist[]) throws BwException {
			//System.out.print("setList:" + var_name + " " + vlist.toString() + "\n");
			BwValue valmin;
			BwValue valcur;
			BwValue valmax;
			// slider ... x => min 0, cur x, max 5*x
			if (vlist.length == 1) {
				valmin = mkValue(0);
				valcur = vlist[0];
				valmax = mkValue(valcur.floatValue()*5F);	// Must be const
			} else if (vlist.length == 2) {	// slider ... x y => min: 0, cur x, max y
				valmin = vlist[0];
				valmax = vlist[1];
				valcur = mkValue((valmin.floatValue()
						+valmax.floatValue())/2F);
			} else if(vlist.length == 3) {
				valmin = vlist[0];
				valcur = vlist[1];
				valmax = vlist[2];
			} else {
				sT.error(String.format("%d is an unsupported number of slider values",
						vlist.length));
				return false;
			}
			setMinVal(valmin);
			setCurVal(valcur);
			setMaxVal(valmax);
			setVarName(var_name);
			BwValue slider_var = this.sT.addVariable(var_name);		// In case we come directly here
			slider_var.setDataType(BwValue.BwDataType.FLOAT);
			slider_var.setValue(valcur);	// Set as current value
			return true;
		}
		
		
	/**
	 * set slider from list of varname, values
	 * @return
	 * @throws BwException 
	 */
	public Boolean setList(ArrayList<BwValue> vlist) throws BwException {
		System.out.print("setList" + vlist);
		BwValue firstpart = vlist.get(0);
		String var_name;		// Set if variable name
		if (firstpart.getType() == BwValueType.VARIABLE) {
			var_name = firstpart.varName();
			this.setTitle(var_name);
			this.setVarName(var_name);
			vlist.remove(0);	// Remove name part
		}
								// slider ... x => min 0, cur x, max 5*x
		if (vlist.size() == 1) {
			this.setMinVal(mkValue(0));
			this.setCurVal(vlist.get(0));
			this.setMaxVal(mkValue(vlist.get(0).floatValue()*5));	// Must be const
		} else if (vlist.size() == 2) {	// slider ... x y => min: 0, cur x, max y
			this.setMinVal(mkValue(0));
			this.setCurVal(vlist.get(0));
			this.setMaxVal(vlist.get(1));
		} else if (vlist.size() == 3) {
			this.setMinVal(vlist.get(0));
			this.setCurVal(vlist.get(1));
			this.setMaxVal(vlist.get(2));
		} else {
			firstpart.syntaxError(String.format("%d is an unsupported number of slider values",
					vlist.size()));
			return false;
		}
		return true;
	}

	/**
	 * Setting slider variable name
	 * Creating variable if necessary
	 * @param var_name
	 * @return
	 */
	public Boolean setVariable(String var_name) {
		this.setTitle(var_name);
		this.setVarName(var_name);
		this.sT.addVariable(var_name);
		return true;
	}

	public Boolean setVariable(BwValue name) {
		String var_name = name.varName();
		return this.setVariable(var_name);
	}
	
	public String getTitle() {
		return title;
	}
	public String getVarName() {
		return varName;
	}
	public BwValue getMinVal() {
		return minVal;
	}
	public BwValue getCurVal() {
		return curVal;
	}
	public BwValue getMaxVal() {
		return maxVal;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public void setMinVal(BwValue minVal) {
		this.minVal = minVal;
	}
	public void setCurVal(BwValue curVal) {
		this.curVal = curVal;
	}
	public void setMaxVal(BwValue maxVal) {
		this.maxVal = maxVal;
	}

	/**
	 * String version for documentation/display
	 */
	public String toString() {
		String str = "(";
		if (this.title != null)
			str += this.title;
		else if (this.varName != null)
			str += this.varName;
		if (this.minVal != null)
			str += "," + this.minVal;
		if (this.curVal != null)
			str += "," + this.curVal;
		if (this.maxVal != null)
			str += "," + this.maxVal;
		str += ")";
		return str;
	}

	/**
	 * String version for documentation/display
	 */
	public String toStringEval() throws BwException {
		String str = "(";
		if (this.title != null)
			str += this.title;
		else if (this.varName != null)
			str += this.varName;
		if (this.minVal != null)
			str += "," + this.minVal.toStringEval();
		if (this.curVal != null)
			str += "," + this.curVal.toStringEval();
		if (this.maxVal != null)
			str += "," + this.maxVal.toStringEval();
		str += ")";
		return str;
	}

	/**
	 * Shortcuts to Symbol Table
	 */
	public BwValue mkValue() {
		return new BwValue(this.sT);
	}

	public BwValue mkValue(int val) {
		return new BwValue(this.sT, val);
	}

	public BwValue mkValue(float val) {
		return new BwValue(this.sT, val);
	}
	
	private BwSymTable sT;	// Access to symbol table
	private String title;	// Name
	private String varName;	// Variable name usually lower case (title)
	private BwValue minVal;	// Minimum
	private BwValue curVal;	// Current value
	private BwValue maxVal;
}
