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
An attempt to avoid black lines
*/
private static Color3f colmag(Color3f pt) {
	float c_x = pt.x;
	float c_y = pt.y;
	float c_z = pt.z;
	float c_min = Math.min(c_x, Math.min(c_y, c_z));
	c_x -= c_min;
	c_y -= c_min;
	c_z -= c_min;
	float c_mag = c_x + c_y + c_z;
	if (c_mag < .1) {
		float c_offset = .1F;
		c_x += c_offset;
		c_y += c_offset;
		c_z += c_offset;
	}
	Color3f col_new = new Color3f(c_x, c_y, c_z);
    return col_new;
}

	public static void main() throws Exception {
		BlockWJ bW = new BlockWJ("input,execute,graphics");
		int npoints = 200;    			// Number of points to display
		///npoints = 1000;     				// TFD - shorten test
		///npoints = 5;     				// TFD - shorten test
		while (true) {
			loop(bW, npoints);
		}
	}
	
	private static void loop(BlockWJ bW, int npoints) throws Exception {
		boolean do_blanking = false;
		
		float max_line_width = .2F;
		float min_line_width = max_line_width/10.F;
		float tdly_max = .1F;         	// Time between displays
		float tdly_min = tdly_max/10.F;

		ArrayList<Point3f> points = new ArrayList<Point3f>();     // array of points to create/display
		float maxval = 4.F;     // Maximum (x,y,z) dimensional value
		float minval = -maxval;    // Minimum (x,y,z) dimensional value
		Point3f prev_point = new Point3f(0F,0F,0F);
		for (int i= 0; i < npoints; i++) {
		    float xval = (float)Math.random()*(maxval-minval) + minval;
		    float yval = (float)Math.random()*(maxval-minval) + minval;
		    float zval = (float)Math.random()*(maxval-minval) + minval;
		    float xval_new = prev_point.x + xval;
		    if (xval_new < minval)
		    	xval_new -= minval;
		    else if (xval_new > maxval)
		    	xval_new -= maxval;
		    float yval_new = prev_point.y + yval;
		    if (yval_new < minval)
		    	yval_new -= minval;
		    else if (yval_new > maxval)
		    	yval_new -= maxval;
		    float zval_new = prev_point.z + zval;
		    if (zval_new < minval)
		    	zval_new -= minval;
		    else if (zval_new > maxval)
		    	zval_new -= maxval;
		    Point3f change_point = new Point3f(
		    		xval_new,
		    		yval_new,
		    		zval_new);

		    points.add(change_point);
		    prev_point = change_point;
		}
		    
		/*
		 * save commands for fast display
		*/
		
		//BwCmd[] cmds = new BwCmd[npoints-1];           // Lines
		BwCmd[] cmds = new BwCmd[2*npoints];           	// Lines
		BwCmd[] cmd_spheres = new BwCmd[npoints-1];    // spheres at line ends
		//for (int i = 0; i < cmds.length-1; i++) {       // Loop over sub groups of all points
		for (int i = 0; i < npoints-1; i++) {       		// Loop over sub groups of all points
			Point3f pt1 = points.get(i);
			Point3f pt2 = points.get(i+1);
		//    cmd = bW.add(bW.line, bW.color(0,1,0))
		    Color3f col = new Color3f(new Point3f(
		    		(float)Math.random(),
		    		(float)Math.random(),
		    		(float)Math.random()));
		    col = colmag(col);
		    float line_width = (min_line_width * i/npoints + max_line_width * (npoints-i)/npoints)/2.F;
		    BwCmd cmd = bW.add(bW.line, bW.color(col),
		                        bW.size(line_width));
		    cmd.addPoint(pt1);
		    cmd.addPoint(pt2);
		    cmd.setComplete();
		    //cmds[i] = cmd;
		    cmds[i*2] = cmd;
		    
		    BwCmd cmd_sphere = bW.add(bW.sphere, bW.color(col.x, col.y, col.z),
		    					bW.loc(pt2), bW.size(line_width));
		    cmd_sphere.setComplete();
		    //cmd_spheres[i] = cmd_sphere;
		    cmds[2*i+1] = cmd_sphere;
		}
		/*
		Lines growing
		*/
		System.out.format("construct %d lines connecting %d points\n", npoints-1, npoints);
		for (int i = 0; i < cmds.length; i+=2) {
			//BwCmd cmd = cmds[i];
			BwCmd cmd = cmds[i];
			if (cmd == null)
				continue;
			
		    bW.display(cmd);
		    //BwCmd cmd_sphere = cmd_spheres[i];
		    BwCmd cmd_sphere = cmds[i+1];
		    bW.display(cmd_sphere);
		    float tdly = (tdly_max * i/npoints + tdly_min * (npoints-i)/npoints);
		    bW.delay(tdly);
		}
		    
		/*
		lines shrinking back
		*/
		System.out.println("Erasing lines");
		for (int i = cmds.length-1; i > -1; i--) {
		    BwCmd cmd = cmds[i];
		    if (cmd == null)
		    	continue;
		    
		    //bW.mod(cmd=cmd, color=(0,0,0))
		    bW.setEmpty(cmd);
		    //BwCmd cmd_sphere = cmd_spheres[i];
		    //bW.setEmpty(cmd_sphere);
		    if (i % 2 == 0) {
			    float tdly = (float) (2*(tdly_max * i/npoints + tdly_min * (npoints-i)/npoints)/2.);
			    bW.delay(tdly);
		    }
		}
		if (do_blanking) {
			System.out.print("Blanking points");
			for (int i = cmds.length-1; i > -1; i--) {
			    BwCmd cmd_sphere = cmd_spheres[i];
			    ///bW.setEmpty(cmd_sphere);
			    if (cmd_sphere == null)
			    	continue;
			    
			    bW.mod(cmd_sphere, bW.color(0,0,0));
			    bW.display(cmd_sphere);
			    if (i % 2 == 0) {
			    	float tdly = (float) (2.0F*(tdly_min * i/npoints + tdly_max * (npoints-i)/npoints)/2.);
			    	bW.delay(tdly);
			    }
			}
		}
		    
		System.out.format("End of %d points\n",npoints);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("start");
		main();
		
	}
}