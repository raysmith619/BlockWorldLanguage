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
	
	BwSizeSpec() {
		this.x = new BwValue();
		this.y = new BwValue();
		this.z = new BwValue(); 
	}
	
	BwSizeSpec(float x, float y, float z) {
		this.x = new BwValue(x);
		this.y = new BwValue(y);
		this.z = new BwValue(z); 
	}
	
	
	/**
	 * @return the x
	 * @throws BwException 
	 */
	public float getX() throws BwException {
		return x.floatValue();
	}

	/**
	 * @param x the x to set
	 */
	public void setX(float x) {
		this.x.setValue(x);
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
	public void setY(float y) {
		this.y.setValue(y);
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
	public void setZ(float z) {
		this.z.setValue(z);
	}

	public BwSizeSpec(ArrayList<BwValue> loclist)
		throws Exception {
		this();
		if (loclist.size() <= 0 || loclist.size() > 3) {
			throw new Exception("Illegal number of dimentions:"
								+ loclist.size());
		}
		if (loclist.size() > 0) {
			this.x = loclist.get(0);
			if (loclist.size() > 1) {
				this.y = loclist.get(1);
				if (loclist.size() > 2) {
					this.z = loclist.get(2);
				} else {
					this.z = new BwValue(this.y);
				}
			} else {
				this.y = new BwValue(this.x);
				this.z = new BwValue(this.x);
			}
		} else {
			this.y = new BwValue();
			this.y = new BwValue();
			this.y = new BwValue();
		}
	}
	
	
	/**
	 * 
	 */
	public BwSizeSpec(float...coords)
		throws Exception {
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
					this.z = this.y;
				}
				
			} else {
				this.z = this.y = this.x;

			}
		} 
	}

	/**
	 * String version for documentation/display
	 */
	public String toString() {
		
		return ("" + this.x
			+ "," + this.y
			+ "," + this.z);
	}

	/**
	 * String version for documentation/display
	 */
	public String toStringEval() throws BwException {
		
		return ("" + this.x.toStringEval()
			+ "," + this.y.toStringEval()
			+ "," + this.z.toStringEval());
	}

	
	
	/**
	 * short format 
	 */
	public String fmt(double d)	{
		return BwValue.fmt(d);
	}
	
	
	private BwValue x;
	private BwValue y;
	private BwValue z;
}
