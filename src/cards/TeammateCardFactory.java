package cards;

import java.util.ArrayList;
import java.util.Scanner;

import extras.StringUtils;

/* Usage in creating a card.
 * -------------------
 * The Pirates,The Pirates Have Joined Your Team,The-Pirates.png,2,3
 */
public class TeammateCardFactory {
	public static TeammateCard createCard(String s) {
		Scanner scanner = new Scanner(s);
		scanner.useDelimiter(",");
		String name = StringUtils.getNextStr(scanner);
		String desc = StringUtils.getNextStr(scanner);
		String image = StringUtils.getNextStr(scanner);
		int value = StringUtils.getNextInt(scanner);
		return createCard(image, name, desc, value);
	}
	
	public static TeammateCard createCard(String cImg, String name, String desc, int value) {
		return new TeammateCard(cImg, name, desc, value);
	}
	
	protected static ArrayList<TeammateCard> createCards(String img, String name, String desc, int value, int numCards) {
		ArrayList<TeammateCard> cards = new ArrayList<TeammateCard>();
		for(int i = 0; i < numCards; i++) {
			cards.add(new TeammateCard(img, name, desc, value));
		}
		return cards;
	}
}
