package cards;

public class CardGameUtils {
	public static void pause(int millis) {
		try{
			Thread.sleep(millis);
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}
