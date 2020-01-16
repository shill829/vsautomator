package viewspotautomator;

import java.util.Scanner;

public class UIElement {
	private String text = "";
	private String description = "";
	private boolean clickable = false;
	private boolean longclickable = false;
	private boolean selected = false;
	private int xcenter=0;
	private int ycenter=0;

	@SuppressWarnings("resource")
	public UIElement(String u) {
		String unparsed = u;
		System.out.println(unparsed);
		Boolean foundText = false;
		if (u.contains("clickable=\"true\"")) {
			clickable = true;
		}
		if (u.contains("long-clickable=\"true\"")) {
			longclickable = true;
		}
		if (u.contains("selected=\"true\"")) {
			selected = true;
		}
		Scanner scan = new Scanner(unparsed).useDelimiter("\" ");
		while (scan.hasNext()) {
			String test = scan.next();
			if (test.contains("text=") && !foundText) {
				String[] s = test.split("text=\"", 2);
				text = s[1].replaceAll("\"", "");
				foundText = true;
			} else if (test.contains("content-desc=")) {
				String[] s = test.split("content-desc=", 2);
				description = s[1].replaceAll("\"", "");
			} else if (test.contains("bounds=")) {
				String[] s = test.split("bounds=", 2);
				int[] vals = new int[] { 0, 0, 0, 0 };
				int i = 0;
				@SuppressWarnings("resource")
				Scanner numScan = new Scanner(s[1]).useDelimiter("\\D+");
				while (numScan.hasNextInt()) {
					vals[i] = numScan.nextInt();
					i++;
				}
				numScan.close();
				xcenter = (vals[0] + vals[2]) / 2;
				ycenter = (vals[1] + vals[3]) / 2;
			}

		}
		scan.close();

	}

	public String toString() {
		return ("Element: " + text + " (" + xcenter + "," + ycenter + ")");
	}

	public Boolean isClickable() {
		return clickable;
	}

	public Boolean islongClickable() {
		return longclickable;
	}

	public Boolean isselected() {
		return selected;
	}

	public int getXCenter() {
		return xcenter;
	}

	public int getYCenter() {
		return ycenter;
	}

	public String getText() {
		return text;
	}

	public String getDesc() {
		return description;
	}

}
