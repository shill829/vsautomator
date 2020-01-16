package viewspotautomator;

import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;
/**
 * 
 * UIautomator is used for interacting with device GUIs
 * @author shill
 *
 */
public class UIAutomator {
	private ArrayList<UIElement> entries = new ArrayList<UIElement>();
	private Device dev;
	private int enterX;
	private int enterY;

	public UIAutomator(Device d) throws FileNotFoundException {
		dev = d;
		File enterCords=new File("enterlocs.csv");
		Scanner scan=new Scanner(enterCords);
		while (scan.hasNext()) {
			String[]s=scan.nextLine().split(",",3);
			System.out.println(dev.getModelNumber().trim()+s[0].trim());
				if(s[0].trim().contains(dev.getModelNumber().trim())) {
					enterX=Integer.parseInt(s[1].trim());
					enterY=Integer.parseInt(s[2].trim());					
				}		
		}
		scan.close();
	}

	public void updateEntries() throws IOException, JadbException {

		entries.clear();
		dev.getDevice().executeShell("rm -f", "/sdcard/window_dump.xml");
		dev.getDevice().executeShell("uiautomator dump", "");
		File windowdump = new File("windowdump.xml");
		RemoteFile remote = new RemoteFile("/sdcard/window_dump.xml");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dev.getDevice().pull(remote, windowdump);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(windowdump,"utf-8").useDelimiter("<node");

		while (scan.hasNext()) {
			String throin=scan.next();
			entries.add(new UIElement(throin.replaceAll("</node>", "").replaceAll("</hierarchy>", "")
					.replaceAll("/>", "").replaceAll("<", "").replaceAll(">", "")));
		}
		if (entries.size() > 0) {
			entries.remove(0);
		}
		scan.close();
		for (int i = 0; i < entries.size(); i++) {
			System.out.println(entries.get(i));
		}

	}

	public void touchButton(String s) throws IOException, JadbException {
		updateEntries();
		for (int x = 0; x < entries.size(); x++) {
			if (entries.get(x).getText().contains(s)) {
				int xcord = entries.get(x).getXCenter();
				int ycord = entries.get(x).getYCenter();
				dev.getDevice().executeShell("input tap " + xcord, " " + ycord);
				break;
			}
		}
	}

	public void longTouchButton(String s) throws IOException, JadbException {
		updateEntries();
		for (int x = 0; x < entries.size(); x++) {
			if (entries.get(x).getText().contains(s)) {
				int xcord = entries.get(x).getXCenter();
				int ycord = entries.get(x).getYCenter();
				System.out.println(TaskProcessor.convertStreamToString(dev.getDevice()
						.executeShell("input swipe " + xcord + " " + ycord + " " + xcord + " " + ycord + " 2500")));
				break;
			}
		}
	}

	public void enterText(String s) throws IOException, JadbException {
		System.out.println(
				TaskProcessor.convertStreamToString(dev.getDevice().executeShell("input text ", "\"" + s + "\"")));
	}

	public void keyEvent(int s) throws IOException, JadbException {
		dev.getDevice().executeShell("input keyevent " + s, "");

	}
	
	public void pushEnter() throws IOException, JadbException {		
		dev.getDevice().executeShell("input tap " + enterX, " " + enterY);

	}

}
