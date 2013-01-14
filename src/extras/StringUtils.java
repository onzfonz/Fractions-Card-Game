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
		if((leftChar == null || rightChar == null || s == null) || ((!s.contains(leftChar) || !s.contains(rightChar)))) {
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
	
	public static String convertToSentenceCase(String line) {
		ArrayList<String> words = breakStrings(line, " ");
		String newStr = "";
		int i;
		for(i = 0; i < words.size() - 1; i++) {
			String w = words.get(i);
			String corrected = convertWordToSentenceCase(w);
			newStr = corrected + " ";
		}
		return newStr + convertWordToSentenceCase(words.get(i)); 
	}
	
	private static String convertWordToSentenceCase(String word) {
		String str = word.toLowerCase();
		char firstChar = str.charAt(0);
		return Character.toUpperCase(firstChar) + str.substring(1);
	}
}
