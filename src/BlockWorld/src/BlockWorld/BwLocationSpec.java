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
	
	BwLocationSpec(BwSymTable sT) {
		this.x = mkValue();
		this.y = mkValue();
		this.z = mkValue(); 
	}


	/**
	 * precise setting of location
	 * @param x
	 * @param y
	 * @param z
	 */
	public BwLocationSpec(BwSymTable sT, double x, double y, double z) {
		this(sT);
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
	public BwLocationSpec(BwSymTable sT, float x, float y, float z) {
		this(sT);
		this.x.setValue(x);
		this.y.setValue(y);
		this.z.setValue(z);
	}
	
	
	public BwLocationSpec(BwSymTable sT, int x, int y, int z) {
		this(sT);
		this.x.setValue(x);
		this.y.setValue(y);
		this.z.setValue(z);
	}
	
	
	public BwLocationSpec(BwSymTable sT, ArrayList<BwValue> loclist)
		throws Exception {
		this(sT, sT.list2array(loclist));
	}	
	
	public BwLocationSpec(BwSymTable sT, BwValue loclist[])
		throws Exception {
		this(sT);
		if (loclist.length <= 0 || loclist.length > 3) {
			throw new Exception("Illegal number of coords:"
								+ loclist.length);
		}
		if (loclist.length > 0) {
			this.x = loclist[0];
			if (loclist.length > 1) {
				this.y = loclist[1];
				 if (loclist.length > 2) {
					this.z = loclist[2];
				} else {
					this.z = this.y;
				}
			} else {
				this.z = this.y = this.x;
			}
		} else {
			this.z = this.y = this.x =loclist[0];		
		}
	}
			
	public BwLocationSpec(BwSymTable sT, float...coords)
		throws Exception {
		this(sT);
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
	 * @param x set value
	 */
	public void setX(BwValue x) {
		this.x = x;
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
	 * @param y set value
	 */
	public void setY(BwValue x) {
		this.y = y;
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
	 * @param z set value
	 */
	public void setZ(BwValue z) {
		this.x = z;
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
		String str = "(";
		if (this.x != null)
			str += this.x;
		if (this.y != null)
			str += "," + this.y;
		if (this.z != null)
			str += "," + this.z;
		str += ")";
		return str;
	}

	/**
	 * String version for documentation/display
	 */
	public String toStringEval() throws BwException {
		String str = "(";
		if (this.x != null)
			str += this.x.toStringEval();
		if (this.y != null)
			str += "," + this.y.toStringEval();
		if (this.z != null)
			str += "," + this.z.toStringEval();
		str += ")";
		return str;
	}

	/**
	 * Shortcuts to Symbol Table
	 */
	public BwValue mkValue() {
		return new BwValue(this.sT);
	}
	
	private BwSymTable sT;		// Access to Symbol Table
	private BwValue x;
	private BwValue y;
	private BwValue z;

}
