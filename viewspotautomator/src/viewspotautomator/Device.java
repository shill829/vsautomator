package viewspotautomator;

import java.io.IOException;

import se.vidstige.jadb.*;

/**
 * The Device class is an extension of the JadbDevice class from the JADB
 * package. It has a few extra tags relating to ViewSpot in order to more
 * efficiently store information on each device.
 * 
 * @author Spencer Hill
 * @version 0.0.1
 *
 */
public class Device {
	private JadbDevice device;
	private String appName;
	private String packName;
	private String version;
	private String androidVersion;
	private String blockerStatus;
	private String modelNumber;

	public Device(JadbDevice d) throws IOException, JadbException {
		device = d;
		appName = TaskProcessor.scanSmithMicroApps(d)[0];
		packName = TaskProcessor.scanSmithMicroApps(d)[1];
		version = TaskProcessor.scanSmithMicroApps(d)[2];
		androidVersion = TaskProcessor.getAndroidVersion(d);
		blockerStatus = getBlockerStatus();
		modelNumber = TaskProcessor.getDeviceModel(d);

	}

	public void updateDevice() throws IOException, JadbException {
		// device=this.getDevice();
		// appName=TaskProcessor.scanSmithMicroApps(this.getDevice())[0];
		// packName=TaskProcessor.scanSmithMicroApps(this.getDevice())[1]; //These
		// disabled because they probably won't change- optimization.
		// version=TaskProcessor.scanSmithMicroApps(this.getDevice())[2];
//	androidVersion=TaskProcessor.getAndroidVersion(this.getDevice());
		blockerStatus = getBlockerStatus();
	}

	public String toString() {
		return (modelNumber + ": " + appName + " " + version + " SB: " + blockerStatus);
	}

	public String getPackName() {
		return packName;
	}

	public String getAppName() {
		return appName;
	}

	public String getVersion() {
		return version;
	}

	public JadbDevice getDevice() {
		return device;
	}

	public String getAndroidVersion() {
		return androidVersion;
	}

	public String getBlockerStatus() throws IOException, JadbException {
		return TaskProcessor.isSettingsBlockerOn(this);
	}
	
	public String getModelNumber(){
		return modelNumber;
	}

}
