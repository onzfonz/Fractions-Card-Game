package manipulatives;

import basic.Constants;
import extras.RandomGenerator;


public class ManModel extends BasicModel{
	private boolean isStinky;
	private boolean isFresh;
	private int dx;
	private int dy;
	private RandomGenerator rgen;
	private boolean hasJumped;
	private boolean alternatePlace;
	private int numMoves = 0;
	
	public ManModel() {
		rgen = RandomGenerator.getInstance();
		dx = 0;
		dy = 0;
	}
	
	public boolean isStinky() {
		return isStinky;
	}
	
	public boolean isFresh() {
		return isFresh && !isStinky;
	}
	
	public void setRegular() {
		isStinky = false;
		isFresh = false;
	}
	
	public void setStinked() {
		isStinky = true;
		alternatePlace = rgen.nextBoolean();
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
}
