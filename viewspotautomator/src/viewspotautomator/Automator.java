package viewspotautomator;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.io.IOException;
import se.vidstige.jadb.*;
//import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.awt.Composite;
import java.awt.event.*;
import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.PrintStream;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Font;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class Automator {
	private List<Device> devices = new ArrayList<Device>();// list of connected devices
	private List<Device> queueDevList = new ArrayList<Device>();// list of devices to send off to TaskProcessor
	// class
	private Device selected;// currently selected device
	private int toRem = 0;
	private List<Boolean> tasks = new ArrayList<Boolean>();// list of tasks to send off to TaskProcessor class

	public Automator() throws IOException, JadbException, InterruptedException {

		for (int x = 0; x < 5; x++) { // setup Tasks arraylist, will have to increase x if want more than 5 tasks.
			// Stupid way of doing it but I'll change it when i come up with something
			// better.
			tasks.add(false);
		}
		// index 0=list packages
		// index 1=install APK

		JFrame primary = new JFrame("ViewSpot Automator");// Setup window
		primary.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		primary.setSize(650, 600);
		primary.getContentPane().setLayout(null);

		try { // Scan for/update connected devices
			updateDevices();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JadbException e1) {
		}

		DefaultListModel<String> connDevs = new DefaultListModel<String>(); // Collect devices for list model
		for (int x = 0; x < devices.size(); x++) {
			connDevs.addElement(devices.get(x).toString());
		}

		JList<String> connectedDevices = new JList<String>(connDevs); // Connected device list
		connectedDevices.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()) {
					if (connectedDevices.getSelectedIndex() != -1) {
						selected = devices.get(connectedDevices.getSelectedIndex());
					}
				}

			}
		});
		connectedDevices.setBackground(Color.GRAY);
		connectedDevices.setFont(new Font("Tahoma", Font.PLAIN, 8));
		connectedDevices.setBorder(new LineBorder(new Color(0, 0, 0), 5));
		connectedDevices.setBounds(25, 62, 221, 328);
		primary.getContentPane().add(connectedDevices);

		JLabel connDevLbl = new JLabel("Connected Devices"); // Label for connected devices list
		connDevLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
		connDevLbl.setBounds(25, 29, 238, 35);
		primary.getContentPane().add(connDevLbl);

		DefaultListModel<String> queueDevs = new DefaultListModel<String>(); // list model for queue

		JList<String> queuedDevices = new JList<String>(queueDevs);
		queuedDevices.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (queuedDevices.getSelectedIndex() != -1) {
						toRem = queuedDevices.getSelectedIndex();
					}

				}
			}
		});
		queuedDevices.setFont(new Font("Tahoma", Font.PLAIN, 8));
		queuedDevices.setBorder(new LineBorder(new Color(0, 0, 0), 5));
		queuedDevices.setBackground(Color.GRAY);
		queuedDevices.setBounds(403, 62, 221, 328);
		primary.getContentPane().add(queuedDevices);

		JLabel queuedDevLbl = new JLabel("Queued Devices");
		queuedDevLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
		queuedDevLbl.setBounds(396, 29, 238, 35);
		primary.getContentPane().add(queuedDevLbl);

		JButton addToQueueButton = new JButton("Add -->"); // add item to queue
		addToQueueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean isPresent = false;

				if (!queueDevList.isEmpty()) {
					for (int x = 0; x < queueDevList.size(); x++) {
						if (selected.equals(queueDevList.get(x))) {
							isPresent = true;
						}
					}
				}

				if (!isPresent) {
					queueDevs.addElement(selected.toString());
					queueDevList.add(selected);

				}

			}
		});
		addToQueueButton.setBounds(281, 117, 89, 23);
		primary.getContentPane().add(addToQueueButton);

		JButton removeFromQueueButton = new JButton("Remove");// remove item from queue
		removeFromQueueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ((toRem >= 0) && (!queueDevList.isEmpty() && (queueDevList != null))) {
					queueDevs.removeElementAt(toRem);
					queueDevList.remove(toRem);
				}
			}
		});
		removeFromQueueButton.setBounds(281, 164, 89, 23);
		primary.getContentPane().add(removeFromQueueButton);

		JButton doEnableBlocker = new JButton("Blocker on");// settings blocker on
		doEnableBlocker.setFont(new Font("Tahoma", Font.PLAIN, 8));
		doEnableBlocker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int x = 0; x < queueDevList.size(); x++) {
					try {
						TaskProcessor.enableSettingsBlocker(queueDevList.get(x));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JadbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				connDevs.clear();
				try {
					updateDevices();
				} catch (IOException | JadbException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (int x = 0; x < devices.size(); x++) {
					connDevs.addElement(devices.get(x).toString());
				}
				connectedDevices.revalidate();
				queueDevList.clear();
				queueDevs.clear();
				queuedDevices.revalidate();
			}
		});
		doEnableBlocker.setBounds(403, 401, 89, 23);
		primary.getContentPane().add(doEnableBlocker);

		JButton doBlockerOff = new JButton(" Blocker off");// settings blocker off
		doBlockerOff.setFont(new Font("Tahoma", Font.PLAIN, 8));
		doBlockerOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int x = 0; x < queueDevList.size(); x++) {
					try {
						TaskProcessor.disableSettingsBlocker(queueDevList.get(x));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (JadbException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				connDevs.clear();
				try {
					updateDevices();
				} catch (IOException | JadbException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (int x = 0; x < devices.size(); x++) {
					connDevs.addElement(devices.get(x).toString());
				}
				connectedDevices.revalidate();
				queueDevList.clear();
				queueDevs.clear();
				queuedDevices.revalidate();

			}
		});
		doBlockerOff.setBounds(535, 401, 89, 23);
		primary.getContentPane().add(doBlockerOff);

		JButton doWifiOn = new JButton("Wifi On");
		doWifiOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int x = 0; x < queueDevList.size(); x++) {
					try {
						TaskProcessor.enableWifi(queueDevList.get(x));
					} catch (IOException | JadbException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}			
				queueDevList.clear();
				queueDevs.clear();
				queuedDevices.revalidate();
			}
			
		});
		doWifiOn.setFont(new Font("Tahoma", Font.PLAIN, 8));
		doWifiOn.setBounds(403, 424, 89, 23);
		primary.getContentPane().add(doWifiOn);

		JButton doWifiOff = new JButton("Wifi off");
		doWifiOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int x = 0; x < queueDevList.size(); x++) {
					try {
						TaskProcessor.disableWifi(queueDevList.get(x));
					} catch (IOException | JadbException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}			
				queueDevList.clear();
				queueDevs.clear();
				queuedDevices.revalidate();
			}
		});
		doWifiOff.setFont(new Font("Tahoma", Font.PLAIN, 8));
		doWifiOn.setFont(new Font("Tahoma", Font.PLAIN, 8));
		doWifiOff.setBounds(535, 423, 89, 23);
		primary.getContentPane().add(doWifiOff);

		JButton doZonaYoo = new JButton("ZonaYoo");
		doZonaYoo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int x = 0; x < queueDevList.size(); x++) {
					try {
						TaskProcessor.connectToWifi(queueDevList.get(x), "..ZonaYoo..");
					} catch (IOException | JadbException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				connDevs.clear();
				try {
					updateDevices();
				} catch (IOException | JadbException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (int x = 0; x < devices.size(); x++) {
					connDevs.addElement(devices.get(x).toString());
				}
				connectedDevices.revalidate();
				queueDevList.clear();
				queueDevs.clear();
				queuedDevices.revalidate();
			}
		});
		doZonaYoo.setFont(new Font("Tahoma", Font.PLAIN, 8));
		doZonaYoo.setBounds(403, 449, 89, 23);
		primary.getContentPane().add(doZonaYoo);

		JButton doSmsiTest = new JButton("Smsitestpsk");
		doSmsiTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int x = 0; x < queueDevList.size(); x++) {
					try {
						TaskProcessor.connectToWifi(queueDevList.get(x), "SMSI_TESTPSK", "pleaseletmein");
					} catch (IOException | JadbException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				connDevs.clear();
				try {
					updateDevices();
				} catch (IOException | JadbException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (int x = 0; x < devices.size(); x++) {
					connDevs.addElement(devices.get(x).toString());
				}
				connectedDevices.revalidate();
				queueDevList.clear();
				queueDevs.clear();
				queuedDevices.revalidate();

			}
		});
		doSmsiTest.setFont(new Font("Tahoma", Font.PLAIN, 8));
		doSmsiTest.setBounds(535, 448, 89, 23);
		primary.getContentPane().add(doSmsiTest);

		JButton doRebootDevice = new JButton("Reboot");
		doRebootDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int x = 0; x < queueDevList.size(); x++) {
					try {
						TaskProcessor.rebootDevice(queueDevList.get(x));
					} catch (IOException | JadbException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				connDevs.clear();
				try {
					updateDevices();
				} catch (IOException | JadbException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (int x = 0; x < devices.size(); x++) {
					connDevs.addElement(devices.get(x).toString());
				}
				connectedDevices.revalidate();
				queueDevList.clear();
				queueDevs.clear();
				queuedDevices.revalidate();

			}

		});
		doRebootDevice.setFont(new Font("Tahoma", Font.PLAIN, 8));
		doRebootDevice.setBounds(403, 497, 89, 23);
		primary.getContentPane().add(doRebootDevice);

		JButton doRefreshDevs = new JButton("Refresh");
		doRefreshDevs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connDevs.clear();
				try {
					updateDevices();
				} catch (IOException | JadbException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (int x = 0; x < devices.size(); x++) {
					connDevs.addElement(devices.get(x).toString());
				}
				connectedDevices.revalidate();
				queueDevList.clear();
				queueDevs.clear();
				queuedDevices.revalidate();
			}
		});
		doRefreshDevs.setFont(new Font("Tahoma", Font.PLAIN, 8));
		doRefreshDevs.setBounds(25, 401, 89, 23);
		primary.getContentPane().add(doRefreshDevs);

		JButton doLoggerTest = new JButton("Log Test");
		doLoggerTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int x = 0; x < queueDevList.size(); x++) {
					
						try {
							LogProcessor.logTester(queueDevList.get(x));
						} catch (IOException | JadbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}
				connDevs.clear();
				try {
					updateDevices();
				} catch (IOException | JadbException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (int x = 0; x < devices.size(); x++) {
					connDevs.addElement(devices.get(x).toString());
				}
				connectedDevices.revalidate();
				queueDevList.clear();
				queueDevs.clear();
				queuedDevices.revalidate();
			}
		});
		doLoggerTest.setFont(new Font("Tahoma", Font.PLAIN, 8));
		doLoggerTest.setBounds(403, 474, 89, 23);
		primary.getContentPane().add(doLoggerTest);
		
		JButton doInstallApp = new JButton("Install File");
		doInstallApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f=FileSelector.pickFile();
				for (int x = 0; x < queueDevList.size(); x++) {
					try {
						TaskProcessor.installApp(queueDevList.get(x), f);
					} catch (IOException | JadbException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				connDevs.clear();
				try {
					updateDevices();
				} catch (IOException | JadbException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (int x = 0; x < devices.size(); x++) {
					connDevs.addElement(devices.get(x).toString());
				}
				connectedDevices.revalidate();
				queueDevList.clear();
				queueDevs.clear();
				queuedDevices.revalidate();

			}
			
		});
		doInstallApp.setFont(new Font("Tahoma", Font.PLAIN, 8));
		doInstallApp.setBounds(535, 473, 89, 23);
		primary.getContentPane().add(doInstallApp);
		
		JButton doPackMan = new JButton("Package Manager");
		doPackMan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					new PackageListManager(selected);
				} catch (IOException | JadbException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		doPackMan.setFont(new Font("Tahoma", Font.PLAIN, 8));
		doPackMan.setBounds(140, 401, 106, 23);
		primary.getContentPane().add(doPackMan);

		primary.setVisible(true);// should be at end of method
	}

	public void updateDevices() throws IOException, JadbException, InterruptedException { // scans for devices and adds
		// to list
		// AdbServerLauncher launch=new AdbServerLauncher("C:\","adb");
		devices.clear();
		JadbConnection scanDevices = new JadbConnection();
		List<JadbDevice> toConvert = scanDevices.getDevices();
		for (int x = 0; x < toConvert.size(); x++) {
			devices.add(new Device(toConvert.get(x)));
			devices.get(x).getDevice().executeShell("settings put system screen_off_timeout 15000", "");

		}
		/*
		 * ArrayList<String>devs=LogProcessor.parseDevLogs(LogProcessor.getCurrLogs(
		 * devices.get(0))).get(1);
		 * ArrayList<String>names=LogProcessor.parseDevLogs(LogProcessor.getCurrLogs(
		 * devices.get(0))).get(0); for(int x=0;x<devs.size();x++) {
		 * System.out.print(x+"  "); System.out.print(names.get(x)+"   ");
		 * System.out.println(devs.get(x)); System.out.println(); }
		 * 
		 */
		
	}

	@PostConstruct
	public void createControls(Composite parent) {
		System.out.println(this.getClass().getSimpleName() + " @PostConstruct method called.");
	}
}
