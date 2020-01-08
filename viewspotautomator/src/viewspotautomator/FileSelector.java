package viewspotautomator;
import javax.annotation.PostConstruct;
import javax.swing.*;

import java.awt.Composite;
import java.awt.FileDialog;
import java.io.File;
public class FileSelector {

	
	public static File pickFile(){
	JFrame fileSelector = new JFrame("ViewSpot Automator");//Setup window
	fileSelector.setSize(10, 10);
	fileSelector.getContentPane().setLayout(null);
	FileDialog fd=new FileDialog(fileSelector,"Choose an APK",FileDialog.LOAD);
	fileSelector.setVisible(true);
	fd.setDirectory("C:\\");
	fd.setFile("*.apk");
	fd.setVisible(true);
	File filename = new File (fd.getDirectory()+fd.getFile());	
	fileSelector.setVisible(false);
	fileSelector.dispose();
	return filename;
	}
	
	
	
	
	public static File pickFile(String fileType){
		JFrame fileSelector = new JFrame("ViewSpot Automator");//Setup window
		fileSelector.setSize(10, 10);
		fileSelector.getContentPane().setLayout(null);
		FileDialog fd=new FileDialog(fileSelector,"Choose an APK",FileDialog.LOAD);
		fileSelector.setVisible(true);//should be at end of method
		fd.setDirectory("C:\\");
		fd.setFile("*"+fileType);
		fd.setVisible(true);
		File filename = new File (fd.getDirectory()+fd.getFile());	
		fileSelector.setVisible(false);
		fileSelector.dispose();
		return filename;
	}
	
	
	@PostConstruct
	public void createControls(Composite parent) {
		System.out.println(this.getClass().getSimpleName() + " @PostConstruct method called.");
	}
}
