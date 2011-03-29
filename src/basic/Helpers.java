/* File: Helpers.java
 * ------------------
 * This is meant to be the constants file but for variables and methods.  Everything in here should be static for the most part.
 */
package basic;

import java.util.concurrent.Semaphore;

public class Helpers {
public static Semaphore cardDist = new Semaphore(1, true);
	
	public static void acquireSem(Semaphore sem) {
		boolean didIt = false;
		try{
			sem.acquire();
			didIt = true;
		}catch(InterruptedException e) {
			if(didIt) sem.release();
		}
	}
	
	public static void releaseSem(Semaphore sem) {
		sem.release();
	}
}
