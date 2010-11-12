package pebblebag;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessageDisplay implements ActionListener {
	private PebblePanel pPanel;
	
	public MessageDisplay(PebblePanel pp) {
		pPanel = pp;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		pPanel.wrapUpBag();
	}
	
}
