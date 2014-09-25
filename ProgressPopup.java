package mediaPlayer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressPopup extends JDialog{

//http://www.java2s.com/Tutorial/Java/0240__Swing/ASimpleModalDialog.htm

	//private JFrame parent;
	
	public ProgressPopup(JFrame parent, String title, String message) {
		super(parent, title, true);
		
		//parent = parent;
		
		if (parent != null) {
		      Dimension parentSize = parent.getSize(); 
		      Point p = parent.getLocation(); 
		      setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
		    }
		
		JPanel messagePane = new JPanel();
		messagePane.add(new JLabel(message));
		getContentPane().add(messagePane);
		
		/*JPanel buttonPane = new JPanel();
		JButton button = new JButton("Cancel");
		buttonPane.add(button);
		button.addActionListener(this);*/
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		
		getContentPane().add(progressBar, BorderLayout.SOUTH);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	public void close(){
		setVisible(false);
	}

}
