package deck;
/*
 * TeammatesDeck.java
 * ------------------
 * This file will do the initialization of the cards
 * for the deck.  Here we will hard code the Teammates Deck.
 * We will look for a file that has this
 * Name Description Image Value Quantity
 * Twins The Twins Have Joined Your Team recruits6.jpg 2 6
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import basic.Constants;
import cards.Card;
import cards.TeammateCard;

public class TeammatesDeck extends Deck {
	private TeammatesDeck alternateDeck;
	private int numDraws;
	
	public static void main(String[] args) {
		System.out.println("creatingTeammatesDeck");
		TeammatesDeck teammates = new TeammatesDeck(Constants.FNAME_TEAM_DECK);
		System.out.println(teammates.drawTopCard().getName());
	}
	
	public TeammatesDeck(String aFileName, String bFileName) {
		this(aFileName);
		alternateDeck = new TeammatesDeck(bFileName);
		alternateDeck.shuffle();
		numDraws = 0;
	}
	
	public TeammatesDeck(String aFileName) {
		cards = new ArrayList<TeammateCard>();
		setupDeck(aFileName, cards);
		createBackup();
	}
	
	public void setupDeck(String aFileName, ArrayList<TeammateCard> cardList) {
		BufferedReader bf = null;
		//File aFile = new File(aFileName);
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream is = cl.getResourceAsStream(aFileName);
		bf = new BufferedReader(new InputStreamReader(is));
		processLineByLine(bf, cardList);
	}
	
	/* method for initializing deck by reading in deck contents from a file
	 * each line has number of certain cards to hold.
	 */
	private void processLineByLine(BufferedReader bf, ArrayList<TeammateCard> cardList) {
		try {
			String l = bf.readLine();  //first use a Scanner to get each line
			l = bf.readLine();
			while (l != null && !l.equals("")){
				if(!l.equals(Constants.FILE_CONTENT_SPACING)) {
					processLine(l, cardList);
				}
				l = bf.readLine();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	/** 
	 * A method for processing lines in different ways.
	 * The way it will process is by parsing the line first, and then creating teammate cards
	 * based on the information on that line.
	 * This method depends on the field having 5 fields total (4 commas on each line)
	 * it will break if that doesn't work.
	 * 
	 */
	protected void processLine(String aLine, ArrayList<TeammateCard> cardList){
		//use a second Scanner to parse the content of each line 
		Scanner scanner = new Scanner(aLine);
		scanner.useDelimiter(",");
		String name = getNextStr(scanner);
		String desc = getNextStr(scanner);
		String img = getNextStr(scanner);
		int value = getNextInt(scanner);
//		valueStr.trim();
//		int value = Integer.parseInt(valueStr);
		int numCards = getNextInt(scanner);
		
		for(int i = 0; i < numCards; i++) {
			cardList.add(new TeammateCard(img, name, desc, value));
		}
	}
	
	@Override
	public Card drawTopCard() {
		Card myCard;
		if(numDraws < Constants.NUM_PLAYERS*Constants.TEAM_HAND_SIZE*Constants.NUM_ROUNDS_B4_SHADOW && alternateDeck != null) {
			myCard = alternateDeck.drawTopCard();
		}else{
			myCard = super.drawTopCard();
		}
		numDraws++;
		return myCard;
	}
	
	private String getNextStr(Scanner s) {
		return s.next().trim();
	}
	
	private int getNextInt(Scanner s) {
		String num = getNextStr(s);
		return Integer.parseInt(num);
	}
}
