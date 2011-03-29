package extras;

import basic.Constants;

/* Class just has handy utilities to use */

public class Debug {
	public static void println(String s) {
		if(Constants.DEBUG_MODE) {
			System.out.println(s);
		}
	}
	
	public static void println(int n) {
		println(n + "");
	}
	
	public static void println(double d) {
		println(d + "");
	}
	
	public static void println(boolean f) {
		println(f + "");
	}
	
	public static void println(char c) {
		println(c + "");
	}
	
	public static void println(Object o) {
		println(o.toString());
	}
}
