package mediaPlayer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class ProjectGUI {

	/*
	 * TODO AKNOWLEDGE CODE FROM THE LETURE (AND HENCE THAT SITE) THE FILE
	 * VIEWER. File Viewer =
	 * http://docs.oracle.com/javase/tutorial/uiswing/components
	 * /filechooser.html
	 * 
	 * create title page(s) and credit page(s) for the video.
	 * 
	 * 
	 * Subtitle -Add subtitle -Create Title Page -Create Credit Page -Edit
	 * Subtitle -> Click on subtitle while playing so it pauses and editing can
	 * be done
	 * 
	 * 
	 * Need to make the transition between fast forward and rewind more seamless
	 * if play is not first used to stop it.
	 * 
	 * Subtitle class - Don't do it in here :)
	 * 
	 * TODO Need to add cancel options to all places where choices are made
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

	private JProgressBar timeBar = new JProgressBar();

	private JButton play = new JButton("Play");
	private JButton stop = new JButton("Stop");
	private JButton mute = new JButton("Mute");
	private JButton forward = new JButton("Forward");
	private JButton back = new JButton("Rewind");
	private JSlider volume = new JSlider(0,150,0);

	boolean paused = false;
	boolean goforward = false;
	boolean endofvideo = false;
	boolean gobackward = false;

	String videoLocation = "";
	String saveLocation = "";

	private ProjectGUI(String[] args) {
		JFrame frame = new JFrame("Lysandros Media Player");

		main.add(menu, BorderLayout.NORTH);
		main.add(bottom, BorderLayout.SOUTH);
		bottom.add(timeBar, BorderLayout.NORTH);
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
				String URL = JOptionPane
						.showInputDialog("Please enter file URL: ");
				URL url;

				try {
					url = new URL(URL);
					if (URL == null) {
						// do nothing
					} else {
						JFileChooser fileSaver = new JFileChooser();
						fileSaver.setSelectedFile(new File(url.getFile()));
						fileSaver.showDialog(null,"Save");
						File file = fileSaver.getSelectedFile();
						DownloadBackground download = new DownloadBackground(URL,file.toString());
						download.execute();

					}

				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}


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
				//Open file to be extracted
				JFileChooser fileOpener = new JFileChooser();
				fileOpener.showDialog(null,"Choose video file to be extracted");
				File sourcefile = fileOpener.getSelectedFile();

				//Choose location and name for audio file
				JFileChooser fileSaver = new JFileChooser();
				fileSaver.setFileFilter(new FileNameExtensionFilter(".mp3","MP3 audio format"));
				fileSaver.showDialog(null,"Name output audio file");
				File file = fileSaver.getSelectedFile();

				//If both are correctly set, extract.
				if ((sourcefile!=null) && (file!=null)){
					if(!file.getName().endsWith(".mp3"))
					{
						file = new File(file.getAbsoluteFile() + ".mp3");
					}
					ExtractAudioBackground extract = new ExtractAudioBackground(sourcefile.getAbsolutePath(),file.getAbsolutePath());
					extract.execute();		
				}else{
					JOptionPane.showMessageDialog(null, "File not extracted. Please specify both files correctly!");
				}

			}
		});

		replace.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {

				//Open video file to be extracted
				JFileChooser videoOpener = new JFileChooser();
				videoOpener.showDialog(null,"Choose source video file");
				File sourceVideo = videoOpener.getSelectedFile();

				//Open audio file to be extracted
				JFileChooser audioOpener = new JFileChooser();
				audioOpener.showDialog(null,"Choose source audio file");
				File sourceAudio = audioOpener.getSelectedFile();

				//Choose location and name for output video file
				JFileChooser videoSaver = new JFileChooser();
				videoSaver.setFileFilter(new FileNameExtensionFilter(".avi","AVI audio format"));
				videoSaver.showDialog(null,"Name output audio file");
				File file = videoSaver.getSelectedFile();

				//If both are correctly set, extract.
				if ((sourceVideo!=null) && (file!=null) && (sourceAudio!=null)){
					if(!file.getName().endsWith(".avi"))
					{
						file = new File(file.getAbsoluteFile() + ".avi");
					}
					ReplaceAudioBackground replace = new ReplaceAudioBackground(sourceVideo.getAbsolutePath(),sourceAudio.getAbsolutePath(),file.getAbsolutePath());
					replace.execute();		
				}else{
					JOptionPane.showMessageDialog(null, "Replace not completed. Please specify all files correctly!");
				}
			}

		});


		overlay.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {

				//Open video file to be extracted
				JFileChooser videoOpener = new JFileChooser();
				videoOpener.showDialog(null,"Choose source video file");
				File sourceVideo = videoOpener.getSelectedFile();

				//Open audio file to be extracted
				JFileChooser audioOpener = new JFileChooser();
				audioOpener.showDialog(null,"Choose source audio file");
				File sourceAudio = audioOpener.getSelectedFile();

				//Choose location and name for output video file
				JFileChooser videoSaver = new JFileChooser();
				videoSaver.setFileFilter(new FileNameExtensionFilter(".avi","AVI audio format"));
				videoSaver.showDialog(null,"Name output audio file");
				File file = videoSaver.getSelectedFile();

				//If both are correctly set, extract.
				if ((sourceVideo!=null) && (file!=null) && (sourceAudio!=null)){
					if(!file.getName().endsWith(".avi"))
					{
						file = new File(file.getAbsoluteFile() + ".avi");
					}
					OverlayBackground overlay = new OverlayBackground(sourceVideo.getAbsolutePath(),sourceAudio.getAbsolutePath(),file.getAbsolutePath());
					overlay.execute();		
				}else{
					JOptionPane.showMessageDialog(null, "Overlay not completed. Please specify all files correctly!");
				}
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
				video.stop();
				new TitleAndCreditAdder(true,false,null,null,null);
			}
		});

		credit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				video.stop();
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
				System.out.println(value);
				// TODO Set volume to that level
				video.setVolume(value);
			}
		});
	}



	class Skip extends SwingWorker<Void, Integer> {
		@Override
		protected Void doInBackground() throws Exception {
			while (goforward == true) {
				video.skip(0010);
			}
			while (gobackward == true) {
				video.skip(-0010);
			}
			if (paused == true) {
				video.pause();
			}
			return null;
		}

		@Override
		protected void process(List<Integer> chunks) {
		}

		@Override
		protected void done() {
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
			while (video.getTime() < max) {
				publish((int) video.getTime());
			}
			return null;
		}

		@Override
		protected void process(List<Integer> chunks) {
			for (int progress : chunks) {
				timeBar.setValue(progress);
			}

		}

		@Override
		protected void done() {
			video.stop();
			timeBar.setValue(0);
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