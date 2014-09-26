package mediaPlayer;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class Download {
	private JFrame parent;
	private String outputFile;
	
	private JDialog downloadMain = new JDialog(parent, "Download");
	private JPanel main = new JPanel();
	private FlowLayout flow = new FlowLayout();

	private JPanel urlPanel = new JPanel(new FlowLayout());
	private JLabel urlLabel = new JLabel("URL: ");
	private JTextField urlField = new JTextField();

	private JPanel outputPanel = new JPanel(new FlowLayout());
	private JLabel outputLabel = new JLabel("Save As: ");
	private JTextField outputField = new JTextField();
	private JButton outputSelectButton = new JButton("Browse");

	
	private JProgressBar progressBar = new JProgressBar();

	private JPanel bottomPanel = new JPanel(new FlowLayout());
	private JButton downloadButton = new JButton("Download");
	
	private JPanel openSourcePanel = new JPanel(flow);
	private JLabel openSourceLabel = new JLabel("Open Source?");
	private JCheckBox openSourceCheck = new JCheckBox();
	
	
	
	public Download(JFrame parent){
		this.parent = parent;
		
		if (parent != null) {
			Dimension parentSize = parent.getSize();
			Point p = parent.getLocation();
			downloadMain.setLocation(p.x + parentSize.width / 4, p.y
					+ parentSize.height / 4);
		}
		
		flow.setVgap(15);
		
		main.setLayout(new BoxLayout(main,BoxLayout.PAGE_AXIS));
		
		main.add(urlPanel);
		urlField.setColumns(30);
		urlPanel.add(urlLabel);
		urlPanel.add(urlField);
		urlPanel.add(openSourcePanel);
		urlPanel.add(openSourceLabel);
		urlPanel.add(openSourceCheck);
		
		main.add(outputPanel);
		outputPanel.add(outputLabel);
		outputField.setColumns(30);
		outputPanel.add(outputField);
		outputPanel.add(outputSelectButton);
		
		main.add(bottomPanel);
		bottomPanel.add(progressBar);
		bottomPanel.add(downloadButton);
		
		downloadMain.getContentPane().add(main);
		
		downloadMain.pack();
		downloadMain.setVisible(true);
		
		outputSelectButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					JFileChooser fileSaver = new JFileChooser();
					URL downloadURL;
					if(urlField.getText()!=""){
						downloadURL = new URL(urlField.getText());
						fileSaver.setSelectedFile(new File(downloadURL.getFile()));
					}					
					fileSaver.showDialog(null,"Save");
					outputFile = fileSaver.getSelectedFile().toString();
					outputField.setText(outputFile);
				} catch (MalformedURLException e) {
					JFileChooser fileSaver = new JFileChooser();
					fileSaver.showDialog(null,"Save");
					outputFile = fileSaver.getSelectedFile().toString();
					outputField.setText(outputFile);
				}
			}
		});
		
		downloadButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(urlField!=null && outputFile!=null && openSourceCheck.isSelected()){
					DownloadBackground download = new DownloadBackground(urlField.getText(),outputFile);
					download.execute();
				} else if(!openSourceCheck.isSelected()){
					JOptionPane.showMessageDialog(null,"File is not open source, I refuse to download!");
				} else{
					JOptionPane.showMessageDialog(null,"Please ensure both URL and output file are specified.");
				}
			}
		});
	}
	
	
	public class DownloadBackground extends SwingWorker<Integer, Integer> {
		private String url;
		private String outputFile;
		private int status;
		
		public DownloadBackground(String url,String outputFile) {
			this.url = url;
			this.outputFile = outputFile;
		}
		
		@Override
		protected Integer doInBackground() throws Exception {
			String chkFileExistsCmd = "test -e " + outputFile;
			ProcessBuilder checkFileBuilder = new ProcessBuilder("bash", "-c",
					chkFileExistsCmd);
			checkFileBuilder.redirectErrorStream(true);
			Process checkFileProcess = checkFileBuilder.start();
			if (!isCancelled()) {
				status = checkFileProcess.waitFor();
			}
			if (checkFileProcess.exitValue() == 0) { 
				// if file exists -alert user,ask for options
				Object[] options = { "Override File", "Resume Download",
						"Cancel" };
				int a = JOptionPane
						.showOptionDialog(
								null,
								"File exists. Would you like to override, resume, or cancel your download?",
								"File Exists!",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[2]);
				if (a == JOptionPane.YES_OPTION) { // Override option
					String ovrCmd = "wget " + " --progress=dot " + url + " -O "
							+ outputFile;
					ProcessBuilder ovrBuilder = new ProcessBuilder("bash",
							"-c", ovrCmd);
					ovrBuilder.redirectErrorStream(true);

					Process ovrProcess = ovrBuilder.start();
					progressBar.setIndeterminate(true);

					BufferedReader stdoutOverride = new BufferedReader(
							new InputStreamReader(ovrProcess.getInputStream()));
					String line;
					final AtomicInteger percent = new AtomicInteger();
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
					}
					ovrProcess.getInputStream().close();
					ovrProcess.getOutputStream().close();
					ovrProcess.getErrorStream().close();
					ovrProcess.destroy();
				} else if (a == JOptionPane.NO_OPTION) { 
					// resume option
					String resCmd = "wget " + "--progress=dot -c " + url
							+ " -O " + outputFile;
					ProcessBuilder resBuilder = new ProcessBuilder("bash",
							"-c", resCmd);
					resBuilder.redirectErrorStream(true);
					Process resProcess = resBuilder.start();
					progressBar.setIndeterminate(true);
					BufferedReader stdoutDownload = new BufferedReader(
							new InputStreamReader(resProcess.getInputStream()));
					String line;
					final AtomicInteger percent = new AtomicInteger();
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
					}
					resProcess.getInputStream().close();
					resProcess.getOutputStream().close();
					resProcess.getErrorStream().close();
					resProcess.destroy();
				} else {
					this.cancel(true);
				}
			} else { 
				// file doesn't exist, download
				String dwnCmd = "wget " + " --progress=dot " + url + " -O "
						+ outputFile;
				System.out.println(dwnCmd);
				ProcessBuilder downloadBuilder = new ProcessBuilder("bash",
						"-c", dwnCmd);
				downloadBuilder.redirectErrorStream(true);
				Process downloadProcess = downloadBuilder.start();

				progressBar.setIndeterminate(true);
				
				BufferedReader stdoutDownload = new BufferedReader(
						new InputStreamReader(downloadProcess.getInputStream()));
				String line;
				final AtomicInteger percent = new AtomicInteger();
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
				}
				downloadProcess.getInputStream().close();
				downloadProcess.getOutputStream().close();
				downloadProcess.getErrorStream().close();
				downloadProcess.destroy();

			}

			return status;
		}

		
		
		@Override
		protected void process(List<Integer> chunks) {
		}

		@Override
		protected void done() {
			if (!this.isCancelled()) {
				JOptionPane.showMessageDialog(null, "Download completed!");
				progressBar.setIndeterminate(false);
			} else if (this.isCancelled()) {
				JOptionPane.showMessageDialog(null, "Download not completed.");
				progressBar.setIndeterminate(false);
			}
		}
	}
}
