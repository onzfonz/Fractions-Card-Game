package basic;

/*
 * This is the interface that clases have to use if they want to get
 * updated about changes to the trickHand, or to one of the playDecks.
 * This would ensure that any views would redraw themselves.
 */
import deck.*;
import cards.*;

public interface PlayerListener {
	public void trickDeckChanged(Player p);
	public void trickCardAdded(Player p, CardView cv);
	public void trickCardRemoved(Player p, CardView cv);
	public void trickCardsThrownAway(Player p);
	public void playDeckChanged(Player p);
	public void playDeckAdded(Player p, DeckView pd);
	public void playDeckRemoved(Player p, DeckView pd);
	public void playDecksTossed(Player p);
	public void iceCreamPlayed(Player player, DeckView p, Player cvOwner);
	public void airFreshenerPlayed(Player player, DeckView p, Player cvOwner);
	public void radioPlayed(Player player, DeckView p, Player cvOwner);
	public void stinkBombPlayed(Player player, DeckView p, Player cvOwner);
	public void scoreUpdated(Player p);
	public void cardAnimationLaunched(PossibleMove cMove, Player computer, Player opponent, String message);
}
