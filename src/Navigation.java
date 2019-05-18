import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
/*
 * Author: Ivan Mykolenko
 * Date: 27.03.2017
 */
public class Navigation extends JPanel implements HyperlinkListener {
	private static final long serialVersionUID = 1L;
	private JPanel controlPanel = new JPanel();
	private JButton refreshButton, backButton, forwardButton, favouriteButton, homeButton;
	private JTextField urlTextField;
	private JEditorPane editorPane;
	private URL currentURL;
	private UserData userData;
	private List<URL> list;
	private int iterator;
	private boolean validationSuccess;
	private Document doc = new HTMLEditorKit().createDefaultDocument();
	
	public Navigation() { // Navigation constructor
		backButton = new JButton("\u21E6");
		backButton.setEnabled(true);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				back();
			}
		});
		refreshButton = new JButton("\u21BB");
		refreshButton.setToolTipText("Refresh page");
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			        editorPane.setDocument(doc);
					showPage(currentURL, false);	
			}
		});
		forwardButton = new JButton("\u21E8");
		forwardButton.setEnabled(true);
		forwardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				forward();
			}
		});
		favouriteButton = new JButton("\u2606");
		favouriteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addToBookmarks();
			}
		});
		homeButton = new JButton("\u2302");
		homeButton.setToolTipText("Go home");
		homeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goToHomePage();
			}
		});
		urlTextField = new JTextField();
		urlTextField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					validateURL(urlTextField.getText(), true);
				}
			}
		});
		setLayout(new BorderLayout());
		controlPanel.add(backButton);
		controlPanel.add(refreshButton);
		controlPanel.add(forwardButton);
		controlPanel.add(homeButton);
		add(urlTextField);
		add(favouriteButton, BorderLayout.EAST);
		add(controlPanel, BorderLayout.WEST);
		editorPane = new JEditorPane();
		editorPane.addHyperlinkListener(this);
		editorPane.setEditable(false);
		userData = new UserData();
		list = new LinkedList<URL>();
		iterator = -1;
		goToHomePage();
	}

	private void goToHomePage() { // This method navigates Ragnarock to the default homepage
		validateURL(userData.getValue("homepage"), true);
	}

	public void validateURL(String url, boolean showPage) { // validateURL method is used to check the validity of URLs entered by the user
		if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://")) { //Allow only http and https protocols
			showError("The requested URL is invalid!");
			validationSuccess = false;
		} else {
			URL newUrl = null;
			try {
				newUrl = new URL(url);
				validationSuccess = true;
			} catch (Exception e) {
				showError("Invalid URL");
				validationSuccess = false;
			}
			if (showPage) {
				showPage(newUrl, true);
			}
		}
	}

	private void showPage(URL url, boolean addToList) { //The method which passess URLs to the JEditorPane to be displayed as a document.
		try {
			if (addToList) { // If the condition is true the link will be added to back-forward navigation list.
				list.add(url);
				iterator++;
				if (iterator+1!= list.size()){
					for (int i = iterator+1; i < list.size(); i++  ){  //Support backward/forward navigation alike in popular web browsers
					list.remove(i); 
					}
				}
			}
			currentURL = new URL(url.toString());
			editorPane.setPage(url);
			urlTextField.setText(url.toString());
			refreshButtons();
			userData.addTo("history", url.toString()); // Saves user's browsing history
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getClass().getCanonicalName() == "java.net.UnknownHostException") {
				showError("Could not load the page, please check your internet connection.");
			} else {
				showError("Something went wrong...");
			}
		}
	}
	
	private void back() { // Web navigation
		iterator--; //At first version of the program, I was using java.util.ListIterator, however later on I've found a much simpler alternative.
		showPage(list.get(iterator), false);
	}

	private void forward() { // Web navigation
		iterator++;
		showPage(list.get(iterator), false);
	}

	private void addToBookmarks() { // If page is not yet in the bookmarks, it's will be added
		if (checkBookmarks()) {
			userData.removeElement("bookmarks", currentURL.toString());
			refreshButtons();
		} else {
			userData.addTo("bookmarks", currentURL.toString());
			refreshButtons();
		}
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent event) {  // Implemented method of the abstract HyperlinkListener class // Used for web navigation //Hyperlink recognition within webpages.
		if (event.getEventType() == HyperlinkEvent.EventType.ENTERED) {
			editorPane.setToolTipText(event.getDescription());
		} else if (event.getEventType() == HyperlinkEvent.EventType.EXITED) {
			editorPane.setToolTipText(null);
		} else if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			showPage(event.getURL(), true);
		}
	}

	public void showError(String errorMessage) { //Default error message template
		JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public JEditorPane getEditorPane() {
		return editorPane;
	}
	
	public UserData getUserData() {
		return userData;
	}

	private boolean checkBookmarks() { // Check whether current page displayed is not in the bookmarks yet
		return userData.deriveFrom("bookmarks").contains(currentURL.toString());
	}

	public boolean checkSuccess() { // Return whther page has passed validation successfuly
		return validationSuccess;
	}

	public void setSuccess(boolean validationSuccess) {
		this.validationSuccess = validationSuccess;
	}
	
	private void refreshButtons() { // Refreshing buttons and toolTip text
		backButton.setEnabled(iterator > 0 ? true : false);
		backButton.setToolTipText(backButton.isEnabled() ? "Go back" : null);
		forwardButton.setEnabled(iterator < (list.size()-1) ? true : false);
		forwardButton.setToolTipText(forwardButton.isEnabled() ? "Go forward" : null);
		favouriteButton.setText(checkBookmarks() ? "\u2605" : "\u2606");
		favouriteButton.setToolTipText(checkBookmarks() ? "Remove from bookmarks" : "Add to bookmarks");
	
	}
}
