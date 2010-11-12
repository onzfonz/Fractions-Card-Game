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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import cards.TeammateCard;

public class TeammatesDeck extends Deck {
	public static void main(String[] args) {
		System.out.println("creatingTeammatesDeck");
		TeammatesDeck teammates = new TeammatesDeck("Team1Deck.txt");
		System.out.println(teammates.drawTopCard().getName());
	}
	
	public TeammatesDeck(String aFileName) {
		BufferedReader bf = null;
		try {
			File aFile = new File(aFileName);
			bf = new BufferedReader(new FileReader(aFile));
		}catch (IOException e) {
			e.printStackTrace();
		}
		cards = new ArrayList<TeammateCard>();
		processLineByLine(bf);
		createBackup();
	}
	
	/* method for initializing deck by reading in deck contents from a file
	 * each line has number of certain cards to hold.
	 */
	private void processLineByLine(BufferedReader bf) {
		try {
			String l = bf.readLine();  //first use a Scanner to get each line
			l = bf.readLine();
			while (l != null && !l.equals("")){
				processLine(l);
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
	protected void processLine(String aLine){
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
			cards.add(new TeammateCard(img, name, desc, value));
		}
	}
	
	private String getNextStr(Scanner s) {
		return s.next().trim();
	}
	
	private int getNextInt(Scanner s) {
		String num = getNextStr(s);
		return Integer.parseInt(num);
	}
}
