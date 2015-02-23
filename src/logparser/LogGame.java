package logparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

import extras.StringUtils;

/* Class: LogGame
 * --------------
 * This is the class that will in a way store all of the information in the last four tables
 * for the fractions game.  
 * Each object will be an instantiation of one game.
 * Because of the log game will be storing a few things, from all of the tables, as well as a way to look
 * up the particular log, that we are looking for.
 * 
 * 
 * One more thing I need is a way to go from the name combos to the pair id;
 */

public class LogGame implements SQLType {
	private ArrayList<String> userList;
	private HashMap<String, String> uidMap;
	private HashMap<String, ArrayList<String>> pairOrder;
	private HashMap<String, String> pairsToPids;
	private String[] gamePairNames;
	private int[] pairIds;
	private String startTime, endTime;
	private int gameId;
	private ArrayList<String> allLogs;
	private ArrayList<LogPair> pairLogs;	
	private ArrayList<LogUser> userLogs;
	private ArrayList<LogUserMove> userMoveLogs;
	private ArrayList<LogState> stateLogs;
	private HashMap<String, Stack<LogQaId>> userLogStacks;
	private HashMap<String, LogCard> cardMap;
	private LogQaId lastQaId;
	private GameStateMachine gsm;
	
	public LogGame(String[] participants, String[] pids, int gid, ArrayList<String> pairList, HashMap<String, String> map, HashMap<String, LogCard> cMap) {
		uidMap = map;
		cardMap = cMap;
		pairLogs = new ArrayList<LogPair>();
		userLogs = new ArrayList<LogUser>();
		userMoveLogs = new ArrayList<LogUserMove>();
		stateLogs = new ArrayList<LogState>();
		allLogs = new ArrayList<String>();
		
		pairsToPids = DBUtils.generatePairsToPids(participants, pids, pairList);
		gamePairNames = Arrays.copyOf(participants, participants.length);
		copyParticipants(participants);
		pairIds = new int[pids.length];
		gameId = gid;
		for(int i = 0; i < pairIds.length; i++) {
			pairIds[i] = Integer.parseInt(pids[i]);
		}
		setUpMaps();
	}
	
	private void setUpMaps() {
//		System.out.println(toSQLString());
		pairOrder = new HashMap<String, ArrayList<String>>();
		for(int i = 0; i < pairIds.length; i++) {
			pairOrder.put(pairIds[i]+"", new ArrayList<String>());
		}
		userLogStacks = new HashMap<String, Stack<LogQaId>>();
		for(String user: userList) {
			userLogStacks.put(user, new Stack<LogQaId>());
		}
	}
	
	private void copyParticipants(String[] participants) {
		userList = new ArrayList<String>();
		for(int i = 0; i < participants.length; i++) {
			if(participants[i] != null && !participants[i].equalsIgnoreCase("null")) {
				userList.add(participants[i]);
			}
		}
	}
	
	public void populateFieldsFromFullLog() {
		setGameTimes();
		divideLogs();
		populatePairsWithQaIds();
		gsm = new GameStateMachine(gameId, pairIds, pairLogs, cardMap);
	}
	
	private void populatePairsWithQaIds() {
		for(int i = 0; i < pairLogs.size(); i++) {
			LogPair lp = pairLogs.get(i);
			ArrayList<String> qids = lp.getShadowQIDs();
			if(qids != null) {
				setQaIdForLpHand(lp, qids.get(0), i);
				if(qids.size() > 1) { //set it in the tricks hand
					LogPair prevLp = findPreviousTrickLP(i, lp.getPairId());
					setQaIdForLpHand(prevLp, qids.get(1), i);
				}
			}
		}
	}
	
	private void setQaIdForLpHand(LogPair lp, String qid, int i) {
		String qaid = findCorrespondingQaid(qid, i, lp.getPairId());
		lp.setQaId(qaid);
	}
	
	private String findCorrespondingQaid(String qid, int index, int pid) {
		for(int i = index+1; i < pairLogs.size(); i++) {
			LogPair lp = pairLogs.get(i);
			if(lp.isQAType()) {
				if(lp.getPairId() == pid && qid.equals(lp.getContent())) {
					return lp.getQaId();
				}
			}
		}
		return null;
	}
	
	private LogPair findPreviousTrickLP(int index, int pid) {
		for(int i = index-1; i >= 0; i--) {
			LogPair lp = pairLogs.get(i);
			if(lp.getType().equals("myHand") && lp.getPairId() == pid) {
				return lp;
			}
		}
		return null;
	}
	
	private void divideLogs() {
		System.out.println("Dividing logs");
		for(String l:allLogs) {
//			System.out.println("line - " + l);
			ArrayList<String> parsed = DBUtils.getParsedRegex(l);
			boolean isUserLog = DBUtils.decideIfUserLog(parsed);
			if(isUserLog) {
				addToUserLog(parsed);
			}else{
				addToPairLog(parsed);
			}
		}
	}
	
	private void addToUserLog(ArrayList<String> parsed) {
		ArrayList<String> parsedUser = DBUtils.getParsedUserLog(parsed);
		String userName = StringUtils.convertToSentenceCase(DBUtils.getParsedNameString(parsedUser.get(0)));
		Stack<LogQaId> stack = userLogStacks.get(userName);
		/* Added due to the reversals */
		if(stack == null) {
			userName = MiningUtils.reversePairNames(userName);
			stack = userLogStacks.get(userName);
		}
		String uid = uidMap.get(userName);
		String userName2 = DBUtils.getParsedNameString(parsed.get(1));
		String pids = pairsToPids.get(userName2);
		if(pids == null) {
			userName2 = MiningUtils.reversePairNames(userName2);
			pids = pairsToPids.get(userName2);
		}
		ArrayList<String> acts = pairOrder.get(pids);
		String userAction = parsedUser.get(1);
		if(userAction.equals("Started")) {
			startQuestionLog(parsed, parsedUser, stack, uid, acts);
		}else if(userAction.equals("Done")) {
			endQuestionLog(parsed, parsedUser, stack, uid, acts);
		}else if(userAction.equals("attempting")) {
//			createUserAttemptLog(parsed, parsedUser, stack, uid, acts);
		}else if(userAction.equals("suggesting")) {
//			createUserSuggestLog(parsed, parsedUser, stack, uid, acts);
		}else if(userAction.equals("Opened")) {
//			createUserOpenedLog(parsed, parsedUser, stack, uid, acts);
		}else if(userAction.equals("Closed")) {
//			createUserClosedLog(parsed, parsedUser, stack, uid, acts);
		}else if(userAction.equals("Help")) {
			helpedQuestionLog(parsed, parsedUser, stack, uid, acts);
		}else if(userAction.equals("Oops")) {
			triedQuestionLog(parsed, parsedUser, stack, uid, acts);
		}else{
			System.out.println(userAction + " Didn't fall into anything");
		}
	}
	
	private void addToPairLog(ArrayList<String> parsed) {
		String userName = DBUtils.getParsedNameString(parsed.get(1));
		String pid = pairsToPids.get(userName);
		if(pid == null) {
			userName = MiningUtils.reversePairNames(userName);
			pid = pairsToPids.get(userName);
		}
		ArrayList<String> acts = pairOrder.get(pid);
		//we are ignoring ready to start and 
		String type = parsed.get(2);
		String content = null;
		if(parsed.size() >= 6 && parsed.get(4) != null) {
			type = parsed.get(4);
			content = parsed.get(5);
		}
		createPairLog(pid, acts, null, parsed, type, content);
	}
	
	private void createPairLog(String pid, ArrayList<String> acts, String qaid, ArrayList<String> parsed, String type, String content) {
		LogPair lp = new LogPair(gameId, Integer.parseInt(pid), acts.size(), qaid, DBUtils.getTimestampFromRegex(parsed), type, content);
		pairLogs.add(lp);
		acts.add(parsed.get(2));
//		System.out.println(lp);
	}
	
	private void startQuestionLog(ArrayList<String> parsed, ArrayList<String> parsedUser, Stack<LogQaId> stack, String uid, ArrayList<String> acts) {
		LogPair previousPairLog = pairLogs.get(pairLogs.size()-1);
		if(previousPairLog.shouldBeRevised() && lastQaId != null) {
//			System.out.println("REVISED? " + previousPairLog);
			previousPairLog.setQaId(""+lastQaId.getQaId());
		}
		String qid = DBUtils.getFractKeyFromQuestion(parsedUser.get(2));
		LogQaId qaid = new LogQaId(qid);
		stack.push(qaid);
		addQStartToPairLog(parsed, parsedUser, qaid, acts);
		addUserToLog(uid, qaid, "QStarted", null, null, null, null, null, acts, parsed, 1);
	}
	
	private void addQStartToPairLog(ArrayList<String> parsed, ArrayList<String> parsedUser, LogQaId qaid, ArrayList<String> acts) {
		int pid = findPairId(DBUtils.getParsedNameString(parsedUser.get(0)));
		createPairLog(""+pid, acts, ""+qaid.getQaId(), parsed, DBUtils.QATYPE, qaid.getQId());
	}
	
	private int findPairId(String name) {
		for(int i = 0; i < gamePairNames.length; i++) {
			if(name.equals(gamePairNames[i])) {
				return pairIds[i/2];
			}
		}
		return -1;
	}
	
	private void helpedQuestionLog(ArrayList<String> parsed, ArrayList<String> parsedUser, Stack<LogQaId> stack, String uid, ArrayList<String> acts) {
		doQuestionLog(parsed, parsedUser, stack, uid, acts, DBUtils.LOG_QSHOWN);
	}
	
	private void triedQuestionLog(ArrayList<String> parsed, ArrayList<String> parsedUser, Stack<LogQaId> stack, String uid, ArrayList<String> acts) {
		doQuestionLog(parsed, parsedUser, stack, uid, acts, DBUtils.LOG_QTRIED);
	}
	
	private void doQuestionLog(ArrayList<String> parsed, ArrayList<String> parsedUser, Stack<LogQaId> stack, String uid, ArrayList<String> acts, String action) {
		LogQaId qaid = null;
		if(stack.isEmpty()) {
			System.out.println("Stack was empty and on QTried or QShown, so skipping " + parsed.get(0));
			return;
		}
		qaid = stack.peek();
		DBUtils.removeExtraTriesFromLog(parsedUser);
		ArrayList<String> parts = DBUtils.getParsedUserTriedLog(parsedUser);
		DBUtils.removeMarksFromHelpLog(parts);
		addUserToLog(uid, qaid, action, parts.get(0), parts.get(1), parts.get(2), parts.get(3), null, acts, parsed, qaid.incrementOrder());		
	}
	
	private void endQuestionLog(ArrayList<String> parsed, ArrayList<String> parsedUser, Stack<LogQaId> stack, String uid, ArrayList<String> acts) {
		if(stack == null || stack.size() == 0) {
//			System.out.println("stack was null, maybe a duplicate end call?" + parsed);
			return;
		}
		LogQaId qaid = null;
		qaid = stack.pop();
		ArrayList<String> parts = DBUtils.getParsedUserDoneLog(parsedUser);
		addUserToLog(uid, qaid, DBUtils.LOG_QDONE, null, parts.get(1), parts.get(2), parts.get(3), parts.get(4), acts, parsed, qaid.incrementOrder());
		lastQaId = qaid;
	}
	
	private void endQuestionLogOld(ArrayList<String> parsed, ArrayList<String> parsedUser, Stack<LogQaId> stack, String uid, ArrayList<String> acts) {
		LogQaId qaid = null;
		qaid = stack.pop();
		
		ArrayList<String> parts = DBUtils.getParsedUserDoneLog(parsedUser);
		int order = 2;
		if(parts.get(0) != null) {
			String[] nums = parts.get(0).split(",");
			for(int i = 0; i < nums.length; i++) {
				addUserToLog(uid, qaid, DBUtils.LOG_QTRIED, nums[i], parts.get(1), parts.get(2), parts.get(3), parts.get(4), acts, parsed, order++);
			}
		}
		if(parts.get(4).equals("y")) {
			addUserToLog(uid, qaid, DBUtils.LOG_QSHOWN, null, parts.get(1), parts.get(2), parts.get(3), parts.get(4), acts, parsed, order++);
		}
		addUserToLog(uid, qaid, DBUtils.LOG_QDONE, null, parts.get(1), parts.get(2), parts.get(3), parts.get(4), acts, parsed, order);
		lastQaId = qaid;
	}
	
	private void addUserToLog(String uid, LogQaId qaid, String type, String attempt, String ppl, String lines, String marks, String shown, ArrayList<String> acts, ArrayList<String> parsed, int uorder) {
		LogUser lu = new LogUser(Integer.parseInt(uid), qaid.getQaId(), uorder, Integer.parseInt(qaid.getQId()), DBUtils.getTimestampFromRegex(parsed), type, attempt, ppl, lines, marks, shown, parsed.get(2));
		acts.add(parsed.get(2));
		userLogs.add(lu);
//		System.out.println(lu);
	}
	
	private void setGameTimes() {
		startTime = DBUtils.getTimestampFromRegex(allLogs.get(0));
		endTime = DBUtils.getTimestampFromRegex(allLogs.get(allLogs.size()-1));
	}
	
	public void setGameID(int gid) {
		gameId = gid;
	}
	
	public void addToPairLog(LogPair lp) {
		pairLogs.add(lp);
	}
	
	public void addToUserLog(LogUser lu) {
		userLogs.add(lu);
	}
	
	public void addToStateLog(LogState ls) {
		stateLogs.add(ls);
	}
	
	public void addToFullLog(String line) {
		allLogs.add(line);
	}
	
	public String[] getPair1() {
		return new String[]{gamePairNames[0], gamePairNames[1]};
	}

	public String[] getPair2() {
		return new String[]{gamePairNames[2], gamePairNames[3]};
	}

	public int getGameId() {
		return gameId;
	}

	public ArrayList<LogPair> getPairLogs() {
		return pairLogs;
	}

	public ArrayList<LogUser> getUserLogs() {
		return userLogs;
	}

	public ArrayList<LogState> getStateLogs() {
		return gsm.getStateLogs();
	}
	
	public void printAllLogs() {
		System.out.println("LogGame: " + Arrays.asList(gamePairNames));
		System.out.println("GameID: " + Arrays.asList(pairIds));
		DBUtils.printArrayList(allLogs);
	}
	
	public String[] toSQLString() {
		String[] strs = new String[5];
		strs[0] = gameId + "";
		strs[1] = pairIds[0] + "";
		strs[2] = pairIds[1] + "";
		strs[3] = startTime;
		strs[4] = endTime;
		return strs;
	}
}
