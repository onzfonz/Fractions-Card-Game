package test;
/*
 * File: PlayDeckTest.java
 * -----------------------
 * This is a Junit testing file for the PlayDeck class.
 * Anytime changes are made to the PlayDeck class we should make sure that this class gets run
 * to make sure that things don't become broken.
 */

import junit.framework.TestCase;
import basic.Constants;
import cards.TeammateCard;
import cards.TrickCard;
import deck.PlayDeck;


public class PlayDeckTest extends TestCase {
	private PlayDeck p1;
	private PlayDeck p2;
	private PlayDeck p3;
	private PlayDeck p4;
	private PlayDeck p5;
	private PlayDeck p6;
	private PlayDeck p7;
	
	
	private TrickCard halfStink;
	private TrickCard qtrStink;
	private TrickCard threeQtrStink;
	private TrickCard twoThirdStink;
	private TrickCard thirdStink;
	
	private TrickCard halfAir;
	private TrickCard qtrAir;
	private TrickCard threeQtrAir;
	private TrickCard twoThirdAir;
	private TrickCard thirdAir;
	private TrickCard fifthAir;
	
	private TrickCard two2Ice;
	private TrickCard one2Ice;
	private TrickCard one9Ice;
	private TrickCard thirteen11Ice;
	private TrickCard one5Ice;
	
	
	public PlayDeckTest(String name) {
		super(name);
		/*Constants c = */new Constants();
		p1 = new PlayDeck(new TeammateCard("", "Johnson Family", "", 8));
		p2 = new PlayDeck(new TeammateCard("", "Music Geeks", "", 6));
		p3 = new PlayDeck(new TeammateCard("", "Basketball Team", "", 12));
		p4 = new PlayDeck(new TeammateCard("", "Basketball Team", "", 12));
		p5 = new PlayDeck(new TeammateCard("", "Basketball Team", "", 12));
		p6 = new PlayDeck(new TeammateCard("", "1st Graders", "", 4));
		p7 = new PlayDeck(new TeammateCard("", "Johnson Family", "", 8));
		
		halfStink = new TrickCard(Constants.HALF_FILENAME, 1, 2, "Stink");
		qtrStink = new TrickCard(Constants.HALF_FILENAME, 1, 4, "Stink");
		threeQtrStink = new TrickCard(Constants.HALF_FILENAME, 3, 4, "Stink");
		twoThirdStink = new TrickCard(Constants.HALF_FILENAME, 2, 3, "Stink");
		thirdStink = new TrickCard(Constants.HALF_FILENAME, 1, 3, "Stink");
		
		halfAir = new TrickCard(Constants.HALF_AIR_FILENAME, 1, 2, "Air");
		qtrAir = new TrickCard(Constants.HALF_AIR_FILENAME, 1, 4, "Air");
		threeQtrAir = new TrickCard(Constants.HALF_AIR_FILENAME, 3, 4, "Air");
		twoThirdAir = new TrickCard(Constants.HALF_AIR_FILENAME, 2, 3, "Air");
		thirdAir = new TrickCard(Constants.HALF_AIR_FILENAME, 1, 3, "Air");
		fifthAir = new TrickCard(Constants.HALF_AIR_FILENAME, 1, 5, "Air");
		
		two2Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 2, 2, "Ice");
		one2Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 1, 2, "Ice");
		one9Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 1, 9, "Ice");
		thirteen11Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 13, 11, "Ice");
		one5Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 1, 5, "Ice");
	}
	
	protected void setupStinks() {
		p1.addTrickCard(halfStink);
		p2.addTrickCard(qtrStink);
		p3.addTrickCard(qtrStink);
		p3.addTrickCard(twoThirdStink);
		p4.addTrickCard(threeQtrStink);
		p5.addTrickCard(thirdStink);
		p6.addTrickCard(qtrStink);
	}
	
	protected void setupAirs() {
		p1.addTrickCard(qtrAir);
		p2.addTrickCard(twoThirdAir);
		p3.addTrickCard(threeQtrAir);
		p4.addTrickCard(fifthAir);
		p4.addTrickCard(halfAir);
		p5.addTrickCard(halfAir);
	}
	
	private void setupIces() {
		p1.addTrickCard(one2Ice);
		p2.addTrickCard(one5Ice);
		p3.addTrickCard(one2Ice);
		p3.addTrickCard(one2Ice);
		p4.addTrickCard(one9Ice);
		p5.addTrickCard(thirteen11Ice);
		p6.addTrickCard(two2Ice);
	}
	
	public static void oneTimeSetup() {
	}
	
	public void testStink1() {
		boolean added = p1.addTrickCard(halfStink);
		assertTrue(added);
		assertEquals(p1.numLeftAfterStink(), 4);
	}
	
	public void testStink2() {
		boolean added = p2.addTrickCard(qtrStink);
		assertFalse(added);
		assertEquals(p2.numLeftAfterStink(), 6);
	}
	
	public void testStink3() {
		boolean added = p3.addTrickCard(qtrStink);
		boolean added2 = p3.addTrickCard(twoThirdStink);
		boolean added3 = p4.addTrickCard(threeQtrStink);
		assertTrue(added);
		assertTrue(added2);
		assertTrue(added3);
		assertEquals(p3.numLeftAfterStink(), 3);
		assertEquals(p4.numLeftAfterStink(), 3);
	}
	
	public void testAirs() {
		setupStinks();
		boolean added = p1.addTrickCard(qtrAir);
		boolean added2 = p2.addTrickCard(twoThirdAir);
		boolean added3 = p3.addTrickCard(threeQtrAir);
		boolean added4 = p4.addTrickCard(fifthAir);
		assertTrue(added);
		assertFalse(added2);
		assertTrue(added3);
		assertFalse(added4);
	}
	
	public void testCalculations() {
		setupStinks();
		setupAirs();
		int numLeft = p1.calculateStinksAndAirs();
		int numLeft2 = p2.calculateStinksAndAirs();
		int numLeft3 = p3.calculateStinksAndAirs();
		int numLeft4 = p4.calculateStinksAndAirs();
		int numLeft5 = p5.calculateStinksAndAirs();
		assertEquals(numLeft, 6);
		assertEquals(numLeft2, 6);
		assertEquals(numLeft3, 12);
		assertEquals(numLeft4, 9);
		assertEquals(numLeft5, 12);
	}
	
	public void testHypos() {
		int n = PlayDeck.applyHypoNumbers(9, 1, 9);
		assertEquals(n, 8);
	}
	
	public void testIceBasic() {
		setupIces();
		int num = p1.calculatePotentialScore();
		int num2 = p2.calculatePotentialScore();
		int num3 = p3.calculatePotentialScore();
		int num4 = p4.calculatePotentialScore();
		int num5 = p5.calculatePotentialScore();
		assertEquals(num, 5);
		assertEquals(num2, 5);
		assertEquals(num3, 5);
		assertEquals(num4, 11);
		assertEquals(num5, 6);
	}
	
	public void testIce() {
		setupStinks();
		setupAirs();
		setupIces();
		int num = p1.calculatePotentialScore();
		int num2 = p2.calculatePotentialScore();
		int num3 = p3.calculatePotentialScore();
		int num4 = p4.calculatePotentialScore();
		int num5 = p5.calculatePotentialScore();
		assertEquals(num, 4);
		assertEquals(num2, 5);
		assertEquals(num3, 5);
		assertEquals(num4, 8);
		assertEquals(num5, 6);
	}
	
	public void testp6IceStinkCombo() {
		setupStinks();
		setupIces();
		boolean n = p6.addTrickCard(qtrStink);
		assertFalse(n);
		assertEquals(p6.calculatePotentialScore(), 2);
	}
	
	public void testMultipleStinksAndAirs() {
		setupStinks();
		boolean a = p6.addTrickCard(qtrAir);
		assertTrue(a);
		assertEquals(p6.calculatePotentialScore(), 4);
		a = p6.addTrickCard(qtrStink);
		assertTrue(a);
		assertEquals(p6.calculatePotentialScore(), 3);
		a = p6.addTrickCard(qtrStink);
		assertFalse(a);
		assertEquals(p6.calculatePotentialScore(), 3);
	}
	
	public void testSeparate1() {
		p7.addTrickCard(halfStink);
		int []test = new int[2];
		test = p7.calculateStinksAndAirsSeparately();
		assertEquals(test[0], 4);
		assertEquals(test[1], 0);
		p7.addTrickCard(threeQtrAir);
		test = p7.calculateStinksAndAirsSeparately();
		assertEquals(test[0], 0);
		assertEquals(test[1], 4);
		p7.addTrickCard(halfStink);
		test = p7.calculateStinksAndAirsSeparately();
		assertEquals(test[0], 4);
		assertEquals(test[1], 0);
		p7.addTrickCard(threeQtrAir);
		test = p7.calculateStinksAndAirsSeparately();
		assertEquals(test[0], 0);
		assertEquals(test[1], 4);
		p7.addTrickCard(halfStink);
		test = p7.calculateStinksAndAirsSeparately();
		assertEquals(test[0], 4);
		assertEquals(test[1], 0);
	}
	
	public void testSeparate2() {
		p7.addTrickCard(halfStink);
		int []test = new int[2];
		test = p7.calculateStinksAndAirsSeparately();
		assertEquals(test[0], 4);
		assertEquals(test[1], 0);
		p7.addTrickCard(threeQtrAir);
		test = p7.calculateStinksAndAirsSeparately();
		assertEquals(test[0], 0);
		assertEquals(test[1], 4);
		p7.addTrickCard(halfStink);
		test = p7.calculateStinksAndAirsSeparately();
		assertEquals(test[0], 4);
		assertEquals(test[1], 0);
		p7.addTrickCard(qtrAir);
		test = p7.calculateStinksAndAirsSeparately();
		assertEquals(test[0], 2);
		assertEquals(test[1], 2);
		p7.addTrickCard(halfStink);
		test = p7.calculateStinksAndAirsSeparately();
		assertEquals(test[0], 5);
		assertEquals(test[1], 0);
	}
	
	public void testSeparate3() {
		p7.addTrickCard(threeQtrStink);
		int []test = new int[2];
		test = p7.calculateStinksAndAirsSeparately();
		assertEquals(test[0], 6);
		assertEquals(test[1], 0);
		p7.addTrickCard(halfAir);
		test = p7.calculateStinksAndAirsSeparately();
		assertEquals(test[0], 2);
		assertEquals(test[1], 4);
	}
}
