package manipulatives;
//ManPanel.java
/**
 A Panel that draws a series of dots.
 The data model is a list of DotModel objects.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import pebblebag.BagShaker;

import acm.util.RandomGenerator;
import cards.CardGameConstants;

public class ManPanel extends JPanel  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5043717894855235511L;
	// our data model is a list of DotModel objects
	// (java.util is specified here to distinguish vs.
	//  the GUI java.awt.List)
	private java.util.List<ManModel> dots;
	private ArrayList<Line> lines;
	private ArrayList<Line> circles;
	private ArrayList<Arc> arcs;
	private static RandomGenerator rgen = RandomGenerator.getInstance();

	public static final int DRAG_VS_CLICK = 25;
	public static final int CLICKING_ON_LINE = 25;

	// remember the last dot for mouse tracking
	private int lastX, lastY;
	private ManModel lastDot;
	private DoublePoint origPoint;
	private Line newLine;
	

	// Booleans that control how we draw
	private boolean print;
	private boolean smartRepaint;
	private boolean oldRepaint;
	private boolean redPaint;
	private boolean pencilMode;
	private int buttonPressed;
	private Image img;
	private JLabel numberMen;
	private JLabel message;
	private ManFrame frame;
	
	// dirty bit = changed from disk version
	private boolean dirty;

	/**
	 Creates an empty ManPanel.
	 */
	public ManPanel(int width, int height, ManFrame f) {
		setPreferredSize(new Dimension(width, height));

		// Subclasing off JPanel, these things work
		setOpaque(true);
		// optimization: set opaque true if we fill 100% of our pixels
		setBackground(Color.gray);

		frame = f;
		dots = new ArrayList<ManModel>();
		lines = new ArrayList<Line>();
		circles = new ArrayList<Line>();
		arcs = new ArrayList<Arc>();
		clear();

		try {
			img = ImageIO.read(new File(CardGameConstants.MAN_IMG_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Controls for debugging options
		print = CardGameConstants.DEBUG_MODE;
		smartRepaint = true;
		oldRepaint = true;
		redPaint = false;
		pencilMode = false;
		origPoint = null;
		newLine = null;
		numberMen = new JLabel("0");
		message = new JLabel("");
		numberMen.setFont(new Font("Sans-serif", Font.BOLD, 48));
		message.setFont(new Font("Sans-serif", Font.BOLD, 48));
		message.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		setLayout(new BorderLayout());
		add(numberMen, BorderLayout.SOUTH);
		add(message, BorderLayout.NORTH);

		/*
		 Mouse Strategy:
		 -if the click is not on an existing dot, then make a dot
		 -note where the first click is into lastX, lastY
		 -then in MouseMotion: compute the delta of this position
		 vs. the last
		 -Use the delta to change things (not the abs coordinates)
		 */

		addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (print) {
					System.out.println("press:" + e.getX() + " " + e.getY() + ", " + e.getButton());
				}
				if(buttonPressed != 0) {
					return;
				}
				origPoint = new DoublePoint(e.getX(), e.getY());
				lastDot = findDot(e.getX(), e.getY());
				buttonPressed = e.getButton();
				if (buttonPressed == MouseEvent.BUTTON3) {
					rightPress(e, lastDot);
				}else{
					leftPress(e, lastDot);
				}

			}

			private void rightPress(MouseEvent e, ManModel dotModel) {
				if(dotModel == null) {
					newLine = findClosestLine(origPoint);
				}
			}

			private void leftPress(MouseEvent e, ManModel dotModel) {
				// invariant -- dotModel var is now set,
				// one way or another

				// Note the starting setup to compute deltas later
				//lastDot = origDot;
				lastX = e.getX();
				lastY = e.getY();

				// Change color of dot in some cases
				// shift -> change to black
				// double-click -> change to red
//				if(dotModel != null) {
//					if (e.isShiftDown()) {
//						doSetColor(dotModel, Color.BLACK);
//					}
//					else if (e.getClickCount() == 2) {
//						doSetColor(dotModel, Color.RED);
//					}
//				}
			}

			public void mouseReleased(MouseEvent e) {
				if (print) {
					System.out.println("release: " + e.getX() + " " + e.getY());
				}
				if(buttonPressed == MouseEvent.BUTTON3){
					rightRelease(e);
				}else{
					leftRelease(e);
				}
				buttonPressed = 0;
				newLine = null;
				lastDot = null;
			}

			private void leftRelease(MouseEvent e) {
				if (lastDot == null && newLine == null) {	// make a dot if nothing there
					doAdd(e.getX(), e.getY());
				}
			}

			private void rightRelease(MouseEvent e) {
				if(newLine != null && lastDot == null) {
					lines.remove(newLine);
					repaintLine(newLine);
				}else if(lastDot != null) {
					dots.remove(lastDot);
					repaintDot(lastDot);
				}
			}
		});


		addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (print) {
					System.out.println("drag:" + e.getX() + " " + e.getY() + ", " + e.getButton());
				}
				DoublePoint latestPoint = new DoublePoint(e.getX(), e.getY());

				if(buttonPressed == MouseEvent.BUTTON3) {
					rightDrag(e, latestPoint);
				}else{
					leftDrag(e, latestPoint);
				}
			}

			private void leftDrag(MouseEvent e, DoublePoint latestPoint) {
				if(lastDot == null) {
					if(pencilMode) {
						if(newLine == null) {
							createLine(origPoint, latestPoint);
							return;
						}
						DoublePoint start = newLine.getEnd();
						if(start.distance(latestPoint) > (DRAG_VS_CLICK*2)) {
							createLine(start, latestPoint);
						}else{
							//changeLine(newLine, latestPoint);
						}
					}else{
						if(newLine != null) {
							changeLine(newLine, latestPoint);
						}else if(origPoint.distance(latestPoint) > DRAG_VS_CLICK && newLine == null) {
							newLine = addLine(origPoint, latestPoint, lines);
						}
					}
				}

				if (lastDot != null) {
					// compute delta from last point
					int dx = e.getX()-lastX;
					int dy = e.getY()-lastY;
					lastX = e.getX();
					lastY = e.getY();

					// apply the delta to that dot model
					doMove(lastDot, dx, dy);
				}
			}

			private void rightDrag(MouseEvent e, DoublePoint latestPoint) {
				if(newLine != null) {
					moveLine(newLine, latestPoint);
				}
				lastDot = new ManModel();
			}
			
			private void createLine(DoublePoint start, DoublePoint end) {
				newLine = new Line(start, end);
				newLine.setPencil(pencilMode);
				lines.add(newLine);
				repaintLine(newLine);
			}
		});

	}

	// Clears out all the data (used by new docs, and for opening docs)
	public void clear() {
		dots.clear();
		lines.clear();
		circles.clear();
		arcs.clear();
		dirty = false;
		repaint();
	}

	// Default ctor, uses a default size
	public ManPanel() {
		this(300, 300, null);
	}

	public void placeDivider(int num) {
		if(num < 2 || num > 5) {
			return;
		}
		if(num == 2) {

		}
	}
	/**
	 Moves a dot from one place to another.
	 Does the necessary repaint.
	 This animation can repaint two ways.
	 Plain repaint: repaint the whole panel
	 Smart repaint: repaint just the old+new bounds of the dot
	 */
	public void doMove(ManModel dotModel, int dx, int dy) {
		if (!smartRepaint) {
			// Change the data model, then repaint the whole panel
			dotModel.moveBy(dx, dy);
			repaint();
		}
		else {
			// Smart repaint: old + new
			// Repaint the "old" rectangle
			if (oldRepaint) {
				repaintDot(dotModel);
			}
			// Change the model
			dotModel.moveBy(dx, dy);
			// Repaint the "new" rectangle
			repaintDot(dotModel);
		}

		setDirty(true);
	}

	private void moveLine(Line l, DoublePoint latestPoint) {
		DoublePoint origEndPoint = l.getEnd();
		DoublePoint origStartPoint = l.getStart();
		//repaintRegion(origStartPoint, origEndPoint);
		repaint();
		int dx = (int) (latestPoint.getX()-origPoint.getX());
		int dy = (int) (latestPoint.getY()-origPoint.getY());
		origPoint = latestPoint;
		l.moveBy(dx, dy);
		DoublePoint newStartPoint = l.getStart();
		DoublePoint newEndPoint = l.getEnd();
		repaintRegion(newStartPoint, newEndPoint);
		repaint();
	}

	private void changeLine(Line l, DoublePoint latestPoint) {
		DoublePoint origEndPoint = l.getEnd();
		repaintRegion(l.getStart(), origEndPoint);
		l.setEndPoint(latestPoint);
		repaintRegion(origEndPoint, l.getEnd());
	}

	/**
	 Utility -- change the color of the given dot model,
	 and then do the needed repaint/setDirty.
	 */
	private void doSetColor(ManModel dot, Color color) {
		repaint();      // bookeeping for the view: repaint and set dirty
		setDirty(true);
	}

	/**
	 Utility -- does a repaint rect just around one dot. Used
	 by smart repaint when dragging a dot.
	 */
	private void repaintDot(ManModel dot) {
		repaint(dot.getX()-CardGameConstants.MAN_WIDTH/2, dot.getY()-CardGameConstants.MAN_HEIGHT/2, CardGameConstants.MAN_WIDTH+1, CardGameConstants.MAN_HEIGHT+1);
	}

	private void repaintLine(Line l) {
		repaintRegion(l.getStart(), l.getEnd());
	}

	/**
	 Utility -- does a repaint rect around a region specified by two points. Used
	 by smart repaint when dragging a dot.
	 */
	private void repaintRegion(DoublePoint start, DoublePoint end) {
		int smallX = Math.min((int) start.getX(), (int) end.getX());
		int smallY = Math.min((int) start.getY(), (int) end.getY());
		int width = (int) Math.abs(end.getX() - start.getX());
		int height = (int) Math.abs(end.getY() - start.getY());
		repaint(smallX-2, smallY-2, width+5, height+5);
	}

	/**
	 Utility -- given a completed dot model, adds it and sets things up.
	 This is the bottleneck for adding a dot.
	 */
	public void doAdd(ManModel dotModel) {
		dots.add(dotModel);
		repaint();
		setDirty(true);
	}


	/**
	  Convenience doAdd() that takes an int x,y, adds and returns
	  a dot model for it.
	 */
	public ManModel doAdd(int x, int y) {
		ManModel dotModel = new ManModel();
		dotModel.setXY(x, y);
		//		System.out.println("MAN_WIDTH is " + MAN_WIDTH + "  and MAN_HEIGHT is " + MAN_HEIGHT);
		doAdd(dotModel);
		return dotModel;
	}

	private Line findClosestLine(DoublePoint p) {
		Line closest = null;
		double smallestDistance = 0;
		for(int i = 0; i < lines.size(); i++) {
			Line l = lines.get(i);
			double distance = calculateDistance(l, p);
			if(i == 0 || distance < smallestDistance) {
				closest = l;
				smallestDistance = distance;
			}
		}
		if(smallestDistance < CLICKING_ON_LINE) {
			return closest;
		}else{
			return null;
		}
	}

	/* Going to use Heron's Formula to for calculating the distance
	 * first we calculate the distance of each of the sides,
	 * and use that info to calcualate the height of the triangle, giving us back the
	 * distance
	 */
	private double calculateDistance(Line l, DoublePoint p) {
		double side1 = l.getStart().distance(l.getEnd());
		double side2 = l.getStart().distance(p);
		double side3 = l.getEnd().distance(p);
		double hS = (side1 + side2 + side3) / 2;
		double triangleArea = Math.sqrt(hS * (hS-side1) * (hS-side2) * (hS-side3));
		double distance = (2 * triangleArea) / side1;
		return distance;
	}

	/**
	 Finds a dot in the data model that contains
	 the given x,y or returns null.
	 */
	public ManModel findDot(int x, int y) {
		// Search through the dots in reverse order, so
		// hit topmost ones first.
		for (int i=dots.size()-1; i>=0; i--) {
			ManModel dotModel = dots.get(i);
			int centerX = dotModel.getX();
			int centerY = dotModel.getY();

			// figure x-squared + y-squared, see if it's
			// less than radius squared.
			// trick: don't need to take square root this way
			if(x > (centerX - CardGameConstants.MAN_WIDTH/2) && x < (centerX + CardGameConstants.MAN_WIDTH/2) && y > (centerY - CardGameConstants.MAN_HEIGHT/2) && y < (centerY + CardGameConstants.MAN_HEIGHT/2)){
				return dotModel;
			}
		}
		return null;
	}


	/**
	 Standard override -- draws all the dots.
	 */
	public void paintComponent(Graphics g) {
		// As a JPanel subclass we need call super.paintComponent()
		// so JPanel will draw the white background for us.
		super.paintComponent(g);

		for (Line l:lines) {
			if(l.isPencil()) {
				g.setColor(Color.RED);
			}else{
				g.setColor(Color.BLACK);
			}
			g.drawLine((int) l.getX1(), (int) l.getY1(), (int) l.getX2(), (int) l.getY2());
		}

		// Go through all the dots, drawing a circle for each
		for (ManModel dotModel : dots) {
			//g.drawImage(img, dotModel.getX() - MAN_WIDTH/2, dotModel.getY() - MAN_HEIGHT/2, MAN_WIDTH, MAN_HEIGHT, null);
			g.drawImage(img, dotModel.getX()-CardGameConstants.MAN_WIDTH/2, dotModel.getY()-CardGameConstants.MAN_HEIGHT/2, null);
		}

		numberMen.setText(""+dots.size());
		
		// Draw the "requested" clip rect in red
		// (this just shows off smart-repaint)
		if (redPaint) {
			Rectangle clip = g.getClipBounds();
			if (clip != null) {
				g.setColor(Color.red);
				g.drawRect(clip.x, clip.y, clip.width-1, clip.height-1);
			}
		}
		
		g.setColor(Color.GREEN);
		for(Line l:circles) {
			g.drawLine((int) l.getX1(), (int) l.getY1(), (int) l.getX2(), (int) l.getY2());
		}
		
		for(Arc l:arcs) {
			g.drawArc((int) l.getX1(), (int) l.getY1(), (int) l.getWidth(), (int) l.getHeight(), (int) l.getStartAngle(), (int) l.getSweep());
			//g.drawArc((int) l.getX1(), (int) l.getY1(), (int) l.getWidth()+1, (int) l.getHeight()+1, l.getStartAngle()-1, l.getSweep());
			
		}
		g.setColor(Color.BLACK);
	}

	// Setters used by checkboxes
	public void setPrint(boolean print) {
		this.print = print;
	}
	public void setSmart(boolean smart) {
		smartRepaint = smart;
	}
	public void setOld(boolean old) {
		oldRepaint = old;
	}
	public void setRed(boolean red) {
		redPaint = red;
	}


	/**
	 --File Saving Stuff--
	 from here down.
	 */


	/**
	 Accessors for the dirty bit.
	 */
	public boolean getDirty() {
		return dirty;
	}
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public void shuffle() {
		for(ManModel man:dots) {
			int randX = rgen.nextInt(CardGameConstants.MAN_WIDTH/2, getWidth() - CardGameConstants.MAN_WIDTH/2);
			int randY = rgen.nextInt(CardGameConstants.MAN_HEIGHT/2, getHeight() - CardGameConstants.MAN_HEIGHT/2);
			man.setXY(randX, randY);
			//			System.out.println(randX + ", " + randY);
			//			System.out.println("width" + getWidth() + ": height" + getHeight());
		}
		repaint();
	}

	public void clearAll() {
		clear();
	}

	public void setPencilMode(boolean actAsPencil) {
		pencilMode = actAsPencil;
	}
	
	public void enableControls() {
		frame.enableControls();
	}
	
	public void displayMessage(String s) {
		message.setText(s);
	}
	
	public void launchDividingAnimation(int den, int ppl, int numer, int ans) {
		clearAll();
		PageDivider pDiv = new PageDivider(this, den, ppl, numer, ans);
		Timer divTimer = new Timer(1500, pDiv);
		displayMessage("Make " + den + " equal area" + ((den==1)?"":"s"));
		divTimer.setInitialDelay(3000);
		pDiv.setTimer(divTimer);
		divTimer.start();
	}
	
	public void launchPeopleAddAnimation(int ppl, int den, int numer, int ans) {
		ManipAdder mAdd = new ManipAdder(this, ppl, den, numer, ans);
		Timer manipTimer = new Timer(1000, mAdd);
		displayMessage("Count off " + ppl + " " + ((ppl==1)?"person":"people") + ".");
		manipTimer.setInitialDelay(3000);
		mAdd.setTimer(manipTimer);
		manipTimer.start();
	}
	
	public void launchCirclerAnimation(int ppl, int den, int numer, int ans) {
		GroupCircler gCir = new GroupCircler(this, ppl, den, numer, ans);
		Timer manipTimer = new Timer(1000, gCir);
		if(ans == -1) {
			displayMessage("The Groups aren't equal!  Not Cool Man!");
		}else{
			displayMessage("Circle " + numer + " group" + ((numer==1)?"":"s") + ".");
		}
		manipTimer.setInitialDelay(3000);
		gCir.setTimer(manipTimer);
		manipTimer.start();
	}
	
	public void clearMessage() {
		message.setText("");
	}
	
	public void drawLines(int n) {
		String s = "Make " + n + " equal area" + ((n==1)?"":"s");
		addLines(lines, n, n);
		displayMessage(s);
		repaint();
	}
	
	public void addLines(ArrayList<Line> list, int n, int divs) {
		double theta = calculateTheta(divs);
		double r = calculateLineLength();
		DoublePoint center = getCenter();
		System.out.println("Center pt:" + center);
		for(int i = 0; i < n; i++) {
			DoublePoint offShoot = getPolarProjectedPoint(center, r, theta*i);
			System.out.println("offshoot" + i + ": " + offShoot + " theta: " + theta*i);
			addLine(center, offShoot, list);
		}
	}
	
	public void addALine(int i, double theta, double r, DoublePoint center) {
		DoublePoint offShoot = getPolarProjectedPoint(center, r, theta*i);
		addLine(center, offShoot, lines);
	}
	
	public void addACircle(int i, double theta, double r, DoublePoint center, int den) {
		addASide(i, theta, r, center);
		addASide((i+1)%den, theta, r, center);
		addAnArc(i, theta);
	}
	
	public void addASide(int i, double theta, double r, DoublePoint center) {
		DoublePoint offShoot = getPolarProjectedPoint(center, r, theta*i);
		addLine(center, offShoot, circles);
	}
	
	public void addAnArc(int i, double theta) {
		Arc temp = new Arc(new DoublePoint(0, 0), new DoublePoint(getWidth(), getHeight()), -90 + (int) (i*theta), (int) theta);
		arcs.add(temp);
	}
	
	public void drawPeopleOld(int num, int divs) {
//		if(divs == 2) {
//			divs = 4;
//		}
		double theta = calculateTheta(divs);
		double offset = theta / 2;
		boolean quarter = true;
		DoublePoint center = getCenter();
		for(int i = 0; i < num; i++) {
			double r = calculatePersonDistance(i/divs);
			if(divs <= 3 && i % divs == 0) {
				if(quarter) {
					offset = theta / 4;
				}else{
					offset = (3 * theta) / 4;
				}
				quarter = !quarter;
			}
			if(divs <= 3) {
				r = calculatePersonDistance(i/(divs*2));
			}
			double angle = theta*i+offset;
			DoublePoint pos = getPolarProjectedPoint(center, r, angle);
			doAdd((int) pos.getX(), (int) pos.getY());
		}
		repaint();
	}
	
	public void drawPeople(int num, int divs) {
		double theta = calculateTheta(divs);
		DoublePoint center = getCenter();
		for(int i = 0; i < num; i++) {
			addAManip(i, theta, center, divs);
		}
		repaint();
	}
	
	public void addAManip(int ithPerson, double theta, DoublePoint center, int divs) {
		DoublePoint pos = null;
		while(true) {
			double offset = calculateOffsetAngle(theta);
			double r = calculatePersonDistance2(ithPerson/divs);
			double angle = theta*ithPerson+offset;
			pos = getPolarProjectedPoint(center, r, angle);
			if(findDotArea((int) pos.getX(), (int) pos.getY()) == null) {
				break;
			}
		}
		doAdd((int) pos.getX(), (int) pos.getY());
	}
	
	public void drawOvals(int num, int divs) {
		addLines(circles, num+1, divs);
		addArcs(divs);
	}
	
	private void addArcs(int divs) {
		double theta = calculateTheta(divs);
		for(int i = 0; i < circles.size()-1; i++) {
			Line nextLine = circles.get((i + 1) % circles.size());
			DoublePoint start = circles.get(i).getEnd();
			Arc temp = new Arc(new DoublePoint(0, 0), new DoublePoint(getWidth(), getHeight()), -90 + (int) (i*theta), (int) theta+5);
			System.out.println(start + ", " + nextLine.getEnd() + "start: " + (i*theta) + ", with sweep: " + theta);
			arcs.add(temp);
		}
	}
	
	private ManModel findDotArea(int x, int y) {
		if(CardGameConstants.MANIPS_OVERLAP) {
			return null;
		}
		if(findDot(x, y) != null) {
			return findDot(x, y);
		}
		double manW = CardGameConstants.MAN_WIDTH/2;
		double manH = CardGameConstants.MAN_HEIGHT/2;
		double[] xS = {x - manW, x - manW, x + manW, x + manW};
		double[] yS = {y - manH, y + manH, y - manH, y + manH};
		for(int i = 0; i < xS.length; i++) {
			ManModel m = findDot((int) xS[i], (int) yS[i]);
			if(m != null) {
				return m;
			}
		}
		return null;
	}

	public double calculateTheta(int n) {
		return 360.0 / (n);
	}
	
	public double calculateLineLength() {
		double l = Math.max(getWidth(), getHeight());
		return l/Math.sqrt(2);
	}
	
	private double calculatePersonDistance(double sep) {
		return CardGameConstants.MAN_HEIGHT + sep * CardGameConstants.MAN_HEIGHT;
	}
	
	private double calculateOffsetAngle(double wideAngle) {
		double angleMargin = wideAngle/5;
		return rgen.nextDouble(angleMargin, (int) wideAngle-angleMargin);
	}
	
	private double calculatePersonDistance2(int sep) {
		return CardGameConstants.MAN_HEIGHT + rgen.nextDouble(0, Math.min(getHeight()/2-CardGameConstants.MAN_HEIGHT, sep * CardGameConstants.MAN_HEIGHT));
	}
	
	public DoublePoint getCenter() {
		return new DoublePoint(getWidth()/2, getHeight()/2);
	}
	
	private DoublePoint getPolarProjectedPoint(DoublePoint orig, double r, double theta) {
		double x = r * Math.sin(Math.toRadians(theta));
		double y = r * Math.cos(Math.toRadians(theta));
		return new DoublePoint((orig.getX() + x), (orig.getY() + y));
	}
	
	public Line addLine(DoublePoint p1, DoublePoint p2, ArrayList<Line> list) {
		Line temp = new Line(p1, p2);
		list.add(temp);
		return temp;
	}
	
	
	
	/**
	 Saves out our state (all the dot models) to the given file.
	 Uses Java built-in XMLEncoder.
	 */
	public void save(File file) {
		try {
			XMLEncoder xmlOut = new XMLEncoder(
					new BufferedOutputStream(
							new FileOutputStream(file)));

			// Could do something like this to control which
			// properties are sent. By default, it just sends
			// all of them with getters/setters, which is fine in this case.
			//  xmlOut.setPersistenceDelegate(ManModel.class,
			//       new DefaultPersistenceDelegate(
			//           new String[]{ "x", "y", "color" }) );


			// Make a ManModel array of everything
			ManModel[] dotArray = dots.toArray(new ManModel[0]);

			// Dump that whole array
			xmlOut.writeObject(dotArray);

			// And we're done!
			xmlOut.close();
			setDirty(false);
			// cute: only clear dirty bit *after* all the things that
			// could fail/throw an exception
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 Reads in all the dots from the file and set the panel
	 to show them.
	 */
	public void open(File file) {
		ManModel[] dotArray = null;
		try {
			XMLDecoder xmlIn = new XMLDecoder(new BufferedInputStream(
					new FileInputStream(file)));

			dotArray = (ManModel[]) xmlIn.readObject();
			xmlIn.close();

			// now we have the data, so go ahead and wipe out the old state
			// and put in the new. Goes through the same doAdd() bottleneck
			// used by the UI to add dots.
			// Note that we do this after the operations that might throw.
			clear();
			for(ManModel dm:dotArray) {
				doAdd(dm);
			}
			setDirty(false);

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 Saves the current appearance of the ManPanel out as a PNG
	 in the given file.
	 */
	public void saveImage(File file) {
		// Create an image bitmap, same size as ourselves
		BufferedImage image = (BufferedImage) createImage(getWidth(), getHeight());

		// Get Graphics pointing to the bitmap, and call paintAll()
		// This is the RARE case where calling paint() is appropriate
		// (normally the system calls paint()/paintComponent())
		Graphics g = image.getGraphics();
		paintAll(g);
		g.dispose();  // Good but not required-- 
		// dispose() Graphics you create yourself when done with them.

		try {
			javax.imageio.ImageIO.write(image, "PNG", file);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// For testing, also have a main that makes
	// a ManPanel but with no controls around it
	public static void main(String[] args) {
		JFrame frame = new JFrame("ManPanel");
		ManPanel manPanel = new ManPanel(800, 600, null);
		frame.add(manPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}

