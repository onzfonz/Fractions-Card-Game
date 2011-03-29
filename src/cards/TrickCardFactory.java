package cards;

import java.util.ArrayList;
import java.util.Scanner;

import extras.Debug;
import extras.StringUtils;

//Format for Trick Card should be
//stink, One-Half.png, 1, 2

public class TrickCardFactory {
	public static TrickCard createCard(String s) {
		Scanner scanner = new Scanner(s);
		Debug.println("Scanner line given is " + s);
		scanner.useDelimiter(",");
		String cardType = StringUtils.getNextStr(scanner);
		String cImg = StringUtils.getNextStr(scanner);
		int numer = StringUtils.getNextInt(scanner);
		int denom = StringUtils.getNextInt(scanner);
		return createCard(cImg, numer, denom, cardType);
	}
	
	public static TrickCard createCard(String img, int n, int d, String tType) {
		return new TrickCard(img, n, d, tType);
	}
	
	protected static ArrayList<TrickCard> createCards(String img, String cardType, int num, int den, int numCards) {
		ArrayList<TrickCard> cards = new ArrayList<TrickCard>();
		for(int i = 0; i < numCards; i++) {
			cards.add(new TrickCard(img, num, den, cardType));
		}
		return cards;
	}
}
