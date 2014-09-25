package mediaPlayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class TitleAndCreditBackground extends SwingWorker<Integer, Integer> {
	private String _instruction;
	private JFrame progressBar = new JFrame("Adding Text");
	private JProgressBar pbar = new JProgressBar();
	private boolean updatePBar = false;
	private int _frames;

	public TitleAndCreditBackground (String instruction,int frames) {
		_instruction = instruction;
		_frames = frames;

		progressBar.add(pbar); // TODO Implement progress bar
		pbar.setMaximum(frames);

		progressBar.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		progressBar.setSize(100, 200);
		progressBar.setVisible(true);
	}

	@Override
	protected Integer doInBackground() throws Exception {
		try {
			ProcessBuilder titleAdder = new ProcessBuilder("bash", "-c", _instruction);
			titleAdder.redirectErrorStream(true);
			Process downloadProcess = titleAdder.start();
			BufferedReader stdoutDownload = new BufferedReader(new InputStreamReader(downloadProcess.getInputStream()));
			String line = stdoutDownload.readLine();
			while (line != null && !isCancelled()) {
				line = stdoutDownload.readLine();
				System.out.println(line);
//				if (updatePBar == true) {
					String[] lines = line.split(" ");
					System.out.println(lines[1]);
//					int frame = Integer.parseInt(lines[1]);
//					System.out.println(frame);
//					while (frame < _frames) {
//						publish((int) frame);
//					}
//					pbar.setValue(frame);
//				}
//				if (line.equals("Press ctrl-c to stop encoding")) {
//					updatePBar = true;
//				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	protected void process(List<Integer> chunks) {
		for (int percent : chunks) {
			pbar.setValue(percent);
		}
	}

	@Override
	protected void done() {
		if (!this.isCancelled()) {
			JOptionPane.showMessageDialog(null, "Text successfully added to video");
		} else if (this.isCancelled()) {
			JOptionPane.showMessageDialog(null, "Something went wrong :(");
			// this.getState()).toString() + " " + status
		}
		// progressBar.setValue(0);
		// progressBar.setStringPainted(false);
		// cclDownload.setEnabled(false);
	}
}
