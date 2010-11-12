package transform;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import cards.TrickCard;

import extras.CardIcon;

/* Hopefully for this we would somehow get all of the cards to talk to each other.  Or at the very least get some type of
 * drag and drop without using the DnD structure setup in java
 */
public class GameTablePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3475003390742814950L;
	ArrayList<CardIcon> tricks;
	
	public GameTablePanel(int w, int h) {
		setPreferredSize(new Dimension(w, h));
		setOpaque(true);
		setBackground(Color.gray);
		repaint();
		tricks = new ArrayList<CardIcon>();
		CardIcon ci = new CardIcon(new TrickCard("cards_stink.jpg", 3, 5, "stink"));
        JLabel label = new JLabel(ci);
      //Adds a draggable component to the handler
		
        label.setTransferHandler(new TransferHandler("icon"));
        MouseListener listener = new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            JComponent c = (JComponent)e.getSource();
            TransferHandler handler = c.getTransferHandler();
            handler.exportAsDrag(c, e, TransferHandler.MOVE);
          }
        };
        
        label.addMouseListener(listener);
		tricks.add(ci);
		add(label);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame("PlayDeckView Test");
		
		JComponent cont = (JComponent) f.getContentPane();
		cont.setLayout(new FlowLayout());
		
		GameTablePanel t = new GameTablePanel(600, 600);
		cont.add(t);
		JPanel panel = new JPanel(new FlowLayout());
		panel.setPreferredSize(new Dimension(500, 500));
		cont.add(panel);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
