import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
/*
 * Author: Ivan Mykolenko
 * Date: 27.03.2017
 */
public class Menu extends JMenuBar {
	private static final long serialVersionUID = 4754984243405249915L;
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private JMenuBar menuBar;
	private JMenu menu, about, history, bookmarks;
	private JMenuItem settingsMenuItem, exitMenuItem;
	private DefaultListModel<String> listModel;
	private JList<String> list;
	private Navigation navigation;
	private UserData userData;
	private CardLayout layer;
	private JPanel userViewPort, buttonsPanel, panel;
	private JButton backButton, removeButton, saveButton, clearHistory, clearBookmarks;
	private JDialog settings;
	private JTextField urlField;
	private JLabel edit;
	private int viewMode; // 1 = WebBrowser, 2 = History, 3 = Bookmarks //Used as CardLayout layers

	public Menu() { //Menu pannel constructor
		menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		about = new JMenu("About");
		about.addMenuListener(new MenuListener() {
			public void menuSelected(MenuEvent e) {
				JOptionPane.showMessageDialog(null, "Ragnarock Web Browser 2017\nIvan Mykolenko\u00AE", "About",
						JOptionPane.INFORMATION_MESSAGE);
			}
			public void menuDeselected(MenuEvent e) {
			}
			public void menuCanceled(MenuEvent e) {
			}
		});
		history = new JMenu("History");
		history.addMenuListener(new MenuListener() {
			public void menuSelected(MenuEvent e) {
				createList("history");
				addBackButton();
				userViewPort.add(new JScrollPane(list), "History");
				layer.show(userViewPort, "History");
				menuBar.remove(removeButton);
				menuBar.revalidate();
				viewMode = 2;
			}
			public void menuDeselected(MenuEvent e) {
			}
			public void menuCanceled(MenuEvent e) {
			}
		});
		bookmarks = new JMenu("Bookmarks");
		bookmarks.addMenuListener(new MenuListener() {
			public void menuSelected(MenuEvent e) {
				createList("bookmarks");
				addBackButton();
				userViewPort.add(new JScrollPane(list), "Bookmarks");
				layer.show(userViewPort, "Bookmarks");
				menuBar.remove(removeButton);
				menuBar.revalidate();
				viewMode = 3;
			}
			public void menuDeselected(MenuEvent e) {
			}
			public void menuCanceled(MenuEvent e) {
			}
		});
		settingsMenuItem = new JMenuItem("Settings", KeyEvent.VK_X);
		settingsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createSettings();
				settings.setVisible(true);
			}
		});
		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		backButton = new JButton("Back");
		removeButton = new JButton("Remove");
		menu.add(settingsMenuItem);
		menu.add(exitMenuItem);
		menuBar.add(menu);
		menuBar.add(history);
		menuBar.add(bookmarks);
		menuBar.add(about);
		menuBar.add(Box.createHorizontalGlue()); // Changing backButton's alignment to right
		settings = new JDialog();
		add(menuBar);
		saveButton = new JButton("Save");
		clearHistory = new JButton("Clear History");
		clearBookmarks = new JButton("Clear Bookmarks");
		buttonsPanel = new JPanel();
		panel = new JPanel();
		urlField = new JTextField(15);
		edit = new JLabel("Edit Homepage:");
		viewMode = 1;
		listModel = new DefaultListModel<String>();
		list = new JList<String>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
	}

	private void createSettings() { //Settings menu
		settings.setTitle("Settings");
		settings.setSize(300, 160);
		settings.setResizable(false);
		settings.setLocation((int) (screenSize.getWidth() - settings.getWidth()) / 2,
				(int) (screenSize.getHeight() - settings.getHeight()) / 2);
		panel.add(edit);
		urlField.removeAll();
		urlField.setText(userData.getValue("homepage"));
		saveButton.setEnabled(true);
		navigation.setSuccess(false);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				navigation.validateURL(urlField.getText(), false);
				if (navigation.checkSuccess() == true) {
					userData.editConfigFile("homepage", urlField.getText());
					saveButton.setEnabled(false);
				}
			}
		});
		clearHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int dialogResult = JOptionPane.showOptionDialog(null,
						"Are you sure you want to remove browsing history?", "", JOptionPane.OK_CANCEL_OPTION, 1, null,
						null, JOptionPane.WARNING_MESSAGE);
				if (dialogResult == JOptionPane.OK_OPTION) {
					userData.clearHistory();
					listModel.removeAllElements();
					JOptionPane.showMessageDialog(null, "Browsing history cleared!");
				}
			}
		});
		clearBookmarks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int dialogResult = JOptionPane.showOptionDialog(null, "Are you sure you want to remove all bookmarks?",
						"", JOptionPane.OK_CANCEL_OPTION, 1, null, null, JOptionPane.INFORMATION_MESSAGE);
				if (dialogResult == JOptionPane.OK_OPTION) {
					userData.clearBookmarks();
					listModel.removeAllElements();
					JOptionPane.showMessageDialog(null, "All bookmarks have been removed!");
				}
			}
		});
		panel.add(urlField);
		panel.add(saveButton);
		buttonsPanel.add(clearHistory);
		buttonsPanel.add(clearBookmarks);
		settings.add(panel);
		settings.add(buttonsPanel, BorderLayout.SOUTH);
	}

	private void addBackButton() { //Button to return to the previous layer
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				layer.show(userViewPort, "WebBrowser");
				menuBar.remove(backButton);
				menuBar.remove(removeButton);
				menuBar.revalidate();
				viewMode = 3;
			}
		});
		menuBar.add(backButton);
		menuBar.revalidate();
	}

	private void addRemoveButton() { //Button to remove a link from the list
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuBar.remove(removeButton);
				menuBar.revalidate();
				if (viewMode == 2) {
					userData.removeByIndex("history", list.getSelectedIndex());
					listModel.removeElementAt(list.getSelectedIndex());
				} else if (viewMode == 3) {
					userData.removeByIndex("bookmarks", list.getSelectedIndex());
					listModel.removeElementAt(list.getSelectedIndex());
				}
			}
		});
		menuBar.add(removeButton);
		menuBar.revalidate();
	}

	private void createList(String dataType) { //Method to create a Jlist of links (History/Bookmarks)
		listModel.clear();
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 1) { //One click to select a link
					addRemoveButton();
				} else if (evt.getClickCount() == 2) { //Double click to open a link
					layer.show(userViewPort, "WebBrowser");
					menuBar.remove(backButton);
					menuBar.remove(removeButton);
					menuBar.revalidate();
					if (viewMode == 2) {
						navigation.validateURL(userData.deriveFrom("history").get(list.getSelectedIndex()), true);
					} else if (viewMode == 3) {
						navigation.validateURL(userData.deriveFrom("bookmarks").get(list.getSelectedIndex()), true);
					}			
					viewMode = 1;
				}
			}
		});
		if (userData.getValue(dataType).length() != 0) {
			for (String s : userData.deriveFrom(dataType)) {
				try {
					listModel.addElement(new URL(s).getHost());
				} catch (MalformedURLException e) {
					e.printStackTrace();
					navigation.showError("Something went wrong!");
				}
			}
		}
	}
	
	public void setUserViewPort(JPanel userViewPort) {
		this.userViewPort = userViewPort;
		layer = (CardLayout) (userViewPort.getLayout());
	}

	public void setNaviagtion(Navigation navigation) {
		this.navigation = navigation;
	}

	public void setUserData(UserData userData) {
		this.userData = userData;
	}
}
