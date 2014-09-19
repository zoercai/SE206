package mediaPlayer;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

class ReplaceAudioBackground extends SwingWorker<Integer, Integer> {
	private int status;
	private String originVideo;
	private String originAudio;
	private String destURL;

	public ReplaceAudioBackground(String originVideo,String originAudio,String destURL) {
		this.originVideo = originVideo;
		this.originAudio = originAudio;
		this.destURL = destURL;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		
		//TODO have a progress bar for extract in a dialog that can be minimized.
		//TODO give warning if no audio signal.
		
		String chkFileExistsCmd = "test -e " + destURL;
		ProcessBuilder checkFileBuilder = new ProcessBuilder("bash", "-c",
				chkFileExistsCmd);
		checkFileBuilder.redirectErrorStream(true);
		Process checkFileProcess = checkFileBuilder.start();
		if (!isCancelled()) {
			status = checkFileProcess.waitFor();
		}
		if (checkFileProcess.exitValue() == 0) { // file exists already
			Object[] confirm = { "Override", "Cancel" };
			int a = JOptionPane
					.showOptionDialog(
							null,
							"Output file name already exists! Would you like to override existing file? Click Cancel if you would like to specify another output file name.",
							"Output File Name Exists!",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, confirm,
							confirm[1]);
			if (a == JOptionPane.YES_OPTION) { // override
				
				//ffmpeg -i video.avi -i audio.mp3 -map 0 -map 1 -codec copy -shortest output_video.avi
				
				String avconvCmd = "avconv -y -i "+originVideo + " -i "+originAudio + " -c:v copy -map 0:0 -map 1:0 -codec copy " + destURL;
				ProcessBuilder avconvBuilder = new ProcessBuilder("bash", "-c",
						avconvCmd);
				avconvBuilder.redirectErrorStream(true);
				Process avconvProcess = avconvBuilder.start();
				if (!isCancelled()) {
					status = avconvProcess.waitFor();
				}
				if (avconvProcess.exitValue() != 0) {
					this.cancel(true);
				} else {
					// checkLog("EXTRACT");
				}
			} else {
				this.cancel(true);
				JOptionPane
						.showMessageDialog(
								null,
								"Error! Relace was not successful. Please check output file name and make sure it contains the appropriate extension.");
			}

		} else { // file doesn't exist
			// avconv it
			String avconvCmd = "avconv -i "+ originVideo + " -i "+originAudio + " -c:v copy -map 0:0 -map 1:0 -codec copy " + destURL;
			ProcessBuilder avconvBuilder = new ProcessBuilder("bash", "-c",
					avconvCmd);
			avconvBuilder.redirectErrorStream(true);
			Process avconvProcess = avconvBuilder.start();
			if (!isCancelled()) {
				status = avconvProcess.waitFor();
			}
			if (avconvProcess.exitValue() != 0) {
				this.cancel(true);
				JOptionPane
						.showMessageDialog(
								null,
								"Error! Replace was not successful. Please check output file name and make sure it contains the appropriate extension.");
			} else {
				// checkLog("EXTRACT");
			}
		}

		return null;
	}

	@Override
	protected void done() {
		if (!this.isCancelled()) {
			JOptionPane.showMessageDialog(null, "Replace completed!");
		} else if (this.isCancelled()) {
			JOptionPane.showMessageDialog(null, "Replace not completed.");
		}
		// progressBar.setValue(0);
		// progressBar.setStringPainted(false);
	}

}