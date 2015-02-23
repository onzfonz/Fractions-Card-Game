package generalbar;

import cards.TrickCard;

public class RepresentationPanelFactory {
	public static RepPanel createRep(TrickCard c) {
		if(c.isCombo()) {
			return new ComboRepresentationPanel(c);
		}
		if(c.isRadio()) {
			return null;
		}
		return new RepresentationPanel(c);
	}
}
