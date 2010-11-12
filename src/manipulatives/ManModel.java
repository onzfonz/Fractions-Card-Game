package manipulatives;
// ManModel.java
/*
 Contains the data model for a single dot: x,y, color
 Uses the "bean" getter/setter style, so works with the XML encode/decode.
 Has a zero-arg constructor.
*/

import java.awt.Color;

public class ManModel {
	private int x;
	private int y;
	
	public ManModel() {
		x = 0;
		y = 0;
	}
	
	// standard getters/setters for x/y/color
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	// Convenience setters for clients
	
	// Moves x,y both the given dx,dy
	public void moveBy(int dx, int dy) {
		x += dx;
		y += dy;
	}
	
	// Sets both x and y
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
