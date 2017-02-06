package BlockWorld;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import iiuf.swing.*;
/*
 * Support slider variable control/display
 */
public class BwSliderVar extends JPanel
                        implements ActionListener,
                                   WindowListener,
                                   ChangeListener {
    //Set up animation parameters.
	static final int SB_RANGE = 100;
    static final int SB_MIN = 0;
    static final int SB_MAX = SB_RANGE+SB_MIN;
    static final int SB_INIT = (SB_MIN + SB_MAX)/2;
    public BwSliderVar(BwTrace trace,
    		JPanel panel,
    		String var_name,
    		float min, float cur, float max) {
    	this.trace = trace;
    	this.minVal = min;
    	this.maxVal = max;
    	this.curVal = cur;
    	nSlide++;
    	this.varName = var_name;
        double mi = (double)min;
        double cu = (double)cur;
        double ma = (double)max;

        this.minValue = new JNumberField(6, mi, ma);
        this.minValue.setNumber((double) min);        
        this.minValue.addActionListener(new AbstractAction() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		updateFromFields();
        		if (trace.traceGraphics())
        			System.out.println("minValue event" + e);
        	}
        });
        
        this.curValue = new JNumberField(6, mi, ma);
        this.curValue.setNumber((double) cur);
        this.curValueAction = new AbstractAction() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		updateFromFields();
        		if (trace.traceGraphics())
        			System.out.println("curValue event" + e);
        	}
        };
        this.curValue.addActionListener(this.curValueAction);
        
        this.maxValue = new JNumberField(6, mi, ma);
        this.maxValue.setNumber((double) max);        
        this.maxValue.addActionListener(new AbstractAction() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		updateFromFields();
        		if (trace.traceGraphics())
        			System.out.println("maxValue event" + e);
        	}
        });

        
        
    	JPanel vsbPanel = new JPanel(new BorderLayout());

    	JPanel varSliderPanel = new JPanel(new BorderLayout());		// name slider
        JLabel variableName = new JLabel(var_name);
        															//Create the slider.
        JSlider variableSlider = new JSlider(JSlider.HORIZONTAL,
                                              SB_MIN, SB_MAX, SB_INIT);
        this.slider = variableSlider;
        variableSlider.addChangeListener(this);
        variableSlider.setBorder(
                BorderFactory.createEmptyBorder(0,0,10,0));
        Font font = new Font("Serif", Font.ITALIC, 15);
        variableSlider.setFont(font);
        

        varSliderPanel.add(variableName, BorderLayout.WEST);
        varSliderPanel.add(variableSlider);
        vsbPanel.add(varSliderPanel);
        
        JPanel valsPanel = new JPanel();
        								// Update, min, cur, max
        JButton bset = new JButton("SET");
        bset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               updateFromFields();
            }          
         });

        valsPanel.add(bset, BorderLayout.WEST);
        valsPanel.add(minValue, BorderLayout.SOUTH);
        valsPanel.add(curValue);
        valsPanel.add(maxValue);

        vsbPanel.add(varSliderPanel);
        vsbPanel.add(valsPanel, BorderLayout.SOUTH);
        vsbPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        vsbPanel.setVisible(true);
        
        													// Size accordingly
        Dimension vsbPanelSize = vsbPanel.getSize();
        int vsbStart = 50;
        int vsbWidth = 400;
        int vsbHeight = 70;
        int frameHeight = vsbStart + vsbHeight * this.nSlide;
		panel.setPreferredSize(new Dimension(vsbWidth, frameHeight));
		panel.add(vsbPanel, BorderLayout.AFTER_LAST_LINE);
 //       panel.pack();
    }

    /** Add a listener for window events. */
    void addWindowListener(Window w) {
        w.addWindowListener(this);
    }

    //React to window events.
    public void windowIconified(WindowEvent e) {
    }
    public void windowDeiconified(WindowEvent e) {
    }
    
	
	
	/**
	 * short format 
	 */
	public String fmt(double d)	{
		return BwValue.fmt(d);
	}
    
    
    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    /**
     * Set slider value
     */
    public void setValue(double value) {
    	if (value < this.minVal)
    		value = this.minVal;
    	if (value > this.maxVal)
    		value = this.maxVal;
    	
    	int ival = real2isval(value);
    	this.slider.setValue(ival);
    	this.iprevVal = ival;			// Record for comparison
    }
    
    /**
     * convert slider integer value to real value
     */
    public  double isval2real(int ival) {
    	double val = this.minVal + (double)(ival-SB_MIN)/SB_RANGE
    				* (double)(this.maxVal-this.minVal);
    	return val;
    }
    
    /**
     * convert real value to slider integer value
     */
    public int real2isval(double val) {
    	if (this.maxVal <= this.minVal)
    		return SB_MIN;
    	int ival = (int) (SB_MIN + (SB_RANGE *
    			(val-this.minVal)/(this.maxVal-this.minVal)));
    	return ival;
     }

    /** Listen to the slider. */
    public void stateChanged(ChangeEvent e) {
    					/**
    					 * Do continuous update, delayed so as not to
    					 * overwhelm the display.
    					 */
    	/**
    	if (this.adjustDelay == null) {
    		this.adjustDelay = new Timer(defaultAdjustDelay,
    				this.curValueAction);
    		this.adjustDelay.setRepeats(false);
    		return;
    	}
    	if (adjustDelay.isRunning()) {
    		return;		// Awaiting minimum time
    	}
    	**/
        JSlider source = (JSlider)e.getSource();
        int sbval = source.getValue();
        this.curVal = (float)isval2real(sbval);
        this.curValue.setNumber(this.curVal);
       	setValue(this.curVal);
        if (!source.getValueIsAdjusting()) {
        	updateFromFields();
        }

        /**
        this.minVal = (float)this.minValue.getNumber();
        this.maxVal = (float)this.maxValue.getNumber();
        this.updateVar.update(getVarName(), this.curVal);
        */
    }
    
    /**
     * Update slider values from input fields
     */
    public void updateFromFields() {
    	this.curVal = (float) this.curValue.getNumber();
    	this.minVal = (float) this.minValue.getNumber();
    	this.maxVal = (float) this.maxValue.getNumber();
        this.updateVar.update(getVarName(), this.curVal);
        if (trace.traceGraphics()) {
        	System.out.printf("updateFromFields: curVal=%s minVal=%s maxVal=%s\n",
        			fmt(this.curVal), fmt(this.minVal), fmt(this.maxVal));
        }
        // slider updated by getNUmber ???  setValue(this.curVal);		// Update slider
     }
    
    
    /**
     * Get variable name
     * @return
     */
    public String getVarName() {
    	return this.varName;
    }

	public void update(String name, double value) {
		System.out.printf("%s has been updated to %g\n", name, value);
	}

    //Called when the Timer fires.
    public void actionPerformed(ActionEvent e) {
    }

    /** Update the label to display the image for the current frame. */
    protected void updatePicture(int frameNum) {
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
    	return null;
    }
    
    /**
     * Setup callback for updated
     * @param update
     */
    public static void connectUpdate(BwUpdate update) {
    	updateVar = update;
    }

    	private BwTrace trace;			// tracing/diagnostic control
    	private JSlider slider;			// slider component
    	private int defaultAdjustDelay = 10;	// msec
    	private Timer adjustDelay;		// Adjust delay to prevent too much
    	private AbstractAction curValueAction;	// slider action, time reaction
    									//   action
    	private String varName;			// Variable name
    	private float minVal;			// Minimum value
    	private float curVal;			// Current value
    	private int iprevVal;			// Previous slider setting
    	private float maxVal;
        private JNumberField minValue;
        private JNumberField curValue;
        private JNumberField maxValue;
        private static BwUpdate updateVar;		// Update class - updates outside
        private static int nSlide = 0;			// Number of slides
        
    public static void main(String[] args) throws Exception {
    	BwTrace trace = new BwTrace();
    	trace.setExecute(1);
    	BwUpdate bwupdate = new BwUpdate();
        JFrame frame = new JFrame("SliderDemo");
        JPanel panel = new JPanel();
        panel.setSize(600, 400);
        panel.setLayout(new GridLayout(0,1, 5, 10));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BwSliderVar.connectUpdate(bwupdate);

        BwSliderVar vs1 = new BwSliderVar(trace, panel, "variable1", 0F, 1F, 2F);
        BwSliderVar vs2 = new BwSliderVar(trace, panel, "variable2", -10F, 0, 10F);
        BwSliderVar vs3 = new BwSliderVar(trace, panel, "variable3", 0F, 100F, 1000F);
        BwSliderVar vs4 = new BwSliderVar(trace, panel, "variable4", 0, 20F, 40F);
        BwSliderVar vs5 = new BwSliderVar(trace, panel, "vs5", -10F, 0, 10F);
        //Display the window.
        panel.setVisible(true);
    }
}
