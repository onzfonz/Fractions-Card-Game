package extras;

import manipulatives.ManCardPanel;
import basic.Player;

import combo.ComboFrame;

public interface PanelListener {
	public void updateLabels(Player p);
	public void enableControls();
	public void disableControls();
	public void opponentTurn();
	public void manViewCreated(ManCardPanel mPanel);
	public boolean manViewDone(ManCardPanel mPanel);
	public void toggleManipView();
	public void comboViewCreated(ComboFrame cPanel);
	public boolean comboViewDone(ComboFrame cPanel);
}
