import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
/*
 * Author: Ivan Mykolenko
 * Date: 27.03.2017
 */
public class Main {

	public static void main(String args[]) {
		final Dimension MAX_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension MIN_SIZE = new Dimension(650, 400);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("Ragnarock");
				Navigation navigation = new Navigation();
				Menu menu = new Menu();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setMaximumSize(MAX_SIZE);
				frame.setMinimumSize(MIN_SIZE);
				frame.setJMenuBar(menu);
				frame.add(navigation, BorderLayout.NORTH);
				JScrollPane scrollPane = new JScrollPane(navigation.getEditorPane());
				JPanel userViewPort = new JPanel(new CardLayout());
				userViewPort.add(scrollPane, "WebBrowser");
				frame.add(userViewPort);
				frame.setVisible(true);
				menu.setNaviagtion(navigation);
				menu.setUserData(navigation.getUserData());
				menu.setUserViewPort(userViewPort);
				navigation.getUserData().setNavigation(navigation);
			}
		});
	}
}