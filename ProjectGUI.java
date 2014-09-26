package mediaPlayer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class ProjectGUI{

	/*
	 * Media Player/Trailer Editor
	 * 
	 * Ruoyi Cai (rcai861)
	 * Genevieve Taka (gtak640)
	 * 
	 * References:
	 * Basic GUI code from Nasser's lecture.
	 * http://www.java2s.com/Tutorial/Java/0240__Swing/ASimpleModalDialog.htm for positioning of dialog over parent frame.
	 */

	private final EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
	private final EmbeddedMediaPlayer video = mediaPlayerComponent.getMediaPlayer();

	JPanel main = new JPanel(new BorderLayout());
	private JMenuBar menu = new JMenuBar();
	private JPanel bottom = new JPanel(new BorderLayout());
	private JPanel dock = new JPanel(new FlowLayout());

	private JMenu media = new JMenu("Media");
	private JMenuItem browse = new JMenuItem("Open File...");
	private JMenuItem download = new JMenuItem("Download File...");
	private JMenuItem quit = new JMenuItem("Quit");

	private JMenu playback = new JMenu("Playback");
	private JMenuItem menuPlay = new JMenuItem("Play");
	private JMenuItem menuStop = new JMenuItem("Stop");
	private JMenuItem menuForward = new JMenuItem("Forward");
	private JMenuItem menuBackward = new JMenuItem("Backward");

	private JMenu audio = new JMenu("Audio");
	private JMenuItem strip = new JMenuItem("Extract Audio");
	private JMenuItem replace = new JMenuItem("Replace Audio");
	private JMenuItem overlay = new JMenuItem("Overlay Audio");
	private JMenuItem menuMute = new JMenuItem("Mute");

	private JMenu sub = new JMenu("Subtitle");
	private JMenuItem title = new JMenuItem("Create Title Page");
	private JMenuItem credit = new JMenuItem("Create Credit Page");
	private JMenuItem editT = new JMenuItem("Edit Title or Credit Page");

	private JMenu help = new JMenu("Help");
	private JMenuItem f1 = new JMenuItem("Help...");
	private JMenuItem about = new JMenuItem("About");

	private JPanel timePanel = new JPanel();
	private JProgressBar timeBar = new JProgressBar();
	private JLabel timeCount = new JLabel(" --:-- ");
	private JLabel timeTotal = new JLabel(" --:-- ");

	private JButton play = new JButton("Play");
	private JButton stop = new JButton("Stop");
	private JButton mute = new JButton("Mute");
	private JButton forward = new JButton("Forward");
	private JButton back = new JButton("Rewind");

	boolean paused = false;
	boolean goforward = false;
	boolean endofvideo = false;
	boolean gobackward = false;
	private JSlider volume = new JSlider(0,150,50);

	String videoLocation = "";
	String saveLocation = "";

	private ProjectGUI(String[] args) {
		final JFrame frame = new JFrame("Lysandros Media Player");
		
		main.add(menu, BorderLayout.NORTH);
		main.add(bottom, BorderLayout.SOUTH);
		
		timeCount.setHorizontalAlignment(JTextField.CENTER);
		timePanel.add(timeCount);
		
		timePanel.add(timeBar);
		
		timeTotal.setHorizontalAlignment(JTextField.CENTER);
		timePanel.add(timeTotal);
		
		bottom.add(timePanel,BorderLayout.NORTH);
		bottom.add(dock, BorderLayout.SOUTH);

		menu.add(media);
		media.add(browse);
		media.add(download);
		media.add(quit);

		menu.add(playback);
		playback.add(menuPlay);
		playback.add(menuStop);
		playback.add(menuForward);
		playback.add(menuBackward);

		menu.add(audio);
		audio.add(strip);
		audio.add(replace);
		audio.add(overlay);
		audio.add(menuMute);

		menu.add(sub);
		sub.add(title);
		sub.add(credit);
		sub.add(editT);

		menu.add(help);
		help.add(f1);
		help.add(about);

		dock.add(play);
		dock.add(stop);
		dock.add(mute);
		dock.add(back);
		dock.add(forward);
		dock.add(volume);

		main.add(mediaPlayerComponent, BorderLayout.CENTER);

		frame.setContentPane(main);
		frame.setLocation(100, 100);
		frame.setSize(1050, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		checkForLog();
		
		
		browse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.showOpenDialog(null);
				File file = fc.getSelectedFile();
				videoLocation = file.getAbsolutePath();
				VideoPlayer video = new VideoPlayer();
				video.execute();
			}
		});

		download.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Download download = new Download(frame);
			}
		});

		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		strip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				StripAudio strip = new StripAudio(frame);
			}
		});
		
		replace.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				ReplaceAudio replace = new ReplaceAudio(frame);
				
			}			
		});
		
		
		overlay.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				OverlayAudio overlay = new OverlayAudio(frame);
			}
		});

		play.addActionListener(new playListener());
		menuPlay.addActionListener(new playListener());

		stop.addActionListener(new stopListener());
		menuStop.addActionListener(new stopListener());

		mute.addActionListener(new muteListener());
		menuMute.addActionListener(new muteListener());

		forward.addActionListener(new forwardListener());
		menuForward.addActionListener(new forwardListener());

		back.addActionListener(new rewindListener());
		menuBackward.addActionListener(new rewindListener());
		
		title.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new TitleAndCreditAdder(true,false,null,null,null);
			}
		});
		
		credit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new TitleAndCreditAdder(false,false,null,null,null);
			}
		});
		
		editT.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				video.stop();
				new EditTitleOrCredit();
			}
		});
		
		volume.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				int value = volume.getValue();
				video.setVolume(value);
			}
		});
	}

	

	class Skip extends SwingWorker<Void, Integer> {
		@Override
		protected Void doInBackground() throws Exception {
			while (goforward == true) {
				video.skip(0100);
			}
			while (gobackward == true) {
				video.skip(-0100);
			}
			if (paused == true) {
				video.pause();
			}
			return null;
		}
	}

	class VideoPlayer extends SwingWorker<Void, Integer> {
		@Override
		protected Void doInBackground() throws Exception {
			paused = false;
			goforward = false;
			gobackward = false;
			video.playMedia(videoLocation);
			video.parseMedia();
			int max = (int) video.getMediaMeta().getLength();
			timeBar.setMaximum(max);
			timeTotal.setText(String.format("%02d:%02d", (int)max/60000, (int)max%60000/1000));
			while (video.getTime() < max) {
				publish((int) video.getTime());
			}
			return null;
		}

		@Override
		protected void process(List<Integer> chunks) {
				timeBar.setValue(chunks.get(chunks.size()-1));
				if (!timeCount.getText().equals(String.format("%02d:%02d", (int)chunks.get(chunks.size()-1)/60000, (int)(chunks.get(chunks.size()-1))%60000/1000))){
					timeCount.setText(String.format("%02d:%02d", (int)chunks.get(chunks.size()-1)/60000, (int)(chunks.get(chunks.size()-1))%60000/1000));
				}
		}

		@Override
		protected void done() {
			video.stop();
			timeBar.setValue(0);
			timeCount.setText("--:--");
			endofvideo = true;
			goforward = false;
		}
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ProjectGUI(args);
			}
		});
	}

	private class forwardListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (gobackward == true) {
				gobackward = false;
			} else {
				if (paused == true) {
					paused = false;
					video.play();
				}
				goforward = true;
				Skip forwardjob = new Skip();
				forwardjob.execute();
			}
		}
	}

	private class rewindListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (goforward == true) {
				goforward = false;
			} else {
				if (paused == true) {
					video.play();
					paused = false;
				}
				gobackward = true;
				Skip backwardjob = new Skip();
				backwardjob.execute();
			}
		}
	}

	private class playListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (goforward == true) {
				goforward = false;
			} else if (gobackward == true) {
				gobackward = false;
			} else if (endofvideo == true) {
				endofvideo = false;
				VideoPlayer video = new VideoPlayer();
				video.execute();
			} else if (paused == true){
				paused = false;
				video.play();
			}
			else {
				paused = true;
				video.pause();
			}
		}
	}

	private class stopListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			video.stop();
		}
	}

	private class muteListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			video.mute();
		}
	}

	private void checkForLog() {
		try {
			String homeDir = System.getProperty("user.home");
			String logname = homeDir + "/VAMIXlog.txt";
			File log = new File(logname);
			if (!log.exists()) {
				log.createNewFile();
			}
		} catch (Exception e) {
		}
	}
}