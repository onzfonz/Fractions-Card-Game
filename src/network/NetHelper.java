package network;

import pebblebag.IceWindowListener;
import pebblebag.PebbleListener;
import basic.Constants;
import extras.Debug;

public class NetHelper {
	public static void sendNetworkCommand(String msg) {
		Debug.println(Constants.NETWORK_SEND_DEBUG + msg + ":");
	}
	
	public static void sentNetworkCommandAndArgs(String cmd, String args) {
		Debug.println(Constants.NETWORK_SEND_DEBUG + cmd + ":" + args);
	}
	
	public static void sendNetMove(NetDelegate n, int c, int d) {
		if(n != null) {
			n.sendCommand(Constants.CMD_MOVE, c + Constants.CMD_MOVE_SEP + d);
		}
	}
	
	public static void sendNetComboMove(NetDelegate n, int c, int d, int comboOption) {
		if(n != null) {
			n.sendCommand(Constants.CMD_MOVE, c + "c" + comboOption + Constants.CMD_MOVE_SEP + d);
		}
	}
	
	public static void sendNetChip(NetDelegate n, boolean c) {
		if(n != null) {
			n.sendCommand(Constants.CMD_CHIP, c + "");
		}
	}
	
	public static void sendNetShaked(NetDelegate n, PebbleListener l) {
		if(n != null) {
			n.sendShakedCommand(Constants.CMD_SHAKED, "", l);
		}
	}
	
	public static void sendNetCalc(NetDelegate n) {
		if(n != null) {
			n.sendCommand(Constants.CMD_CALC, "");
		}
	}
	
	public static void sendNetNewRound(NetDelegate n) {
		if(n != null) {
			n.sendCommand(Constants.CMD_NEW_ROUND, "");
		}
	}
	
	public static void sendNetRadios(NetDelegate n, int num) {
		if(n != null) {
			n.sendCommand(Constants.CMD_RADIOS, num + "");
		}
	}
	
	public static void sendNetNoMoves(NetDelegate n) {
		if(n != null) {
			n.sendCommand(Constants.CMD_NO_MOVES, "");
		}
	}
	
	public static void logMessage(NetDelegate n, String name, String logStr) {
		if(n != null) {
			n.sendCommand(Constants.CMD_LOG, name + " " + Constants.CMD_LOG_HYPHEN + " " + logStr);
		}
	}
}
