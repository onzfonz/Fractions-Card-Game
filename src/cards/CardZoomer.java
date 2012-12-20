package cards;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import basic.Constants;
import basic.GamePanel;
import deck.DeckView;

public class CardZoomer implements ActionListener {
	public static final int SCREEN_WIDTH = 500;
	public static final int SCREEN_HEIGHT = 400;
	public static final int MS_DELAY = 80;
	public static final int NUM_TICKS = 10;
	public static final int CLEAR_DELAY = 50;
	
	private CardView cv;
	private CardView shadowCard;
	private DeckView dv;
	private GamePanel panel;
	private Timer timer;
	private boolean expanding;
	private double panelWidth;
	private double panelHeight;
	private int count;	
	private int rectWidth;
	private int rectHeight;
	private int rectStartX;
	private int rectStartY;
	private int centerX;
	private int centerY;
	private double finalCenterX;
	private double finalCenterY;
	private double totalDeltaCenterX;
	private double totalDeltaCenterY;
	private double totalDeltaWidth;
	private double totalDeltaHeight;
	private double sizeFactor;
	private double WidthChangePerTime;
	private double HeightChangePerTime;
	private double centerXChangePerTime;
	private double centerYChangePerTime;
		
	public CardZoomer(GamePanel panel, DeckView dv, CardView cv, boolean expanding) {
        this.cv = cv;
        this.dv = dv;
        this.panel = panel;
    	timer = new Timer(MS_DELAY, this);
    	this.expanding = expanding;
    	panelWidth = panel.getWidth();
    	panelHeight = panel.getHeight();
    	count = 0;
    	timer.setInitialDelay(Constants.BETWEEN_GAME_PAUSE);
    	timer.start();
    	shadowCard = dv.getTeammateCard();			
	}
	
	private void setExpandConditions() {
		setRectWidth(0);
		setRectHeight(0);
		finalCenterX = panelWidth / 2;
		finalCenterY = panelHeight / 2;
		totalDeltaWidth = panelWidth - getRectWidth();
		totalDeltaHeight = panelHeight - getRectHeight();
	}
	
	private void setShrinkConditions() {
		totalDeltaWidth = 0 - getRectWidth();
		totalDeltaHeight = 0 - getRectHeight();
	}
	
	private void setIncrementConditions() {
		totalDeltaCenterX = finalCenterX - getCenterX();
		totalDeltaCenterY = finalCenterY - getCenterY();				
		sizeFactor = NUM_TICKS;
		WidthChangePerTime = totalDeltaWidth / sizeFactor;
		HeightChangePerTime = totalDeltaHeight / sizeFactor;
		centerXChangePerTime = totalDeltaCenterX / sizeFactor;
		centerYChangePerTime = totalDeltaCenterY / sizeFactor;	
	}
	
	public void actionPerformed(ActionEvent e) {
		resize();
	}
	
	private void resize() {
		if(count == 0) {
			setupZoomer();
		}
		if (count < sizeFactor) {
			count++;
			setCenterX(getCenterX() + (int) centerXChangePerTime);
			setCenterY(getCenterY() + (int) centerYChangePerTime);
			setRectWidth(getRectWidth() + (int) WidthChangePerTime);
	        setRectHeight(getRectHeight() + (int) HeightChangePerTime);
	        panel.repaint();
		} else {
			perfectRectSize();
			count = 0;
			panel.repaint();
			expanding = !expanding;
			panel.cardFullyZoomed(dv, cv);
			timer.stop();
		}
	}
	
	private void setupZoomer() {
		int x = shadowCard.getX()+shadowCard.getWidth()/2;
    	int y = shadowCard.getY()+shadowCard.getHeight()/2;
    	if (expanding) {
			rectStartX = x;
			rectStartY = y;
			setExpandConditions();
		} else {
			finalCenterX = x;
			finalCenterY = y;
			setShrinkConditions();				
		}
    	
		setCenterX(rectStartX + getRectWidth() / 2);
		setCenterY(rectStartY + getRectHeight() / 2);
		setIncrementConditions();
	}
	
	private void perfectRectSize() {
		if (expanding) {
			if (rectNotSizedCorrectly()) {
				setCenterX((int) panelWidth / 2);
				setCenterY((int) panelHeight / 2);
				setRectWidth((int) panelWidth);
				setRectHeight((int) panelHeight);
				panel.repaint();
			}
		}
	}
	
	private boolean rectNotSizedCorrectly() {
		return rectStartX != 0 || rectStartY != 0 || rectStartX + getRectWidth() != panelWidth
		            	|| rectStartY + getRectHeight() != panelHeight;
	}
	
    public Dimension getPreferredSize() {
        return new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
    }
    
    public void drawCardZoomer(Graphics g) {
    	if(count <= 0) return;
    	rectStartX = getCenterX() - (getRectWidth() / 2);
    	rectStartY = getCenterY() - (getRectHeight() / 2);
    	shadowCard.drawCard(g, rectStartX, rectStartY, getRectWidth(), getRectHeight());
    }

//    public void paintComponent(Graphics g) {
//        
//    	super.paintComponent(g);
//    	if (count <= 0) {
//            g.drawRect(0, 0, 0, 0);
//    		return;
//    	}
//    	
//        g.drawImage(img, rectStartX = getCenterX() - (getRectWidth() / 2), 
//        		rectStartY = getCenterY() - (getRectHeight() / 2), getRectWidth(), getRectHeight(), null);
//        
//    }  
    
    private int getRectWidth() {
    	return this.rectWidth;
    }
    
    private void setRectWidth(int newWidth) {
    	this.rectWidth = newWidth;
    }
    
    private int getRectHeight() {
    	return this.rectHeight;
    }
    
    private void setRectHeight(int newHeight) {
    	this.rectHeight = newHeight;
    }
    
    private int getCenterX() {
    	return this.centerX;
    }
    
    private void setCenterX(int newCenterX) {
    	this.centerX = newCenterX;
    }
    
    private int getCenterY() {
    	return this.centerY;
    }
    
    private void setCenterY(int newCenterY) {
    	this.centerY = newCenterY;
    }
}
