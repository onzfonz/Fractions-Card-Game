package extras;

import java.util.*;

public class RandomSequence {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		System.out.println("High strand");
		for(int i = 0; i < 10; i++) {
			list.add("1");
		}
		for(int i = 0; i < 10; i++) {
			list.add("2");
		}
		RandomGenerator rgen = RandomGenerator.getInstance();
		while(list.size() > 0) {
			int randInt = rgen.nextInt(0, list.size()-1);
			System.out.print(list.remove(randInt) + ", ");
		}
	}

}
