package manipulatives;

import cards.CardView;
import deck.DeckView;

public class ManPanelUtils {
	/* Logic for determining whether or not this is a panel generated from a shadow player question, which would mean
	 * there is no trick card and the deck should only be 1 card (the shadow player card)
	 */
	public static boolean isShadowOnly(DeckView dv, CardView cp) {
		return dv != null && dv.getAllCards() != null && dv.getAllCards().size() <= 1 && cp == null;
	}

	
}
