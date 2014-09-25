package mediaPlayer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class TitleAndCreditAdder {

	/*
	 * Progress bar??? TODO
	 * Change the setout to make it better TODO
	 */

	JFrame frame = new JFrame("Add Title Scene");
	JFrame goop = new JFrame("Pop-up");

	JTextArea title = new JTextArea("Enter your text: "); //TODO Put limit on amount of text
	JTextArea text = new JTextArea(1,10); // TODO Change the size rearranging 
	JPanel textPanel = new JPanel(new FlowLayout());

	JTextArea size = new JTextArea("Size ");
	String[] sizeStrings = {"Small","Medium","Large"};
	JComboBox<String> sizeChoice = new JComboBox<String>(sizeStrings);
	JPanel sizePanel = new JPanel(new FlowLayout());

	JTextArea font = new JTextArea("Font ");
	String[] fontStrings = {"Font 1","Font 2","Font 3","Font 4","Font 5"};
	JComboBox<String> fontChoice = new JComboBox<String>(fontStrings);
	JPanel fontPanel = new JPanel(new FlowLayout());

	JTextArea colour = new JTextArea("Colour ");
	String[] colourStrings = {"White","Blue","Black","Purple","Red"};
	JComboBox<String> colourChoice = new JComboBox<String>(colourStrings);
	JPanel colourPanel = new JPanel(new FlowLayout());

	JTextArea position = new JTextArea("Position ");
	String[] vertical = {"Top","Centre","Bottom"};
	JComboBox<String> positionChoiceVertical = new JComboBox<String>(vertical); // Always centred horizontally
	JPanel posPanel = new JPanel(new FlowLayout());

	JTextArea duration = new JTextArea("Duration (seconds) ");  // Limit of 10
	Integer[] durationStrings = {1,2,3,4,5,6,7,8,9,10};
	JComboBox<Integer> durationChoice = new JComboBox<Integer>(durationStrings);
	JPanel durPanel = new JPanel(new FlowLayout());

	JButton enter = new JButton("Enter");
	JButton preview = new JButton("Preview");
	JPanel buttons = new JPanel(new FlowLayout());

	JTextArea previewArea = new JTextArea("Preview ");   // Maybe have a video player that will play for their chosen duration

	JPanel panelCont = new JPanel(new GridLayout(0,1));
	JTextArea temp = new JTextArea(50,50);

	String videoLocation;
	Boolean _isTitle;

	public TitleAndCreditAdder(boolean isTitle) {
		// TODO First need to browse for file, then choose name of output then show this. Global Variables. Check input is video
		_isTitle = isTitle;
		if (_isTitle == false) {
			frame.setTitle("Add Credit Scene");
		}

		JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(null);
		File file1 = fc.getSelectedFile(); // TODO If cancel???
		videoLocation = file1.getAbsolutePath();

		// TODO Save bit thing if exists + overwrite just delete first

		textPanel.add(title);
		textPanel.add(text); 

		sizePanel.add(size);
		sizePanel.add(sizeChoice);
		size.setEditable(false);

		fontPanel.add(font);
		fontPanel.add(fontChoice);
		font.setEditable(false);

		colourPanel.add(colour);
		colourPanel.add(colourChoice);
		colour.setEditable(false);

		posPanel.add(position);
		posPanel.add(positionChoiceVertical);
		position.setEditable(false);

		durPanel.add(duration);
		durPanel.add(durationChoice);
		duration.setEditable(false);
		buttons.add(preview);
		buttons.add(enter);

		panelCont.add(textPanel);
		panelCont.add(sizePanel);
		panelCont.add(fontPanel);
		panelCont.add(colourPanel);
		panelCont.add(posPanel);
		panelCont.add(durPanel);
		panelCont.add(buttons);

		enter.addActionListener(new EnterListener());

		frame.add(panelCont,BorderLayout.NORTH);
		frame.add(temp,BorderLayout.SOUTH);

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setVisible(true);


	}

	private class EnterListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String instruction = "avconv -i " + videoLocation + " -strict experimental -vf ";

			// colour reading
			Object instructcolour = colourChoice.getSelectedItem();
			if (instructcolour.equals("Blue")) {
				instruction+="\"drawtext=fontcolor=blue:";
			} else if (instructcolour.equals("Black")){
				instruction+="\"drawtext=fontcolor=black:";
			} else if (instructcolour.equals("Purple")){
				instruction+="\"drawtext=fontcolor=purple:";
			} else if (instructcolour.equals("Red")){
				instruction+="\"drawtext=fontcolor=red:";
			} else {
				instruction+="\"drawtext=fontcolor=white:";
			}

			// size reading => Sizes are 30,50,70
			Object instructsize = sizeChoice.getSelectedItem();
			if (instructsize.equals("Small")) {
				instruction+="fontsize=30:";
			} else if (instructsize.equals("Medium")){
				instruction+="fontsize=45:";
			} else {
				instruction+="fontsize=60:";
			}

			// font reading TODO Change names
			Object instructfont = fontChoice.getSelectedItem();
			if (instructfont.equals("Font 1")) {
				instruction+="fontfile=/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-C.ttf:";
			} else if (instructfont.equals("Font 2")){
				instruction+="fontfile=/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-M.ttf:";
			} else if (instructfont.equals("Font 3")){
				instruction+="fontfile=/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-R.ttf:";
			} else if (instructfont.equals("Font 4")){
				instruction+="fontfile=/usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-B.ttf:";
			} else {
				instruction+="fontfile=/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-B.ttf:";
			}

			// text reading
			String words = text.getText();
			instruction+="text='"+words+"':";


			// Position reading 
			instruction+="x=(main_w/2-text_w/2):";
			Object instructYposition = positionChoiceVertical.getSelectedItem();
			if (instructYposition.equals("Top")) {
				instruction+="y=h-text_h-30:";
			} else if (instructYposition.equals("Centre")){
				instruction+="y=main_h/2-text_h/2:";
			} else {
				instruction+="y=main_h+30:";
			} 

			Object instructduration = durationChoice.getSelectedItem(); //TODO add if _isTitle==false
			String num = instructduration.toString();
			if (_isTitle == true) {
				instruction+="draw='lt(t,"+num+")'\" -crf 18 ";
			} else {
				int n = Integer.parseInt(num);
				int end = getLength(videoLocation);
				int t = end - n;
				instruction+="draw='gt(t,"+t+")'\" -crf 18 ";
			}



			instruction = instruction + " /home/genevieve/Videos/swg.mp4";
			System.out.println(instruction);
			TitleAndCreditBackground titleCreator = new TitleAndCreditBackground(instruction);
			titleCreator.execute();
			frame.dispose();
		}
	}

	public int getLength(String vid) { //TODO will take an input of the filename
		// TODO temp bit for test
		try {
			ProcessBuilder titleAdder = new ProcessBuilder("bash", "-c", "avconv -i " + vid + " 2>&1 | grep 'Duration' | awk '{print $2}' | sed s/,//");
			titleAdder.redirectErrorStream(true);
			Process downloadProcess = titleAdder.start();
			BufferedReader stdoutDownload = new BufferedReader(new InputStreamReader(downloadProcess.getInputStream()));
			String line = stdoutDownload.readLine();
			System.out.println(line);
			int num = stringTimetToInt(line);
			return num;
		} catch (Exception e) {
			return 0;
		}
	}

	public int stringTimetToInt(String inTime) {
		String[] nums = inTime.split(":");
		System.out.println(nums[2]);

		String[] seconds = nums[2].split("");
		String sec = seconds[1] + seconds[2];

		int hour = Integer.parseInt(nums[0]);
		int minute = Integer.parseInt(nums[1]);
		int second = Integer.parseInt(sec);

		int totalTime = (hour*60*60) + minute * 60 + second;
		System.out.println(totalTime);

		return totalTime;
	}

}
