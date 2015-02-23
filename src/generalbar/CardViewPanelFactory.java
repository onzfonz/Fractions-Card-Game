package generalbar;

import basic.Constants;
import cards.Card;
import cards.TeammateCard;
import cards.TrickCard;

public class CardViewPanelFactory {
	public static CardViewPanel createCard(Card c) {
		return createCard(c, true);
	}
	
	public static CardViewPanel createCard(Card c, boolean visible) {
		return createCard(c, Constants.MAX_CARD_WIDTH, Constants.MAX_CARD_HEIGHT, visible);
	}
	
	public static CardViewPanel createCard(Card c, int width, int height) {
		return createCard(c, width, height, true);
	}
	
	public static CardViewPanel createCard(Card c, int width, int height, boolean visible) {
		if(c instanceof TrickCard) {
			TrickCard tc = (TrickCard) c;
			if(tc.isCombo()) {
				//return new ComboCardView(c, width, height, visible);
			}else{
				if(!Constants.TEXT_AS_IMAGES) {
					//return new TrickCardView(c, width, height, visible);
				}
			}
		}else if(c instanceof TeammateCard) {
			TeammateCard tc = (TeammateCard) c;
			if(tc.isShadowPlayer()) {
				//return new ShadowCardView(c, width, height, visible);
			}
		}
		return new CardViewPanel(c, width, height, visible);
	}
}
