package extras;

import basic.Player;

public interface PanelListener {
	public void updateLabels(Player p);
	public void enableControls();
	public void disableControls();
	public void computerTurn();
}
