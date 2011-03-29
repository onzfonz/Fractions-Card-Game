package tutorial;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

public class AnimatorApplicationTimer extends JFrame implements ActionListener {
	//where instance variables
	//are declared:
	int delay = 100;
	Timer timer;
	int frameNumber = 0;
	boolean frozen = false;

	public AnimatorApplicationTimer() {
		
		// Set up a timer that calls this 
		// object's action handler.
		timer = new Timer(delay, this);
		timer.setInitialDelay(0);
		timer.setCoalesce(true);
	}

	public void startAnimation() {
		if (frozen) {
			// Do nothing.  The user has
			// requested that we stop
			// changing the image.
		} else {
			//Start (or restart) animating!
			timer.start();
		}
	}

	public void stopAnimation() {
		//Stop the animating thread.
		timer.stop();
	}

	public void actionPerformed(ActionEvent e) {
		//Advance the animation frame.
		frameNumber++;

		//Display it.
		repaint();
	}
}
