package cards;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class CardGamePanelFrame extends JFrame {
	public CardGamePanelFrame() {
		add(new CardGamePanel(null));
		
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		new CardGamePanelFrame();
	}
}
