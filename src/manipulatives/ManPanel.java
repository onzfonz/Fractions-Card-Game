package manipulatives;
//ManPanel.java
/**
 A Panel that draws a series of manips.
 The data model is a list of DotModel objects.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Arc2D;
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
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import basic.Constants;
import cards.CardView;
import cards.TeammateCard;
import deck.DeckView;
import extras.CardGameUtils;
import extras.Debug;
import extras.GameImages;
import extras.GraphicUtils;
import extras.RandomGenerator;

public class ManPanel extends JPanel implements ManipPanelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5043717894855235511L;
	// our data model is a list of DotModel objects
	// (java.util is specified here to distinguish vs.
	//  the GUI java.awt.List)
	private ArrayList<ManipInterface> manips;
	private ArrayList<Line> lines;
	private ArrayList<Line> circles;
	private ArrayList<Arc2D> arcs;
	private static RandomGenerator rgen = RandomGenerator.getInstance();

	public static final int DRAG_VS_CLICK = 25;
	public static final int CLICKING_ON_LINE = 25;

	// remember the last dot for mouse tracking
	private int lastX, lastY;
	private ManipInterface lastManip;
	private ManipInterface stinkAirObj;
	private BufferedImage stinkAirImg;
	private BufferedImage trashCanImg;
	private DoublePoint origPoint;
	private Line newLine;
	

	// Booleans that control how we draw
	private boolean smartRepaint;
	private boolean oldRepaint;
	private boolean redPaint;
	private boolean draggingManip, onTrashCan, canClickPanel;
	private int leftToChoose;
	private int pencilMode;
	private int buttonPressed;
	private BufferedImage img;
	private BufferedImage alternateImg, alternateImg2, alternateBaseImg, alternateBaseImg2;
	private JLabel numberMen;
	private JLabel message;
	private ManPanelListener frame;
	private Timer pTimer;
	private String laterMessage;
	
	// dirty bit = changed from disk version
	private boolean dirty;
	private DeckView currentDeckGiven;
	private CardView currentCardPlayed;

	/**
	 Creates an empty ManPanel.
	 */
	public ManPanel(int width, int height, DeckView dv, CardView cv, ManPanelListener f) {
		setPreferredSize(new Dimension(width, height));

		// Subclasing off JPanel, these things work
		setOpaque(true);
		// optimization: set opaque true if we fill 100% of our pixels
		setBackground(Color.gray);

		frame = f;
		manips = new ArrayList<ManipInterface>();
		lines = new ArrayList<Line>();
		circles = new ArrayList<Line>();
		arcs = new ArrayList<Arc2D>();
		currentDeckGiven = dv;
		currentCardPlayed = cv;
		clear();

		img = GameImages.getMan();
//		ClassLoader cl = Thread.currentThread().getContextClassLoader();
//		InputStream imageURL = cl.getResourceAsStream(Constants.MAN_IMG_PATH);
//		try{
//			img = ImageIO.read(imageURL);
//		}catch(IOException e) {
//			e.printStackTrace();
//		}
//		// Controls for debugging options
		smartRepaint = true;
		oldRepaint = true;
		redPaint = false;
		draggingManip = false;
		leftToChoose = -1;
		canClickPanel = true;
		trashCanImg = GameImages.getTrashCan();
		pencilMode = Constants.LINE_MODE;
		origPoint = null;
		newLine = null;
		pTimer = null;
		numberMen = new JLabel("0");
		message = new JLabel("");
		numberMen.setFont(Constants.FONT_LARGE);
		message.setFont(Constants.FONT_MED_LARGE);
		if(isShadowPanel()) {
			numberMen.setForeground(Constants.MANIP_SHADOW_TEXT_FOREGROUND);
			message.setForeground(Constants.MANIP_SHADOW_TEXT_FOREGROUND);
		}
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
			@Override
			public void mousePressed(MouseEvent e) {
				manPanelMousePressed(e);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				manPanelMouseReleased(e);
			}
		});


		addMouseMotionListener( new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				manPanelMouseDragged(e);
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if(leftToChoose > 0) {
					ManipInterface manip = findManip(e.getX(), e.getY());
					if(manip != null && !manip.isSelected()) {
						clearHighlights();
						manip.setHighlighted(true);
						repaint();
					}
				}
			}
		});

	}

	/* ---------------------- Mouse Helper Methods -------------------------- */
	
	public void manPanelMousePressed(MouseEvent e) {
		manPanelMousePressed(e.getX(), e.getY(), e.getButton());
	}
	
	public void manPanelMousePressed(int x, int y, int bPress) {
		Debug.println("press:" + x + " " + y + ", " + bPress);
		
		if(leftToChoose > 0) {
			setHighlightedManipAsChosen();
			return;
		}
		if(buttonPressed != 0 || !canClickPanel) {
			return;
		}
		origPoint = new DoublePoint(x, y);
		lastManip = findManip(x, y);
		buttonPressed = bPress;
		if (buttonPressed == MouseEvent.BUTTON1) {
			leftPress(x, y);
		}else{
			rightPress(lastManip);
		}
	}

	private void setHighlightedManipAsChosen() {
		ManipInterface manip = findHighlightedManip();
		if(manip == null)  return;
		manip.setHighlighted(false);
		manip.setSelected(true);
		leftToChoose--;
		displayMessage(leftToChoose + " left to choose");
		repaint();
		if(leftToChoose == 0) {
			startSwirlAnimation();
		}
	}

	public void manPanelMouseDragged(MouseEvent e) {
		manPanelMouseDragged(e.getX(), e.getY(), e.getButton());
	}
	
	public void manPanelMouseDragged(int x, int y, int bPress) {
		Debug.println("drag:" + x + " " + y + ", " + bPress);
		
		if(!canClickPanel) return;
		DoublePoint latestPoint = new DoublePoint(x, y);

		if(buttonPressed == MouseEvent.BUTTON3) {
			rightDrag(latestPoint);
		}else{
			leftDrag(x, y, latestPoint);
		}
		repaint();
	}
	
	public boolean isShadowPanel() {
		return ManPanelUtils.isShadowOnly(currentDeckGiven, currentCardPlayed);
	}
	
	public void manPanelMouseReleased(MouseEvent e) {
		manPanelMouseReleased(e.getX(), e.getY());
	}
	
	public void manPanelMouseReleased(int x, int y) {
		Debug.println("release: " + x + " " + y);
		
		if(!canClickPanel) return;
		if(buttonPressed == MouseEvent.BUTTON3){
			rightRelease();
		}else{
			leftRelease(x, y);
		}
		buttonPressed = 0;
		newLine = null;
		lastManip = null;
		draggingManip = false;
		repaint();
	}
	
	public void clearHighlights() {
		for(ManipInterface manip:manips) {
			manip.setHighlighted(false);
		}
	}
	
	private void rightPress(ManipInterface dotModel) {
		if(dotModel == null) {
			newLine = findClosestLine(origPoint);
		}
	}

	private void leftPress(int x, int y) {
		// invariant -- dotModel var is now set,
		// one way or another

		// Note the starting setup to compute deltas later
		//lastManip = origDot;
		lastX = x;
		lastY = y;

		// Change color of dot in some cases
		// shift -> change to black
		// double-click -> change to red
//		if(dotModel != null) {
//			if (e.isShiftDown()) {
//				doSetColor(dotModel, Color.BLACK);
//			}
//			else if (e.getClickCount() == 2) {
//				doSetColor(dotModel, Color.RED);
//			}
//		}
	}
	
	private void leftRelease(int x, int y) {
		if (lastManip == null && newLine == null && (frame == null || frame.canAddManips(manips.size(), currentDeckGiven, currentCardPlayed))) {	// make a dot if nothing there
			int curX = CardGameUtils.keepInBoundary(x, Constants.MAN_WIDTH, 0, getWidth());
			int curY = CardGameUtils.keepInBoundary(y, Constants.MAN_HEIGHT, 0, getHeight());
			doAdd(curX, curY, false, ManPanelUtils.isShadowOnly(currentDeckGiven, currentCardPlayed));
		}else if(lastManip != null) {
			if(insideTrashCanArea(x, y)) {
				removeManip(lastManip);
			}
		}
	}
	
	private void rightRelease() {
		if(newLine != null && lastManip == null) {
			lines.remove(newLine);
			repaintLine(newLine);
		}else if(lastManip != null) {
			removeManip(lastManip);
		}
	}
	
	private void leftDrag(int x, int y, DoublePoint latestPoint) {
		if(lastManip == null) {
			if(pencilMode == Constants.PENCIL_MODE) {
				if(changeActivePencil(latestPoint)) {
					return;
				}
			}else if(pencilMode == Constants.LINE_MODE){
				possiblyCreateLine(latestPoint);
			}
		}

		if (lastManip != null) {
			// compute delta from last point
			int curX = x;
			int curY = y;
			curX = CardGameUtils.keepInBoundary(curX, Constants.MAN_WIDTH, 0, getWidth());
			curY = CardGameUtils.keepInBoundary(curY, Constants.MAN_HEIGHT, 0, getHeight());
			int dx = curX-lastX;
			int dy = curY-lastY;
			lastX = curX;
			lastY = curY;
			draggingManip = true;
			if(insideTrashCanArea(x, y)) {
				onTrashCan = true;
			}else{
				onTrashCan = false;
			}
			// apply the delta to that dot model
			doMove(lastManip, dx, dy);
		}
		repaint();
	}

	private void rightDrag(DoublePoint latestPoint) {
		if(newLine != null) {
			moveLine(newLine, latestPoint);
		}
		lastManip = new ManModel();
	}
	
	public void createActivePencil(DoublePoint start, DoublePoint end) {
		createActiveLine(start, end);
	}
	
	public boolean changeActivePencil(DoublePoint latestPoint) {
		if(newLine == null) {
			createActiveLine(origPoint, latestPoint);
			return true;
		}
		DoublePoint start = newLine.getEnd();
		if(start.distance(latestPoint) > (DRAG_VS_CLICK*2)) {
			createActiveLine(start, latestPoint);
		}else{
			//changeLine(newLine, latestPoint);
		}
		return false;
	}
	
	public void createActiveLine(DoublePoint start, DoublePoint end) {
		newLine = new Line(start, end);
		newLine.setPencil(pencilMode);
		lines.add(newLine);
		repaintLine(newLine);
	}
	
	public void possiblyCreateLine(DoublePoint latestPoint) {
		if(newLine != null) {
			changeLine(newLine, latestPoint);
		}else if(origPoint.distance(latestPoint) > DRAG_VS_CLICK && newLine == null) {
			newLine = addLine(origPoint, latestPoint, lines);
		}
	}
	
	public void changeActiveLine(DoublePoint end) {
		newLine.setEndPoint(end);
		repaintLine(newLine);
	}
	
	private void removeManip(ManipInterface manip) {
		manips.remove(manip);
		repaintManip(manip);
	}
	
	/* -----------------------End Mouse Helper Methods------------------*/
	// Clears out all the data (used by new docs, and for opening docs)
	public void clear() {
		manips.clear();
		lines.clear();
		circles.clear();
		arcs.clear();
		origPoint = null;
		newLine = null;
		dirty = false;
		repaint();
	}

	// Default ctor, uses a default size
	public ManPanel() {
		this(300, 300, null, null, null);
	}

	private boolean insideTrashCanArea(int x, int y) {
		return x > getWidth() - trashCanImg.getWidth() && y > getHeight() - trashCanImg.getHeight();
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
	public void doMove(ManipInterface dotModel, int dx, int dy) {
		if (!smartRepaint) {
			// Change the data model, then repaint the whole panel
			dotModel.moveBy(dx, dy);
			repaint();
		}
		else {
			// Smart repaint: old + new
			// Repaint the "old" rectangle
			if (oldRepaint) {
				repaintManip(dotModel);
			}
			// Change the model
			dotModel.moveBy(dx, dy);
			// Repaint the "new" rectangle
			repaintManip(dotModel);
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
	
	public void setMouseClicks(boolean canClick) {
		canClickPanel = canClick;
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
	private void repaintManip(ManipInterface dot) {
		repaint(dot.getX()-Constants.MAN_WIDTH/2, dot.getY()-Constants.MAN_HEIGHT/2, Constants.MAN_WIDTH+1, Constants.MAN_HEIGHT+1);
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
	public void doAdd(ManipInterface dotModel) {
		manips.add(dotModel);
		repaint();
		setDirty(true);
	}


	/**
	  Convenience doAdd() that takes an int x,y, adds and returns
	  a dot model for it.
	 */
	public ManipInterface doAdd(int x, int y, boolean isStinky) {
		return doAdd(x, y, isStinky, false);
	}
	
	public ManipInterface doAdd(int x, int y, boolean isStinky, boolean isShadow) {
		ManipInterface dotModel = retrieveNewCorrectAssetFromGame(isStinky, isShadow);
		dotModel.setXY(x, y);
		if(isStinky && !dotModel.isStinky()) {
			dotModel.setStinked();
		}else if(isShadow) {
			dotModel.setShadow(true);
		}
		doAdd(dotModel);
		return dotModel;
	}
	
	public ManipInterface doAddOld(int x, int y, boolean isStinky, boolean isShadow) {
		ManipInterface dotModel;
//		if(Constants.USE_CHARACTER_MANIPS_IN_CALC) {
			BufferedImage correctImage;
//			if(Constants.USE_CHARACTER_MANIPS_IN_GAME) {
				correctImage = retrieveCorrectImageFromGame(isStinky, isShadow);
//			}else{
//				correctImage = retrieveCorrectImage();				
//			}
			dotModel = new AssetView(correctImage);
//		}else{
//			dotModel = new ManModel();
//		}
		dotModel.setXY(x, y);
		if(isStinky) {
			dotModel.setStinked();
		}else if(isShadow) {
			dotModel.setShadow(true);
		}
		//		Debug.println("MAN_WIDTH is " + MAN_WIDTH + "  and MAN_HEIGHT is " + MAN_HEIGHT);
		doAdd(dotModel);
		return dotModel;
	}
	
	public ManipInterface doAdd(int x, int y) {
		return doAdd(x, y, false);
	}
	
	private BufferedImage retrieveCorrectImage() {
		if(currentDeckGiven == null) {
			return GameImages.getRandomCharacter();
		}
		int num = manips.size();
		TeammateCard tc = (TeammateCard) currentDeckGiven.getTeammateCard().getCard();
		return GameImages.getCharacterImage(num, tc);
	}
	
	/* What we are doing here is making sure we get the stinky images first
	 * Because in the deck they are placed at the end of the array, we are getting
	 * the last elements first when we place them or when we add them to the screen.
	 * otherwise if it isn't stinky, then we just grab the image from the front
	 */
	private BufferedImage retrieveCorrectImageFromGame(boolean isStinky, boolean isShadow) {
		return retrieveCorrectAssetFromGame(isStinky, isShadow).getImage();
	}
	
	private AssetView retrieveNewCorrectAssetFromGame() {
		return retrieveNewCorrectAssetFromGame(false, false);
	}
	
	private AssetView retrieveNewCorrectAssetFromGame(boolean isStinky, boolean isShadow) {
		return new AssetView(retrieveCorrectAssetFromGame(isStinky, isShadow));
	}
	
	private AssetView retrieveCorrectAssetFromGame(boolean isStinky, boolean isShadow) {
		if(currentDeckGiven == null || isShadow) {
			if(isShadow) {
				return new AssetView(retrieveCorrectImageFromGame(isStinky, false));
			}
			return new AssetView(GameImages.getRandomCharacter());
		}
		ArrayList<ManipInterface> deckManips = currentDeckGiven.getManipulatives();
		int num = retrieveCorrectIndex(isStinky, isShadow, deckManips);
		return (AssetView) deckManips.get(num);
	}
	
	private int retrieveCorrectIndex(boolean isStinky, boolean isShadow, ArrayList<ManipInterface> deckManips) {
		int num = getCorrectIndex(isStinky);
		if(!isShadow) {
			num = num % deckManips.size();
			if(airFreshenerPlayed()) {
				num = (deckManips.size()-1)-num;
			}
		}
		Debug.println("final number retrieved is " + num + ", isStinky? " + isStinky + ", isShadow? " + isShadow);
		return num;
	}
	
	private int getCorrectIndex(boolean isStinky) {
		int num = 0;
		for(int i = 0; i < manips.size(); i++) {
			ManipInterface manip = manips.get(i);
//			if(manip.isStinky() == isStinky) {
			if(isStinky && manip.isStinky() || !isStinky) { //TODO: make sure this doesn't give us problems
				num++;
			}
		}
		return num;
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
	 Finds a manip that is on top of the xy and y or returns null.
	 */
	public ManipInterface findManip(int x, int y) {
		// Search through the manips in reverse order, so
		// hit topmost ones first.
		for (int i=manips.size()-1; i>=0; i--) {
			ManipInterface manipModel = manips.get(i);
			int centerX = manipModel.getX();
			int centerY = manipModel.getY();

			// figure x-squared + y-squared, see if it's
			// less than radius squared.
			// trick: don't need to take square root this way
			if(x > (centerX - Constants.MAN_WIDTH/2) && x < (centerX + Constants.MAN_WIDTH/2) && y > (centerY - Constants.MAN_HEIGHT/2) && y < (centerY + Constants.MAN_HEIGHT/2)){
				return manipModel;
			}
		}
		return null;
	}
	
	public ManipInterface findHighlightedManip() {
		for(ManipInterface manip:manips) {
			if(manip.isHighlighted()) {
				return manip;
			}
		}
		return null;
	}


	/**
	 Standard override -- draws all the manips.
	 */
	public void paintComponent(Graphics g) {
		// As a JPanel subclass we need call super.paintComponent()
		// so JPanel will draw the white background for us.
		super.paintComponent(g);
		Color origColor = g.getColor();
		g.setColor(Color.BLACK);
		for(Line l:circles) {
			g.drawLine((int) l.getX1(), (int) l.getY1(), (int) l.getX2(), (int) l.getY2());
		}
		if(!isShadowPanel()) {
			g.setColor(Constants.MANIP_ARC_SHADE_COLOR);
		}else{
			g.setColor(Constants.MANIP_ARC_SHADOW_SHADE_COLOR);
		}
		for(Arc2D l:arcs) {
			Graphics2D g2 = (Graphics2D) g;
			g2.fill(l);
//			g.fillArc((int) l.getX1(), (int) l.getY1(), (int) (l.getWidth()+150), (int) (l.getHeight()+150), (int) l.getStartAngle(), (int) l.getSweep());
//			g.fillArc((int) l.getX1()-(Constants.ARC_BUFFER/2), (int) l.getY1()-(Constants.ARC_BUFFER/2), (int) (l.getWidth()+Constants.ARC_BUFFER), (int) (l.getHeight()+Constants.ARC_BUFFER), (int) l.getStartAngle(), (int) l.getSweep());
//			g.fillArc((int) l.getX1(), (int) l.getY1(), (int) l.getWidth()+1, (int) l.getHeight()+1, (int) l.getStartAngle()-1, (int) l.getSweep());
			Color prevColor = g.getColor();
			g.setColor(Constants.LOUD_BUTTON_TEXT_COLOR);
			double buf = Constants.ARC_BUFFER;
			g.drawArc((int) (l.getX() + buf/2-getWidth()/6), (int) (l.getY() + buf/2), (int) (l.getWidth()-1-buf+getWidth()/3), (int) (l.getHeight()-1-buf), (int) l.getAngleStart(), (int) l.getAngleExtent());
			g.setColor(prevColor);
		}
		g.setColor(origColor);
		for (Line l:lines) {
			if(l.isPencil()) {
				g.setColor(Color.RED);
			}else{
				if(ManPanelUtils.isShadowOnly(currentDeckGiven, currentCardPlayed)) {
					g.setColor(Constants.MANIP_SHADOW_TEXT_FOREGROUND);
				}else{
					g.setColor(Color.BLACK);
				}
			}
			g.drawLine((int) l.getX1(), (int) l.getY1(), (int) l.getX2(), (int) l.getY2());
		}
		g.setColor(origColor);

		if(draggingManip) {
			if(onTrashCan) {
				trashCanImg = GameImages.getTrashCanOpen();
			}else{
				trashCanImg = GameImages.getTrashCan();
			}
			g.drawImage(trashCanImg, getWidth()-trashCanImg.getWidth(), getHeight()-trashCanImg.getHeight(), null);
		}
		numberMen.setText(""+manips.size());

		// Go through all the manips, drawing a circle for each
		for (ManipInterface dotModel : manips) {
			//g.drawImage(img, dotModel.getX() - MAN_WIDTH/2, dotModel.getY() - MAN_HEIGHT/2, MAN_WIDTH, MAN_HEIGHT, null);
			BufferedImage imgToDraw = img;
			if(dotModel.isShadow()) {
//				if(Constants.USE_CHARACTER_MANIPS_IN_CALC) {
//					imgToDraw = ((AssetView) dotModel).getImage();
//				}
				drawShadows(g, dotModel, imgToDraw);
			}else{
//				g.drawImage(imgToDraw, dotModel.getX()-Constants.MAN_WIDTH/2, dotModel.getY()-Constants.MAN_HEIGHT/2, null);
				drawSinglePerson(g, imgToDraw, dotModel);
			}
		}
		
		// Do the images
		if(stinkAirObj != null) {
			g.drawImage(stinkAirImg, stinkAirObj.getX(), stinkAirObj.getY(), null);
		}
		
		// Draw the "requested" clip rect in red
		// (this just shows off smart-repaint)
		if (redPaint) {
			Rectangle clip = g.getClipBounds();
			if (clip != null) {
				g.setColor(Color.red);
				g.drawRect(clip.x, clip.y, clip.width-1, clip.height-1);
				g.setColor(origColor);
			}
		}
		
		g.setColor(Color.BLACK);
	}
	
	private void drawSinglePerson(Graphics g, BufferedImage imgToDraw, ManipInterface man) {
		ArrayList<BufferedImage> layers = getImgsToDraw(imgToDraw, man);
		drawSinglePersonLayers(g, layers, man);
	}
	
	private void drawSinglePerson(Graphics g, BufferedImage imgToDraw, ManipInterface man, int width, int height) {
		ArrayList<BufferedImage> layers = getImgsToDraw(imgToDraw, man);
		drawSinglePersonLayers(g, layers, man, width, height);
	}
	
	private void drawSinglePersonLayers(Graphics g, ArrayList<BufferedImage> layers, ManipInterface man) {
		for(BufferedImage layer: layers) {
			g.drawImage(layer, man.getX()-layer.getWidth()/2, man.getY()-layer.getHeight()/2, null);
		}
	}
	
	private void drawSinglePersonLayers(Graphics g, ArrayList<BufferedImage> layers, ManipInterface man, int width, int height) {
		for(BufferedImage layer: layers) {
			g.drawImage(layer, man.getX()-width/2, man.getY()-height/2, width, height, null);
		}
	}
	
	private ArrayList<BufferedImage> getImgsToDraw(BufferedImage imgToDraw, ManipInterface man) {
		ArrayList<BufferedImage> imgsToDraw = new ArrayList<BufferedImage>();
		BufferedImage stinkyImg = GameImages.getStinkyLayer();
		BufferedImage freshImg = GameImages.getFreshenedLayer();
		BufferedImage addedLayer = null;
		if(Constants.USE_CHARACTER_MANIPS_IN_CALC || man.shouldUseImage()) {
			AssetView sv = (AssetView) man;
			imgToDraw = sv.getImage();
		}
		if(man.isSelected()) {
			imgsToDraw.add(GameImages.getSelectedLayer());
		}else if(man.isHighlighted()) {
			imgsToDraw.add(GameImages.getHighlightLayer());
		}
		if(man.isStinky()) {
			double radians = Math.PI/16;
			if(alternateImg == null) {
				alternateImg = GameImages.rotatePerson(stinkyImg, radians, true);
				alternateBaseImg = GameImages.rotatePerson(imgToDraw, radians, true);
			}
			if(alternateImg2 == null) {
				alternateImg2 = GameImages.rotatePerson(stinkyImg, radians, false);
				alternateBaseImg2 = GameImages.rotatePerson(imgToDraw, radians, false);
			}
			if(Constants.USE_CHARACTER_MANIPS_IN_CALC) {
				AssetView sv = (AssetView) man;
				alternateBaseImg = sv.getTransformedImage(radians, true);
				alternateBaseImg2 = sv.getTransformedImage(radians, false);
			}
			addedLayer = alternateImg2;
			imgToDraw = alternateBaseImg2;
			if(man.inAlternatePlace()) {
				addedLayer = alternateImg;
				imgToDraw = alternateBaseImg;
			}
		}else if(man.isFresh()) {
			addedLayer = freshImg;
		}
		imgsToDraw.add(imgToDraw);
		if(addedLayer != null) {
			imgsToDraw.add(addedLayer);
		}
		return imgsToDraw;
	}
	
	private void drawShadows(Graphics g, ManipInterface dotModel, BufferedImage img) {
		Color orig = g.getColor();
		g.setColor(Constants.TINY_SHADOW_COLOR);
		int circleD = img.getHeight()+getShadowOffset();
		if(dotModel.isShadowPlayer()) {
//			g.fillOval(dotModel.getX()-circleD/2, dotModel.getY()-circleD/2, circleD, circleD);
			drawSinglePerson(g, img, dotModel, img.getWidth(), img.getHeight());
		}
		int alpha = dotModel.getAlpha();
		Color curColor = g.getColor();
		g.setColor(new Color(curColor.getRed(), curColor.getGreen(), curColor.getBlue(), alpha));
		g.fillOval(dotModel.getX()-circleD/2, dotModel.getY()-circleD/2, circleD, circleD);
		g.setColor(Constants.MANIP_SHADOW_CENTER_BACKGROUND);
		g.drawOval(dotModel.getX()-circleD/2, dotModel.getY()-circleD/2, circleD, circleD);
		g.setColor(orig);
	}
	
	private int getShadowOffset() {
		return img.getHeight()/10;
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
		for(ManipInterface man:manips) {
			int randX = rgen.nextInt(Constants.MAN_WIDTH/2, getWidth() - Constants.MAN_WIDTH/2);
			int randY = rgen.nextInt(Constants.MAN_HEIGHT/2, getHeight() - Constants.MAN_HEIGHT/2);
			man.setXY(randX, randY);
			//			Debug.println(randX + ", " + randY);
			//			Debug.println("width" + getWidth() + ": height" + getHeight());
		}
		repaint();
	}

	public void clearAll() {
		clear();
	}

	public void setPencilMode(boolean actAsPencil) {
		int mode = Constants.LINE_MODE;
		if(actAsPencil) {
			mode = Constants.PENCIL_MODE;
		}
		pencilMode = mode;
	}
	
	public void setPplMode(boolean drawMen) {
		if(drawMen) {
			pencilMode = Constants.PPL_MODE;
		}else{
			pencilMode = Constants.LINE_MODE;
		}
	}
	
	public void enableControls() {
		frame.enableControls();
	}
	
	public void displayMessage(String s) {
		message.setText(s);
	}
	
	public void launchResultAnimation(int total, int num, int den, int numAffected, boolean isStinky) {
		if(manips.size() > total) {
			clearAll();
		}
		drawPeople(total-manips.size(), total-manips.size(), num, numAffected, isStinky); //changed 2nd arg to total from den so that it is more circular
		PeopleSprayer pSpray = new PeopleSprayer(this, total, num, den, numAffected, isStinky);
		pTimer = new Timer(Constants.ANIMATION_MS_PAUSE, pSpray);
		laterMessage = num + "/" + den + ((isStinky)?Constants.MAN_MSG_NOW_STINKY:Constants.MAN_MSG_NOW_FRESH);
		pTimer.setInitialDelay(Constants.ANIMATION_DELAY);
		pSpray.setTimer(pTimer);
		if(userShouldSelectChars(isStinky)) {
			enterUserPickMode(numAffected);
		}else{
			//this will get fixed by figuring out how to pick the items in the start swirl animation
			selectDefaultChars(manips, numAffected);
			startSwirlAnimation();
		}
	}
	
	private boolean userShouldSelectChars(boolean isStinky) {
		return isStinky && Constants.ASK_USERS_TO_PICK_PPL;
	}
	
	public void enterUserPickMode(int numAffected) {
		leftToChoose = numAffected;
		displayMessage("Who gets it?");
	}
	
	public void startSwirlAnimation() {
		displayMessage(laterMessage);
		pTimer.start();
	}
	
	public void launchShadowResultAnimation(int total, int num, int den, int numAffected) {
		clearAll();
		drawPeople(total, den, num, numAffected, true, false);
		for(int i = 0; i < manips.size(); i++) {
			if(i%den < num) {
				ManipInterface man = manips.get(i);
				man.setShadowPlayer(true);
			}
		}
		PeopleRevealer pReveal = new PeopleRevealer(this);
		Timer rTimer = new Timer(Constants.ANIMATION_MS_PAUSE, pReveal);
		rTimer.setInitialDelay(Constants.ANIMATION_DELAY);
		pReveal.setTimer(rTimer);
		rTimer.start();
	}
	
	public void fireAnimationDone(DeckView manipsDeck) {
		fireAnimationDone();
	}
	
	public void fireAnimationDone() {
		CardGameUtils.pause(Constants.RESULT_ANIMATION_FIRE_WINDOW_PAUSE);
		//set the assets in the currentDeck
		//if(Constants.USE_CHARACTER_MANIPS_IN_GAME) {
		ArrayList<AssetView> assets = new ArrayList<AssetView>();
		for(ManipInterface manip:manips) {
			assets.add((AssetView)manip);
		}
		clearAllTransformedManipulatives(assets);   //TODO: Change this back to uncomment this
		currentDeckGiven.setAssetManips(assets);
		//}
		frame.windowFinished();
	}
	
	public void launchPeopleResultAnimation(int ppl, int regions, int numerator, int answer, boolean isStinky) {
		assert(manips.size() == ppl);
		setAffectedManipulatives(manips, regions, numerator, isStinky);
		PeopleDisperser pDis = new PeopleDisperser(this, isStinky);
		Timer disTimer = new Timer(25, pDis);
		disTimer.setInitialDelay(1000);
		pDis.setTimer(disTimer);
		disTimer.start();
	}
	
	public void launchPeopleTransformAnimation(int ppl, int regions, int numerator, int answer, boolean isStinky) {
		assert(manips.size() == ppl);
		setTransformedManipulatives(manips, regions, numerator, isStinky);
		PeopleTransformer pTrans = new PeopleTransformer(this, ppl, regions, numerator, answer, isStinky);
		Timer tTimer = new Timer(75, pTrans);
		pTrans.setTimer(tTimer);
		tTimer.start();
	}
	
	public void launchPeopleRevealedAnimation() {
		cloneShadowPlayersWithoutShadow(manips);
		setRandomlyDesiredLocations(manips, getWidth(), getHeight()/2);
		ManipMover mMover = new ManipMover(this, manips, currentDeckGiven);
		Timer mTimer = new Timer(Constants.ANIMATION_MS_PAUSE, mMover);
		mTimer.setInitialDelay(Constants.ANIMATION_DELAY);
		mMover.setTimer(mTimer);
		mTimer.start();
	}
	
	private void setAffectedManipulatives(List<ManipInterface> manModels, int regions, int numerator, boolean isStinky) {
		for(int i = 0; i < manModels.size(); i++) {
			ManipInterface man = manModels.get(i);
//			if(i%regions < numerator) {
			if(man.isSelected()) {
				man.setSelected(false);
				if(isStinky) {
					man.setStinked();
				}else{
					man.setFresh();
				}
			}
		}
	}
	
	private void setTransformedManipulatives(List<ManipInterface> manModels, int regions, int numerator, boolean isStinky) {
		for(int i = 0; i < manModels.size(); i++) {
			ManipInterface man = manModels.get(i);
//			if(i%regions < numerator) {  this is the old way of getting it
			if(man.isSelected()) {
				man.setToTransform(isStinky);	
			}
		}
	}
	
	private void clearAllTransformedManipulatives(List<AssetView> manModels) {
		for(ManipInterface manip: manModels) {
			manip.clearTransform();
		}
	}
	
	private void selectDefaultChars(List<ManipInterface> manModels, int numAffected) {
		//change this to get the stinky ones.
		int numSoFar = 0;
		for(int i = 0; i < manModels.size(); i++) {
//			if(i%regions < numerator) {
			ManipInterface man = manModels.get(i);
			if(man.isStinky()) {
				man.setSelected(true);
				numSoFar++;
				if(numSoFar == numAffected) return;
			}
		}
		if(numSoFar == numAffected) return;
		for(int i = 0; i < manModels.size(); i++) {
			ManipInterface man = manModels.get(i);
			if(!man.isStinky()) {
				man.setSelected(true);
				numSoFar++;
				if(numSoFar == numAffected) return;
			}
		}
	}
	
	private void cloneShadowPlayersWithoutShadow(List<ManipInterface> manModels) {
		ArrayList<ManipInterface> duplicates = new ArrayList<ManipInterface>();
		ArrayList<ManipInterface> toRemove = new ArrayList<ManipInterface>();
		for(int i = 0; i < manModels.size(); i++) {
			ManipInterface manip = manModels.get(i);
			if(manip.isShadowPlayer()) {
				manip.setShadowPlayer(false);
				ManipInterface newManip = new AssetView(manip, GameImages.getCharacterImage(0, (TeammateCard) currentDeckGiven.getTeammateCard().getCard()));
				newManip.setShadow(false);
//				newManip.moveBy(getShadowOffset()/2, getShadowOffset()/2);
				duplicates.add(newManip);
				toRemove.add(manip);
			}
		}
		//get rid of bad shadow thing?
		manModels.clear();
		for(ManipInterface m:duplicates) {
			manModels.add(m);
		}
		for(ManipInterface m:toRemove) {
			manModels.remove(m);
		}
	}
	
	private void setRandomlyDesiredLocations(List<ManipInterface> manips, int baseX, int baseY) {
		for(int i = 0; i < manips.size(); i++) {
			ManipInterface man = manips.get(i);
			man.setDesiredX(baseX + man.getX()/rgen.nextInt(1, 4));
			man.setDesiredY((baseY + man.getY())/2);
		}
	}
	
	public void launchDividingAnimation(int den, int ppl, int numer, int ans) {
		clearAll();
		PageDivider pDiv = new PageDivider(this, den, ppl, numer, ans);
		Timer divTimer = new Timer(3000, pDiv);
		displayMessage("Make " + den + " equal area" + ((den==1)?"":"s"));
		frame.fireDenomExplained();
		divTimer.setInitialDelay(6000);
		pDiv.setTimer(divTimer);
		divTimer.start();
	}
	
	public void launchPeopleAddAnimation(int ppl, int den, int numer, int ans) {
		ManipAdder mAdd = new ManipAdder(this, ppl, den, numer, ans);
		Timer manipTimer = new Timer(2000, mAdd);
		displayMessage("Count off " + ppl + " " + ((ppl==1)?Constants.MAN_HELP_PERSON:Constants.MAN_HELP_PEOPLE) + ".");
		frame.firePplExplained();
		manipTimer.setInitialDelay(6000);
		mAdd.setTimer(manipTimer);
		manipTimer.start();
	}
	
	public void launchCirclerAnimation(int ppl, int den, int numer, int ans) {
		GroupCircler gCir = new GroupCircler(this, den, numer, ans);
		Timer manipTimer = new Timer(2000, gCir);
		if(ans == -1) {
			displayMessage(Constants.MAN_MSG_GROUPS_NOT_EQUAL);
		}else{
			displayMessage("Circle " + numer + " group" + ((numer==1)?"":"s") + ".");
			frame.fireNumerExplained();
		}
		
		manipTimer.setInitialDelay(6000);
		gCir.setTimer(manipTimer);
		manipTimer.start();
	}
	
	public void explanationDone() {
		repaint();
		enableControls();
		frame.fireExplainDone();
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
		Debug.println("Center pt:" + center);
		for(int i = 0; i < n; i++) {
			DoublePoint offShoot = GraphicUtils.getPolarProjectedPoint(center, r, theta*i);
			Debug.println("offshoot" + i + ": " + offShoot + " theta: " + theta*i);
			addLine(center, offShoot, list);
		}
	}
	
	public void addALine(int i, double theta, double r, DoublePoint center) {
		DoublePoint offShoot = GraphicUtils.getPolarProjectedPoint(center, r, theta*i);
		addLine(center, offShoot, lines);
	}
	
	public void moveItem(int i, double theta, double r, DoublePoint center) {
		DoublePoint newPlace = GraphicUtils.getPolarProjectedPoint(center, r, theta*i);
		Debug.printlnVerbose("center is " + center + " and newPlace is " + newPlace + ", and theta is " + theta*i);
		stinkAirObj.setCenteredXY(newPlace, stinkAirImg);
	}
	
	public void moveAffectedPeople(boolean isStinky) {
		for(ManipInterface m:manips) {
			if(isStinky && m.isStinky()) {
				m.franticMove();
			}else if(!isStinky && m.isFresh()) {
				m.jumpUpAndDown();
			}
		}
	}
	
	public void transformAffectedPeople(boolean isStinky) {
		for(ManipInterface m:manips) {
			if(m.shouldTransform()) {
				m.toggleTransform(isStinky);
			}
		}
	}
	
	public void addACircle(int i, double theta, double r, DoublePoint center, int den) {
		addASide(i, theta, r, center);
		addASide((i+1)%den, theta, r, center);
		addAnArc(i, theta);
	}
	
	public void addASide(int i, double theta, double r, DoublePoint center) {
		DoublePoint offShoot = GraphicUtils.getPolarProjectedPoint(center, r, theta*i);
		addLine(center, offShoot, circles);
	}
	
	public void addAnArc(int i, double theta) {
//		Arc temp = new Arc(new DoublePoint(0, 0), new DoublePoint(getWidth(), getHeight()), (int) (-90 + (double) (i*theta)), (d) theta);
		int buf = Constants.ARC_BUFFER;
		Arc2D temp = new Arc2D.Double((getWidth()-getHeight())/2-buf/2, 0-buf/2, getHeight()+buf, getHeight()+buf, ((theta*i))-90, theta, Arc2D.PIE); 
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
			double r = calculatePersonDistance(((double)i)/divs);
			if(divs <= 3 && i % divs == 0) {
				if(quarter) {
					offset = theta / 4;
				}else{
					offset = (3 * theta) / 4;
				}
				quarter = !quarter;
			}
			if(divs <= 3) {
				r = calculatePersonDistance(((double)i)/(divs*2));
			}
			double angle = theta*i+offset;
			DoublePoint pos = GraphicUtils.getPolarProjectedPoint(center, r, angle);
			doAdd((int) pos.getX(), (int) pos.getY());
		}
		repaint();
	}
	
	public void drawPeople(int totalPeople, int divs) {
		double theta = calculateTheta(divs);
		DoublePoint center = getCenter();
		for(int i = 0; i < totalPeople; i++) {
			addAManip(i, theta, calculatePersonDistanceAlt(i, divs, totalPeople), center, divs, false, false);
		}
		repaint();
	}
	
	public void drawPeople(int totalPeople, int divs, int sectionsAffected, int answer, boolean isStinky) {
		drawPeople(totalPeople, divs, sectionsAffected, answer, false, isStinky);
	}
	
	public void drawPeople(int totalPeople, int divs, int sectionsAffected, int answer, boolean isShadow, boolean isStinky) {
		int stinky = numToDrawStinky();
		if(totalPeople == 0 || divs == 0) {
//			if(!isShadow && !isStinky) {
//				for(int i = 0; i < stinky; i++) {
//					ManipInterface
//				}
//			}
			return;
		}
		
		double theta = calculateTheta(divs);
		DoublePoint center = getCenter();
		int numStinkyDrawn = 0;
		int pplPerSection = totalPeople/divs;
		int sections = (stinky+(pplPerSection-1)) / pplPerSection;
		int remainder = stinky % pplPerSection;
		for(int i = 0; i < totalPeople; i++) {
			if(isShadow) {
				addAManip(i, theta, center, divs, false, isShadow);
			}else{
				if((i % divs) < sections-1) {
					addAManip(i, theta, center, divs, true);
				}else if((i % divs) < sections && ((numStinkyDrawn < remainder) || remainder == 0)) {
					addAManip(i, theta, center, divs, true);
					numStinkyDrawn++;
				}else{
					addAManip(i, theta, center, divs, false);
				}
			}
		}
		if(/*Constants.USE_CHARACTER_MANIPS_IN_CALC && */!isShadow && isStinky) {
			AssetView.shuffleImages(manips);
		}
		repaint();
	}
	
	/* should be initial value - stinky right? */
	private int numToDrawStinky() {
		int stinky = 0;
		if(airFreshenerPlayed()) {
			stinky = currentDeckGiven.initialDeckValue() - currentDeckGiven.calculateDeck();
			if(currentCardPlayed == null) {
				stinky = currentDeckGiven.initialDeckValue() - currentDeckGiven.calculateDeckMinusTop();
			}
		}
		return stinky;
	}
	
	public void setManipsAlpha(int alpha) {
		for(ManipInterface man:manips) {
			man.setAlpha(alpha);
		}
	}
	
	private boolean airFreshenerPlayed() {
		CardView trick = null;
		if(currentDeckGiven != null && currentDeckGiven.getAllCards().size() > 1) {
			trick = currentDeckGiven.getTrickOnTop();
		}
		return (currentDeckGiven != null && (currentCardPlayed == null && trick != null && trick.isAir())) || (currentCardPlayed != null && currentCardPlayed.isAir());
	}
	
	public void addAManip(int ithPerson, double theta, DoublePoint center, int divs) {
		addAManip(ithPerson, theta, center, divs, false);
	}
	
	public void addAManip(int ithPerson, double theta, DoublePoint center, int divs, boolean isStinky) {
		addAManip(ithPerson, theta, center, divs, isStinky, false);
	}
	
	//The commented line was for testing out different scenarios to make sure that things could be drawn
	public void addAManip(int ithPerson, double theta, DoublePoint center, int divs, boolean isStinky, boolean isShadow) {
//		addAManip(ithPerson, theta, calculatePersonDistance2(ithPerson/divs), center, divs, isStinky, isShadow);
		addAManip(ithPerson, theta, calculatePersonDistanceAlt(ithPerson, divs, 12), center, divs, isStinky, isShadow);
	}
	
	public void addAManip(int ithPerson, double theta, double r, DoublePoint center, int divs, boolean isStinky, boolean isShadow) {
		DoublePoint pos = null;
		int numTimesPlaced = 0;
		double origR = r;
		Debug.println("add A Manip r is: " + r);
		while(true) {
			double offset = calculateOffsetAngle(theta);
			double angle = theta*ithPerson+offset;
			if(numTimesPlaced >= Constants.TRIED_ADDING_LIMIT-1){
				r = origR * 2;
			}
			pos = GraphicUtils.getPolarProjectedPoint(center, r, angle);
			movePointInBounds(pos);
			if(findDotArea((int) pos.getX(), (int) pos.getY(), numTimesPlaced) == null) {
				break;
			}
			r*=Constants.TRIED_ADDING_MODIFIER;
			numTimesPlaced++;
		}
		doAdd((int) pos.getX(), (int) pos.getY(), isStinky, isShadow);
	}
	
	private void movePointInBounds(DoublePoint e) {
		Debug.println("moving point in bounds: " + e.getX() + ", " + e.getY());
		BufferedImage img = GameImages.getRandomCharacter();
		int xLimit = getWidth() - img.getWidth()/2;
		int yLimit = getHeight() - img.getHeight()/2;
		int xLower = 0 + img.getWidth()/2;
		int yLower = 0 + img.getHeight()/2;
		if(e.getX() < xLower) {
			e.setLocation(xLower, e.getY());
		}
		if(e.getY() < yLower) {
			e.setLocation(e.getX(), yLower);
		}
		if(e.getY() > yLimit) {
			e.setLocation(e.getX(), yLimit-1);
		}
		if(e.getX() > xLimit){
			e.setLocation(xLimit-1, e.getY());
		}
	}
	
//	public void drawOvals(int num, int divs) {
//		addLines(circles, num+1, divs);
//		addArcs(divs);
//	}
//	
//	private void addArcs(int divs) {
//		double theta = calculateTheta(divs);
//		for(int i = 0; i < circles.size()-1; i++) {
//			Line nextLine = circles.get((i + 1) % circles.size());
//			DoublePoint start = circles.get(i).getEnd();
//			Arc2D temp = new Arc2D.Double(0, 0, getWidth(), getHeight(), -90 + (i*theta), theta, Arc2D.PIE);
//			Debug.println(start + ", " + nextLine.getEnd() + "start: " + (i*theta) + ", with sweep: " + theta);
//			arcs.add(temp);
//		}
//	}
	
	private ManipInterface findDotArea(int x, int y, int numTimesPlaced) {
		return findOverlap(x, y, numTimesPlaced);
//		if(Constants.MANIPS_OVERLAP) {
//			return null;
//		}
//		if(findDot(x, y) != null) {
//			return findDot(x, y);
//		}
//		double manW = Constants.MAN_WIDTH/2;
//		double manH = Constants.MAN_HEIGHT/2;
//		double[] xS = {x - manW, x - manW, x + manW, x + manW};
//		double[] yS = {y - manH, y + manH, y - manH, y + manH};
//		for(int i = 0; i < xS.length; i++) {
//			ManModel m = findDot((int) xS[i], (int) yS[i]);
//			if(m != null) {
//				return m;
//			}
//		}
//		return null;
	}

	//new model based on web search for rectangle overlap.
	private ManipInterface findOverlap(int x, int y, int numTimesPlaced) {
		if(Constants.MANIPS_OVERLAP || numTimesPlaced > Constants.TRIED_ADDING_LIMIT) {
			return null;
		}
		if(findManip(x, y) != null) {
			return findManip(x, y);
		}
		double manW = Constants.MAN_WIDTH/2;
		double manH = Constants.MAN_HEIGHT/2;
		double pLeftX = x - manW;
		double pRightX = x + manW;
		double pTopY = y - manH;
		double pBotY = y + manH;
		return findOverlap(pLeftX, pTopY, pRightX, pBotY);
	}
	
	private ManipInterface findOverlap(double x1, double y1, double x2, double y2) {
		double manW = Constants.MAN_WIDTH/2;
		double manH = Constants.MAN_HEIGHT/2;
		for(ManipInterface dot:manips) {
			double leftX = dot.getX()-manW;
			double topY = dot.getY()-manH;
			double rightX = dot.getX()+manW;
			double botY = dot.getY()+manH;
			if(x1 < rightX && y1 < botY && x2 > leftX && y2 > topY) {
				return dot;
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
	
	public double calculateShortestLineLengthFromCenter() {
		return Math.min(getWidth()/2, getHeight()/2);
	}
	
	private double calculatePersonDistance(double sep) {
		return Constants.MAN_HEIGHT + sep * Constants.MAN_HEIGHT;
	}
	
	private double calculateOffsetAngle(double wideAngle) {
		double angleMargin = wideAngle/5;
		return rgen.nextDouble(angleMargin, (int) wideAngle-angleMargin);
	}
	
	private double calculatePersonDistance2(int sep) {
		return Constants.MAN_HEIGHT + rgen.nextDouble(0, Math.min(getHeight()/2-Constants.MAN_HEIGHT, sep * Constants.MAN_HEIGHT));
	}
	
	//And then figure out how to add this to just this part where they have to select the characters
	private double calculatePersonDistanceAlt(int ith, int divs, int totalPeople) {
		int pplPerDiv = totalPeople / divs;
		int ithToDiv = ith / divs;
		double r = calculatePersonDistance2((int) (10 * ((double)pplPerDiv-ithToDiv)));
		return r;
	}
	
	public DoublePoint getCenter() {
		return new DoublePoint(getWidth()/2.0, getHeight()/2.0);
	}
	
	public Line addLine(DoublePoint p1, DoublePoint p2, ArrayList<Line> list) {
		Line temp = new Line(p1, p2);
		list.add(temp);
		return temp;
	}
	
	public void addItem(boolean isStink, BufferedImage img) {
		stinkAirObj = new ManModel();
		stinkAirObj.setXY(getWidth()/2-img.getWidth()/2, getHeight()/2-img.getHeight()/2);
		stinkAirImg = img;
	}
	
	public void removeItem() {
		stinkAirObj = null;
		stinkAirImg = null;
	}
	public String generateStatLine() {
		int numDots = manips.size();
		int numLines = getNumPencilLines(false);
		int marks = getNumPencilLines(true);
		String dotSeg = "ppl " + numDots;
		String lineSeg = "lines " + numLines;
		String markSeg = "marks " + marks;
		if(numDots == 0) {
			dotSeg = "";
		}
		if(numLines == 0) {
			lineSeg = "";
		}
		if(marks == 0) {
			markSeg = "";
		}
		return dotSeg + ", " + lineSeg + ", " + markSeg; 
	}
	
	public int getNumPencilLines(boolean isPencil) {
		int numLines = 0;
		for(int i = 0; i < lines.size(); i++) {
			Line l = lines.get(i);
			if(l.isPencil() == isPencil) {
				numLines++;
			}
		}
		return numLines;
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
			ManModel[] dotArray = manips.toArray(new ManModel[0]);

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
	 Reads in all the manips from the file and set the panel
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
			// used by the UI to add manips.
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
	
	public void tickPassedInTruckMover(DeckView impactedDeck, int tickNum) {
		//do nothing this is here to satisfy the ManipMover
	}

	// For testing, also have a main that makes
	// a ManPanel but with no controls around it
	public static void main(String[] args) {
		JFrame frame = new JFrame("ManPanel");
		ManPanel manPanel = new ManPanel(800, 600, null, null, null);
		frame.add(manPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}

