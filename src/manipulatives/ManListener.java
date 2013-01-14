package manipulatives;

import deck.DeckView;

public interface ManListener {
//	public void manipWindowDone(DeckView dv, ManFrame mf);
	public void manipPanelClosed(ManCardPanel mcp);
	public void toggleManipView();
}