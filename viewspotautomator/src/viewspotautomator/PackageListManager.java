package viewspotautomator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import se.vidstige.jadb.managers.Package;
import se.vidstige.jadb.JadbException;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class PackageListManager {
	private Package selected;
	private List<se.vidstige.jadb.managers.Package> packs1;
	private final JScrollPane scrollPane = new JScrollPane();

	/**
	 * @throws JadbException
	 * @throws IOException
	 * @wbp.parser.entryPoint
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PackageListManager(Device d) throws IOException, JadbException {
		packs1 = TaskProcessor.listPackages(d.getDevice());		
		ArrayList<Package> packlist = new ArrayList(packs1);
		PackComparator pc=new PackComparator();
		Collections.sort(packlist, pc);
		JFrame packManager = new JFrame("VSauto Package List " + d.toString());// Setup window
		packManager.setSize(532, 567);
		packManager.getContentPane().setLayout(null);

		DefaultListModel<String> packs = new DefaultListModel<String>(); // Collect devices for list model
		for (int x = 0; x < packlist.size(); x++) {
			packs.addElement(packlist.get(x).toString());
		}

		JLabel lblnstPacks = new JLabel("Installed Packages");
		lblnstPacks.setBounds(10, 1, 254, 25);
		packManager.getContentPane().add(lblnstPacks);
		scrollPane.setBounds(20, 22, 309, 476);
		packManager.getContentPane().add(scrollPane);

		JList<Package> list = new JList(packs);
		scrollPane.setViewportView(list);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()) {
					if (list.getSelectedIndex() != -1) {
						selected = packlist.get(list.getSelectedIndex());
					}
				}

			}
		});
		JButton doInstallApp = new JButton("Install Package");
		doInstallApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					TaskProcessor.installApp(d);
				} catch (IOException | JadbException t) {
					// TODO Auto-generated catch block
					t.printStackTrace();
				}

				try {
					packs1 = TaskProcessor.listPackages(d.getDevice());
				} catch (IOException | JadbException y) {
					// TODO Auto-generated catch block
					y.printStackTrace();
				}
				ArrayList<Package> packlist = new ArrayList(packs1);
				Collections.sort(packlist, pc);
				packs.clear();
				for (int x = 0; x < packlist.size(); x++) {
					packs.addElement(packlist.get(x).toString());
				}
				list.revalidate();

			}

		});
		doInstallApp.setBounds(339, 475, 126, 23);
		packManager.getContentPane().add(doInstallApp);

		JButton btnUninstall = new JButton("Uninstall");
		btnUninstall.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				try {
					TaskProcessor.removeApp(d, selected);
				} catch (IOException | JadbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					packs1 = TaskProcessor.listPackages(d.getDevice());
				} catch (IOException | JadbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ArrayList<Package> packlist = new ArrayList(packs1);
				Collections.sort(packlist, pc);
				packs.clear();
				for (int x = 0; x < packlist.size(); x++) {
					packs.addElement(packlist.get(x).toString());
				}
				list.revalidate();

			}
		});
		btnUninstall.setBounds(339, 446, 126, 23);
		packManager.getContentPane().add(btnUninstall);
		JButton btnForceStop = new JButton("Force Stop");
		btnForceStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					TaskProcessor.forceStopApp(d, selected);
				} catch (IOException | JadbException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnForceStop.setBounds(339, 418, 126, 23);
		packManager.getContentPane().add(btnForceStop);
		JButton btnLaunch = new JButton("Launch");
		btnLaunch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					TaskProcessor.launchApp(d, selected);
				} catch (IOException | JadbException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnLaunch.setBounds(339, 389, 126, 23);
		packManager.getContentPane().add(btnLaunch);
		
		JButton btnSelectViewspot = new JButton("Select ViewSpot");
		btnSelectViewspot.setFont(new Font("Tahoma", Font.PLAIN, 9));
		btnSelectViewspot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!d.getPackName().contains("unknown")) {
				Package p=new Package(d.getPackName());
				selected=p;
			}
			}
		});
		btnSelectViewspot.setBounds(339, 361, 126, 23);
		packManager.getContentPane().add(btnSelectViewspot);

		packManager.setVisible(true);

	}
}
