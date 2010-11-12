package deck;
/*
 * PlayDeckView.java
 * ---------------------
 * This is the view portion for the cards.  This will be the main mouse style interaction for
 * play decks that we will use.
 */

import javax.swing.*;

import cards.*;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

public class PlayDeckView extends JPanel {
	private PlayDeck deck;
	private ArrayList<CardViewOld> cardImages;
	private CardViewOld currentSelection;
	private int lastX;
	private int lastY;
	private int origX;
	private int origY;
	private boolean dragging;

	public PlayDeckView(PlayDeck p, int width, int height) {
		super();
		deck = p;
		currentSelection = null;
		origX = lastX = 0;
		origY = lastY = 0;
		dragging = false;

		cardImages = new ArrayList<CardViewOld>();
		ArrayList<Card> cards = deck.getAllCards();
		for(Card c : cards) {
			cardImages.add(new CardViewOld(c));
		}
		setPreferredSize(new Dimension(width, height));

		for(int k = 0; k < cardImages.size(); k++) {
			CardViewOld cv = cardImages.get(k);
			cv.setZOrder(cardImages.size()-(k+1));
			add(cv, 0, 0);
		}
		setZComponents();
		validate();

		addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				CardViewOld cv = findCardView(e.getX(), e.getY());
				makeSelectedCard(cv);
				if(currentSelection != null) {
					origX = currentSelection.getX();
					origY = currentSelection.getY();
					lastX = e.getX();
					lastY = e.getY();
				}
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

			public void mouseReleased(MouseEvent e) {
				repaint(0, 0, getWidth(), getHeight());
				//				repaintCard(findCardView(e.getX(), e.getY()));
				if(currentSelection != null) {
					repaintCard(currentSelection);
					currentSelection.setLocation(origX, origY);
					setComponentZOrder(currentSelection, currentSelection.getZOrder());
					repaintCard(currentSelection);
				}
				origX = origY = lastX = lastY = 0;
				dragging = false;
			}
		});

		addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				dragging = true;
				if (currentSelection != null) {
					// compute delta from last point
					int dx = e.getX()-lastX;
					int dy = e.getY()-lastY;
					lastX = e.getX();
					lastY = e.getY();

					// apply the delta to that dot model
					moveCard(currentSelection, dx, dy);
				}
			}

			public void mouseMoved(MouseEvent e) {

			}
		});
	}

	private void moveCard(CardViewOld card, int dx, int dy) {
		repaintCard(card);
		card.setLocation(card.getX()+dx, card.getY()+dy);
		repaintCard(card);
	}

	private void repaintCard(CardViewOld card) {
		repaint(card.getX(), card.getY(), card.getWidth()+1, card.getHeight()+1);
	}

	private CardViewOld findCardView(int x, int y) {
		for(int i = cardImages.size()-1; i >= 0; i--) {
			CardViewOld cv = cardImages.get(i);
			if(cv.withinBounds(x, y)) {
				return cv;
			}
		}
		return null;
	}

	private void makeSelectedCard(CardViewOld cv) {
		if(cv == null) {
			if(currentSelection != null) {
				unSelectCard(currentSelection);
			}
		}else{
			if(cv != currentSelection && currentSelection != null) {
				unSelectCard(currentSelection);
				//repaintCard(currentSelection);
			}
			selectCard(cv);
		}
		currentSelection = cv;
	}
	
	private void unSelectCard(CardViewOld cv) {
		if(cv != null) {
			cv.setHighlighted(false);
			setComponentZOrder(cv, cv.getZOrder());
			repaintCard(cv);
		}
		
	}
	
	private void selectCard(CardViewOld cv) {
		if(cv != null) {
			cv.setHighlighted(true);
			setComponentZOrder(cv, 0);
			repaintCard(cv);
		}
	}

	private CardViewOld currentlySelected() {
		return currentSelection;
	}
	
	private void recomputeLocations() {
		Dimension size = getSize();
		
		for(int k = 0; k < cardImages.size(); k++) {
			CardViewOld cv = cardImages.get(k);
			cv.setLocation(size.width/2 - cv.getWidth()/2, k*Math.max(60, Math.min(cv.getHeight()*(cardImages.size()-1), size.height-cv.getHeight()))/(cardImages.size()-1));
		}
	}
	
	private void setZComponents() {
		for(int i = 0; i < cardImages.size(); i++) {
			CardViewOld cv = cardImages.get(i);
			setComponentZOrder(cv, cv.getZOrder());
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(!dragging) {
			recomputeLocations();
		}
		//		
		//		for(int k = 0; k < cardImages.size(); k++) {
		//			Image img = cardImages.get(k);
		//			//The max is to make sure that we don't draw in negative coordinates should the space given to us in the view is smaller
		//			//than the card size.
		//			g.drawImage(img, size.width/2 - img.getWidth(null)/2, k*(Math.max(0, Math.min(img.getHeight(null)*(images.size()-1), size.height-img.getHeight(null)))/(images.size()-1)), null);
		//		}
		//		g.drawRect(0, 0, size.width-1, size.height-1);
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

		PlayDeckView pd1 = new PlayDeckView(p1, 200, 200);
		PlayDeckView pd2 = new PlayDeckView(p1, 200, 500);
		PlayDeckView pd3 = new PlayDeckView(p2, 200, 400);
		PlayDeckView pd4 = new PlayDeckView(p2, 200, 800);

		//		JXTransformer t = new JXTransformer(pd4);
		//		t.rotate(Math.toRadians(180));

		cont.add(pd1);
		cont.add(pd2);
		cont.add(pd3);
		cont.add(pd4);

		cont.repaint();
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
