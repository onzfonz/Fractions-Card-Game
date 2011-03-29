package network;

import pebblebag.IceWindowListener;
import pebblebag.PebbleListener;

public interface NetDelegate {
	public void sendCommand(String cmd, String args);
	public void sendShakedCommand(String cmd, String args, PebbleListener l);
}
