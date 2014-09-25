package mediaPlayer;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class TitleAndCreditBackground extends SwingWorker<Integer, Integer> {
	private String _instruction;
	private JFrame progressBar = new JFrame("Adding Text");
	private JProgressBar pbar = new JProgressBar();
	private JTextArea progress = new JTextArea("Progress:");

	public TitleAndCreditBackground (String instruction,int frames) {
		_instruction = instruction;

		progressBar.add(progress,BorderLayout.CENTER);
		progressBar.add(pbar,BorderLayout.SOUTH); 
		pbar.setMaximum(frames);

		progressBar.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		progressBar.setSize(500, 60);
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
				String[] lines = line.split(" ");
				System.out.println(lines[1]);
				try {
					int i = Integer.parseInt(lines[1]);
					publish((int) i);
				} catch (Exception e) {
				}
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
			progressBar.dispose();
		} else if (this.isCancelled()) {
			JOptionPane.showMessageDialog(null, "Something went wrong :(");
			progressBar.dispose();
		}
	}
}
