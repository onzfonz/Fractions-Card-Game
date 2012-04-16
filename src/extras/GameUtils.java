package extras;

import java.util.ArrayList;
import java.util.Scanner;

public class GameUtils {
	public static String formQuestion(int num, int den, int value) {
		return "What is " + num + "/" + den + " of " + value + "?";
	}
	
	public static String extractQuestion(String line) {
		int startPos = extractNumeratorPos(line);
		int endPos = extractPplEndPos(line);
		return "What is " + line.substring(startPos, endPos) + "?";
	}
	
	public static String determineIfDecimal(String val) {
		if(val.indexOf(".") != -1) {
			return "1";
		}
		return "0";
	}
	
	public static int generateAnswer(String question) {
		int num = GameUtils.extractNumerator(question);
		int den = GameUtils.extractDenominator(question);
		int ppl = GameUtils.extractPeople(question);
		return solveEasyFraction(num, den, ppl);
	}

	//Does not take into account simplifying the fraction first
	//Can do this later if need be.
	public static int solveEasyFraction(int num, int den, int value) {
		if(value % den != 0) {
			return -1;
		}
		return((value / den) * num);
	}
	
	/* Assumes that there is only one forward slash and that the
	 * numbers needed are:  num/den of ppl?  Also assumes spaces after fraction, but can handle if question
	 * starts before without a space.
	 */
	public static int extractNumerator(String question) {
		int pos = question.indexOf("/");
		if(pos == -1) {
			return -1;
		}
		int spacePos = extractNumeratorPos(question);
		try {
			return Integer.parseInt(question.substring(spacePos, pos));
		}catch(NumberFormatException e) {
			System.out.println("ERRROR! question:" + question + ", spacePos:" + spacePos + ", pos:" + pos);
		}
		return -1;
	}
	
	public static int extractNumeratorPos(String question) {
		int pos = question.indexOf("/");
		return question.lastIndexOf(" ", pos) + 1;
	}
	
	public static int extractDenominator(String question) {
		int pos = question.indexOf("/");
		int spacePos = question.indexOf(" ", pos);
		if(pos == -1 || spacePos == -1) {
			return -1;
		}
		return Integer.parseInt(question.substring(pos+1, spacePos));
	}
	
	public static int extractPeople(String question) {
		int pos = extractPplEndPos(question);	
		int spacePos = question.lastIndexOf(" ", pos);
		if(spacePos == -1) {
			return -1;
		}
		return Integer.parseInt(question.substring(spacePos+1, pos));
	}
	
	public static int extractPplEndPos(String question) {
		int pos = question.indexOf("?");
		if(pos == -1) {
			String temp = "of ";
			pos = question.indexOf(temp);
			pos += temp.length();
			while(pos < question.length() && Character.isDigit(question.charAt(pos))) {
				pos++;
			}
		}
		return pos;
	}
	
	
}
