package manipulatives;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import extras.GameImages;
import extras.Utils;


public class AssetView implements ManipInterface {
	private ManModel model;
	private BufferedImage image;
	private int width;
	private int height;
	
	public AssetView(ManModel m, BufferedImage img) {
		setup(m, img);
	}
	
	public AssetView(ManipInterface m, BufferedImage img) {
		if(m instanceof AssetView) {
			setup(((AssetView)m).getModel(), img);
		}else if(m instanceof ManModel) {
			setup((ManModel) m, img);
		}
	}
	
	public AssetView(BufferedImage img) {
		setup(null, img);
		model = new ManModel();
	}
	
	private void setup(ManModel m, BufferedImage img) {
		model = m;
		image = img;
		width = img.getWidth();
		height = img.getHeight();
	}
	
	public int getX() {
		return model.getX();
	}
	
	public int getY() {
		return model.getY();
	}
	
	public void setX(int x) {
		model.setX(x);
	}
	
	public void setY(int y) {
		model.setY(y);
	}
	
	public void setCenteredXY(DoublePoint point, BufferedImage img) {
		model.setCenteredXY(point, img);
	}
	
	public void setXY(int x, int y) {
		model.setXY(x, y);
	}
	
	public void setXY(DoublePoint point) {
		model.setXY(point);
	}
	
	public void moveBy(int dx, int dy) {
		model.moveBy(dx, dy);
	}
	
	public boolean isStinky() {
		return model.isStinky();
	}
	
	public boolean isShadow() {
		return model.isShadow();
	}
	
	public boolean isShadowPlayer() {
		return model.isShadowPlayer();
	}
	
	public boolean isFresh() {
		return model.isFresh();
	}
	
	public void setRegular() {
		model.setRegular();
	}
	
	public boolean shouldTransform() {
		return model.shouldTransform();
	}
	
	public void setToTransform(boolean isStinky) {
		model.setToTransform(isStinky);
	}
	
	public void clearTransform() {
		model.clearTransform();
	}

	public void toggleTransform(boolean isStinky) {
		model.toggleTransform(isStinky);
	}
	
	public void setAlpha(int a) {
		model.setAlpha(a);
	}
	
	public int getAlpha() {
		return model.getAlpha();
	}
	
	public void setStinked() {
		model.setStinked();
	}
	
	public void setShadow(boolean isShadow) {
		model.setShadow(isShadow);
	}
	
	public void setShadowPlayer(boolean isPlayer) {
		model.setShadowPlayer(isPlayer);
	}
	
	public void setFresh() {
		model.setFresh();
	}
	
	public void franticMove() {
		model.franticMove();
	}
	
	public boolean inAlternatePlace() {
		return model.inAlternatePlace();
	}
	
	public void jumpUpAndDown() {
		model.jumpUpAndDown();
	}
	
	public int getNewSpeed(int velocity) {
		return model.getNewSpeed(velocity);
	}

	public int getDesiredX() {
		return model.getDesiredX();
	}

	public void setDesiredX(int desiredX) {
		model.setDesiredX(desiredX);
	}

	public int getDesiredY() {
		return model.getDesiredY();
	}

	public void setDesiredY(int desiredY) {
		model.setDesiredY(desiredY);
	}
	
	public void setDesiredLocation(int desiredX, int desiredY) {
		model.setDesiredLocation(desiredX, desiredY);
	}
	
	public void setHighlighted(boolean highlight) {
		model.setHighlighted(highlight);
	}
	
	public boolean isHighlighted() {
		return model.isHighlighted();
	}
	
	public boolean isSelected() {
		return model.isSelected();
	}

	public void setSelected(boolean selected) {
		model.setSelected(selected);
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public BufferedImage getTransformedImage(double radians, boolean myside) {
		return GameImages.rotatePerson(image, radians, myside);
	}
	
	public ManModel getModel() {
		return model;
	}

	public void setModel(ManModel model) {
		this.model = model;
	}

	public void drawObject(Graphics g) {
		g.drawImage(image, model.getX(), model.getY(), width, height, null);
	}
	
	public static void shuffleImages(ArrayList<ManipInterface> assets) {
		ArrayList<BufferedImage> imgList = new ArrayList<BufferedImage>();
		for(ManipInterface mi: assets) {
			imgList.add(((AssetView) mi).getImage());
		}
		Utils.shuffle(imgList);
		for(int i = 0; i < imgList.size(); i++) {
			AssetView asset = (AssetView) assets.get(i);
			asset.setImage(imgList.get(i));
		}
	}
}
