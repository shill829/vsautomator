package viewspotautomator;

import javax.annotation.PostConstruct;
import javax.swing.*;

import java.awt.Composite;
import java.awt.FileDialog;
import java.io.File;
/**
 * A GUI to pick and return a file from local storage. 
 * @author shill
 *
 */
public class FileSelector {
/**
 * 
 * @return A File class with a selected APK
 */
	public static File pickFile() {
		JFrame fileSelector = new JFrame("ViewSpot Automator");// Setup window
		fileSelector.setSize(10, 10);
		fileSelector.getContentPane().setLayout(null);
		FileDialog fd = new FileDialog(fileSelector, "Choose an APK", FileDialog.LOAD);
		fileSelector.setVisible(true);
		fd.setDirectory("/");
		fd.setFile("*.apk");
		fd.setVisible(true);
		File filename = new File(fd.getDirectory() + fd.getFile());
		fileSelector.setVisible(false);
		fileSelector.dispose();
		return filename;
	}
/**
 * 
 * @param type of file to pick
 * @return the chosen file
 */
	public static File pickFile(String fileType) {
		JFrame fileSelector = new JFrame("ViewSpot Automator");// Setup window
		fileSelector.setSize(10, 10);
		fileSelector.getContentPane().setLayout(null);
		FileDialog fd = new FileDialog(fileSelector, "Choose an APK", FileDialog.LOAD);
		fileSelector.setVisible(true);// should be at end of method
		fd.setDirectory("/");
		fd.setFile("*" + fileType);
		fd.setVisible(true);
		File filename = new File(fd.getDirectory() + fd.getFile());
		fileSelector.setVisible(false);
		fileSelector.dispose();
		return filename;
	}

	@PostConstruct
	public void createControls(Composite parent) {
		System.out.println(this.getClass().getSimpleName() + " @PostConstruct method called.");
	}
}
