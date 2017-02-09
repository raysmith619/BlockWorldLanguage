/**
 * 
 */
package BlockWorld;

import java.util.ArrayList;

/**
 * @author raysm
 *
 */
public class BwColorSpec {

	
	BwColorSpec(BwSymTable sT) {
		this.red= mkValue();
		this.green = mkValue();
		this.blue = mkValue(); 
	}
	
	BwColorSpec(BwSymTable sT, float red, float green, float blue) {
		this.red= mkValue(red);
		this.green = mkValue(green);
		this.blue = mkValue(blue); 
	}

	/**
	 * 
	 *
	 * @return the color_name
	 */
	public String getColorName() {
		return colorName;
	}

	/**
	 * @param color_name the color_name to set
	 */
	public void setColorName(String color_name) {
		this.colorName = color_name;
	}

	/**
	 * @return the red
	 */
	public float getRed() throws BwException {
		return red.floatValue();
	}

	/**
	 * @param red the red to set
	 */
	public void setRed(float red) {
		this.red.setValue(red);
	}

	/**
	 * @param red the red to set
	 * @throws BwException 
	 */
	public void setRed(BwValue val) throws BwException {
		this.red.setValue(val);
	}

	/**
	 * @return the green
	 */
	public float getGreen() throws BwException {
		return green.floatValue();
	}

	/**
	 * @param green the green to set
	 */
	public void setGreen(float val) {
		this.green.setValue(val);
	}

	/**
	 * @param green 
	 * @throws BwException 
	 */
	public void setGreen(BwValue val) throws BwException {
		this.green.setValue(val);
	}

	/**
	 * @return the blue
	 * @throws BwException 
	 */
	public float getBlue() throws BwException {
		return blue.floatValue();
	}

	/**
	 * @param blue the blue to set
	 */
	public void setBlue(float blue) {
		this.blue.setValue(blue);
	}

	/**
	 * @param green 
	 * @throws BwException 
	 */
	public void setBlue(BwValue val) throws BwException {
		this.blue.setValue(val);
	}

	public BwColorSpec(String name) {
		setColorName(name);
	}
	
	public BwColorSpec(BwSymTable sT, ArrayList<BwValue> strenth_list)
		throws Exception {
		this(sT);
		if (strenth_list.size() <= 0 || strenth_list.size() > 3) {
			throw new Exception("Illegal number of coords:"
								+ strenth_list.size());
		}
		if (strenth_list.size() > 0) {
			this.red = strenth_list.get(0);
			if (strenth_list.size() > 1) {
				this.green = strenth_list.get(1);
				if (strenth_list.size() > 2) {
					this.blue = strenth_list.get(2);
				} else {
					this.blue = this.green;
				}
			} else {
				//TBD - deep copy needed
				this.blue = this.green = this.red;
			}
		} else {
			this.red.setValue(0F);
			this.green.setValue(0F);
			this.blue.setValue(0F);
		}
	}
	
	/**
	 * 
	 */
	public BwColorSpec(BwSymTable sT, float...strengths)
		throws Exception {
		this(sT);
		if (strengths.length > 0) {
			this.red.setValue(strengths[0]);
			if (strengths.length > 1) {
				this.green.setValue(strengths[1]);
				if (strengths.length > 2) {
					this.blue.setValue(strengths[2]);
					if (strengths.length > 3) {
						throw new Exception("Illegal number of strengths:"
							+ strengths.length);
					}
				} else {
					this.blue = this.green;
				}
				
			} else {
				this.blue = this.green = this.red;

			}
		} 
	}

	/**
	 * From JasonD, ToolmakerSteve 
	 * @param d
	 * @return
	 */
	private static String fmt(double d)
	{
	    if(d == (long) d)
	        return String.format("%d",(long)d);
	    else
	        return String.format("%s",d);
	}

	/**
	 * String version for documentation/display
	 */
	public String toString() {
		BwValue red = (this.red != null) ? this.red : mkValue(0);
		BwValue green = (this.green != null) ? this.green : mkValue(0);
		BwValue blue = (this.blue != null) ? this.blue : mkValue(0);
		
		return ("" + red
			+ "," + green
			+ "," + blue);
	}

	/**
	 * String version for documentation/display
	 */
	public String toStringEval() throws BwException {
		
		return ("" + this.red.toStringEval()
			+ "," + this.green.toStringEval()
			+ "," + this.blue.toStringEval());
	}

	
	/**
	 * Symbol Table short cuts
	 */
	
	public BwValue mkValue() {
		return new BwValue(this.sT);
	}
	
	public BwValue mkValue(float val) {
		return new BwValue(this.sT, val);
	}
		
	private BwSymTable sT;		// Symbol Table access
	private String colorName;	// red, green, blue, ... OR use strengths
	private BwValue red;
	private BwValue green;
	private BwValue blue;

}
