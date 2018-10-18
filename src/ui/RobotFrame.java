package ui;

import javax.swing.*;     

public class RobotFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	public RobotFrame() {
		super();
		init();
	}
	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void init() {
        setVisible(true);
        setTitle("Robot Viewer");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new RobotPanel());
        
        pack();
        setVisible(true);
    }
}

