package viewspotautomator;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;



public class UIAutomator {
private ArrayList<UIElement> entries=new ArrayList<UIElement>();	
private Device dev;


public UIAutomator(Device d){
	dev=d;	
}


private void updateEntries() throws IOException, JadbException {
	entries.clear();
	dev.getDevice().executeShell("rm -f","/sdcard/window_dump.xml");
	dev.getDevice().executeShell("uiautomator dump ","/sdcard/window_dump.xml");
	try {
		Thread.sleep(4500);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	RemoteFile remote=new RemoteFile("/sdcard/window_dump.xml");
	File windowdump=new File("windowdump.xml");
	dev.getDevice().pull(remote, windowdump);
	@SuppressWarnings("resource")
	Scanner scan=new Scanner(windowdump).useDelimiter("<node");

	while (scan.hasNext()){
		entries.add(new UIElement(scan.next().replaceAll("</node>","").replaceAll("</hierarchy>","").replaceAll("/>","").replaceAll("<","").replaceAll(">","")));
	}
	entries.remove(0);	
	scan.close();
	for(int i=0;i<entries.size();i++) {
		System.out.println(entries.get(i));
	}
	
}

public void touchButton(String s) throws IOException, JadbException {
	updateEntries();
	for(int x=0; x<entries.size();x++) {
		if(entries.get(x).getText().contains(s)) {
			int xcord=entries.get(x).getXCenter();
			int ycord=entries.get(x).getYCenter();			
			dev.getDevice().executeShell("input tap "+xcord," "+ycord);
			break;
		}
	}	
}


public void longTouchButton(String s) throws IOException, JadbException {
	updateEntries();
	for(int x=0; x<entries.size();x++) {
		if(entries.get(x).getText().contains(s)) {
			int xcord=entries.get(x).getXCenter();
			int ycord=entries.get(x).getYCenter();
			System.out.println(TaskProcessor.convertStreamToString(dev.getDevice().executeShell("input swipe "+xcord+" "+ycord+" "+xcord+" "+ycord+" 2500")));
			break;
		}
	}	
}
	
 
	
	
	
	
	
	
	
	
	

}
