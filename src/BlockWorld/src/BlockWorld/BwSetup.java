package BlockWorld;
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

public class BwSetup extends Application {

	/**
	 * 
	 */
	public void init() {
		System.out.printf("BwDisplay init\n");
	}
	
	public void destroy() {
		System.out.printf("BwDisplay destroy\n");
	}

	public void update(String name, double value) {
		System.out.printf("%s has been updated to %g\n", name, value);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
        mainSetup();
    }
	
	public void stop() {
		System.out.printf("BwDisplay stop\n");
	}

	public BwSetup() {
		// TODO Auto-generated constructor stub
	}
	public static void main(String args[]) {
		Application.launch(args);
	}
	
	private void mainSetup() throws Exception {
		System.out.printf("mainSetup\n");
		Parameters parm = getParameters();
		List<String> parms = parm.getRaw();
		String[] args = parms.toArray(new String[0]);
		System.out.printf("Args:");
		for (int i = 0; i < args.length; i++) {
			System.out.printf(" %s",  args[i]);
		}
		System.out.printf("\n");
		
		int i = 0;
		BwTrace trace = new BwTrace();
		BwExec bExec = new BwExec(trace);
		BwParser parser = bExec.getParser();
		trace.setAccept(1);
		trace.setTokenAccept(1);
		trace.setTokQueue(1);
		trace.setAll();
		trace.clearAll();
		trace.setInput(1);
	
		JFrame graphicsFrame = new JFrame("Graphics"); 
        graphicsFrame.setVisible(true);       //Display the window.
		BwControls controls = new BwControls(trace, bExec);
		controls.setVisible(true);
		/**
		 * values 
		 */


/**
 * replaced by slider commands in bwif file
 *
        new BwSliderVar(frame, "winW", 0, 900, 2000);
        new BwSliderVar(frame, "winH", 0, 1400, 2000);
        
        new BwSliderVar(frame, "locAtEyeX", -10, 10, 100);
        new BwSliderVar(frame, "locAtEyeY", -10, 10, 100);
        new BwSliderVar(frame, "locAtEyeZ", -10, 20, 100);
        
        new BwSliderVar(frame, "locAtCenterX", -10, 0, 100);
        new BwSliderVar(frame, "locAtCenterY", -10, 0, 100);
        new BwSliderVar(frame, "locAtCenterZ", -10, 0, 100);
        
        new BwSliderVar(frame, "locAtUpX", -1, 0, 1);
        new BwSliderVar(frame, "locAtUpY", -1, 1, 1);
        new BwSliderVar(frame, "locAtUpZ", -1, 0, 1);
        
        new BwSliderVar(frame, "blockAtX", -20, -21, 20);
        new BwSliderVar(frame, "blockAtY", -20, -2, 20);
        new BwSliderVar(frame, "blockAtZ", -20, -2, 20);
        
        new BwSliderVar(frame, "blockSizeX", -10, 1, 20);
        new BwSliderVar(frame, "blockSizeY", -10, 2, 20);
        new BwSliderVar(frame, "blockSizeZ", -10, 3, 20);

        //Display the window.
        frame.setVisible(true);
**/
        /**
         * How do we scroll the slider window?
         */
        /**
        JPanel controlPanel = new JPanel();
        controlPanel.setSize(600, 400);
        controlPanel.setLayout(new GridLayout(13, 6, 10, 0));
        controlPanel.add(frame);
		JScrollPane controlScroll = new JScrollPane(controlPanel);
		**/
		float timeLimit = 1;			// Time limit(seconds) neg - no limit
        String helpstr = "\n"
        		+ "Options, preceeded by '-' or '--'\n\n"
        		+ " ifile | if \n"
        		+ "   input_file - execute commands from input file\n"
        		+ " help | h \n"
        		+ "   print this help and exit\n"
        		+ " run | r \n"
        		+ "   input_list - list of files to run\n"
        		+ " timeLimit time_seconds - limit run time\n"
        		+ " trace\n"
        		+ "   comma-separated-trace-list:\n"
        		+ "    parse, execute, state, input, token, tokenAccept\n"
        		+ "    mark, accept, backup, graphics, tokStack\n"
        		+ "    tokQueue, verbose\n"
        		+ "\n";
		int nfile = 0;						// Count files processed
											// Process flags -<...>
		for (; i < args.length; i++) {
			String arg = args[i];
			Pattern pattern = Pattern.compile("^-{1,2}(.*)");
			Matcher matcher = pattern.matcher(arg);
			if (!matcher.matches())
				break;		// No more flags
			String opt = matcher.group(1);
			if (opt.matches("^(verbose|v)$")) {
				trace.setVerbose(1);
			} else if (opt.matches("^(help|h)$")) {
				System.out.printf(helpstr);
				System.exit(0);
			} else if (opt.matches("^(ifile|if)$")) {
				String inFile = args[++i];
				if (!parser.procFile(inFile)) {
					System.err.printf("Quitting\n");
					System.exit(1);
				}
				nfile++;		// Count files processed
			} else if (opt.matches("^(run|rl)$")) {
				String runListFile = args[++i];
				if (!bExec.runList(runListFile)) {
					System.err.printf("Quitting\n");
					System.exit(1);
				}
				nfile++;		// Count files processed
			} else if (opt.equalsIgnoreCase("trace")) {
				String trace_spec = args[++i];
				String [] trace_levels = trace_spec.split(",");
				for (String trace_level : trace_levels) {
					trace.setLevel(trace_level);
				}
			} else if (opt.matches("^(timeLimit|tl)$")) {
				timeLimit = Float.valueOf(args[++i]);
				bExec.setTimeLimit(timeLimit);
				
			} else {
				System.err.printf("Unrecognized option:'%s' - Quitting", arg);
				System.exit(1);
			}
		}
							/**
							 * Use sample if no data commands
							 * and no files processed
							 */
		
		if (nfile == 0 && i >= args.length) {
			i = 0;				// Start at beginning of sample
			args = new String[] {
				//"add block color=red loc=.1 size=.5",
				"add block color=1,0,0 loc=.1 size=.5",
				//"add sphere color=blue loc=.1,.2,.3 size=.5",
				"add block color=1,1,0 loc=-12,-2,-2 size=1",
				"add sphere color=0,0,1 loc=4.5,5,2 size=2",
				"add cone color=0,1,0 loc=5,5,6 size=1,4",
				"add cylinder color=0,1,1 loc=4,4,1 size=1,8",
				"display",
				};
		}
		
		for (; i < args.length; i++) {
			String arg = args[i];
			Pattern pat_ends_with_semi = Pattern.compile(";[ \b\t]*$");
			Matcher matcher = pat_ends_with_semi.matcher(arg);
			if (!matcher.matches()) {
				arg += ";";			// Terminate last command on line
			}
			try {
				if (!parser.procInput(arg)) {
					System.out.printf("Quitting\n");
					if (bExec.isError()) {
						System.err.printf("Error: %s\n", bExec.errorDescription());
						System.err.printf("Quitting\n");
						System.exit(1);
					}
					System.exit(0);
				}
			} catch (BwException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}
		
								// Default - display if not already displayed
		if (bExec.size() > 0
				&& bExec.getCmd(bExec.size()-1).getCmd_type() != BwCmdType.DISPLAY_SCENE) {
			BwCmd cmd = parser.mkCmd(BwCmdType.DISPLAY_SCENE);
			cmd.setComplete();
			bExec.addCmd(cmd);
		}
		if (bExec.isError()) {
			int nerror = bExec.getnError();
			System.out.printf("%d errors\n", nerror);
			try {
				BwCmd first_error = bExec.firstError();
				System.out.printf("First Error: %s\n", first_error.errorDescription());
				System.exit(1);
			} catch (BwException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Display commands
		if (bExec.size() == 0) {
			System.out.printf("No Display Commands - quitting\n");
			System.exit(0);
		}
		bExec.display();
	}
	boolean terminateLines = true;		// true - terminate each arg/line with ";"
}
