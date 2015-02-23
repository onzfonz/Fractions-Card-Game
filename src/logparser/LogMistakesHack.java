package logparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import extras.GameUtils;

public class LogMistakesHack {
	public static void main(String[] args) {
		try {
			BufferedReader bf = new BufferedReader(new FileReader("Garfield2013Tree-2.log"));
			String line = bf.readLine();
			int count = 0;
			while(line != null) {
				String start = "Started ";
				int index = line.indexOf(start);
				if(index != -1) {
					line = line.substring(index + start.length());
					if(GameUtils.generateAnswer(line) == -1) {
						System.out.println(line);
						count++;
					}
				}
				line = bf.readLine();
			}
			System.out.println(count + " incorrect moves made");
			bf.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
