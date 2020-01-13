package viewspotautomator;

import java.awt.Composite;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import se.vidstige.jadb.*;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

public class LogProcessor {

	public static String getCurrLogs(Device d, int logSource) throws IOException, JadbException { // returns most recent
		ArrayList<String> entries = new ArrayList<String>();
		// logs from

	
		
		if (logSource == 0) {
			try {
				TaskProcessor.refreshLogs(d);
			} catch (IOException | JadbException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] command = { "curl", "-silent", "-XGET", "https://apollo-logrx.smithmicro.io/_qs/1", "|", "tail",
					"-n", "1" };
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.directory(new File("/"));
			Process getLogs = processBuilder.start();
			InputStream webLogInput = getLogs.getInputStream();
			String input = TaskProcessor.convertStreamToString(webLogInput);
			String[] inputParts = input.split("charset=utf-8", 2);
			@SuppressWarnings("resource")
			Scanner logScan = new Scanner(inputParts[1].trim()).useDelimiter("LOG,");
			String deviceName = TaskProcessor.getDeviceModel(d.getDevice()).trim();
			while (logScan.hasNext()) {
				String testVal = logScan.next();
				if (testVal.contains(deviceName)) {
					entries.add(testVal);
				}
			}
			logScan.close();
			getLogs.destroy();
			webLogInput.close();
		} else if (logSource == 1) {
			
			
			String[] command = {"adb","-s",d.getDevice().getSerial(),"logcat","-v","threadtime","|","find","\"cm_Logger\""};
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.directory(new File("/"));
			Process getLogs = processBuilder.start();
			Thread kill=new Thread() {
				public void run(){
					try {
						TaskProcessor.refreshLogs(d);
						Thread.sleep(5000);
					} catch (InterruptedException | IOException | JadbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					getLogs.destroy();
				}
			};
			kill.start();
			InputStream localLogInput = getLogs.getInputStream();
			String input = TaskProcessor.convertStreamToString(localLogInput);
			//System.out.println(input);
			String deviceName = TaskProcessor.getDeviceModel(d.getDevice()).trim();
			@SuppressWarnings("resource")
			Scanner logScan2 = new Scanner(input).useDelimiter(Pattern.quote("persist: map = {"));
			int x=0;
			while ((logScan2.hasNext())&&(x<3)) {
				String testVal = logScan2.next();
				if (testVal.contains(deviceName)) {
					entries.add(testVal);				
					x++;
				}
				
			}
			logScan2.close();
			
		}		
		return entries.get(entries.size() - 1);
	}

	public static ArrayList<ArrayList<String>> parseDevLogs(String s) { // Splits log string into names list and values
																		// list
		@SuppressWarnings("resource")
		Scanner logScan = new Scanner(s).useDelimiter(",");
		ArrayList<String> entries = new ArrayList<String>();
		ArrayList<String> names = new ArrayList<String>();
		while (logScan.hasNext()) {
			String str = logScan.next();
			if (str.contains("=") && !str.contains("event")) {
				String[] parts = str.split("=", 2);
				names.add(parts[0]);
				entries.add(parts[1]);
			}

		}
		ArrayList<ArrayList<String>> combined = new ArrayList<ArrayList<String>>();
		combined.add(names);
		combined.add(entries);
		logScan.close();
		return combined;

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public static ArrayList<String> logTester(Device d, int logSource) throws IOException, JadbException {

		File testCases = new File("logtester.csv");
		ArrayList<ArrayList<String>> combined = parseDevLogs(getCurrLogs(d, logSource));
		ArrayList<String> names = combined.get(0);
		ArrayList<String> values = combined.get(1);
		ArrayList<String> commands = new ArrayList<String>();
		ArrayList<String> arguments = new ArrayList<String>();
		ArrayList<String> results = new ArrayList<String>();
		results.add("Analytics Testing for: " + d);
		Scanner fileScan = new Scanner(testCases);
		while (fileScan.hasNext()) {
			String str = fileScan.nextLine();
			String[] comb = str.split(",", 2);
			commands.add(comb[0]);
			if (comb.length > 1) {
		
				arguments.add(comb[1]);
			} else {
				arguments.add("");
			}

		}
		fileScan.close();
		int numValues = commands.size();
		int numCorrect = 0;
		int numWrong=0;
		int numUntested=0;
		for (int x = 0; x < commands.size(); x++) {
			String match = "Fail";
			String expected = "";
			if ((!commands.get(x).contains("manualtest")) && !commands.get(x).contains("nullValue")&&!commands.get(x).contains("static")) { // Special case
																										// tests
				if (commands.get(x).contains("getVersion")) {
					System.out.println(d.getVersion().trim());
					if (values.get(x).trim().contains(d.getVersion().trim())) {
						match = "Pass";
						numCorrect++;
					}
				} else if (commands.get(x).contains("getPackName")) {
					if (values.get(x).contains(d.getPackName())) {
						match = "Pass";
						numCorrect++;
					}
				} else if (commands.get(x).contains("getChargeType")) {
					if (values.get(x).contains(TaskProcessor.getChargeType(d))) {
						match = "Pass";
						numCorrect++;
					}
				} else if (commands.get(x).contains("getSettingsBlocker")) {
					if ((d.getBlockerStatus().contains("Enabled") && values.get(x).contains("true"))
							|| (d.getBlockerStatus().contains("Disabled") && values.get(x).contains("false"))) {
						match = "Pass";
						numCorrect++;
					}

				} else if (commands.get(x).contains("getConnection")) {
					match = "N/A";
					numUntested++;				
				} else if (commands.get(x).contains("getDataState")) {
					match = "N/A";
					numUntested++;
				} else {
					String result = TaskProcessor
							.convertStreamToString(d.getDevice().executeShell(commands.get(x), arguments.get(x)));

					if (result.contains(values.get(x)) || values.get(x).contains(result)) {
						match = "Pass";
						numCorrect++;
					} else {
						String cleanString = result.replaceAll("\r", "").replaceAll("\n", "");
						expected = (" | Expected: " + cleanString + "  Got: " + values.get(x));
					}

				}

			} else if (commands.get(x).contains("manualtest")) {
				match = "Manual";
				numUntested++;
				expected=(" | Got: "+values.get(x));
			} else if (commands.get(x).contains("nullValue")) {
				if (values.get(x).contains("null")) {
					match = "Pass";
					numCorrect++;
				}
			}else if (commands.get(x).contains("static")) {
				if(arguments.get(x).contains(values.get(x))) {
					numCorrect++;
					match="Pass"; 
				}else {				
					expected = (" | Expected: " + arguments.get(x) + "  Got: " + values.get(x));
				}
			}
		
			numWrong=numValues-numCorrect-numUntested;
			String test = ("Test " + (x + 1) + "/" + numValues + ": " + names.get(x) + " | " + match + " " + expected);
			results.add(test);
		}
		results.add("Testing complete: " + numCorrect + " out of " + numValues + " correct.");
		results.add(numWrong+" out of "+numValues+" failed.");
		results.add(numUntested+" out of "+numValues+" untested.");
		JFrame logTester = new JFrame("ViewSpot Automator- Log Testing " + d.getDevice());// Setup window
		logTester.setSize(763, 567);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 727, 0 };
		gridBagLayout.rowHeights = new int[] { 506, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		logTester.getContentPane().setLayout(gridBagLayout);
		JTextArea textArea = new JTextArea(727, 506);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		logTester.getContentPane().add(scrollPane, gbc_scrollPane);
		for (int x = 0; x < results.size(); x++) {
			textArea.append(results.get(x));
			textArea.append("\n");
		}
		logTester.setVisible(true);
		return results;
	}

	@PostConstruct
	public void createControls(Composite parent) {
		System.out.println(this.getClass().getSimpleName() + " @PostConstruct method called.");
	}
}
