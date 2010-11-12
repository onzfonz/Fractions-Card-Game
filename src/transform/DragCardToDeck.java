package transform;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;




import extras.CardIcon;

public class DragCardToDeck {
	private int lastX;
	private int lastY;
	
	public DragCardToDeck() {
		JFrame frame = new JFrame("Drag Card To Deck");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = frame.getContentPane();

		final Clipboard clipboard = frame.getToolkit().getSystemClipboard();

		final JLabel label = new JLabel();
		Icon icon = new CardIcon("cards_stink.jpg", 1, 2, "stink");
		label.setIcon(icon);
		label.setTransferHandler(new DeckSelection());

		MouseListener mouseListener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				JComponent comp = (JComponent)e.getSource();
				
				TransferHandler handler = comp.getTransferHandler();
				handler.exportAsDrag(comp, e, TransferHandler.MOVE);
				if(comp != null && comp instanceof JLabel) {
					CardIcon icon = (CardIcon)((JLabel)comp).getIcon();
					icon.setHighlighted(true);
					comp.repaint();
				}
				lastX = e.getX();
				lastY = e.getY();
			}
		};
		
		MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				System.out.println("mouse dragged");
				JComponent comp = (JComponent)e.getSource();
				if (comp != null && comp instanceof JLabel) {					// compute delta from last point
//					int dx = e.getX()-comp.getX();
//					int dy = e.getY()-comp.getY();
//
//					// apply the delta to that dot model
//					moveCard((JLabel) comp, dx, dy);
//					
					
				}
			}

			public void mouseMoved(MouseEvent e) {
//				JComponent comp = (JComponent)e.getSource();
//				if (comp != null && comp instanceof JLabel) {					// compute delta from last point
//					int dx = lastX-comp.getX();
//					int dy = lastY-comp.getY();
//					// apply the delta to that dot model
//					moveCard((JLabel)comp, dx, dy);
//					lastX = comp.getX();
//					lastY = comp.getY();
//				}
			}
		};
		
		label.addMouseListener(mouseListener);
		label.addMouseMotionListener(mouseMotionListener);
		JLabel label2 = new JLabel();
		Icon icon2 = new CardIcon("cards_icecream.jpg", 2, 2, "icecream");
		label2.setIcon(icon2);
		label2.setTransferHandler(new DeckSelection());
		label2.addMouseListener(mouseListener);
		label2.addMouseMotionListener(mouseMotionListener);
		JScrollPane pane2 = new JScrollPane(label2);
		contentPane.add(pane2, BorderLayout.EAST);
		JScrollPane pane = new JScrollPane(label);
		contentPane.add(pane, BorderLayout.CENTER); 
		JButton copy = new JButton("Copy");
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// fire TransferHandler's built-in copy 
				// action with a new actionEvent having 
				// "label" as the source
				Action copyAction = TransferHandler.getCopyAction();
				copyAction.actionPerformed(new ActionEvent(label, 
								ActionEvent.ACTION_PERFORMED,
								(String)copyAction.getValue(Action.NAME),
								EventQueue.getMostRecentEventTime(), 0));
			}
		});

		JButton clear = new JButton("Clear");
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				label.setIcon(null);
			}
		});

		JButton paste = new JButton("Paste");
		paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				// use TransferHandler's built-in 
				// paste action
				Action pasteAction = TransferHandler.getPasteAction();
				pasteAction.actionPerformed(new ActionEvent(label,
								ActionEvent.ACTION_PERFORMED,
								(String)pasteAction.getValue(Action.NAME),
								EventQueue.getMostRecentEventTime(), 0));
			}
		});

		JPanel p = new JPanel();
		p.add(copy);
		p.add(clear);
		p.add(paste);
		contentPane.add(p, BorderLayout.SOUTH);

		frame.setSize(300, 300);
		frame.pack();
		frame.setVisible(true);
	}
	public static void main(String args[]) {
		DragCardToDeck di = new DragCardToDeck();
	}
	
	private void moveCard(JLabel card, int dx, int dy) {
		repaintCard(card);
		card.setLocation(card.getX()+dx, card.getY()+dy);
		repaintCard(card);
	}

	private void repaintCard(JLabel card) {
		card.repaint(card.getX(), card.getY(), card.getWidth()+1, card.getHeight()+1);
	}
}
