package manipulatives;

import deck.DeckView;

public interface ManPanelListener {
	public void enableControls();
	public void windowFinished();
	public void fireDenomExplained();
	public void fireNumerExplained();
	public void firePplExplained();
	public void fireExplainDone();
}