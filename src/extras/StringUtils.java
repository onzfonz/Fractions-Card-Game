package extras;

import java.util.ArrayList;
import java.util.Scanner;

public class StringUtils {
	public static String getNextStr(Scanner s) {
		return s.next().trim();
	}
	
	public static int getNextInt(Scanner s) {
		String num = getNextStr(s);
		return Integer.parseInt(num);
	}
	
	public static String within(String s, String leftChar, String rightChar) {
		if((leftChar == null || rightChar == null || s == null) && ((!s.contains(leftChar) || !s.contains(rightChar)))) {
			return null;
		}
		return s.substring(s.indexOf(leftChar)+leftChar.length(), s.indexOf(rightChar));
	}
	
	public static ArrayList<String> breakStrings(String longS, String pattern) {
		Scanner s = new Scanner(longS);
		s.useDelimiter(pattern);
		ArrayList<String> strs = new ArrayList<String>();
		while(s.hasNext()) {
			strs.add(StringUtils.getNextStr(s));
		}
		return strs;
	}
}
