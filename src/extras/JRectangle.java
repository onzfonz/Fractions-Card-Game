package extras;
import java.awt.*;

import javax.swing.*;

public class JRectangle extends JComponent {
	
	private static final long serialVersionUID = -2401172901752076297L;
	private int x;
	private int y;
	private int width;
	private int height;
	private Color color;

	public JRectangle(int x, int y, int w, int h, Color c) {
		super();
		this.x = x;
		this.y = y;
		width = w;
		height = h;
		color = c;
		setOpaque(true);
		setBackground(Color.black);
		setPreferredSize(new Dimension(width, height));
	}
	
	public JRectangle(int x, int y, int w, int h) {
		this(x, y, w, h, Color.RED);
	}
	
	public JRectangle(int w, int h) {
		this(0, 0, w, h, Color.RED);
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(color);
		drawThickRectangle(x, y, width-1, height-6, 4, g);
	}
	
	private void drawThickRectangle(int x, int y, int width, int height, int lineSize, Graphics g) {
		for(int i = 0; i < lineSize; i++) {
			g.drawRect(x, y, width, height);
			x++;
			y++;
			width-=2;
			height-=2;
		}
	}
	
	public Rectangle getRect() {
		return new Rectangle(x, y, width, height);
	}
	
	public void setBounds(Rectangle r) {
		x = (int) r.getX();
		y = (int) r.getY();
		width = (int) r.getWidth();
		height = (int) r.getHeight();
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame("JRectangle Test");
		
		JComponent cont = (JComponent) f.getContentPane();
		cont.setLayout(new FlowLayout());
		
		JRectangle r = new JRectangle(200, 300);
		cont.add(r);
		
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
