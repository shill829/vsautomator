
package viewspotautomator;

import java.io.File;
import java.io.IOException;

import se.vidstige.jadb.JadbException;

public class main {

	public static void main(String[] args) throws IOException, JadbException, InterruptedException {
		String command ="adb devices";
		ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
		processBuilder.directory(new File("/"));
		Process adb = processBuilder.start();		
		@SuppressWarnings("unused")
		Automator a = new Automator();
	}
}
   