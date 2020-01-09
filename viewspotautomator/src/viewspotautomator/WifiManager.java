package viewspotautomator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import se.vidstige.jadb.JadbException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WifiManager {
	private JTextField ssidSelector;
	private JTextField passwordSelector;
	private ArrayList<WifiNetwork> nets = new ArrayList<WifiNetwork>();
	private WifiNetwork selected;
	private Boolean fileAlready = false;

	@SuppressWarnings("rawtypes")
	public WifiManager(Device d) {

		try {
			File netList = new File("netList.csv");
			FileInputStream fis = new FileInputStream(netList);
			Scanner fileScan = new Scanner(fis);
			while (fileScan.hasNext()) {
				String next = fileScan.nextLine();
				String[] comb = next.split(",", 2); // WARNING: if the wifi network's SSID has a comma in
				fileAlready = true; // it, this will not work properly.

				if (comb.length == 2) {
					WifiNetwork n = new WifiNetwork(comb[0], comb[1]);
					nets.add(n);
				} else if (comb.length == 1) {
					nets.add(new WifiNetwork(comb[0]));
				}
			}
			fileScan.close();
			fis.close();

		} catch (IOException e) {

		}

		JFrame networkConnector = new JFrame("ViewSpot Automator");// Setup window
		networkConnector.setSize(549, 423);
		networkConnector.getContentPane().setLayout(null);

		JScrollPane netScrollPane = new JScrollPane();
		netScrollPane.setBounds(10, 32, 236, 341);
		networkConnector.getContentPane().add(netScrollPane);

		DefaultListModel<String> networks = new DefaultListModel<String>(); // Collect devices for list model
		if (fileAlready) {
			for (int x = 0; x < nets.size(); x++) {
				networks.addElement(nets.get(x).toString());
			}
		}

		@SuppressWarnings("unchecked")
		JList previousNetworkList = new JList(networks);
		netScrollPane.setViewportView(previousNetworkList);
		previousNetworkList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()) {
					if (previousNetworkList.getSelectedIndex() != -1) {
						selected = nets.get(previousNetworkList.getSelectedIndex());
					}
				}

			}
		});

		JLabel lblPrvNets = new JLabel("Previously Connected Networks");
		lblPrvNets.setBounds(10, 7, 155, 23);
		networkConnector.getContentPane().add(lblPrvNets);

		ssidSelector = new JTextField();
		ssidSelector.setBounds(256, 238, 203, 20);
		networkConnector.getContentPane().add(ssidSelector);
		ssidSelector.setColumns(10);

		passwordSelector = new JTextField();
		passwordSelector.setColumns(10);
		passwordSelector.setBounds(256, 281, 203, 20);
		networkConnector.getContentPane().add(passwordSelector);

		JLabel lblNewLabel = new JLabel("New Network");
		lblNewLabel.setBounds(256, 182, 168, 23);
		networkConnector.getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("SSID");
		lblNewLabel_1.setBounds(256, 216, 46, 14);
		networkConnector.getContentPane().add(lblNewLabel_1);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(256, 266, 46, 14);
		networkConnector.getContentPane().add(lblPassword);

		JButton doConnect = new JButton("Connect");
		doConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!ssidSelector.getText().isEmpty()) {
					WifiNetwork n = new WifiNetwork(ssidSelector.getText(), passwordSelector.getText());
					try {
						Boolean isAlreadySaved = false;
						if (fileAlready) {
							for (int x = 0; x < nets.size(); x++) {
								if (n.equals(nets.get(x))) {
									isAlreadySaved = true;
									break;
								}
							}
						}

						if (!isAlreadySaved) {
							FileOutputStream fos = new FileOutputStream("netlist.csv", true);
							PrintWriter pw = new PrintWriter(fos, true);
							pw.append("\n" + n.ssid + "," + n.password);
							nets.add(n);
							pw.close();
							fos.close();
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						TaskProcessor.connectToWifi(d, n);

					} catch (IOException | JadbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					try {
						TaskProcessor.connectToWifi(d, selected);
					} catch (IOException | JadbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				networks.clear();
				if (fileAlready) {
					for (int x = 0; x < nets.size(); x++) {
						networks.addElement(nets.get(x).toString());
					}
				}
				 previousNetworkList.revalidate();
			}
		});
		doConnect.setBounds(416, 340, 107, 33);
		networkConnector.getContentPane().add(doConnect);
		networkConnector.setVisible(true);
	}

}
