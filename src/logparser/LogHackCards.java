package logparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;

public class LogHackCards {
	public static void main(String[] args) {
		try {
			TreeMap<String, Integer> fractsEntries = new TreeMap<String, Integer>();
			BufferedReader bf = new BufferedReader(new FileReader("../../Docs/Dissertation/Garfield/Logs/Otter - 2-4-2013.log"));
//			BufferedReader bf = new BufferedReader(new FileReader("../../Docs/Dissertation/Garfield/Logs/Hawes/Hawes2011-1.log"));
			String line = bf.readLine();
			int count = 0;
			while(line != null) {
				String start = "sending myHand:";
				int index = line.indexOf(start);
				if(index != -1) {
//					System.out.println(line);
					line = line.substring(index + start.length());
					line = line.substring(1, line.length()-1);
					String[] cards = line.split("_");
					for(int i = 0; i < cards.length; i++) {
						incrementMapEntry(fractsEntries, cards[i]);
					}
				}
				line = bf.readLine();
			}
			System.out.println("Done reading lines, here are the counts");
			System.out.println("------------------------------");
			printHashEntries(fractsEntries);
			bf.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void incrementMapEntry(TreeMap<String, Integer> entries, String key) {
		Integer num = entries.get(key);
		if(num==null) {
			num = new Integer(0);
		}
		num++;
		entries.put(key, num);
	}
	
	private static void printHashEntries(TreeMap<String, Integer> entries) {
		Set<String> keys = entries.keySet();
		for(String key:keys) {
			System.out.println(key + ", " + entries.get(key));
		}
	}
}
