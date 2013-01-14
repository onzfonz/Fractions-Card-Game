package combo;
/*
 * File: IceCreamTruckView
 * This will be the container window that will have the buttons
 * and such to help manipulate the pebble window.  It will also have
 * a pebble panel.
 */
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import basic.Constants;
import cards.CardView;
import cards.CardViewFactory;
import cards.TeammateCard;
import cards.TrickCard;
import deck.PlayDeck;

public class ChooseComboCardPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4850780253187289854L;
	private ComboPanel pPanel;
	
	public ChooseComboCardPanel(CardView cv) {
		setLayout(new BorderLayout());
		pPanel = new ComboPanel(cv, this);
	
		add(pPanel, BorderLayout.WEST);	
        
//		JPanel statusBox = new JPanel();
//		statusBox.setLayout(new FlowLayout());
		
		JLabel message = new JLabel(Constants.COMBO_MSG_SIDE);
		message.setFont(Constants.FONT_LARGE);
		
//		setPreferredSize(new Dimension(Constants.HUGE_CARD_WIDTH, Constants.HUGE_CARD_HEIGHT));
		
		add(message, BorderLayout.NORTH);

		setVisible(true);
	}
	
	public void addListener(ComboListener cl) {
		pPanel.addListener(cl);
	}
	
	public static void main(String[] args) {
		PlayDeck p1 = new PlayDeck(new TeammateCard("", "Johnson Family", "", 8));

		TrickCard halfStink = new TrickCard(Constants.HALF_FILENAME, 1, 2, "Stink");
		TrickCard qtrStink = new TrickCard(Constants.HALF_FILENAME, 1, 4, "Stink");
		TrickCard threeQtrStink = new TrickCard(Constants.HALF_FILENAME, 3, 4, "Stink");
		TrickCard twoThirdStink = new TrickCard(Constants.HALF_FILENAME, 2, 3, "Stink");
		TrickCard thirdStink = new TrickCard(Constants.HALF_FILENAME, 1, 3, "Stink");

		TrickCard halfAir = new TrickCard(Constants.HALF_AIR_FILENAME, 1, 2, "Air");
		TrickCard qtrAir = new TrickCard(Constants.HALF_AIR_FILENAME, 1, 4, "Air");
		TrickCard threeQtrAir = new TrickCard(Constants.HALF_AIR_FILENAME, 3, 4, "Air");
		TrickCard twoThirdAir = new TrickCard(Constants.HALF_AIR_FILENAME, 2, 3, "Air");
		TrickCard thirdAir = new TrickCard(Constants.HALF_AIR_FILENAME, 1, 3, "Air");
		TrickCard fifthAir = new TrickCard(Constants.HALF_AIR_FILENAME, 1, 5, "Air");

		TrickCard two2Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 2, 2, "Ice");
		TrickCard one2Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 1, 2, "Ice");
		TrickCard one9Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 1, 9, "Ice");
		TrickCard thirteen11Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 13, 11, "Ice");
		TrickCard one5Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 1, 5, "Ice");
		TrickCard five1Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 5, 1, "Ice");

		TrickCard radio = new TrickCard(Constants.RADIO_FILENAME, 1, 1, "Radio");
		//p1.addTrickCard(halfStink);
		TrickCard combo = new TrickCard(Constants.COMBO1_FILENAME, 1, 2, "combo");
		p1.addTrickCard(one2Ice);
		p1.addTrickCard(radio);
		CardView cView = CardViewFactory.createCard(combo);

		ChooseComboCardPanel pPanel = new ChooseComboCardPanel(cView);
		JFrame f = new JFrame();
		f.add(pPanel);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
	}
	
}
