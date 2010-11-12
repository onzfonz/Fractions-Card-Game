package manipulatives;

import java.awt.Point;

public class Arc {
	private DoublePoint startPoint;
	private DoublePoint endPoint;
	private double startAngle;
	private double sweep;
	
	public Arc(DoublePoint start, DoublePoint end, int startA, int swp) {
		startPoint = start;
		endPoint = end;
		startAngle = startA;
		sweep = swp;
	}
	
	public DoublePoint getStart() {
		return startPoint;
	}
	
	public DoublePoint getEnd() {
		return endPoint;
	}
	
	public double getX1() {
		return Math.min(startPoint.getX(), endPoint.getX());
	}
	
	public double getY1() {
		return Math.min(startPoint.getY(), endPoint.getY());
	}
	
	public double getWidth() {
		return Math.abs(startPoint.getX()-endPoint.getX());
	}
	
	public double getHeight() {
		return Math.abs(startPoint.getY()-endPoint.getY());
	}
	
	public void setEndPoint(DoublePoint end) {
		endPoint = end;
	}
	
	// Moves x,y both the given dx,dy
	public void moveBy(int dx, int dy) {
		startPoint.setLocation(startPoint.getX()+dx, startPoint.getY()+dy);
		endPoint.setLocation(endPoint.getX()+dx, endPoint.getY()+dy);
	}

	public void setStartAngle(int startAngle) {
		this.startAngle = startAngle;
	}

	public double getStartAngle() {
		return startAngle;
	}

	public void setSweep(int sweep) {
		this.sweep = sweep;
	}

	public double getSweep() {
		return sweep;
	}

}
