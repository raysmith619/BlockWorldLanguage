package BlockWorld;

public class BwSliderSpec {
	public BwSliderSpec() {
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
	public BwSliderSpec(String title) {
		this.title = title;
		this.varName = title.toLowerCase();
	}


	/**
	 * String version for documentation/display
	 */
	public String toString() {
		
		return ("(" + this.title
				+ "," + this.minVal
				+ "," + this.curVal
				+ "," + this.maxVal
				+ ")");
	}

	/**
	 * String version for documentation/display
	 */
	public String toStringEval() throws BwException {
		
		return ("(" + this.title
				+ "," + this.minVal.toStringEval()
				+ "," + this.curVal.toStringEval()
				+ "," + this.maxVal.toStringEval()
				+ ")");
	}
	
	
	private String title;	// Name
	private String varName;	// Variable name usually lower case (title)
	private BwValue minVal;	// Minimum
	private BwValue curVal;	// Current value
	private BwValue maxVal;
}
