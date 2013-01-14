package extras;

import java.util.List;

public class Utils {
	private static RandomGenerator rgen;
	
	static {
		rgen = RandomGenerator.getInstance();
	}
	
	public static void shuffle(List list) {
		for(int i = 0; i < list.size(); i++) {
			swapObjects(list, i, rgen.nextInt(0, list.size()-1));
		}
	}
	
	public static void swapObjects(List list, int pos1, int pos2) {
		Object c1 = list.get(pos1);
		Object c2 = list.get(pos2);
		list.add(pos2+1, c1);
		list.remove(pos2);
		list.add(pos1+1, c2);
		list.remove(pos1);		
	}
}
