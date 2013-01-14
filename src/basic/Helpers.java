/* File: Helpers.java
 * ------------------
 * This is meant to be the constants file but for variables and methods.  Everything in here should be static for the most part.
 */
package basic;

import java.util.concurrent.Semaphore;

import extras.Debug;

public class Helpers {
public static Semaphore cardDist = new Semaphore(1, true);
public static Semaphore systemReady = new Semaphore(1, true);
public static boolean isReady = true;
	
	public static void acquireSem(Semaphore sem, boolean drain) {
		boolean didIt = false;
		try{
			sem.acquire();
			if(drain) {
				sem.drainPermits();
			}
			didIt = true;
		}catch(InterruptedException e) {
			Debug.println("Thread interrupted");
			if(didIt) sem.release();
		}
	}
	
	public static void releaseSem(Semaphore sem) {
		sem.release();
	}
	
	public static void acquireCardDist() {
		acquireSem(cardDist, false);
	}
	
	public static void releaseCardDist() {
		releaseSem(cardDist);
	}
	
	public static void acquireSysReady(String message) {
		Debug.println("acquiring sysready from: " + message);
		acquireSem(systemReady, true);
		Debug.println("acquired sysready from: " + message);
	}
	
	public static void releaseSysReady(String message) {
		Debug.println("released sysready from: " + message);
		releaseSem(systemReady);
	}
}
