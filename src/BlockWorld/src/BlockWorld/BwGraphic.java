package BlockWorld;
/**
 * 
 */
/**
 * @author raysm
 *
 */

public class BwGraphic {

	public static enum Type {
		AXIS,
		BLOCK,
		SPHERE,
		CONE,
		CYLINDER,
		PYRAMID,
		LIGHTSOURCE,
		LINE,
		LOOKATEYE,				// lookAt eye loc
		LOOKATCENTER,			// lookAt center	loc
		LOOKATUP,				// lookAt up Vector
		POINT,					// Point, abbreviated to PT
		SLIDER,					// Setup slider control
		TEXT,					// 3D-text
		TEXT2D,					// 2D-text
		WINDOW,					// Display Window
		UNKNOWN,				// type currently unknown
	}

	public String toString() {
		return type2str(this.getType());
	}

	
	public static Type str2type(String str) {
		switch (str.toUpperCase()) {
			case "UNKNOWN":
				return Type.UNKNOWN;
			case "AXIS":
				return Type.AXIS;
			case "BLOCK":
				return Type.BLOCK;
			case "CONE":
				return Type.CONE;
			case "PYRAMID":
				return Type.PYRAMID;
			case "SPHERE":
				return Type.SPHERE;
			case "CYLINDER":
				return Type.CYLINDER;
			case "LINE":
				return Type.LINE;		// One or more connected segments
			case "LIGHTSOURCE":
				return Type.LIGHTSOURCE;
			case "LOOKATEYE":
				return Type.LOOKATEYE;
			case "LOOKATCENTER":
				return Type.LOOKATCENTER;
			case "LOOKATUP":
				return Type.LOOKATUP;
			case "POINT":
			case "PT":
				return Type.POINT;
			case "SLIDER":
				return Type.SLIDER;
			case "TEXT":
				return Type.TEXT;
			case "TEXT2D":
				return Type.TEXT2D;
			case "WINDOW":
				return Type.WINDOW;
		}
		return Type.UNKNOWN;
	}


	public static String type2str(Type type) {
		switch (type) {
			case UNKNOWN:
				return "UNKNOWN";
			case AXIS:
				return "AXIS";
			case BLOCK:
				return "BLOCK";
			case CONE:
				return "CONE";
			case PYRAMID:
				return "PYRAMID";
			case SPHERE:
				return "SPHERE";
			case CYLINDER:
				return "CYLINDER";
			case LINE:
				return "LINE";
			case LIGHTSOURCE:
				return "LIGNTSOURCE";
			case LOOKATEYE:
				return "LOOKATEYE";
			case LOOKATCENTER:
				return "LOOKATCENTER";
			case LOOKATUP:
				return "LOOKATUP";
			case POINT:
				return "POINT";
			case SLIDER:
				return "SLIDER";
			case TEXT2D:
				return "TEXT2D";
			case WINDOW:
				return "WINDOW";
		}
		return "ILLEGAL";
	}

	public float getLength() { return 0; }
	public float getWidth() { return 0; }
	public float getHeight() { return 0; }
	
	
	public String typeStr() {
		return type2str(getType());
	}
	
	public void setType(Type type) {
		this.type = type;
	}

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public Type getType() {
		return this.type;
	}
	
	protected Type type = Type.UNKNOWN;
							/**
							 * Java3d window/universe variables
							 * 
							 */
}
