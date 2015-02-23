package logparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import extras.GameUtils;

public class LogTester extends TestCase {
	private Pattern logCheck, logCheck2, logCheck3;
	
	public LogTester(String name) {
		super(name);
	}
	
	@Override
	protected void setUp() {
		//command for parsing certain commands
		logCheck2 = Pattern.compile("(.*)\\sINFO\\s(.+)\\ssen\\w+\\s\\.(\\w+)");
		
		//command for parsing other colon commands
		logCheck = Pattern.compile("(.*)\\sINFO\\s(.+)\\ssen\\w+\\s(\\w+):(.*)");
		logCheck = Pattern.compile("(.*)\\sINFO\\s(.+)\\ssen\\w+\\s(((\\w+):(.*))|(\\.(\\w+)))");
		
	}
	
	@Override
	protected void tearDown() {
		
	}
	
	public void testAssertions() {
		assertTrue(logCheck2.matcher("Fri Apr 29 13:05:35 PDT 2011 INFO Edgar & Emiliano sent .readytostart").matches());
		assertTrue(logCheck.matcher("Fri Apr 29 13:11:55 PDT 2011 INFO Edgar & Emiliano sent log:Edgar - Started What is 1/2 (.5)  of 8?").matches());
		System.out.println(System.getProperty("java.home"));
	}
	
	public void testLogExperiment() {
		assertTrue(DBUtils.decideIfUserLog("Fri Apr 29 13:11:55 PDT 2011 INFO Edgar & Emiliano sent log:Edgar - Started What is 1/2 (.5)  of 8?"));
		assertFalse(DBUtils.decideIfUserLog("Fri Apr 29 13:05:35 PDT 2011 INFO Edgar & Emiliano sent .readytostart"));
		assertFalse(DBUtils.decideIfUserLog("Fri Apr 29 13:13:02 PDT 2011 INFO Edgar & Emiliano sent move:1;1"));
		assertFalse(DBUtils.decideIfUserLog("Fri Apr 29 13:13:29 PDT 2011 INFO Edgar & Emiliano sent shaked:"));
		assertFalse(DBUtils.decideIfUserLog("Fri Apr 29 13:18:14 PDT 2011 INFO Edgar & Emiliano sent calc:"));
		assertFalse(DBUtils.decideIfUserLog("Fri Apr 29 13:18:32 PDT 2011 INFO Edgar & Emiliano sending myHand:{stink, Point-5.png, 1, 2_air, One-Quarter.png, 1, 4_stink, One-Third.png, 1, 3_air, Point-5.png, 1, 2}"));
		assertFalse(DBUtils.decideIfUserLog("Fri Apr 29 13:34:19 PDT 2011 INFO Edgar & Emiliano sent radios:0"));
		assertFalse(DBUtils.decideIfUserLog("Fri Apr 29 13:35:17 PDT 2011 INFO Edgar & Emiliano sent chip:false"));
		assertTrue(DBUtils.decideIfUserLog("Fri May 20 13:00:05 PDT 2011 INFO Dianna sent log:Dianna - Done tried 6| ppl 13, , | shownHow? n"));
		
		logCheck = Pattern.compile("(.*)\\sWARNING\\s(.+)\\s(quit)!");
		//printTestLineMatcher("Fri May 20 13:48:30 PDT 2011 WARNING DANIEL & diego quit!");
		assertFalse(DBUtils.decideIfUserLog("Fri May 20 13:48:30 PDT 2011 WARNING DANIEL & diego quit!"));
		
		
	}
	
	public void testUserDoneLog() {
		System.out.println("doing this");
		logCheck = Pattern.compile("tried\\s(.*)\\|\\s(.*),\\s(.*),\\s(.*)\\|\\sshownHow\\?\\s(.*)");
		printTestLineMatcher("tried | ppl 3, lines 3, | shownHow? y");
	}
	
	private void printTestLineMatcher(String s) {
		Matcher a = logCheck.matcher(s);
		assertTrue(a.find());
		printMatcher(a);
		System.out.println("----------------------");
	}
	
	private void printMatcher(Matcher temp) {
		for(int i = 0; i < temp.groupCount()+1; i++) {
			System.out.println(i + ": " + temp.group(i));
		}
	}
	
	public void testArrayCopies() {
		String[] arr = {"1", "2", "3", "4", "5"};
		String[] arr1 = DBUtils.copyAndRemove(arr, 2, 1);
		System.out.println(Arrays.asList(arr1));
		String[] arr2 = DBUtils.copyAndRemove(arr, 0, 1);
		System.out.println(Arrays.asList(arr2));
		String[] arr3 = DBUtils.copyAndRemove(arr, 4, 1);
		System.out.println(Arrays.asList(arr3));
		
		String[] newArr = {"1", "2"};
		newArr = DBUtils.copyAndRemove(newArr, 0, 1);
		System.out.println(Arrays.asList(newArr));
		newArr = DBUtils.copyAndRemove(newArr, 0, 1);
		System.out.println(Arrays.asList(newArr));
		newArr = DBUtils.copyAndRemove(newArr, 0, 1);
		System.out.println(Arrays.asList(newArr));
		newArr = DBUtils.copyAndRemove(newArr, -1, 1);
		System.out.println(Arrays.asList(newArr));
		int n = 5;
		assertEquals(n, 5);
	}
	
	public void testParsedNames() {
//		System.out.println("Testing parsedNames");
//		System.out.println(Arrays.asList(DBUtils.getParsedNames("Sadrac & Pedro")));
//		System.out.println(Arrays.asList(DBUtils.getParsedNames("Daniel and Diego")));
//		System.out.println(DBUtils.getTimestampFromRegex("Fri Apr 29 13:34:01 PDT 2011 INFO Julliana & Sadrac sent log:Sadrac - Started What is 3/4 of 2?"));
//		System.out.println(DBUtils.getTimestampFromRegex("Wed May 04 15:44:59 PDT 2011 INFO Julliana & Sadrac sent log:Sadrac - Started What is 3/4 of 2?"));
	}
	
	public void testFractParsing() {
//		System.out.println(DBUtils.convertFractToID(2, 4, 14, true));
//		System.out.println(DBUtils.convertFractToID(2, 4, 14, false));
//		System.out.println(DBUtils.convertFractToID(1, 3, 10, true));
//		System.out.println(DBUtils.convertFractToID(10, 10, 10, false));
//		System.out.println(DBUtils.convertFractToID(10, 1, 1, false));
//		System.out.println(DBUtils.convertFractToID(1, 10, 10, true));
//		System.out.println("-----------------------------");
//		System.out.println(Arrays.toString(DBUtils.parseIDToFracts(-204014)));
//		System.out.println(Arrays.toString(DBUtils.parseIDToFracts(204014)));
//		System.out.println(Arrays.toString(DBUtils.parseIDToFracts(-103010)));
//		System.out.println(Arrays.toString(DBUtils.parseIDToFracts(10010010)));
//		System.out.println(Arrays.toString(DBUtils.parseIDToFracts(100101)));	
//		System.out.println(Arrays.toString(DBUtils.parseIDToFracts(-1010010)));		
	}
	
	public void testPplParsing() {
		assertEquals(GameUtils.extractPeople("What is 1/4 of 8?"), 8);
		assertEquals(GameUtils.extractPeople("What is 1/4 of 8"), 8);
		assertEquals(GameUtils.extractPeople("What is 1/4 of 143?"), 143);
		assertEquals(GameUtils.extractPeople("What is 1/4 of 8023"), 8023);
		assertEquals(GameUtils.extractPeople("1/4 of 8,2038"), 8);
		assertEquals(GameUtils.extractPeople("1/4 of 834,2038"), 834);
	}
}
