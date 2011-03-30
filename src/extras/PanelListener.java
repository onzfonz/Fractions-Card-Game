package extras;

import manipulatives.ManCardPanel;
import basic.Player;

public interface PanelListener {
	public void updateLabels(Player p);
	public void enableControls();
	public void disableControls();
	public void opponentTurn();
	public void manViewCreated(ManCardPanel mPanel);
	public boolean manViewDone(ManCardPanel mPanel);
	public void toggleManipView();
}
