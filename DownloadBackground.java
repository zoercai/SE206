package mediaPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class DownloadBackground extends SwingWorker<Integer, Integer> {

	private String url;
	private String outputFile;
	private int status;

	public DownloadBackground(String url, String outputFile) {
		this.url = url;
		this.outputFile = outputFile;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		//TODO have a progress bar for download in a dialog.
		
		try {
			Object[] confirm = { "Yes", "No" };
			int n = JOptionPane
					.showOptionDialog(null, "Is this file open-source?",
							"Open Source Confirmation",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, confirm,
							confirm[1]);
			if (n == JOptionPane.YES_OPTION) {
				/*String baseNameCmd = "basename " + outputFile;
				ProcessBuilder baseNameBuilder = new ProcessBuilder("bash",
						"-c",baseNameCmd);
				baseNameBuilder.redirectErrorStream(true);
				Process baseNameProcess = baseNameBuilder.start();
				BufferedReader stdoutBase = new BufferedReader(
						new InputStreamReader(
								baseNameProcess.getInputStream()));
				String outputFile = stdoutBase.readLine();*/
				String chkFileExistsCmd = "test -e " + outputFile;
				System.out.println(chkFileExistsCmd);
				ProcessBuilder checkFileBuilder = new ProcessBuilder(
						"bash", "-c", chkFileExistsCmd);
				checkFileBuilder.redirectErrorStream(true);
				Process checkFileProcess = checkFileBuilder.start();
				if (!isCancelled()) {
					status = checkFileProcess.waitFor();
				}
				if (checkFileProcess.exitValue() == 0) { // if file exists -
															// alert user,
															// ask for
															// options
					Object[] options = { "Override File",
							"Resume Download", "Cancel" };
					int a = JOptionPane
							.showOptionDialog(
									null,
									"File exists. Would you like to override, resume, or cancel your download?",
									"File Exists!",
									JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE, null,
									options, options[2]);
					if (a == JOptionPane.YES_OPTION) { // Override option
						String ovrCmd = "wget " + " --progress=dot " + url 
								+ " -O " + outputFile;
						ProcessBuilder ovrBuilder = new ProcessBuilder(
								"bash", "-c", ovrCmd);
						ovrBuilder.redirectErrorStream(true);
						Process ovrProcess = ovrBuilder.start();
						BufferedReader stdoutOverride = new BufferedReader(
								new InputStreamReader(
										ovrProcess.getInputStream()));
						String line;
						final AtomicInteger percent = new AtomicInteger();
						// System.out.println(stdoutOverride.readLine());
						while ((line = stdoutOverride.readLine()) != null
								&& !isCancelled()) {
							if (line.contains("%")) {
								percent.incrementAndGet();
								publish(percent.get());
							}
						}
						if (!isCancelled()) {
							status = ovrProcess.waitFor();
						}
						if (ovrProcess.exitValue() != 0) {
							JOptionPane
									.showMessageDialog(null,
											"Error! Check your internet connection and that your URL is correct!");
							this.cancel(true);
						} else {
							// checkLog("DOWNLOAD");

						}
						ovrProcess.getInputStream().close();
						ovrProcess.getOutputStream().close();
						ovrProcess.getErrorStream().close();
						ovrProcess.destroy();
					} else if (a == JOptionPane.NO_OPTION) { // resume
															// option
						
						String resCmd = "wget " + " --progress=dot -c " + url + " -O "+ outputFile;
						ProcessBuilder resBuilder = new ProcessBuilder(
								"bash", "-c", resCmd);
						resBuilder.redirectErrorStream(true);
						Process resProcess = resBuilder.start();
						BufferedReader stdoutDownload = new BufferedReader(
								new InputStreamReader(
										resProcess.getInputStream()));
						String line;
						final AtomicInteger percent = new AtomicInteger();
						// System.out.println(stdoutDownload.readLine());
						while ((line = stdoutDownload.readLine()) != null
								&& !isCancelled()) {
							if (line.contains("%")) {
								percent.incrementAndGet();
								publish(percent.get());
							}
						}
						if (!isCancelled()) {
							status = resProcess.waitFor();
						}
						if (resProcess.exitValue() != 0) {
							JOptionPane
									.showMessageDialog(null,
											"Error! Check your internet connection and that your URL is correct!");
							this.cancel(true);
						} else {
							// checkLog("DOWNLOAD");
						}
						resProcess.getInputStream().close();
						resProcess.getOutputStream().close();
						resProcess.getErrorStream().close();
						resProcess.destroy();
					} else {
						this.cancel(true);
					}
				} else { // file doesn't exist, download 
					
					String dwnCmd = "wget " + " --progress=dot " + url + " -O "+ outputFile;
					
					ProcessBuilder downloadBuilder = new ProcessBuilder(
							"bash", "-c", dwnCmd);
					downloadBuilder.redirectErrorStream(true);
					Process downloadProcess = downloadBuilder.start();
					BufferedReader stdoutDownload = new BufferedReader(
							new InputStreamReader(
									downloadProcess.getInputStream()));
					String line;
					final AtomicInteger percent = new AtomicInteger();
					// System.out.println(stdoutDownload.readLine());
					while ((line = stdoutDownload.readLine()) != null
							&& !isCancelled()) {
						if (line.contains("%")) {
							percent.incrementAndGet();
							publish(percent.get());
						}
					}
					if (!isCancelled()) {
						status = downloadProcess.waitFor();
					}
					if (downloadProcess.exitValue() != 0) {
						JOptionPane
								.showMessageDialog(null,
										"Error! Check your internet connection and that your URL is correct!");
						this.cancel(true);
					} else {
						// checkLog("DOWNLOAD");
					}
					downloadProcess.getInputStream().close();
					downloadProcess.getOutputStream().close();
					downloadProcess.getErrorStream().close();
					downloadProcess.destroy();
				}

			} else { // file is not open source!
				JOptionPane.showMessageDialog(null,
						"File is not open source, I refuse to download!");
				this.cancel(true);
			}

		} catch (IOException | InterruptedException ex) {
			ex.printStackTrace(System.err);
		}
		return status;
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
			JOptionPane.showMessageDialog(null, "Download completed!");
		} else if (this.isCancelled()) {
			JOptionPane.showMessageDialog(null, "Download not completed.");
			// this.getState()).toString() + " " + status
		}
		// progressBar.setValue(0);
		// progressBar.setStringPainted(false);
		// cclDownload.setEnabled(false);
	}
}
