package test;

import java.util.ArrayList;
import java.util.TreeMap;

import basic.BasicDealer;
import basic.Constants;
import basic.Dealer;
import basic.SequentialDealer;
import basic.SmartDealer;
import cards.TrickCard;

public class DealerTester {
	public static final int NUM_TRIALS = 5;
	
	public static void main(String[] args) {
//		System.out.println("Testing the smart dealer\n------------------");
//		SmartDealer dealer1 = new SmartDealer();
//		BasicDealer dealer2 = new BasicDealer();
		SequentialDealer dealer3 = new SequentialDealer();
//		testDealer(dealer1);
//		System.out.println("\n now testing the basic dealer\n--------------");
//		testDealer(dealer2);
		System.out.println("\n now testing the sequential dealer\n-----------");
		testDealer(dealer3);
	}
	
	private static void testDealer(Dealer d) {
		TreeMap<String, Integer> cardFreqs = runSimulation(d, NUM_TRIALS);
//		printOutFreqs(cardFreqs, NUM_TRIALS);
		TreeMap<Integer, ArrayList<String>> reverseFreqs = makeReverseFreqs(cardFreqs, NUM_TRIALS);
		printOutFreqsBySize(reverseFreqs, NUM_TRIALS);		
	}
	
	private static TreeMap<String, Integer> runSimulation(Dealer d, int numTimes) {
		TreeMap<String, Integer>cardFrequencies = new TreeMap<String, Integer>();
		//Need to insert dealing with the trick cards here.
		for(int j = 0; j < NUM_TRIALS; j++) {
			for(int i = 0; i < 2*Constants.TEAM_HAND_SIZE; i++) {
				d.dealTeammateCard();
			}
			System.out.println("---------------");
			for(int i = 0; i < 2*Constants.TRICK_HAND_SIZE; i++) {
				TrickCard card = d.dealTrickCard();
				System.out.println(card);
				Integer num = cardFrequencies.get(card.toReadableStream());
				if(num == null) {
					num = Integer.valueOf(0);
				}
				num++;
				cardFrequencies.put(card.toReadableStream(), num);
			}
		}
		return cardFrequencies;
	}
	
	private static void printOutFreqs(TreeMap<String, Integer> cardFreqs, int numTimes) {
//		ArrayList<String> keys = new ArrayList<String>(cardFreqs.keySet());
//		String[] keysArr = (String []) keys.toArray();
//		SortedSet<String> keysSet = new SortedSet<String>(
//		Arrays.sort(keysArr);
		for(String tc:cardFreqs.keySet()) {
			Integer num = cardFreqs.get(tc);
			System.out.println(tc + " appeared " + num + " times");
		}
		System.out.println(numTimes*2*Constants.TRICK_HAND_SIZE + " cards were dealt.");
	}
	
	private static void printOutFreqsBySize(TreeMap<Integer, ArrayList<String>> reverseFreqs, int numTimes) {
//		ArrayList<String> keys = new ArrayList<String>(cardFreqs.keySet());
//		String[] keysArr = (String []) keys.toArray();
//		SortedSet<String> keysSet = new SortedSet<String>(
//		Arrays.sort(keysArr);
		numTimes *= 2 * Constants.TRICK_HAND_SIZE;
		for(Integer num:reverseFreqs.keySet()) {
			ArrayList<String> vals = reverseFreqs.get(num);
			System.out.println("These cards appeared " + num + " times: (" + Math.round((num/(double)numTimes)*100) + "%)" + vals);
		}		
	}
	
	private static TreeMap<Integer, ArrayList<String>> makeReverseFreqs(TreeMap<String, Integer> cardFreqs, int numTimes) {
		TreeMap<Integer, ArrayList<String>> reverse = new TreeMap<Integer, ArrayList<String>>();
		for(String key: cardFreqs.keySet()) {
			Integer num = cardFreqs.get(key);
			if(num == 4102) {
				System.out.println("Woohoo!" + key);
			}
			ArrayList<String> vals = reverse.get(num);
			if(vals == null) {
				vals = new ArrayList<String>();
			}
			vals.add(key);
			reverse.put(num, vals);
		}
		return reverse;
	}
}
