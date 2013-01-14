package manipulatives;

import java.awt.image.BufferedImage;

public interface ManipInterface {
	public boolean isStinky();
	public boolean isShadow();
	public boolean isShadowPlayer();
	public boolean isFresh();
	public void setRegular();
	public boolean shouldTransform();
	public void setToTransform(boolean isStinky);
	public void clearTransform();
	public void toggleTransform(boolean isStinky);
	public void setAlpha(int a);
	public int getAlpha();
	public void setStinked();
	public void setShadow(boolean isShadow);
	public void setShadowPlayer(boolean isPlayer);
	public void setFresh();
	public void franticMove();
	public boolean inAlternatePlace();
	public void jumpUpAndDown();
	public int getNewSpeed(int velocity);
	public int getDesiredX();
	public void setDesiredX(int desiredX);
	public int getDesiredY();
	public void setDesiredY(int desiredY);
	public void setDesiredLocation(int desiredX, int desiredY);
	public int getX();
	public int getY();
	public void setX(int x);
	public void setY(int y);
	public void setXY(int x, int y);
	public void setXY(DoublePoint point);
	public void setCenteredXY(DoublePoint point, BufferedImage img);
	public void moveBy(int dx, int dy);
	public boolean isHighlighted();
	public void setHighlighted(boolean highlight);
	public boolean isSelected();
	public void setSelected(boolean selected);
	public void setShouldUseImage(boolean use);
	public boolean shouldUseImage();
}
