package BlockWorld;

import javax.vecmath.Color3f;
import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Tuple3f.*;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.pickfast.PickCanvas;
import com.sun.j3d.utils.pickfast.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.*;
import javax.swing.Renderer;
import javax.media.j3d.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;

public class BwDisplay extends JFrame implements MouseListener{

	/**
	 * Setup display/execute
	 * 
	 * @param trace tracing/logging control
	 * @param bExec - execution control, parsing, vars
	 * @throws Exception 
	 */
	public BwDisplay(BwTrace trace, BwExec bexec) throws Exception {
		
		this.trace = trace;
		this.bExec = bexec;

		this.parser = bExec.getParser();			// Short cut
		this.sT = this.parser.getSymTable();	// Short cut to sym table
		setupDisplay();
		this.lookAtCenter = this.parser.mkLocationSpec(0,0,0);
		this.lookAtEye = this.parser.mkLocationSpec(10,10,10);
		this.lookAtUp = this.parser.mkLocationSpec(0,0,1);
		setDisplayWindowSize(1400, 900);
		setTitle("Getting Ready");
		setVisible(true);
	}

	
	/**
	 * Setup or resetup graphics platform
	 * Allowing blank display
	 */
	public void setupDisplay() {
		this.app = new Appearance();			// Default graphic appearance
		setLayout(new BorderLayout());
	    GraphicsConfiguration config = SimpleUniverse
	            .getPreferredConfiguration();
	    this.canvas = new Canvas3D(config);
		add(BorderLayout.CENTER, this.canvas);
		this.universe = new SimpleUniverse(this.canvas);
		this.rootGroup = new BranchGroup();
		this.rootGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		this.rootGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		this.rootGroup.setCapability(BranchGroup.ALLOW_DETACH);		
		this.branchGroup = new BranchGroup();
		this.branchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		this.branchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		this.branchGroup.setCapability(BranchGroup.ALLOW_DETACH);
		/*+ Support mouse picking
		this.pickCanvas = new PickCanvas(canvas, this.branchGroup);
		this.pickCanvas.setMode(PickTool.TYPE_GROUP);
		canvas.addMouseListener(this);
		+*/
		// add the group of objects to the Universe
		this.branchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		this.branchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		this.branchGroup.setCapability(BranchGroup.ALLOW_DETACH);
		this.universe.addBranchGraph(this.branchGroup);
	}
	
	/**
	 * Execute one or more command
	 * @param cmds
	 */
	public void display(BwCmd ...cmds) throws BwException {
		if (trace.traceDebug())
			System.out.printf("in BwDisplay.display()\n");
		this.startTime = System.nanoTime();
		if (this.timeLimit >= 0) {
			this.endTime = (long) this.startTime
					+ this.startTime + (long)(1e9*this.timeLimit);
		}
		for (BwCmd cmd : cmds) {
			displayCmd(cmd);
		}
		if (this.branchGroup != null) {
			if (this.branchGroup.numChildren() > 0) {
//	TFD			this.branchGroup.removeAllChildren();
			}
		}
		setTitle("Displaying");
		setVisible(true);
		if (this.controls != null)
			this.controls.startDisplay();
		setVisible(true);
		if (trace.traceDebug())
			System.out.printf("leaving BwDisplay.display()\n");
	}

	
	/**
	 * Check if time limited
	 */
	public boolean isTimeLimited() {
		return this.timeLimit >= 0;
	}

	/**
	 * Set time limit
	 */
	public void setTimeLimit(float limit) {
		this.timeLimit = limit;
		if (limit < 0)
			this.endTime = -1;		// Force ignore
		else
			this.endTime = this.startTime + (long)(limit*1e9F);
	}
	public float getTimeLimit() {
		return this.timeLimit;
	}

	/**
	 * Execute one display command
	 * @param cmd
	 * @return true iff successful
	 */
	public boolean displayCmd(BwCmd cmd) throws BwException {
		if (trace.traceDebug())
			System.out.printf("in displayCmd(cmd)\n");
		switch (cmd.getCmdType()) {
			case DISPLAY_SCENE:		// Display sceen
				break;
				
			case ADD_OBJECT:		// Add object to list
				return addGraphic(cmd);
				
			case DELETE_OBJECT:		// Delete object
				break;
				
			case EMPTY:				// empty object - remove graphic, if one
				return setEmpty(cmd);
				
			case MOVE_OBJECT:		// Move object
				break;
			
			case INCLUDE_FILE:		// Include file (compile directive)
				break;
				
			case INCLUDE_FILE_END:	// Include file (compile directive)
				break;
				
			case MODIFY_OBJECT:		// Modify object
				break;
				
			case QUIT_PROGRAM:		// Quit program
				break;
				
			case LIST_CMD:			// List command line
				break;
				
			case DUPLICATE_CMD:		// Duplicate command
				break;
				
			case NO_OP:				// Do nothing
				return true;

			case SET_CMD:
				return setCmd(cmd);
				

			case SLIDER:
				return sliderCmd(cmd);
				
			case UNKNOWN:			// Unknown
				break;
			
			default:
				System.out.printf("Unrecognized displayCmd: %s\n",  cmd);
				return false;
		}
		return true;				// To ignore any unimplemented
	}

	/**
	 * 
	 * @param cmd - graphic object to add
	 * @return  - true iff ok
	 */
	public boolean addGraphic(BwCmd cmd) throws BwException {
		if (trace.traceDebug())
			System.out.printf("in addGraphic\n");
		BwGraphic.Type type = cmd.getGraphicType();
		switch (type) {
        case AXIS:
        	return addAxis(cmd);

        case BLOCK:
        	return addBlock(cmd);

        case CONE:
            return addCone(cmd);

        case CYLINDER:
        	return addCylinder(cmd);

        case LINE:
        	return addLine(cmd);

        case LIGHTSOURCE:
            break;

        case LOOKATEYE:
        	BwLocationSpec lae = cmd.getLoc();
        	this.lookAtEye = lae;
            break;

        case LOOKATCENTER:
        	BwLocationSpec lac = cmd.getLoc();
        	this.lookAtCenter = lac;
            break;

        case LOOKATUP:
        	BwLocationSpec lau = cmd.getLoc();
        	this.lookAtUp = lau;
            break;

        case PYRAMID:
            break;

        case SPHERE:
            return addSphere(cmd);

        case TEXT:
            return addText(cmd);

        case TEXT2D:
            return addText2D(cmd);
        	
        case WINDOW:
        	return addWindow(cmd);
        	
        default:
            System.out.printf("Unrecognized type %s", type);
		}
		return true;			// Default noop to as yet unsupported
	}

	
	/**
	 * Translate cmd color
	 * @param cmd
	 * @return Color3f object
	 * @throws BwException 
	 */
	private Color3f cmdColor(BwCmd cmd) throws BwException {
		BwColorSpec bwcolor = cmd.getColor();
		Color3f color = bwcolor2color3f(bwcolor);
		return color;
	}

	
	private Color3f bwcolor2color3f(BwColorSpec bwcolor) throws BwException {
		String cname = bwcolor.getColorName();
		Color3f color;
		if (cname != null) {
			
			Color awt_color = Color.decode("RED");
			color = new Color3f(awt_color);
		} else {
			color = new Color3f(bwcolor.getRed(), bwcolor.getGreen(), bwcolor.getBlue());
		}
		return color;
		
	}
	
	/**
	 * Get object location (x,y,z)
	 * @param cmd
	 * @return location in universe
	 */
	private Vector3f cmdLoc(BwCmd cmd) throws BwException {
		BwLocationSpec loc = cmd.getLoc();
		Vector3f vector = new Vector3f(loc.getX(), loc.getY(), loc.getZ());
		return vector;
	}

	/**
	 * Get object size (x,y,z)
	 * @param cmd
	 * @return size
	 * @throws BwException 
	 */
	private Vector3f cmdSize(BwCmd cmd) throws BwException {
		BwSizeSpec size = cmd.getSize();
		Vector3f vector = new Vector3f(size.getX(), size.getY(), size.getZ());
		return vector;
	}

	
	/**
	 * Place graphic primative in display region
	 * @param cmd - annotated with graphics info to facilitate updates
	 * @param prim - primitive graphic object
	 * @param loc - object location in bw terms
	 * @param color - object color in bw terms
	 * @return true iff success
	 */
	public boolean placeGraphic(
			BwCmd cmd,
			Primitive prim,
			BwLocationSpec loc,
			BwColorSpec bwcolor) throws BwException {
		if (!colorGraphic(prim, bwcolor))
			return false;
		////BranchGroup group = new BranchGroup();
		////group.addChild(prim);
		////return placeGraphic(cmd, group, loc);
		return placeGraphic(cmd, prim, loc);
	}

	
	/**
	 * Place graphic primative in display region
	 *  Assumes color already set in prim
	 * @param cmd - annotated with graphics info to facilitate updates
	 * @param prim - primitive graphic object
	 * @param loc - object location in bw terms
	 * @return true iff success
	 */
	public boolean placeGraphic(
			BwCmd cmd,
			Primitive prim,
			BwLocationSpec loc
			) throws BwException {
		BranchGroup prim_group = new BranchGroup();
		prim_group.addChild(prim);
		return placeGraphic(cmd, prim_group, loc);
	}

	
	/**
	 * Color graphic primitive
	 */
	public boolean colorGraphic(
		Primitive prim,
		BwColorSpec bwcolor) throws BwException {
		Color3f color = bwcolor2color3f(bwcolor);
		Appearance app = new Appearance();
		ColoringAttributes ca = new ColoringAttributes(
				   			color, ColoringAttributes.NICEST);
		app.setColoringAttributes(ca);
		prim.setAppearance(app);
		return true;
	}

	/**
	 * Place graphic object in display region
	 * @param cmd - annotated with graphics info to facilitate updates
	 * @param prim - primitive graphic object
	 * @param loc - object location in bw terms
	 * @return true iff success
	 */
	public boolean placeGraphic(
		BwCmd cmd,
		BranchGroup group,
		BwLocationSpec loc
		) throws BwException {

		
		Vector3f vector = new Vector3f(loc.getX(), loc.getY(), loc.getZ());

		TransformGroup tg = new TransformGroup();
		Transform3D transform = new Transform3D();

	    transform.setTranslation(vector);
		tg.setTransform(transform);
		tg.addChild(group);
		BranchGroup gg = new BranchGroup();		// Wrap graphic
		//cmd.setBranchGroup(gg);				// Keep branch group with cmd
		cmd.setBranchGroup(this.branchGroup);
		gg.addChild(tg);

		return placeGraphic(cmd, gg);
	}

	/**
	 * Place graphic object, whose color and location
	 * are self-embedded, in display region
	 * @param cmd - annotated with graphics info to facilitate updates
	 * @param prim - primitive graphic object
	 * @return true iff success
	 */
	public boolean placeGraphic(
		BwCmd cmd,
		BranchGroup group
		) throws BwException {

		
		BranchGroup gg = new BranchGroup();		// Wrap graphic
	    gg.addChild(group);
		gg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		gg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		gg.setCapability(BranchGroup.ALLOW_DETACH);		
	    int old_grid = cmd.getGrid();			// Get old id, if any
	    if (old_grid != BwCmd.GRID_NONE) {
	    	this.branchGroup.removeChild(old_grid);
	    	this.branchGroup.insertChild(gg, old_grid);
	    } else {
	    	this.branchGroup.addChild(gg);
	    	int nchild = this.branchGroup.numChildren();
	    	int grid = nchild-1;
	    	cmd.setGrid(grid);
	    }
		
	    /**
	     * For debugging purposes place small cube in center
	     *
	    BranchGroup contents = new BranchGroup();	// TFD
	    contents.addChild(new ColorCube(0.25));	// TFD
	    this.branchGroup.addChild(contents);		// TFD
	    */

		
		return true;
	}

	/**
	 * Convert Bw location to java3d point
	 * @param loc
	 * @return
	 */
	public Point3d loc2point(BwLocationSpec loc) throws BwException {
		return new Point3d(loc.getX(), loc.getY(), loc.getZ());
	}

	/**
	 * Convert Bw location to java3d vector
	 * @param loc
	 * @return
	 */
	public Vector3d loc2vector(BwLocationSpec loc) throws BwException {
		return new Vector3d(loc.getX(), loc.getY(), loc.getZ());
	}

	
	/**
	 * Set display window size based on
	 * currently stored values
	 */
	void setDisplayWindowSize() {
		setDisplayWindowSize(this.windowHeight, this.windowWidth);
	}

	
	/**
	 * Set display window size
	 * updating currently stored values
	 */
	void setDisplayWindowSize(int height, int width) {
		
		this.windowHeight = height;
		this.windowWidth = width;
		setSize(this.windowHeight, this.windowWidth);
	}

	
	/**
	 * Set variable cmd
	 * Only simple assignment supported at the moment
	 */
	public boolean setCmd (BwCmd cmd) {
		String var_name = cmd.getSetVariableName();
		BwValue set_value = cmd.getSetValue();
		try {
			this.parser.setValue(var_name, set_value);
		} catch (BwException ex) {
			String errmsg = ex.getMessage();
			System.out.printf("setCmd:(%s,): %s\n",
					var_name, errmsg);
			ex.printStackTrace();
		}
		return true;
	}

	
	/**
	 * connect with controls
	 */
	public void setControls(BwControls controls) {
		this.controls = controls;
	}
	
	/**
	 * Setup Slider
	 * Initializes variable to slider's current setting
	 * Sets up slider control if new
	 * @throws BwException 
	 */
	public boolean sliderCmd (BwCmd cmd) throws BwException {
		BwSliderSpec slider = cmd.getSliderSpec();
		if (this.controls == null)
			return false;
		return controls.sliderCmd(slider);
	}
	
	
	/**
	 * Complete display
	 */
	public void setDisplay() throws BwException {
		//   Color3f light1Color = new Color3f(1.8f, 0.1f, 0.1f);
		   Color3f light1Color = new Color3f(0.1f, 0.1f, 1.8f);	// Blue?
		
		   BoundingSphere bounds =
		
		   new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
		
		   Vector3f light1Direction = new Vector3f(10.0f, 10.0f, 12.0f);
		
		   DirectionalLight light1
		
		      = new DirectionalLight(light1Color, light1Direction);
		
		   light1.setInfluencingBounds(bounds);
		
		   this.rootGroup.addChild(light1);
		
			// look towards the objects
		// Position the position from which the user is viewing the scene

		ViewingPlatform viewPlatform = universe.getViewingPlatform();
		TransformGroup viewTransform = viewPlatform.getViewPlatformTransform();
		Transform3D t3d = new Transform3D();
		viewTransform.getTransform(t3d);
		Point3d eye = loc2point(this.lookAtEye);
		Point3d center = loc2point(this.lookAtCenter);
		Vector3d up = loc2vector(this.lookAtUp);
		t3d.lookAt(eye, center, up);
		t3d.invert();
		viewTransform.setTransform(t3d);
		ViewingPlatform vp = universe.getViewingPlatform();
//TFD		vp.setNominalViewingTransform();
		
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDisplayWindowSize();		// If not already set
		setTitle("Bw Display");
	}

	/**
	 * Add axis object to display
	 * @param cmd
	 * @return true --> success
	 * @throws BwException
	 */
	public boolean addAxis(BwCmd cmd) throws BwException {
		BwColorSpec color = cmd.getColor();
		if (color == null) {
			color = mkColorSpec(1f, 1f, 1F);
		}
		float deflen = 10;
		BwSizeSpec size = cmd.getSize();
		if (size == null) {
			size = mkSizeSpec(deflen, deflen, deflen);
		}

		float xlen = size.getX();
		float ylen = size.getY();
		float zlen = size.getZ();
		Color3f axis_color = bwcolor2color3f(color);
	    BranchGroup axis_group = new BranchGroup();

	    // create line for X axis
	    LineArray axisXLines = new LineArray(2,
	    		LineArray.COORDINATES |	LineArray.COLOR_3);
	    axis_group.addChild(new Shape3D(axisXLines));
	    axisXLines.setCoordinate(0, new Point3f(-xlen, 0.0f, 0.0f));
	    axisXLines.setCoordinate(1, new Point3f(xlen, 0.0f, 0.0f));
	    axisXLines.setColor(0, axis_color);
	    Font3D f3d = new Font3D(new Font("TestFont", Font.PLAIN, 1),
	            new FontExtrusion());
	    Text3D text3D = new Text3D(f3d, "X-axis",
	    		new Point3f(-xlen, 0.2f, 0.0f),
	    		Text3D.ALIGN_FIRST, Text3D.PATH_RIGHT);
	    text3D.setCapability(Geometry.ALLOW_INTERSECT);
	    Shape3D s3D1 = new Shape3D();
	    s3D1.setGeometry(text3D);
	    Appearance a = new Appearance();
	    s3D1.setAppearance(a);
	    axis_group.addChild(s3D1);
	    placeText(axis_group, "x=3", new Point3f(3f,0f,0f));
	    placeText(axis_group, "x=-3", new Point3f(-3f,0f,0f));

	    // create line for Y axis
	    LineArray axisYLines = new LineArray(2,
	    		LineArray.COORDINATES |	LineArray.COLOR_3);
	    axis_group.addChild(new Shape3D(axisYLines));
	    axisYLines.setCoordinate(0, new Point3f(0f, -ylen, 0f));
	    axisYLines.setCoordinate(1, new Point3f(0f, ylen, 0f));
	    axisYLines.setColor(0, axis_color);
	    //Font3D f3d = new Font3D(new Font("TestFont", Font.PLAIN, 1),
	    //        new FontExtrusion());
	    Text3D y_text3D = new Text3D(f3d, "Y-axis",
	    		new Point3f(0f, -ylen, 0.0f),
	    		Text3D.ALIGN_FIRST, Text3D.PATH_RIGHT);
	    y_text3D.setCapability(Geometry.ALLOW_INTERSECT);
	    Shape3D y_s3D1 = new Shape3D();
	    y_s3D1.setGeometry(y_text3D);
	    //Appearance a = new Appearance();
	    y_s3D1.setAppearance(a);
	    axis_group.addChild(y_s3D1);
	    placeText(axis_group, "y=3", new Point3f(0f,3f,0f));
	    placeText(axis_group, "y=-3", new Point3f(0f,-3f,0f));
	    
	    // create line for Z axis
	    LineArray axisZLines = new LineArray(2,
	    		LineArray.COORDINATES |	LineArray.COLOR_3);
	    axis_group.addChild(new Shape3D(axisZLines));
	    axisZLines.setCoordinate(0, new Point3f(0f, 0f, -zlen));
	    axisZLines.setCoordinate(1, new Point3f(0f, 0f, zlen));
	    axisZLines.setColor(0, axis_color);
	    //Font3D f3d = new Font3D(new Font("TestFont", Font.PLAIN, 1),
	    //        new FontExtrusion());
	    Text3D z_text3D = new Text3D(f3d, "Z-axis",
	    		new Point3f(0f, 0f, -zlen*.5f),
	    		Text3D.ALIGN_FIRST, Text3D.PATH_RIGHT);
	    z_text3D.setCapability(Geometry.ALLOW_INTERSECT);
	    Shape3D z_s3D1 = new Shape3D();
	    z_s3D1.setGeometry(z_text3D);
	    z_s3D1.setAppearance(a);
	    axis_group.addChild(z_s3D1);
	    placeText(axis_group, "z=3", new Point3f(0f,0f,3f));
	    placeText(axis_group, "z=-3", new Point3f(0f,0f,-3f));

		return placeGraphic(cmd, axis_group);
	}

	
	/**
	 * Place 3d text at location, in group
	 */
	boolean placeText(BranchGroup group,
			String text,
			Point3f loc
			) {
	    Font3D f3d = new Font3D(new Font("TestFont", Font.PLAIN, 1),
	            new FontExtrusion());
	    Text3D text3D = new Text3D(f3d, text,
	    		loc,
	    		Text3D.ALIGN_FIRST, Text3D.PATH_RIGHT);
	    text3D.setCapability(Geometry.ALLOW_INTERSECT);
	    Shape3D s3D1 = new Shape3D();
	    s3D1.setGeometry(text3D);
	    Appearance a = new Appearance();
	    s3D1.setAppearance(a);
	    group.addChild(s3D1);
	
		return true;
	}
	
	
	/**
	 * Add block object to display
	 * @param cmd
	 * @return true --> success
	 * @throws BwException
	 */
	public boolean addBlock(BwCmd cmd) throws BwException {
		BwColorSpec color = cmd.getColor();
		BwSizeSpec size = cmd.getSize();
		BwLocationSpec loc = cmd.getLoc();
		Primitive prim = new Box(size.getX(), size.getY(), size.getZ(),
								this.app);
		placeGraphic(cmd, prim, loc, color);
		return true;
	}

	
	/**
	 * Add text (3D) to display
	 * @param cmd
	 * @return true --> success
	 * @throws BwException
	 */
	public boolean addText(BwCmd cmd) throws BwException {
		if (trace.traceDebug())
			System.out.printf("in addText\n");
		BwValue textString = cmd.getTextString();
		String text_string = "?";
		if (textString != null)
			text_string = textString.stringValue();
		if (trace.traceDebug())
			System.out.printf("textString = %s\n", text_string);

		BwColorSpec color = cmd.getColor();
		if (color == null) {
			color = mkColorSpec(1f, 1f, 1F);
		}
		BwSizeSpec size = cmd.getSize();
		BwLocationSpec loc = cmd.getLoc();
		Point3f loc3f = loc.to3f();
		BwValue fontValue = cmd.getTextFont();
		String font_name = "Tahoma";
		if (fontValue != null)
			font_name = fontValue.stringValue();
		BwValue fontType = cmd.getTextStyle();
		int font_type = Font.PLAIN;
		if (fontType != null)
			font_type = fontType.intValue();
		BwValue fontSize = cmd.getTextSize();
		int font_size = 1;
		if (fontSize != null)
			font_size = fontSize.intValue();
		if (font_size < 1) {
			font_size = 1;					// Limit smallness
		}
		int text_align = Text3D.ALIGN_FIRST;
		BwValue textAlign = cmd.getTextAlignment();
		if (textAlign != null)
			text_align = textAlign.intValue();
		int text_path = Text3D.PATH_LEFT;
		BwValue textPath = cmd.getTextPath();
		if (textPath != null)
			text_path = textPath.intValue();
		
		Font font = new Font(font_name, font_type, font_size);
	    Font3D f3d = new Font3D(font,
	            new FontExtrusion());
	    Text3D text3D = new Text3D(f3d, text_string,
	    		loc3f,
	    		text_align, text_path);
	    text3D.setCapability(Geometry.ALLOW_INTERSECT);
	    Shape3D s3D1 = new Shape3D();
	    s3D1.setGeometry(text3D);
	    TransformGroup textScale = new TransformGroup();
	    Transform3D t3d = new Transform3D();
	    t3d.setScale(0.1);			// Set text scale
	    t3d.setTranslation(new Vector3f(loc3f));		// move to location
	    textScale.setTransform(t3d);
	    textScale.addChild(s3D1);
	    Appearance a = new Appearance();
	    s3D1.setAppearance(a);
	    BranchGroup text_group = new BranchGroup();
	    text_group.addChild(textScale);
		return placeGraphic(cmd, text_group);
	}

	
	/**
	 * Add 2D text to display
	 * @param cmd
	 * @return true --> success
	 * @throws BwException
	 */
	public boolean addText2D(BwCmd cmd) throws BwException {
		BwValue textString = cmd.getTextString();
		String text_string = "?";
		if (textString != null)
			text_string = textString.stringValue();
		BwColorSpec color = cmd.getColor();
		BwSizeSpec size = cmd.getSize();
		BwLocationSpec loc = cmd.getLoc();
		Point3f point3f = new Point3f(loc.getX(), loc.getY(), loc.getZ());
		Point2f point2f = get3DTo2DPoint(point3f);
		BwValue fontValue = cmd.getTextFont();
		String font_name = "Tahoma";
		if (fontValue != null)
			font_name = fontValue.stringValue();
		int font_type = Font.PLAIN;
		BwValue fontSize = cmd.getTextSize();
		int font_size = 10;
		if (fontSize != null)
			font_size = fontSize.intValue();
		Graphics gr = this.canvas.getGraphics();

		Font font = new Font(font_name, font_type, font_size);
		gr.setFont(font);
		int xpix = (int)point2f.x;
		int ypix = (int)point2f.y;
		xpix = 50;
		ypix = 70;
		gr.drawString(text_string, xpix, ypix);
		return true;
	}
	
	public Point2f get3DTo2DPoint(Point3f point3f) {
		Transform3D temp = new Transform3D();
		this.canvas.getVworldToImagePlate(temp);
		temp.transform(point3f);
		Point2d point2d = new Point2d();
		Point3d point3d = new Point3d(point3f);
		this.canvas.getPixelLocationFromImagePlate(point3d,point2d);
		Point2f point2f = new Point2f(point2d);
		return point2f;
	}	
	/**
	 * 
	public Vector2f WorldPosToScreenPos4(Vector3f p)
	{
	    Vector3f.Project(p, 0, 0, Renderer.ClientSize.Width, RenderForm.ClientSize.Height, 1, 10000, ViewProj);
	    return new Vector2f(p.X, p.Y);
	}
	*/
	
	 /**
	  * 4th method was right, all it had missing was retrieving the value returned by Vector3.Project().
	  * @param cmd
	  * @return
	  * @throws BwException
	  */
	public boolean addWindow(BwCmd cmd) throws BwException {
		BwSizeSpec size = cmd.getSize();
		setDisplayWindowSize((int)size.getX(), (int)size.getY());
		return true;
	}

	
	/**
	 * Erase cmd display
	 * Otherwise leave the command unchanged
	 */
	public boolean setEmpty(BwCmd cmd) {
	    int old_grid = cmd.getGrid();			// Get old id, if any
	    if (trace.traceGraphics())
	    	System.out.printf("BwDisplay:setEmpty: old_grid=%d\n", old_grid);
	    if (old_grid != BwCmd.GRID_NONE) {
	    	if (this.branchGroup != null) {
	    	    if (trace.traceGraphics())
	    	    	System.out.printf("Removing old_grid:%d\n", old_grid);
	    		this.branchGroup.removeChild(old_grid);
	    		cmd.setGrid(BwCmd.GRID_NONE);
	    	} else {
	    		System.out.printf("BwDisplay:branchGroup is null\n");
	    	}
	    }
	    return true;
	}
	/**
	 * Add line object to display
	 * A one or more segmented connected line
	 * @param cmd
	 * @return true --> success
	 * @throws BwException
	 */
	public boolean addLine(BwCmd cmd) throws BwException {
		
		Vector<BwLocationSpec> points = cmd.getPoints();
		if (trace.traceGraphics()) {
			System.out.printf("addLine\n");
		}
		if (points == null) {
			throw new BwException("addLine with no points");
		}
		if (points.size() < 2) {
			System.out.printf("addLine with less than 2 points");
			return false;
		}

		int nvert = (points.size()-1)*2;
		Point3f[] points3f = new Point3f[nvert];

		for (int i = 0, iv = 0; i < points.size()-1; i++, iv+=2) {
			points3f[iv] = points.get(i).to3f();
			points3f[iv+1] = points.get(i+1).to3f();	// Connect to next

			if (trace.traceGraphics()) {
				System.out.printf("  %d: %s %s\n", i, points3f[iv], points3f[iv+1]);
			}
		}
		
		BwColorSpec color = cmd.getColor();
		if (color == null) {
			color = mkColorSpec(1f, 1f, 1F);
		}
		Color3f line_color = bwcolor2color3f(color);
		
		BwValue lineWidth = cmd.getLineWidth();
		if (lineWidth == null) {
			lineWidth = mkValue(5);
		}
		float line_width = lineWidth.floatValue();

	    BranchGroup line_group = new BranchGroup();
	    int npoints = points.size();
	    int[] strip_lengths = new int [nvert/2];
	    for (int i = 0; i < strip_lengths.length; i++) {
	    	strip_lengths[i] = 2;
	    }

	    LineStripArray lines = new LineStripArray(
	    		nvert,
	    		LineArray.COORDINATES | LineArray.COLOR_3,
	    		strip_lengths
	    		);
	    lines.setCoordinates(0, points3f);
	    for (int i = 0; i < points.size(); i++)
	    	lines.setColor(i, line_color);
		LineAttributes line_att = new LineAttributes();
		line_att.setLineWidth(line_width);

		Appearance line_app = new Appearance();
		line_app.setLineAttributes(line_att);

		line_group.addChild(new Shape3D(lines, line_app));
		return placeGraphic(cmd, line_group);
	}

	
	public boolean addSphere(BwCmd cmd) throws BwException {
		BwColorSpec color = cmd.getColor();
		BwSizeSpec size = cmd.getSize();
		BwLocationSpec loc = cmd.getLoc();
		Primitive prim = new Sphere(size.getX(),
								this.app);
		placeGraphic(cmd, prim, loc, color);
		return true;
	}

	
	public boolean addCone(BwCmd cmd) throws BwException {
		BwColorSpec color = cmd.getColor();
		BwSizeSpec size = cmd.getSize();
		BwLocationSpec loc = cmd.getLoc();
		Primitive prim = new Cone(size.getX(), size.getY(),
								this.app);
		placeGraphic(cmd, prim, loc, color);
		return true;
	}

	
	public boolean addCylinder(BwCmd cmd) throws BwException {
		BwColorSpec color = cmd.getColor();
		BwSizeSpec size = cmd.getSize();
		BwLocationSpec loc = cmd.getLoc();
		Primitive prim = new Cylinder(size.getX(), size.getY(),
								this.app);
		placeGraphic(cmd, prim, loc, color);
		return true;
	}
	
	/**
	 * Clear out display resources
	 */
	public void clear() {
		if (this.universe != null) {
			this.universe.cleanup();
		}
	    this.setDefaultCloseOperation(JFrame.NORMAL);	// Erase window
	    this.dispose();
	    if (this.controls != null) {
	    	this.controls.clear();
	    }

	}
	
	/**
	 * Erase display
	 */
	public void erase() {
		setupDisplay();
	}
	
	/**
	 * Check if end of display / execution
	 */
	
	public boolean isEndTime() {
		if (this.endTime < 0)
			return false;
		long timeNow = System.nanoTime();
		if ( timeNow >= this.endTime)
			return true;
		return false;
	}
	
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Entered");
		
	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Entered");		
	}

	/**
	 * Short cuts to Symbol Table
	 */
	BwValue mkValue() {
		return new BwValue(this.sT);
	}
	
	BwValue mkValue(int val) {
		return new BwValue(this.sT, val);
	}
	
	BwSizeSpec mkSizeSpec(float x, float y, float z) {
		return new BwSizeSpec(this.sT, x, y, z);
	}
	
	BwColorSpec mkColorSpec(float red, float green, float blue) {
		return new BwColorSpec(this.sT, red, green, blue);
	}
	
	BwTrace trace;						// Program trace / control
	BwExec bExec;						// Access to cmd running...parsing,...vars
	BwParser parser;					// Short cut
	BwSymTable sT;						// Short cut
	BwControls controls;				// sliders... when connected else null

	private int windowHeight = 200;		// Display window height in pixels
	private int windowWidth = 200;		// Display window width in pixels
	private float timeLimit = -1;		// Time limit neg == none
	private long startTime = 0;			// Execution start in nanoseconds
	private long endTime = -1;			// End time if >= 0
	private SimpleUniverse universe;	// Structure to contain objects
	private Canvas3D canvas;			//' Our display canvas
	private BranchGroup rootGroup;
	private BranchGroup branchGroup;
	Appearance app;
										// LookAt values
	private BwLocationSpec lookAtEye;
	private BwLocationSpec lookAtCenter;
	private BwLocationSpec lookAtUp;	// 1,1,1 vector up
	private PickCanvas pickCanvas;		// Mouse picking
}
