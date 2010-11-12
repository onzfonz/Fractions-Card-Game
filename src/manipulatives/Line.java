package manipulatives;

import java.awt.Point;

public class Line {
	private DoublePoint startPoint;
	private DoublePoint endPoint;
	private boolean isPencil;
	
	public Line(DoublePoint start, DoublePoint end) {
		startPoint = start;
		endPoint = end;
		isPencil = false;
	}
	
	public DoublePoint getStart() {
		return startPoint;
	}
	
	public DoublePoint getEnd() {
		return endPoint;
	}
	
	public double getX1() {
		return startPoint.getX();
	}
	
	public double getX2() {
		return endPoint.getX();
	}
	
	public double getY1() {
		return startPoint.getY();
	}
	
	public double getY2() {
		return endPoint.getY();
	}
	
	public void setEndPoint(DoublePoint end) {
		endPoint = end;
	}
	
	// Moves x,y both the given dx,dy
	public void moveBy(int dx, int dy) {
		startPoint.setLocation(startPoint.getX()+dx, startPoint.getY()+dy);
		endPoint.setLocation(endPoint.getX()+dx, endPoint.getY()+dy);
	}
	
	public void setPencil(boolean isP) {
		isPencil = isP;
	}
	
	public boolean isPencil() {
		return isPencil;
	}
}
