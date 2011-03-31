package deck;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import basic.Constants;

import cards.TeammateCard;
import cards.TrickCard;
import extras.StringUtils;

/*
 * TricksDeck.java
 * ---------------
 * This might have the hard coded tricks cards available as a deck.
 * This file will have all the representation of the cards as we see fit.
 */
public class TricksDeck extends Deck {
	public static void main(String[] args) {
		System.out.println("creatingTricksDeck");
		TricksDeck teammates = new TricksDeck(Constants.FNAME_TRICK_DECK);
		System.out.println(teammates.drawTopCard().getName());
	}
	
	public TricksDeck(String aFileName) {
		BufferedReader bf = null;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream is = cl.getResourceAsStream(aFileName);
		bf = new BufferedReader(new InputStreamReader(is));
		cards = new ArrayList<TeammateCard>();
		processCards(bf);
		createBackup();
	}
	
	private void processCards(BufferedReader bf) {
		try {
			String l = bf.readLine(); //throw away first line.
			l = bf.readLine();
			while (l != null && !l.equals("")){
				processOneType(l);
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
	protected void processOneType(String aLine){
		//use a second Scanner to parse the content of each line 
		Scanner scanner = new Scanner(aLine);
		scanner.useDelimiter(",");
		String cardType = StringUtils.getNextStr(scanner);
		String cImg = StringUtils.getNextStr(scanner);
		int numer = StringUtils.getNextInt(scanner);
		int denom = StringUtils.getNextInt(scanner);
		int quant = StringUtils.getNextInt(scanner);
		createCards(cImg, cardType, numer, denom, quant);
		
	}
	
	protected void createCards(String img, String cardType, int num, int den, int numCards) {
		for(int i = 0; i < numCards; i++) {
			cards.add(new TrickCard(img, num, den, cardType));
		}
	}
}
