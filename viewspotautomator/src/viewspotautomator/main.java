
package viewspotautomator;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import se.vidstige.jadb.JadbException;
import javax.swing.JTextPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class main {

	public static void main(String[] args) throws IOException, JadbException, InterruptedException {
		String command = "adb devices";
		ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
		processBuilder.directory(new File("/"));
		@SuppressWarnings("unused")
		Process adb = processBuilder.start();
		try {
			@SuppressWarnings("unused")
			Automator a = new Automator();
		} catch (IOException e) {
			errorThrow(e.toString());

			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JadbException e) {
			errorThrow(e.toString());

			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			errorThrow(e.toString());

			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoDevicesException e) {
			// TODO Auto-generated catch block
			errorThrow(e.toString());
		}
	}
	
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static void errorThrow(String s) {
		JFrame error = new JFrame("ViewSpot Automator error");// Setup window
		error.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		error.setSize(390, 308);
		error.getContentPane().setLayout(null);
		
		JTextPane btnClose = new JTextPane();
		btnClose.setBounds(10, 11, 354, 179);
		error.getContentPane().add(btnClose);
		btnClose.setText(s);
		
		JButton btnNewButton = new JButton("OK");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				error.dispose();
			}
		});
		btnNewButton.setBounds(112, 214, 140, 44);
		error.getContentPane().add(btnNewButton);
		error.setVisible(true);	
		
		
		
	}
}
