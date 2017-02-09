package BlockWorld;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class BwControls extends JFrame{
	JScrollPane scrollpane;

	public BwControls(BwTrace trace, BwExec bexec) {
		super("Variable Controls");

				// Setup sliders
		this.trace = trace;
		this.bExec = bexec;
		this.bExec.setControls(this);				// Link back
		this.parser = bexec.getParser();
		this.bD = bexec.getDisplay();
		BwUpdate bwupdate = new BwUpdate(bExec);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BwSliderVar.connectUpdate(bwupdate);
		this.sliderMap = new Hashtable<String, BwSliderSpec>();	// Keep track of sliders

		this.controlPanel = new JPanel();
        int p_start = 50;
        int p_width = 400;
        int p_height = 70*5;
		this.setPreferredSize(new Dimension(p_width, p_height));
		this.controlPanel.setSize(p_width, p_height);
		setSize(p_width, p_height);
	    this.controlPanel.setLayout(new GridLayout(0,1, 5, 10));
		scrollpane = new JScrollPane(this.controlPanel);
		getContentPane().add(scrollpane, BorderLayout.CENTER);
	}
	
	public boolean sliderCmd(BwSliderSpec slider) throws BwException {
		String var_name = slider.getVarName();
									// Only act if not already in table
		if (this.sliderMap.get(var_name.toLowerCase()) != null) {
			return true;					// enough
		}
		
		BwValue set_value = slider.getCurVal();			// Set variable initial value
		try {
			if (this.parser == null)		// ignore if no parser yet
				return true;
			this.parser.setValue(var_name, set_value);
		
									// Add control iff new
			this.sliderMap.put(var_name.toLowerCase(), slider);
			
	        new BwSliderVar(this.trace,
	        		this.controlPanel, var_name,
	        		slider.getMinVal().floatValue(),
	        		slider.getCurVal().floatValue(),
	        		slider.getMaxVal().floatValue());
		} catch (BwException ex) {
			String errmsg = ex.getMessage();
			System.out.printf("setCmd:(%s,): %s\n",
					var_name, errmsg);
			ex.printStackTrace();
		}
		return true;
	}

	
	public void startDisplay() {
		
		this.setVisible(true);
	}
	
	
	/**
	 * Remove controls / window
	 */
	public void clear() {
	    this.setDefaultCloseOperation(JFrame.NORMAL);	// Erase window
	    this.dispose();
	}
	
	private BwTrace trace;
	JPanel controlPanel;		// Control panel layout

	private BwExec bExec;
	private BwDisplay bD;			// display 
	private BwParser parser;		// Short cut
	private Hashtable<String, BwSliderSpec> sliderMap;
}
