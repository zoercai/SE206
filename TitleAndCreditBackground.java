package mediaPlayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class TitleAndCreditBackground extends SwingWorker<Integer, Integer> {
	private String _instruction;

	public TitleAndCreditBackground (String instruction) {
		_instruction = instruction;
	}
	
	@Override
	protected Integer doInBackground() throws Exception {
		ProcessBuilder titleAdder = new ProcessBuilder("bash", "-c", _instruction);
		titleAdder.redirectErrorStream(true);
		try {
		Process downloadProcess = titleAdder.start();
		BufferedReader stdoutDownload = new BufferedReader(new InputStreamReader(downloadProcess.getInputStream()));
		String line = stdoutDownload.readLine();
		while (line  != null && !isCancelled()) {
			System.out.println(line);
			line = stdoutDownload.readLine();
		}
		} catch (Exception e) {
			
		}
		return null;
	}
	
	@Override
	protected void process(List<Integer> chunks) {
		for (int percent : chunks) {
			// progressBar.setValue(percent);
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
