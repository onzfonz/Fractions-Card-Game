package extras;

import manipulatives.ManCardPanel;
import pebblebag.IceCreamTruckView;
import tugstory.TugPanel;
import basic.Player;
import combo.ChooseComboCardPanel;

public interface PanelListener {
	public void updateLabels(Player p);
	public void enableControls();
	public void disableControls();
	public void opponentTurn();
	public boolean possibleComputerTurn();
	public void doComputerTurn();
	public void manViewCreated(ManCardPanel mPanel);
	public boolean manViewDone(ManCardPanel mPanel);
	public void toggleManipView();
	public void comboViewCreated(ChooseComboCardPanel cPanel);
	public boolean comboViewDone(ChooseComboCardPanel cPanel);
	public void iceViewCreated(IceCreamTruckView iPanel);
	public boolean iceViewDone(IceCreamTruckView iPanel);
	public void tugViewCreated(TugPanel tPanel);
	public boolean tugViewDone(TugPanel tPanel);
	public void suggestDoneWithTurn();
}
