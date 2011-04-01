package extras;

import java.util.*;

public class RandomSequence {

	/**
	 * @param args
	 */
	public static RandomGenerator rgen = RandomGenerator.getInstance();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//randomBinarySequence(10, 10);
		randomABPairings(15);
	}
	
	public static void randomABPairings(int numPairings) {
		for(int i = 0; i < numPairings; i++) {
			if(rgen.nextBoolean()) {
				System.out.println("AB");
			}else{
				System.out.println("BA");
			}
		}
	}
	
	public static void randomBinarySequence(int ones, int twos) {
		ArrayList<String> list = new ArrayList<String>();
		System.out.println("High strand");
		for(int i = 0; i < ones; i++) {
			list.add("1");
		}
		for(int i = 0; i < twos; i++) {
			list.add("2");
		}
		while(list.size() > 0) {
			int randInt = rgen.nextInt(0, list.size()-1);
			System.out.print(list.remove(randInt) + ", ");
		}
	}

}
