package generalbar;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class BarFrame extends JFrame{
	public BarFrame() {
		super();
		setTitle("Bar Drawing");
		setPreferredSize(new Dimension(800, 600));
		setLayout(new BorderLayout());
		final CardRepresentationPanel bp = new CardRepresentationPanel(1, 2);
		add(bp, BorderLayout.CENTER);
		
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
		add(box, BorderLayout.SOUTH);
		final JSlider numSlide = new JSlider(SwingConstants.HORIZONTAL, 1, 20, 1);
		numSlide.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				bp.setNumerator(numSlide.getValue());
				repaint();
			}
		});
		box.add(numSlide);
		
		final JSlider denSlide = new JSlider(SwingConstants.HORIZONTAL, 1, 20, 2);
		denSlide.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				bp.setDenominator(denSlide.getValue());
				repaint();
			}
		});
		box.add(denSlide);
		
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		BarFrame bf = new BarFrame();
	}
}
