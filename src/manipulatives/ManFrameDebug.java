package manipulatives;
// ManFrame.java
/**
 Hosts a ManPanel.
 Implements the UI logic for save/saveAs/open/quit
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import basic.Constants;
import extras.Debug;
import extras.GameUtils;
import extras.RandomGenerator;

public class ManFrameDebug extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -36920913712532509L;
	private static RandomGenerator rgen = RandomGenerator.getInstance();
	private ManFrameDebug myFrame;
	private int solution;
	private ArrayList<ManListener> listeners;
	private String question;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }

		File fileToOpen = null;

		// Notice command line arg filename
		if (args.length != 0) {
			fileToOpen = new File(args[0]);
		}

		new ManFrameDebug(fileToOpen);

	}

	private ManPanel manPanel;
	private File saveFile; // the last place we saved, or null
	
	public ManFrameDebug(String q, int answer) {
		this(q, answer, null);
	}

	public ManFrameDebug(File file) {
		this("What is 1/3 of 30?", 6, file);
	}

	// Creates a new ManFrame
	// If passed a non-null file, opens that file
	public ManFrameDebug(String q, int answer, File file) {
		setTitle("Manipulatives");
		myFrame = this;
		solution = answer;
		listeners = new ArrayList<ManListener>();
		question = q;
		manPanel = new ManPanel(800, 600, null);
		if (file != null) manPanel.open(file);

		JPanel manBox = new JPanel();
		manBox.setLayout(new BorderLayout());
		manBox.add(manPanel, BorderLayout.CENTER);
		add(manBox, BorderLayout.CENTER);

		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
		add(box, BorderLayout.WEST);

		JPanel statusBox = new JPanel();
		statusBox.setLayout(new BoxLayout(statusBox, BoxLayout.X_AXIS));
		manBox.add(statusBox, BorderLayout.SOUTH);

		/*
		 Create the checkboxes and wire them to setters
		 on the ManPanel for each boolean feature.
		 */
		/*final JCheckBox print = new JCheckBox("Print");
		box.add(print);
		print.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					manPanel.setPrint(print.isSelected());
				}
		});

		final JCheckBox smart = new JCheckBox("Smart repaint", true);
		box.add(smart);
		smart.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					manPanel.setSmart(smart.isSelected());
				}
		});


		final JCheckBox old = new JCheckBox("Old repaint", true);
		box.add(old);
		old.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					manPanel.setOld(old.isSelected());
				}
		});

		final JCheckBox red = new JCheckBox("Red paint region");
		box.add(red);
		red.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					manPanel.setRed(red.isSelected());
				}
		});
		 */
		JLabel addLabel = new JLabel("Add this many men");
		statusBox.add(addLabel);

		JPanel questionBox = new JPanel();
		questionBox.setLayout(new BorderLayout());

		JLabel questionLabel = new JLabel(question, JLabel.CENTER);
		questionLabel.setFont(Constants.FONT_REG);
		questionBox.add(questionLabel, BorderLayout.WEST);

		final JTextField questionAnswer = new JTextField(3);
		questionAnswer.setFont(Constants.FONT_REG);
		questionAnswer.setHorizontalAlignment(JTextField.CENTER);
		questionBox.add(questionAnswer, BorderLayout.CENTER);

		JPanel questionButtons = new JPanel();
		questionButtons.setLayout(new BoxLayout(questionButtons, BoxLayout.X_AXIS));
		
		JButton questionBtn = new JButton("Answer");
		questionBtn.setFont(Constants.FONT_REG);
		questionButtons.add(questionBtn);
		questionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					int num = 0;
					try{
						num = Integer.parseInt(questionAnswer.getText());
					}catch(NumberFormatException ex) {
						JOptionPane.showMessageDialog(myFrame,
							    "Please Enter a Whole Number",
							    "Not an Integer",
							    JOptionPane.ERROR_MESSAGE);
						questionAnswer.setText("");
						return;
					}
					compareToAnswer(num);
			}
		});
		
		JButton notCoolManBtn = new JButton("Not Cool Man!");
		notCoolManBtn.setFont(Constants.FONT_REG);
		questionButtons.add(notCoolManBtn);
		notCoolManBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compareToAnswer(-1);
			}
		});
		questionBox.add(questionButtons, BorderLayout.EAST);
		add(questionBox, BorderLayout.NORTH);

		final JTextField addMany = new JTextField("");
		statusBox.add(addMany);

		JButton addButton = new JButton("Add to Screen");
		statusBox.add(addButton);
		addButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int numMen = 0;
				try {
					numMen = Integer.parseInt(addMany.getText());
				}catch(NumberFormatException ne) {
					ne.printStackTrace();
				}
				for(int i = 0; i < numMen; i++) {
					manPanel.doAdd(rgen.nextInt(Constants.MAN_WIDTH/2, manPanel.getWidth()-Constants.MAN_WIDTH/2), rgen.nextInt(Constants.MAN_HEIGHT/2, manPanel.getHeight()-Constants.MAN_HEIGHT/2));
				}
			}
		});

		// Buttons for file save/open

		JButton button;
		button = new JButton("Save...");
		box.add(button);
		button.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSave(false);
			}
		});

		button = new JButton("Save As...");
		box.add(button);
		button.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSave(true);
			}
		});

		button = new JButton("Open...");
		box.add(button);
		button.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doOpen();
			}
		});


		// Make a Save Image button
		JButton imageButton = new JButton("Save PNG Image");
		box.add(imageButton);
		imageButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = createFileChooser();
				int status = chooser.showSaveDialog(ManFrameDebug.this);
				// depending on the os, it may help if the user
				// gives the file a name ending in .png
				if (status == JFileChooser.APPROVE_OPTION) {
					File dest = chooser.getSelectedFile();
					manPanel.saveImage(dest);
				}
			}
		});

		JButton shufButton = new JButton("Shuffle Objects");
		box.add(shufButton);
		shufButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manPanel.shuffle();
			}
		});

		JButton splitButton = new JButton("Clear Screen");
		box.add(splitButton);
		splitButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manPanel.clearAll();
			}
		});
		
		JButton launchDemo = new JButton("Show Me How");
		box.add(launchDemo);
		launchDemo.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchManipSimulation();
			}
		});

		final JCheckBox lineVsPencil = new JCheckBox("Act as a Pencil?");
		lineVsPencil.setSelected(true);
		box.add(lineVsPencil);
		lineVsPencil.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manPanel.setPencilMode(lineVsPencil.isSelected());
			}
		});

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// Listener for the case where the user closes the frame
		// In that case, check the dirty bit
//		addWindowListener(new WindowAdapter() {
//			public void windowClosing(WindowEvent event) {
//				/*boolean ok = doCheckQuit();
//        			if (!ok) return;*/
//				setVisible(false);
//			}
//		});


		pack();
		setVisible(true);
	}
	
	private void launchManipSimulation() {
		int num = GameUtils.extractNumerator(question);
		int den = GameUtils.extractDenominator(question);
		int ppl = GameUtils.extractPeople(question);
		Debug.println("num is " + num + ", den is " + den + ", and ppl are " + ppl);
		manPanel.launchDividingAnimation(den, ppl, num, solution);
		//manPanel.launchPeopleAddAnimation(ppl, den, num, solution);
		//populateWithNPeople(ppl, den);
		//circleNGroups(num, den, solution);
	}
	
	private void drawLines() {
		
	}
	
	private void populateWithNPeople(int ppl, int den) {
		String s = "Place " + ppl + " people going in a circle.";
		manPanel.drawPeople(ppl, den);
		drawMessage(s);
	}
	
	private void circleNGroups(int num, int den, int answer) {
		if(answer == -1) {
			String s = Constants.MAN_MSG_NOT_COOL_MAN;
			drawMessage(s);
			return;
		}
		String s = "Circle " + num + " of those groups.";
		drawMessage(s);
		manPanel.drawOvals(num, den);
	}
	
	private void drawMessage(String s) {
		manPanel.displayMessage(s);
	}

	// Save (or saveAs) the panel
	public void doSave(boolean saveAs) {
		// prompt where to save if needed
		if (saveFile == null || saveAs) {
			JFileChooser chooser = createFileChooser();
			int status = chooser.showSaveDialog(this);
			if (status == JFileChooser.APPROVE_OPTION) {
				saveFile = chooser.getSelectedFile();
			}
			else return;  // i.e. cancel the whole operation
		}

		// Do the save, set the frame title
		manPanel.save(saveFile);
		setTitle(saveFile.getName());
	}

	// Asks if they want to save, and if so does it.
	// Returns false if they cancel.
	public boolean saveOk() {
		int result = JOptionPane.showConfirmDialog(
				this, "Save changes first?", "Save?",
				JOptionPane.YES_NO_CANCEL_OPTION);

		if (result == JOptionPane.YES_OPTION) {
			doSave(false);
		}

		return (result != JOptionPane.CANCEL_OPTION);
	}

	// Checks if quiting is ok, and saves if needed.
	// Returns false if they cancel.
	public boolean doCheckQuit() {
		if (manPanel.getDirty()) {
			boolean ok = saveOk();
			if (!ok) return false;
		}
		return true;
	}

	// Prompts for a file and reads it in
	// Asks to write out old contents first if dirty.
	public void doOpen() {
		if (manPanel.getDirty()) {
			boolean ok = saveOk();
			if (!ok) return;
		}

		JFileChooser chooser = createFileChooser();
		int status = chooser.showOpenDialog(this);
		if (status == JFileChooser.APPROVE_OPTION) {
			manPanel.open(chooser.getSelectedFile());
			// now that file is the "current" file for the window
			saveFile = chooser.getSelectedFile();
			setTitle(saveFile.getName());
		}
	}

	// Creates a new JFileChooser, doing the boilerplate
	// to start it in the current directory.
	private JFileChooser createFileChooser() {
		JFileChooser chooser = new JFileChooser();
		try {
			// The "." stuff attempts to open in the "current"
			// directory.
			File dir = new File(new File(".").getCanonicalPath());
			chooser.setCurrentDirectory(dir);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return chooser;
	}
	
	private void compareToAnswer(int num) {
		if(num == solution) {
			fireWindowDone();
		}else{
			String response = "That is not the right answer, it is " + solution;
			if(solution == -1) {
				response = "That is not the right answer, Are you making equal groups?";
			}
			JOptionPane.showMessageDialog(myFrame,
				    response,
				    "Nope!",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void fireWindowDone() {
		for(ManListener l:listeners) {
			l.manipWindowDone(null, null);
		}
	}
	
	public void addManListener(ManListener l) {
		listeners.add(l);
	}
}

