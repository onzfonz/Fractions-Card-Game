package transform;
import java.awt.*;
import java.awt.image.*;
import java.awt.datatransfer.*;
import java.io.*;
import javax.swing.*;

import deck.PlayDeckIcon;

import extras.CardIcon;


public class DeckSelection extends TransferHandler {
/**
	 * 
	 */
	private static final long serialVersionUID = -6973395112910591218L;
    private static DataFlavor flavors[];
//	private static final DataFlavor flavors[] = {new DataFlavor(CardIcon.class, "Card Icon")};
	public DeckSelection() {
		super();
		flavors = new DataFlavor[1];
		try{
			flavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + CardIcon.class.getName() + "\"");
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/* Method for describing the actions that are allowed when transfering
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;
	}

	/* Looks to see if the object that we want to drag on can import the data
	 * First looks to check to make sure the JComponent being dropped on is a
	 * JLabel, if it isn't then this thing won't work.
	 * Then checks to make sure that there is one data flavor that matches
	 * between the flavors we have stored, and the flavors they are trying to import.
	 * This looks to be code that can pretty much be copied.
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
	 */
	public boolean canImport(JComponent comp, DataFlavor flavor[]) {
		if (!(comp instanceof JLabel)) {
			return false;
		}
		for (int i=0, n=flavor.length; i<n; i++) {
			for (int j=0, m=flavors.length; j<m; j++) {
				if (flavor[i].equals(flavors[j])) {
					return true;
				}
			}
		}
		return false;
	}

	/* createTransferable
	 * This is what will create a Transferable object out of the component that is going to be dragged out
	 * This was created as an inner class so that you can get access to some of the info that can be grabbed
	 * from the JComponent that's being transferred itself. Follow this model instead of creating your own
	 * Transferable object outside of a class like this. 
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	public Transferable createTransferable(JComponent comp) {
		if (comp instanceof JLabel) {
			JLabel label = (JLabel)comp;
			Icon icon = label.getIcon();
			if (icon instanceof CardIcon) {
				final CardIcon cIcon = (CardIcon) icon;
				final JLabel source = label;
				Transferable transferable =	new Transferable() {
					public Object getTransferData(DataFlavor flavor) {
						if (isDataFlavorSupported(flavor)) {
							source.setIcon(null);
							return cIcon;
						}
						return null;
					}

					public DataFlavor[] getTransferDataFlavors() {
						return flavors;
					}

					public boolean isDataFlavorSupported(DataFlavor flavor) {
						return flavor.equals(flavors[0]);
					}
				};
				return transferable;
			}
		}
		return null;
	}

	/*
	 * importData(JComponent, Transferable)
	 * This is what is called to handle what happens when you are going to import the data
	 * Get the data from the transferable object, and create a new image icon with it.
	 * What they did was take the JComponent that it landed on, and changed that
	 * component to the image, suggesting that the jcomponent that they give you back
	 * would be the one that it has directly landed on.  Here what they do is simply
	 * change that object that was landed on to have the image given.
	 */
	/* Have to now check out how to do TransferHandler.TransferSupport */
	public boolean importData(JComponent comp, Transferable t) {
		if (comp instanceof PlayDeckIcon) {
			PlayDeckIcon label = (PlayDeckIcon)comp;
			if (t.isDataFlavorSupported(flavors[0])) {
				try {
					CardIcon icon = (CardIcon)t.getTransferData(flavors[0]);
					icon.setHighlighted(false);
					//label.setIcon(icon);
					return true;
				} catch (UnsupportedFlavorException ignored) {
				} catch (IOException ignored) {
				}
			}
		}
		return false;
	}
}

