package extras;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import basic.Constants;

/* Class just has handy utilities to use */

public class Debug {
	public static void println(String s) {
		if(Constants.DEBUG_MODE) {
			System.out.println(s);
		}
	}
	
	public static void println(int n) {
		println(n + "");
	}
	
	public static void println(double d) {
		println(d + "");
	}
	
	public static void println(boolean f) {
		println(f + "");
	}
	
	public static void println(char c) {
		println(c + "");
	}
	
	public static void println(Object o) {
		if(o != null) {
			println(o.toString());
		}else{
			println(null);
		}
	}
	
	public static void printlnVerbose(String s) {
		if(Constants.DEBUG_MODE && Constants.VERBOSE_MODE) {
			System.out.println(s);
		}
	}
	
	public static void printlnVerbose(int n) {
		println(n + "");
	}
	
	public static void printlnVerbose(double d) {
		println(d + "");
	}
	
	public static void printlnVerbose(boolean f) {
		println(f + "");
	}
	
	public static void printlnVerbose(char c) {
		println(c + "");
	}
	
	public static void printlnVerbose(Object o) {
		println(o.toString());
	}
	
	public static JLabel createDebugLabel(JPanel b, String label) {
		JLabel l = new JLabel(label);
		l.setVisible(Constants.DEBUG_MODE);
		b.add(l);
		return l;
	}

	public static JButton createDebugButton(JPanel b, String label) {
		JButton temp = new JButton(label);
		b.add(temp);
		temp.setVisible(Constants.DEBUG_MODE);
		return temp;
	}

	public static JSlider createDebugSlider(JPanel b, String title, int low, int high, int reg) {
		JLabel sliderTitle = new JLabel(title);
		sliderTitle.setVisible(Constants.DEBUG_MODE);
		b.add(sliderTitle);
		JSlider temp = new JSlider(low, high, reg);
		b.add(temp);
		temp.setVisible(Constants.DEBUG_MODE);
		return temp;
	}

	public static JCheckBox createDebugCheckBox(JPanel p, String label, boolean initValue) {
		JCheckBox temp = new JCheckBox(label);
		temp.setSelected(initValue);
		p.add(temp);
		temp.setVisible(Constants.DEBUG_MODE);
		return temp;
	}

	public static JTextField createDebugTextField(JPanel p, String label) {
		JLabel l = new JLabel(label);
		JTextField temp = new JTextField();
		p.add(temp);
		p.add(l);
		p.setVisible(Constants.DEBUG_MODE);
		temp.setVisible(Constants.DEBUG_MODE);
		return temp;
	}
}
