package extras;
import javax.swing.*;

import transform.JXTransformer;

import cards.TeammateCard;
import cards.TrickCard;
import deck.PlayDeck;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

public class PlayDeckViewOld extends JComponent {
	private PlayDeck deck;
	
	public PlayDeckViewOld(PlayDeck p, int width, int height) {
		super();
		deck = p;
		setPreferredSize(new Dimension(width, height));
		
		addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
//				if (print) System.out.println("press:" + e.getX() + " " + e.getY());
//
//				ManModel dotModel = findDot(e.getX(), e.getY());
//				if (dotModel == null) {	// make a dot if nothing there
//					dotModel = doAdd(e.getX(), e.getY());
//				}
//
//				// invariant -- dotModel var is now set,
//				// one way or another
//
//				// Note the starting setup to compute deltas later
//				lastDot = dotModel;
//				lastX = e.getX();
//				lastY = e.getY();
//
//				// Change color of dot in some cases
//				// shift -> change to black
//				// double-click -> change to red
//				if (e.isShiftDown()) {
//					doSetColor(dotModel, Color.BLACK);
//				}
//				else if (e.getClickCount() == 2) {
//					doSetColor(dotModel, Color.RED);
//				}

			}
		});
		
		addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
//				if (print) System.out.println("drag:" + e.getX() + " " + e.getY());
//
//				if (lastDot != null) {
//					// compute delta from last point
//					int dx = e.getX()-lastX;
//					int dy = e.getY()-lastY;
//					lastX = e.getX();
//					lastY = e.getY();
//
//					// apply the delta to that dot model
//					doMove(lastDot, dx, dy);
//				}
			}
			
			public void mouseMoved(MouseEvent e) {
				
			}
		});
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension size = getSize();
		
		//Must be reinstated to work!
		/*ArrayList<Image> images = deck.getDeckImages();
		for(int k = 0; k < images.size(); k++) {
			Image img = images.get(k);
			//The max is to make sure that we don't draw in negative coordinates should the space given to us in the view is smaller
			//than the card size.
			g.drawImage(img, size.width/2 - img.getWidth(null)/2, k*(Math.max(0, Math.min(img.getHeight(null)*(images.size()-1), size.height-img.getHeight(null)))/(images.size()-1)), null);
		}
		g.drawRect(0, 0, size.width-1, size.height-1);*/
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame("PlayDeckView Test");
		
		JComponent cont = (JComponent) f.getContentPane();
		cont.setLayout(new FlowLayout());
		
		PlayDeck p1 = new PlayDeck(new TeammateCard("cards_recruits.jpg", "Music Geeks", "joined team", 6));
		PlayDeck p2 = new PlayDeck(new TeammateCard("cards_recruits12.jpg", "Basketball Team", "joined team", 12));
		
		TrickCard t0 = new TrickCard("cards_radio.jpg", 1, 1, "radio");
		TrickCard t1 = new TrickCard("cards_air.jpg", 1, 2, "air");
		TrickCard t2 = new TrickCard("cards_icecream.jpg", 2, 2, "ice");
		TrickCard t3 = new TrickCard("cards_stink.jpg", 1, 2, "stink");
		
		p1.addTrickCard(t0);
		p2.addTrickCard(t1);
		p2.addTrickCard(t2);
		p2.addTrickCard(t3);
		
		PlayDeckViewOld pd1 = new PlayDeckViewOld(p1, 200, 200);
		PlayDeckViewOld pd2 = new PlayDeckViewOld(p1, 200, 600);
		PlayDeckViewOld pd3 = new PlayDeckViewOld(p2, 200, 400);
		PlayDeckViewOld pd4 = new PlayDeckViewOld(p2, 200, 800);
		
		JXTransformer t = new JXTransformer(pd4);
		t.rotate(Math.toRadians(180));
		
		cont.add(pd1);
		cont.add(pd2);
		cont.add(pd3);
		cont.add(t);
		
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
