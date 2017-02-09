/**
 * 
 */
package BlockWorld;

import java.util.ArrayList;

/**
 * @author raysm
 *
 */
public class BwSizeSpec {
	
	BwSizeSpec(BwSymTable sT) {
		this.sT = sT;
		this.x = new BwValue(sT);
		this.y = new BwValue(sT);
		this.z = new BwValue(sT); 
	}
	
	BwSizeSpec(BwSymTable sT, double x, double y, double z) {
		this(sT);
		this.x.setValue((float)x);
		this.y.setValue((float)y);
		this.z.setValue((float)z);
	}
	
	BwSizeSpec(BwSymTable sT, float x, float y, float z) {
		this(sT);
		setX(x);
		setY(y);
		setZ(z);
	}
	
	
	/**
	 * @return the x
	 * @throws BwException 
	 */
	public float getX() throws BwException {
		return x.floatValue();
	}
	
	
	/**
	 * @return the x
	 * @throws BwException 
	 */
	public BwValue getXVal() throws BwException {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(float v) {
		this.x.setValue(v);
	}

	/**
	 * @param x set value
	 */
	public void setX(BwValue v) {
		this.x = v;
	}

	/**
	 * @return the y
	 * @throws BwException 
	 */
	public float getY() throws BwException {
		return y.floatValue();
	}

	/**
	 * @param y the y to set
	 */
	public void setY(float v) {
		this.y.setValue(v);
	}

	/**
	 * @param y set value
	 */
	public void setY(BwValue v) {
		this.y = v;
	}

	/**
	 * @return the z
	 * @throws BwException 
	 */
	public float getZ() throws BwException {
		return z.floatValue();
	}

	/**
	 * @param z the z to set
	 */
	public void setZ(float v) {
		this.z.setValue(v);
	}

	/**
	 * @param z set value
	 */
	public void setZ(BwValue v) {
		this.z = v;
	}

	public BwSizeSpec(BwSymTable sT, BwValue[] loclist) throws Exception {
		this(sT);
		if (loclist.length <= 0 || loclist.length > 3) {
			throw new Exception("Illegal number of dimentions:"
								+ loclist.length);
		}
		if (loclist.length >= 1) {
			this.x = loclist[0];
			if (loclist.length >= 2) {
				this.y = loclist[1];
				if (loclist.length == 3) {
					this.z = loclist[2];
				} else {
					setZ(loclist[1]);
				}
			} else {
				setY(loclist[0]);
				setZ(loclist[0]);
			}
		} else {
			setX(0F);
			setY(0F);
			setZ(0F);
		}
	}
	public BwSizeSpec(BwSymTable sT, ArrayList<BwValue> loclist)
			throws Exception {
			this(sT, sT.list2array(loclist));
	}
	
	/**
	 * 
	 */
	public BwSizeSpec(BwSymTable sT, float...coords)
		throws Exception {
		this(sT);
		if (coords.length > 0) {
			this.x.setValue(coords[0]);
			if (coords.length > 1) {
				this.y.setValue(coords[1]);
				if (coords.length > 2) {
					this.z.setValue(coords[2]);
					if (coords.length > 3) {
						throw new Exception("Illegal number of coords:"
							+ coords.length);
					}
				} else {
					setZ(this.y);
				}
				
			} else {
				setY(this.x);
				setZ(this.x);

			}
		} 
	}

	/**
	 * String version for documentation/display
	 */
	public String toString() {
		String str = "";
		if (this.x != null)
			str += this.x;
		if (this.y != null)
			str +=  "," + this.y;
		if (this.z != null)
			str +=  "," + this.z;
		return str;
	}

	/**
	 * String version for documentation/display
	 */
	public String toStringEval() throws BwException {
		String str = "";
		if (this.x != null)
			str += this.x.toStringEval();
		if (this.y != null)
			str +=  "," + this.y.toStringEval();
		if (this.z != null)
			str +=  "," + this.z.toStringEval();
		return str;
	}

	
	
	/**
	 * short format 
	 */
	public String fmt(double d)	{
		return BwValue.fmt(d);
	}
	
	/**
	 * list to array
	 * for parser values to Jython acceptable values
	 */
	public BwValue[] list2array(ArrayList<BwValue> values) {
		return sT.list2array(values);
	}
	
	private BwSymTable sT;		// Symbol table
	private BwValue x;
	private BwValue y;
	private BwValue z;
}
