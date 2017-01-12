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

	
	BwColorSpec() {
		this.red= new BwValue();
		this.green = new BwValue();
		this.blue = new BwValue(); 
	}
	
	BwColorSpec(float red, float green, float blue) {
		this.red= new BwValue(red);
		this.green = new BwValue(green);
		this.blue = new BwValue(blue); 
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
	 * @return the green
	 */
	public float getGreen() throws BwException {
		return green.floatValue();
	}

	/**
	 * @param green the green to set
	 */
	public void setGreen(float green) {
		this.green.setValue(green);
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

	public BwColorSpec(String name) {
		setColorName(name);
	}
	
	public BwColorSpec(ArrayList<BwValue> strenth_list)
		throws Exception {
		this();
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
	public BwColorSpec(float...strengths)
		throws Exception {
		this();
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
		BwValue red = (this.red != null) ? this.red : new BwValue(0);
		BwValue green = (this.green != null) ? this.green : new BwValue(0);
		BwValue blue = (this.blue != null) ? this.blue : new BwValue(0);
		
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

	private String colorName;	// red, green, blue, ... OR use strengths
	private BwValue red;
	private BwValue green;
	private BwValue blue;

}
