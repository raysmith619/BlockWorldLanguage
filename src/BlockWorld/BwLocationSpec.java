/**
 * 
 */
package BlockWorld;

import java.util.ArrayList;

import javax.vecmath.Point3f;

/**
 * @author raysm
 *
 */
public class BwLocationSpec {
	
	BwLocationSpec() {
		this.x = new BwValue();
		this.y = new BwValue();
		this.z = new BwValue(); 
	}
	
	/**
	 * @return the x
	 */
	public float getX() throws BwException {
		return x.floatValue();
	}

	/**
	 * @param x the x to set
	 */
	public void setX(float x) throws BwException {
		this.x.setValue(x);
	}

	/**
	 * @return the y
	 */
	public float getY() throws BwException {
		return this.y.floatValue();
	}

	/**
	 *  @param y the y to set
	 */
	public void setY(float y) {
		this.y.setValue(y);
	}

	/**
	 * @return the z
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


	/**
	 * precise setting of location
	 * @param x
	 * @param y
	 * @param z
	 */
	public BwLocationSpec(double x, double y, double z) {
		this();
		this.x.setValue((float)x);
		this.y.setValue((float)y);
		this.z.setValue((float)z);
	}
	
	/**
	 * precise setting of location
	 * @param x
	 * @param y
	 * @param z
	 */
	public BwLocationSpec(float x, float y, float z) {
		this();
		this.x.setValue(x);
		this.y.setValue(y);
		this.z.setValue(z);
	}
	
	
	public BwLocationSpec(ArrayList<BwValue> loclist)
		throws Exception {
		if (loclist.size() <= 0 || loclist.size() > 3) {
			throw new Exception("Illegal number of coords:"
								+ loclist.size());
		}
		if (loclist.size() > 0) {
			this.x = loclist.get(0);
			if (loclist.size() > 1) {
				this.y = loclist.get(1);
				 if (loclist.size() > 2) {
					this.z = loclist.get(2);
				} else {
					this.z = this.y;
				}
			} else {
				this.z = this.y = this.x;
			}
		} else {
			this.z = this.y = this.x =loclist.get(0);		
		}
	}
			
	public BwLocationSpec(float...coords)
		throws Exception {
		if (coords.length > 0) {
			x.setValue(coords[0]);
			if (coords.length > 1) {
				y.setValue(coords[1]);
				if (coords.length > 2) {
					z.setValue(coords[2]);
					if (coords.length > 3) {
						throw new Exception("Illegal number of coords:"
							+ coords.length);
					}
				} else {
					z.setValue(coords[1]);
				}
				
			} else {
				z.setValue(coords[0]);
				y.setValue(coords[0]);
			}
		}
	}

	
	/**
	 * Convert to 3d point spec
	 * @throws BwException 
	 */
	public Point3f to3f() throws BwException {
		return new Point3f(this.x.floatValue(), this.y.floatValue(), this.z.floatValue());
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

	private BwValue x;
	private BwValue y;
	private BwValue z;

}
