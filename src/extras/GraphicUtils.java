package extras;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Scanner;

import basic.Constants;

public class GraphicUtils {
	public static void drawThickOval(int x, int y, int width, int height, int lineSize, Color c, Graphics g) {
		Color oldColor = g.getColor();
		g.setColor(c);
		for(int i = 0; i < lineSize; i++) {
			g.drawOval(x, y, width, height);
			x++;
			y++;
			width-=2;
			height-=2;
		}
		g.setColor(oldColor);
	}
	
	/* Draws lineSize pixel width rectangle, bounded with the box x y width height */
	public static void drawThickRectangle(int x, int y, int width, int height, int lineSize, Color c, Graphics g) {
		Color oldColor = g.getColor();
		g.setColor(c);
		for(int i = 0; i < lineSize; i++) {
			g.drawRect(x, y, width, height);
			x++;
			y++;
			width-=2;
			height-=2;
		}
		g.setColor(oldColor);
	}

	public static void drawThickRectangle(int x, int y, int width, int height, Color c, Graphics g) {
		drawThickRectangle(x, y, width, height, Constants.DEFAULT_PEN_THICKNESS, c, g);
	}
}
