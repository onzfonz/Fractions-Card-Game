package cards;
/*
 * File: Card.java
 * ---------------
 * This will be the basic holder for holding the cards.  Each cards will have three fields
 * The image the name of the card and the description
 */

import java.awt.datatransfer.Transferable;

public abstract class Card implements Transferable{
	protected String imgName;
	protected String name;
	protected String description;
		
	public Card(String img) {
		setImageName(img);
		name = description = null;
	}
	
	public Card(Card c) {
		this(c.getImageName());
		setName(c.getName());
		setDescription(c.getDescription());
	}
	
	public boolean isTrickCard() {
		return (this instanceof TrickCard);
	}
	
	public boolean isTeammateCard() {
		return (this instanceof TeammateCard);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setImageName(String name) {
		imgName = name;
	}
	public String getDescription() {
		return description;
	}
	public String getImageName() {
		return imgName;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public abstract String toReadableText();
	public abstract String toStream();
}
