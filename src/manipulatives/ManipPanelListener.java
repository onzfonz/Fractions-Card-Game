package manipulatives;

import deck.DeckView;

public interface ManipPanelListener {
	public void repaint();
	public int getWidth();
	public int getHeight();
	public void fireAnimationDone(DeckView manipsDeck);
	public void tickPassedInTruckMover(DeckView manipsDeck, int count);
}
