package extras;

import java.awt.Color;
import java.awt.Graphics;

import manipulatives.DoublePoint;
import manipulatives.ManModel;
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
		if(c == Constants.BAD_MOVE_COLOR) {
			drawThickLine(x, y, x+width, y+height, 12, g);
			drawThickLine(x+width, y, x, y+height, 6, g);
//			g.drawLine(x, y, x+width, y+height);
//			g.drawLine(x+width, y, x, y+height);
		}
		g.setColor(oldColor);
	}
	
	public static void drawThickLine(int x1, int y1, int x2, int y2, int lineSize, Graphics g) {
		for(int i = 0; i < (lineSize+1) / 2; i++) {
			g.drawLine(x1+i, y1+i, x2+i, y2+i);
			g.drawLine(x1-i, y1-i, x2-i, y2-i);
		}
	}

	public static void drawThickRectangle(int x, int y, int width, int height, Color c, Graphics g) {
		drawThickRectangle(x, y, width, height, Constants.DEFAULT_PEN_THICKNESS, c, g);
	}
	
	public static DoublePoint getPolarProjectedPoint(DoublePoint orig, double r, double theta) {
		double x = r * Math.sin(Math.toRadians(theta));
		double y = r * Math.cos(Math.toRadians(theta));
		return new DoublePoint((orig.getX() + x), (orig.getY() + y));
	}
	
	/* given the number of steps left, your current number and the number you want to end on
	 * will give you a good number to move by
	 */
	public static double incrementalMove(double current, int desired, int stepsLeft) {
		return Math.round((desired-current)/(stepsLeft));
	}
	
	public static void incrementalMove(ManModel man, int stepsLeft) {
		int curX = man.getX();
		int desireX = man.getDesiredX();
		int curY = man.getY();
		int desireY = man.getDesiredY();
		man.moveBy((int) incrementalMove(curX, desireX, stepsLeft), (int) incrementalMove(curY, desireY, stepsLeft));
	}
}
