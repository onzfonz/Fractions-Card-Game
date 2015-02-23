package logparser;

import java.util.ArrayList;
import java.util.HashMap;

/* Class: LogGame
 * --------------
 * This is the class that will in a way store all of the information in the last four tables
 * for the fractions game.  
 * Each object will be an instantiation of one game.
 * Because of the log game will be storing a few things, from all of the tables, as well as a way to look
 * up the particular log, that we are looking for.
 */

public class GameStateMachine {
	ArrayList<LogState> stateLogs;
	ArrayList<LogPair> pairLogs;
	int[] pairIds;
	int gid;
	LogState lastLogState;
	boolean shouldApplyIceCream;
	HashMap<String, LogCard> cardMap;
				
	public GameStateMachine(int game, int[] pids, ArrayList<LogPair> pLogs, HashMap<String, LogCard> cMap) {
		gid = game;
		pairLogs = pLogs;
		pairIds = pids;
		cardMap = cMap;
		stateLogs = new ArrayList<LogState>();
		lastLogState = new LogState(game, cardMap);
		assembleStateLogs();
	}
	
	public void assembleStateLogs() {
		for(int i = 0; i < pairLogs.size(); i++) {
			LogPair lp = pairLogs.get(i);
			if(shouldBeApplied(lp)) {
				applyLogToCurrentState(lp);
			}else if(chipDrawn(lp)) {
				ArrayList<Boolean> chips = subsequentChipsDrawn(pairLogs, i);
				if(kidsRanToIceCreamTruck(chips)) {
					applyLogToCurrentState(lp);				
				}
				i += chips.size()-1;
			}
		}
	}
	
	private ArrayList<Boolean> subsequentChipsDrawn(ArrayList<LogPair> pairLogs, int index) {
		ArrayList<Boolean> chips = new ArrayList<Boolean>();
		LogPair lp = pairLogs.get(index);
		while(chipDrawn(lp) && index < pairLogs.size()) {
			chips.add(Boolean.parseBoolean(lp.getContent()));
			index++;
			if(index >= pairLogs.size()) {
				System.out.println("chip at the end");
				break;
			}
			lp = pairLogs.get(index);
		}
		return chips;
	}
	
	private boolean kidsRanToIceCreamTruck(ArrayList<Boolean> chips) {
		for(int i = 0; i < chips.size(); i++) {
			if(!chips.get(i)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean chipDrawn(LogPair lp) {
		return lp.getType().equals(DBUtils.VALID_MOVES[4]);
	}
	
	private boolean shouldBeApplied(LogPair lp) {
		String type = lp.getType();
		for(int i = 0; i < DBUtils.VALID_MOVES.length-1; i++) {
			if(type.equals(DBUtils.VALID_MOVES[i])) {
				return true;
			}
		}
		return false;
	}
	
	private void applyLogToCurrentState(LogPair lp) {
		LogState ls = calculateStateFromLog(lp);
		if(!lp.isRadio()){
			lastLogState = ls;
		}
		stateLogs.add(ls);
	}
	
	private LogState calculateStateFromLog(LogPair lp) {
		int pid = lp.getPairId();
		int index = figureOutArrayIndex(pid);
		return lastLogState.calculateLogStateFromPairAction(lp, index);
	}
	
	private int figureOutArrayIndex(int pairId) {
		if(pairId == pairIds[0]) {
			return 0;
		}
		assert(pairId == pairIds[1]);
		return 1;
	}
	
	//This is for the log that we do when we have to change it into that state.
	@Override
	public String toString() {
		return "";
	}
	
	public ArrayList<LogState> getStateLogs() {
		return stateLogs;
	}
}
