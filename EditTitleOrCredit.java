package mediaPlayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class EditTitleOrCredit {
	private String editLocation;
	private Boolean canEdit = false;

	public EditTitleOrCredit() {
		JFileChooser fc = new JFileChooser();
		int result = fc.showOpenDialog(null);
		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		}
		File file1 = fc.getSelectedFile(); 
		editLocation = file1.getAbsolutePath();
		readLog();
	}


	public void readLog() {
		String homeDir = System.getProperty("user.home");
		String logname = homeDir + "/VAMIXlog.txt";
		String tempFile = homeDir + "/tempFile.txt";
		File log = new File(logname);
		File temp= new File(tempFile);
		try {
			FileReader fr = new FileReader(log.getAbsolutePath());
			BufferedReader textReader = new BufferedReader(fr);
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			String input = textReader.readLine();

			while (input != null) {
				String[] details = input.split(" ");
				if (details[2].equals(editLocation)) {
					canEdit = true;
					
					if (details[1].equals("true")) {
						String text = "";
						for (int i = 3; i < details.length; i++) {
							text = text + details[i] + " ";
						}
						new TitleAndCreditAdder(true, true,details[0],text,details[2]);
					} else {
						String text = "";
						for (int i = 3; i < details.length; i++) {
							text = text + details[i] + " ";
						}
						new TitleAndCreditAdder(false, true,details[0],text,details[2]);
					}
				} else {
					writer.write(input);
				}
				input = textReader.readLine();
			}
			writer.close();
			textReader.close();
			temp.renameTo(log);


			if (canEdit == false) {
				JOptionPane.showMessageDialog(null, "Cannot Edit File");
			}
			
			
		} catch (Exception e){
			JOptionPane.showMessageDialog(null, "Cannot Edit File");
		}



	}
}
