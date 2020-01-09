package viewspotautomator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import se.vidstige.jadb.*;
import se.vidstige.jadb.managers.Package;
import se.vidstige.jadb.managers.PackageManager;

/**
 * This class is responsible for running the tasks selected in Automator.
 * 
 * @author shill
 *
 */
public class TaskProcessor {

	public static String getAndroidVersion(JadbDevice d) throws IOException, JadbException {
		return convertStreamToString(d.execute("getprop ro.build.version.release", ""));
	}

	public static String getDeviceModel(JadbDevice d) throws IOException, JadbException {
		return convertStreamToString(d.execute("getprop ro.product.model", ""));
	}

	public static List<Package> listPackages(JadbDevice d) throws IOException, JadbException {
		PackageManager pm = new PackageManager(d);
		return (pm.getPackages());
	}

	public static String[] scanSmithMicroApps(JadbDevice d) throws IOException, JadbException {// Scans for ViewSpot
																								// apps
		String combinedData[] = { "unknown", "unknown", "unknown" };
		List<se.vidstige.jadb.managers.Package> installedApps = new ArrayList<se.vidstige.jadb.managers.Package>();
		installedApps = TaskProcessor.listPackages(d);
		for (int x = 0; x < installedApps.size(); x++) {
			// System.out.println(installedApps.get(x).toString());
			if (installedApps.get(x).toString().contains("com.smithmicro.viewspot.attmx.da")) {
				combinedData[0] = "ATT Mexico";
				combinedData[1] = "com.smithmicro.viewspot.attmx.da";
				combinedData[2] = getVersion(d, "com.smithmicro.viewspot.attmx.da");
				// System.out.println(v);
				break;
			} else if (installedApps.get(x).toString().contains("com.smithmicro.viewspot")) {
				combinedData[0] = "Generic ViewSpot";
				combinedData[1] = "com.smithmicro.viewspot";
				combinedData[2] = getVersion(d, "com.smithmicro.viewspot");
				break;
			} else if (installedApps.get(x).toString().contains("com.smithmicro.viewspot.orange.esp")) {
				combinedData[0] = "Orange";
				combinedData[1] = "com.smithmicro.viewspot.orange.esp";
				combinedData[2] = getVersion(d, "com.smithmicro.viewspot.orange.esp");
				break;
			}
			
		}

		return combinedData;

	}

	@SuppressWarnings("resource")
	public static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	public static String getVersion(JadbDevice d, String app) throws IOException, JadbException {// Get version of
																									// package on device
		String version = "error";

		Scanner scan = new Scanner(d.execute("dumpsys package " + app + "", ""));
		while (scan.hasNext()) {
			String currLine = scan.nextLine();
			if (currLine.contains("versionName=")) {
				version = currLine.replaceAll("[\\s|\\u00A0]+", "").replaceAll("versionName=", "");
				scan.close();
				break;
			}
		}

		return version;
	}

	public static void installApp(Device d) throws IOException, JadbException {// install app from file selector
		UpdatedPackageManager pm = new UpdatedPackageManager(d.getDevice());
		pm.install(FileSelector.pickFile());
	}

	public static void installApp(Device d, File f) throws IOException, JadbException {// install app with file name
																						// given
		UpdatedPackageManager pm = new UpdatedPackageManager(d.getDevice());
		pm.install(f);
	}

	public static void removeApp(Device d, Package p) throws IOException, JadbException {
		UpdatedPackageManager pm = new UpdatedPackageManager(d.getDevice());
		pm.uninstall(p);
	}

	public static void forceStopApp(Device d, Package p) throws IOException, JadbException {

		d.getDevice().executeShell("am force-stop " + p, "");
	}

	public static void clearDataApp(Device d, Package p) throws IOException, JadbException {

		d.getDevice().executeShell("pm clear " + p, "");
	}

	public static void launchApp(Device d, Package p) throws IOException, JadbException {
		d.getDevice().executeShell("monkey -p " + p + " -c android.intent.category.LAUNCHER 1", "");
	}

	public static String isSettingsBlockerOn(Device d) throws IOException, JadbException {// check if ViewSpot settings
																							// blocker is enabled
		String result = "Enabled";
		if (d.getAppName() != "unknown") {
			String reportingService = (d.getPackName() + "/com.customermobile.demo.ReportingService")
					.replaceAll("[\\s|\\u00A0]+", "");
			if (convertStreamToString(d.getDevice().execute("dumpsys activity services ", reportingService))
					.contains("(nothing)")) {
				result = "Disabled";
			}
		} else {
			result = "N/A";
		}
		return result;
	}

	public static void enableSettingsBlocker(Device d) throws IOException, JadbException { // enable ViewSpot settings
																							// blocker
		if (d.getBlockerStatus() == "Disabled") {
			d.getDevice().executeShell("pm grant " + d.getPackName() + " android.permission.PACKAGE_USAGE_STATS", "");
			d.getDevice().executeShell("appops set " + d.getPackName() + " android:get_usage_stats allow", "");
			d.getDevice().executeShell("settings put secure enabled_accessibility_services " + d.getPackName()
					+ "/com.customermobile.demo.ReportingService", "");
			d.updateDevice();
		}

	}

	public static void disableSettingsBlocker(Device d) throws IOException, JadbException {// disable ViewSpot settings

		// blocker

		if (d.getBlockerStatus() == "Enabled") {
			d.getDevice().executeShell("settings put secure enabled_accessibility_services 0 ",
					d.getPackName() + "/com.customermobile.demo.ReportingService");
			d.getDevice().executeShell("am force-stop " + d.getPackName() + "/com.customermobile.demo.ReportingService",
					"");
			d.updateDevice();
		}

	}

	public static void enableWifi(Device d) throws IOException, JadbException {
		d.getDevice().executeShell("svc wifi enable");
	}

	public static void disableWifi(Device d) throws IOException, JadbException {
		d.getDevice().executeShell("svc wifi disable");
	}

	public static void connectToWifi(Device d, String ssid, String pw)
			throws IOException, JadbException, InterruptedException {
		d.getDevice().executeShell("svc wifi enable", "");
		if (!TaskProcessor
				.convertStreamToString(d.getDevice().executeShell("pm list packages com.steinwurf.adbjoinwifi", ""))
				.contains("com.steinwurf.adbjoinwifi")) {
			File f = new File("adb-join-wifi.apk");
			TaskProcessor.installApp(d, f);
		}
		Thread kill = new Thread() {
			public void run() {
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					d.getDevice().executeShell("am force-stop com.steinwurf.adbjoinwifi", "");
				} catch (IOException | JadbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		kill.start();
		d.getDevice().executeShell("am start -n com.steinwurf.adbjoinwifi/.MainActivity -e ssid " + ssid
				+ " -e	password_type WPA -e password " + pw, "");
	}

	public static void connectToWifi(Device d, String ssid) throws IOException, JadbException, InterruptedException {
		d.getDevice().executeShell("svc wifi enable", "");
		if (!TaskProcessor
				.convertStreamToString(d.getDevice().executeShell("pm list packages com.steinwurf.adbjoinwifi", ""))
				.contains("com.steinwurf.adbjoinwifi")) {
			File f = new File("adb-join-wifi.apk");
			TaskProcessor.installApp(d, f);
		}
		Thread kill = new Thread() {
			public void run() {
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					d.getDevice().executeShell("am force-stop com.steinwurf.adbjoinwifi", "");
				} catch (IOException | JadbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		kill.start();
		d.getDevice().executeShell("am start -n com.steinwurf.adbjoinwifi/.MainActivity -e ssid " + ssid, "");
	}

	public static void refreshLogs(Device d) throws IOException, JadbException, InterruptedException {
		d.getDevice().executeShell("am start -n " + d.getPackName() + "/com.customermobile.demo.MainTab", "");
		d.getDevice().executeShell("input tap 400 400", "");
		d.getDevice().executeShell("input keyevent KEYCODE_HOME", "");
		d.getDevice().executeShell("am start -n " + d.getPackName() + "/com.customermobile.demo.MainTab", "");
	}

	public static void rebootDevice(Device d) throws IOException, JadbException {
		d.getDevice().executeShell("reboot -p", "");
	}

	public static String getChargeType(Device d) throws IOException, JadbException {
		String rawString = convertStreamToString(d.getDevice().execute("dumpsys battery | grep", "powered:"));
		String result = "error";
		if (rawString.contains("AC powered: true")) {
			result = "USB_Charging";
		} else if (rawString.contains("USB powered: true")) {
			result = "USB_Charging";
		} else if (rawString.contains("Wireless powered: true")) {
			result = "Wireless_Charging";
		} else if (rawString.contains("MOD powered: true")) {
			result = "MOD_Charging";
		}

		return result;
	}

	public static String tester(Device d) throws IOException, JadbException {

		String x = TaskProcessor.convertStreamToString(d.getDevice().executeShell("dumpsys battery | grep","temperature:"));

		return x;
	}

	public static String escapeMetaCharacters(String inputString) {
		final String[] metaCharacters = { "\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "<",
				">", "-", "&", "%" };

		for (int i = 0; i < metaCharacters.length; i++) {
			if (inputString.contains(metaCharacters[i])) {
				inputString = inputString.replace(metaCharacters[i], "\\" + metaCharacters[i]);
			}
		}
		return inputString;
	}

}
