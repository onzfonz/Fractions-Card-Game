package deck;
import javax.swing.*;

import cards.Card;
import cards.TeammateCard;
import cards.TrickCard;
import extras.CardIcon;
import extras.JRectangle;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

public class PlayDeckIcon extends JPanel {
	
	private static final long serialVersionUID = 1378026967032763010L;
	private PlayDeck deck;
	private ArrayList<JLabel> cardImages;
	private JLabel currentSelection;
	private boolean selected;
	private JRectangle outline;

	private int lastX;
	private int lastY;
	private int origX;
	private int origY;
	private boolean dragging;

	public PlayDeckIcon(PlayDeck p, int width, int height) {
		super();
		deck = p;
		currentSelection = null;
		origX = lastX = 0;
		origY = lastY = 0;
		dragging = false;
		selected = false;

		cardImages = new ArrayList<JLabel>();
		ArrayList<Card> cards = deck.getAllCards();
		for(Card c : cards) {
			cardImages.add(new JLabel(new CardIcon(c)));
		}
		setPreferredSize(new Dimension(width, height));

		for(int k = 0; k < cardImages.size(); k++) {
			JLabel l = cardImages.get(k);
			CardIcon cv = (CardIcon) l.getIcon();
			cv.setZOrder(cardImages.size()-(k+1));
			add(l, 0, 0);
		}
		outline = new JRectangle(cardImages.get(0).getIcon().getIconWidth(), height);
		setZComponents();
		validate();

		addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				JLabel cv = findCardIcon(e.getX(), e.getY());
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
					CardIcon ci = (CardIcon) currentSelection.getIcon();
					setComponentZOrder(currentSelection, ci.getZOrder());
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

	private void moveCard(JLabel card, int dx, int dy) {
		repaintCard(card);
		card.setLocation(card.getX()+dx, card.getY()+dy);
		repaintCard(card);
	}

	private void repaintCard(JLabel card) {
		repaint(card.getX(), card.getY(), card.getWidth()+1, card.getHeight()+1);
	}

	private JLabel findCardIcon(int x, int y) {
		for(int i = cardImages.size()-1; i >= 0; i--) {
			JLabel l = cardImages.get(i);
			if(withinBounds(l, x, y)) {
				return l;
			}
		}
		return null;
	}
	
	private boolean withinBounds(JLabel l, int x, int y) {
		return (x >= l.getX() && x <= (l.getX()+l.getWidth()) && y >= l.getY() && y <= (l.getY()+l.getHeight()));
	}

	private void makeSelectedCard(JLabel cv) {
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
	
	private void unSelectCard(JLabel l) {
		if(l != null) {
			CardIcon ci = (CardIcon) l.getIcon();
			ci.setHighlighted(false);
			setComponentZOrder(l, ci.getZOrder());
			repaintCard(l);
		}
		
	}
	
	private void selectCard(JLabel l) {
		if(l != null) {
			CardIcon ci = (CardIcon) l.getIcon();
			ci.setHighlighted(true);
			setComponentZOrder(l, 0);
			repaintCard(l);
		}
	}

	private JLabel currentlySelected() {
		return currentSelection;
	}
	
	private void recomputeLocations() {
		Dimension size = getSize();
		
		for(int k = 0; k < cardImages.size(); k++) {
			JLabel cv = cardImages.get(k);
			cv.setLocation(size.width/2 - cv.getWidth()/2, k*Math.max(60, Math.min(cv.getHeight()*(cardImages.size()-1), size.height-cv.getHeight()))/(cardImages.size()-1));
		}
	}
	
	private void setZComponents() {
		for(int i = 0; i < cardImages.size(); i++) {
			JLabel cv = cardImages.get(i);
			CardIcon ci = (CardIcon) cv.getIcon();
			setComponentZOrder(cv, ci.getZOrder());
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
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		if(this.selected) {
			add(outline, getWidth()/2-outline.getWidth()/2, 0);
		}else{
			remove(outline);
		}
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("PlayDeckIcon Test");

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

		PlayDeckIcon pd1 = new PlayDeckIcon(p1, 200, 200);
		PlayDeckIcon pd2 = new PlayDeckIcon(p1, 200, 500);
		PlayDeckIcon pd3 = new PlayDeckIcon(p2, 200, 400);
		PlayDeckIcon pd4 = new PlayDeckIcon(p2, 200, 800);
		pd4.setSelected(true);

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
