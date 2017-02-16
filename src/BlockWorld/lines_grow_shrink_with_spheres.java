package BlockWorld;
/* lines_grow_shrink_with_spheres.java
 * A Java version of the Python program of the same base name.
 * Created to aid the debugging of the problems of multiple erase commands.
 */
import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.applet.Applet;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.j3d.Shader;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.plaf.SliderUI;

import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class lines_grow_shrink_with_spheres extends Application {


/*
lines_grow_shrink_with_spheres.py
Use simple lines, each with two points
Add small spheres at the line ends
*/
	
/*
An unsuccessful attempt to avoid black lines
*/
private float colmag(float num) {
    //num = abs(num)
    //if num1 < .01:
    //    num1 += .1
    return num;
}

	public static void main() throws Exception {
		BlockWJ bW = new BlockWJ("input,execute,graphics");
		
		float max_line_width = .1F;
		float min_line_width = max_line_width/10.F;
		float tdly_max = .5F;         	// Time between displays
		float tdly_min = tdly_max/10.F;
		int npoints = 200;    			// Number of points to display
		///npoints = 5;     		// TFD - shorten test

		ArrayList<Point3f> points = new ArrayList<Point3f>();     // array of points to create/display
		float maxval = 4.F;     // Maximum (x,y,z) dimensional value
		float minval = -maxval;    // Minimum (x,y,z) dimensional value
		for (int i= 0; i < npoints; i++) {
		    float xval = (float)Math.random()*(maxval-minval) + minval;
		    float yval = (float)Math.random()*(maxval-minval) + minval;
		    float zval = (float)Math.random()*(maxval-minval) + minval;
		    points.add(new Point3f(xval,yval,zval));
		}
		    
		/*
		 * save commands for fast display
		*/
		
		BwCmd[] cmds = new BwCmd[npoints];           // Lines
		BwCmd[] cmd_spheres = new BwCmd[npoints];    // spheres at line ends
		for (int i = 0; i < npoints-1; i++) {       // Loop over sub groups of all points
			Point3f pt1 = points.get(i);
			Point3f pt2 = points.get(i+1);
		//    cmd = bW.add(bW.line, bW.color(0,1,0))
		    Color3f col = new Color3f(pt2);
		    float line_width = (min_line_width * i/npoints + max_line_width * (npoints-i)/npoints)/2.F;
		    BwCmd cmd = bW.add(bW.line, bW.color(col),
		                        bW.lines(line_width));

		    cmd.addPoint(pt1);
		    cmd.addPoint(pt2);
		    BwCmd cmd_sphere = bW.add(bW.sphere, bW.color(col.x, col.y, col.z),
		    					bW.loc(pt2), bW.size(line_width));
		    cmd_spheres[i] = cmd_sphere;
		    cmds[i] = cmd;
		System.out.format("construct lines connecting %s points", i);
		}
		/*
		Lines growing
		*/
		for (int i = 0; i < cmds.length; i++) {
			BwCmd cmd = cmds[i];
		    bW.display(cmd);
		    bW.display(cmd_spheres[i]);
		    float tdly = (tdly_max * i/npoints + tdly_min * (npoints-i)/npoints)/2;
		    Thread.sleep((long) ((long) tdly/1000.));
		}
		    
		/*
		lines shrinking back
		*/
		System.out.println("Erasing lines");
		for (int i = cmds.length-1; i > -1; i--) {
		    BwCmd cmd = cmds[i];
		    //bW.mod(cmd=cmd, color=(0,0,0))
		    bW.setEmpty(cmd);
		    BwCmd cmd_sphere = cmd_spheres[i];
		    ///bW.setEmpty(cmd_sphere)
		    
		    float tdly = (float) ((tdly_min * i/npoints + tdly_max * (npoints-i)/npoints)/2.);
		    		Thread.sleep((long) ((long) tdly/1000.));
		}
		System.out.print("Blanking points");
		for (int i = cmds.length-1; i > -1; i--) {
		    BwCmd cmd_sphere = cmd_spheres[i];
		    ///bW.setEmpty(cmd_sphere);
		    bW.mod(cmd_sphere, bW.color(0,0,0));
		    bW.display(cmd_sphere);
		    
		    float tdly = (float) ((tdly_min * i/npoints + tdly_max * (npoints-i)/npoints)/2.);
		    		Thread.sleep((long) ((long) tdly/1000.));
		}
		    
		System.out.format("End of %d points\n",npoints);
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
	}
}