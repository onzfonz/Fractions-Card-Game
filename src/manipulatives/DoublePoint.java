package manipulatives;

import java.awt.Point;


public class DoublePoint {
	private double x;
	private double y;
	private Point pt;
	
	public DoublePoint(double x, double y) {
		this.x = x;
		this.y = y;
		pt = new Point();
		pt.setLocation(x, y);
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
		pt.setLocation(x, y);
	}
	
	private Point getPoint() {
		return pt;
	}
	
	public double distance(DoublePoint point) {
		return pt.distance(point.getPoint());
	}
	
	public String toString() {
		String sx = x + "";
		sx = decimalPlaces(sx, 2);
		String sy = y + "";
		sy = decimalPlaces(sy, 2);
		return "[" + sx + ", " + sy + "]";
	}
	
	private String decimalPlaces(String s, int places) {
		int pos = s.indexOf('.');
		return s.substring(0, Math.min(s.length(), pos+places+1));
	}
}
