package manipulatives;

import basic.Constants;
import extras.RandomGenerator;


public class ManModel extends BasicModel{
	private boolean isStinky;
	private boolean isFresh;
	private boolean isShadowCircle;
	private boolean hasPlayerBehind;
	private int alpha;
	private int dx;
	private int dy;
	private RandomGenerator rgen;
	private boolean hasJumped;
	private boolean alternatePlace;
	private int numMoves = 0;
	private int desiredX;
	private int desiredY;
	private boolean transform;
	private int prevState; //constants has the 3 states reg, stinky fresh -1 to 1 respectively
	
	public ManModel() {
		rgen = RandomGenerator.getInstance();
		dx = 0;
		dy = 0;
		alpha = 255;
	}
	
	public ManModel(ManModel m) {
		this();
		isStinky = m.isStinky();
		isFresh = m.isFresh();
		setShadow(m.isShadow());
		setShadowPlayer(m.isShadowPlayer());
		setXY(m.getX(), m.getY());
	}
	
	public boolean isStinky() {
		return isStinky;
	}
	
	public boolean isShadow() {
		return isShadowCircle;
	}
	
	public boolean isShadowPlayer() {
		return isShadowCircle && hasPlayerBehind;
	}
	
	public boolean isFresh() {
		return isFresh && !isStinky;
	}
	
	public void setRegular() {
		isStinky = false;
		isFresh = false;
	}
	
	public boolean shouldTransform() {
		return transform;
	}
	
	public void setToTransform(boolean isStinky) {
		prevState = determineOldValue();
		transform = true;
	}
	
	public void clearTransform() {
		transform = false;
	}
	
	private int determineOldValue() {
		if(isFresh()) {
			return Constants.MANIP_STATE_FRESH;
		}else if(isStinky()) {
			return Constants.MANIP_STATE_STINKY;
		}
		return Constants.MANIP_STATE_REG;
	}
	
	private boolean inPrevState() {
		if(prevState == Constants.MANIP_STATE_REG){
			return !isFresh() && !isStinky();
		}else if(prevState == Constants.MANIP_STATE_FRESH) {
			return isFresh();
		}
		return isStinky();
	}
	
	public void toggleTransform(boolean isStinky) {
		if(inPrevState()) {
			setToAlteredState(isStinky);
		}else{
			setToOrigState();
		}
	}
	
	private void setToAlteredState(boolean isStinky) {
		if(isStinky) {
			setStinked();
		}else{
			setFresh();
		}
	}
	
	private void setToOrigState() {
		if(prevState == Constants.MANIP_STATE_REG) {
			setRegular();
		}else{
			setToAlteredState(prevState == Constants.MANIP_STATE_STINKY);
		}
	}
	
	public void setAlpha(int a) {
		alpha = a;
	}
	
	public int getAlpha() {
		return alpha;
	}
	
	public void setStinked() {
		isStinky = true;
		alternatePlace = rgen.nextBoolean();
	}
	
	public void setShadow(boolean isShadow) {
		isShadowCircle = isShadow;
	}
	
	public void setShadowPlayer(boolean isPlayer) {
		hasPlayerBehind = isPlayer;
	}
	
	public void setFresh() {
		isStinky = false;
		isFresh = true;
	}
	
	public void franticMove() {
		dx = getNewSpeed(dx);
		dy = getNewSpeed(dy);
		moveBy(dx, dy);
		numMoves++;
		if(numMoves % 3 == 0) {
			alternatePlace = !alternatePlace;
		}
	}
	
	public boolean inAlternatePlace() {
		return alternatePlace;
	}
	
	public void jumpUpAndDown() {
		if(hasJumped) {
			startJump();
		}else if(dy < Constants.JUMP_VELOCITY && alternatePlace){
			dy++;
		}else{
			endJump();
			if(rgen.nextBoolean(Constants.PROPENSITY_TO_FLIP)) {
				startJump();
			}
		}
		moveBy(dx, dy);
	}
	
	private void startJump() {
		dy = -1 * Constants.JUMP_VELOCITY;
		alternatePlace = true;
	}
	
	private void endJump() {
		dy = 0;
		alternatePlace = false;
	}
	
	public int getNewSpeed(int velocity) {
		int ddx = rgen.nextInt(-1, 1);
		velocity += ddx;
		velocity = Math.min(Math.max(velocity, -1 * Constants.MAX_SPEED), Constants.MAX_SPEED);
		if(rgen.nextBoolean(Constants.PROPENSITY_TO_FLIP)) {
			velocity *= -1;
		}
		return velocity;
	}

	public int getDesiredX() {
		return desiredX;
	}

	public void setDesiredX(int desiredX) {
		this.desiredX = desiredX;
	}

	public int getDesiredY() {
		return desiredY;
	}

	public void setDesiredY(int desiredY) {
		this.desiredY = desiredY;
	}
	
	public void setDesiredLocation(int desiredX, int desiredY) {
		setDesiredX(desiredX);
		setDesiredY(desiredY);
	}
}
