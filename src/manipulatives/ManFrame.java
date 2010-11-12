package manipulatives;
// ManFrame.java
/**
 Hosts a ManPanel.
 Implements the UI logic for save/saveAs/open/quit
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;

import basic.Constants;
import cards.CardView;
import deck.DeckView;
import extras.Debug;
import extras.RandomGenerator;

public class ManFrame extends JFrame implements KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -36920913712532509L;
	private static RandomGenerator rgen = RandomGenerator.getInstance();
	private ManFrame myFrame;
	private int solution;
	private ArrayList<ManListener> listeners;
	private String question;
	private boolean userCanEdit;
	private ArrayList<JComponent> controls;
	private JTextField questionAnswer;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }

		File fileToOpen = null;

		// Notice command line arg filename
		if (args.length != 0) {
			fileToOpen = new File(args[0]);
		}

		new ManFrame(fileToOpen);

	}

	private ManPanel manPanel;
	private File saveFile; // the last place we saved, or null

	public ManFrame(String q, int answer, DeckView dv, CardView cv) {
		this(q, answer, null, dv, cv);
	}

	public ManFrame(String q, DeckView dv, CardView cv) {
		this(q, 0, dv, cv);
	}

	public ManFrame(File file) {
		this("1/3 of 51?", null, null);
	}

	// Creates a new ManFrame
	// If passed a non-null file, opens that file
	public ManFrame(String q, int answer, File file, DeckView dv, CardView cv) {
		setTitle("Manipulatives");
		myFrame = this;
		//solution = answer;
		question = q;
		solution = generateAnswer(q);
		assert(answer == solution);
		listeners = new ArrayList<ManListener>();
		controls = new ArrayList<JComponent>();
		userCanEdit = true;
		manPanel = new ManPanel(800, 600, this);
		controls.add(manPanel);
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

		JLabel addLabel = new JLabel("Add this many men");
		statusBox.add(addLabel);

		JPanel questionBox = new JPanel();
		questionBox.setLayout(new BorderLayout());

		JLabel questionLabel = new JLabel(stripQuestion(question), JLabel.CENTER);
		questionLabel.setFont(Constants.FONT_REG);
		questionBox.add(questionLabel, BorderLayout.WEST);

		questionAnswer = new JTextField(3);
		questionAnswer.setFont(Constants.FONT_REG);
		questionAnswer.setHorizontalAlignment(JTextField.CENTER);
		questionAnswer.addKeyListener(this);
		questionAnswer.requestFocusInWindow();
		questionBox.add(questionAnswer, BorderLayout.CENTER);
		controls.add(questionBox);

		JPanel questionButtons = new JPanel();
		questionButtons.setLayout(new BoxLayout(questionButtons, BoxLayout.X_AXIS));

		JButton questionBtn = new JButton("Answer");
		questionBtn.setFont(Constants.FONT_REG);
		questionButtons.add(questionBtn);
		controls.add(questionBtn);
		questionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkUserAnswer();
			}
		});

		JButton notCoolManBtn = new JButton(Constants.MAN_FRAME_NO_ANSWER_BTN_TEXT);
		notCoolManBtn.setFont(Constants.FONT_REG);
		questionButtons.add(notCoolManBtn);
		controls.add(notCoolManBtn);
		notCoolManBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compareToAnswer(-1);

			}
		});
		questionBox.add(questionButtons, BorderLayout.EAST);
		add(questionBox, BorderLayout.NORTH);

		final JTextField addMany = new JTextField("");
		statusBox.add(addMany);
		controls.add(addMany);

		JButton addButton = new JButton("Add to Screen");
		statusBox.add(addButton);
		controls.add(addButton);
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

		//		JButton shufButton = new JButton("Shuffle Objects");
		//		box.add(shufButton);
		//		controls.add(shufButton);
		//		shufButton.addActionListener( new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				manPanel.shuffle();
		//			}
		//		});

		JButton splitButton = new JButton("Clear Screen");
		box.add(splitButton);
		controls.add(splitButton);
		splitButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manPanel.clearAll();
			}
		});

		JButton launchDemo = new JButton("Show Me How");
		box.add(launchDemo);
		controls.add(launchDemo);
		launchDemo.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disableControls();
				launchManipSimulation();
			}
		});
		
		ButtonGroup tools = new ButtonGroup();
		
		ImageIcon lineIcon = new ImageIcon(Constants.PEN_ICON_IMG_PATH);
		ImageIcon pencilIcon = new ImageIcon(Constants.LINE_ICON_IMG_PATH);
		ImageIcon pplIcon = new ImageIcon(Constants.PPL_ICON_IMG_PATH);
		ImageIcon lineIconUn = new ImageIcon(addUnselectedPath(Constants.PEN_ICON_IMG_PATH));
		ImageIcon pencilIconUn = new ImageIcon(addUnselectedPath(Constants.LINE_ICON_IMG_PATH));
		ImageIcon pplIconUn = new ImageIcon(addUnselectedPath(Constants.PPL_ICON_IMG_PATH));

		JRadioButton drawLines = createRadioButton(lineIcon, lineIconUn, box, tools);
		drawLines.setSelected(true);
		drawLines.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manPanel.setPencilMode(false);
			}
		});
		
		JRadioButton drawFreely = createRadioButton(pencilIcon, pencilIconUn, box, tools);
		drawFreely.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manPanel.setPencilMode(true);
			}
		});
		
		JRadioButton makePpl = createRadioButton(pplIcon, pplIconUn, box, tools);
		makePpl.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manPanel.setPplMode(true);
			}
		});

		DeckViewPanel deckViewShown = new DeckViewPanel(dv, cv);
		deckViewShown.setPreferredSize(new Dimension(Constants.ORIG_CARD_WIDTH, getHeight()));
		add(deckViewShown, BorderLayout.EAST);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		pack();
		setVisible(true);
	}

	public void disableControls() {
		userCanEdit = false;
		for(JComponent jc:controls) {
			jc.setEnabled(false);
		}
	}

	public void enableControls() {
		userCanEdit = true;
		for(JComponent jc:controls) {
			jc.setEnabled(true);
		}
	}

	public String stripQuestion(String q) {
		int leftPar = q.indexOf("(");
		int rightPar = q.indexOf(")");
		if(leftPar == -1 || rightPar == -1) {
			return q;
		}
		String leftPart = q.substring(0, leftPar-1);
		String rightPart = q.substring(rightPar+2);
		String decimal = q.substring(leftPar+1, rightPar);
		int num = extractNumerator();
		int numPos = q.indexOf(""+num);
		int denPos = q.indexOf(" of ");
		leftPart = q.substring(0, numPos);
		rightPart = q.substring(denPos);
		return leftPart+decimal+rightPart;
	}

	private void launchManipSimulation() {
		int num = extractNumerator();
		int den = extractDenominator();
		int ppl = extractPeople();
		Debug.println("num is " + num + ", den is " + den + ", and ppl are " + ppl);
		manPanel.launchDividingAnimation(den, ppl, num, solution);
		//manPanel.launchPeopleAddAnimation(ppl, den, num, solution);
		//populateWithNPeople(ppl, den);
		//circleNGroups(num, den, solution);
	}

	private int generateAnswer(String question) {
		int num = extractNumerator();
		int den = extractDenominator();
		int ppl = extractPeople();
		if(ppl % den != 0) {
			return -1;
		}
		return (ppl / den) * num;
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

	/* Assumes that there is only one forward slash and that the
	 * numbers needed are:  num/den of ppl?
	 */
	private int extractNumerator() {
		int pos = question.indexOf("/");
		int spacePos = question.lastIndexOf(" ", pos);
		if(pos == -1) {
			return -1;
		}
		return Integer.parseInt(question.substring(spacePos+1, pos));
	}

	private int extractDenominator() {
		int pos = question.indexOf("/");
		int spacePos = question.indexOf(" ", pos);
		if(pos == -1 || spacePos == -1) {
			return -1;
		}
		return Integer.parseInt(question.substring(pos+1, spacePos));
	}

	private int extractPeople() {
		int pos = question.indexOf("?");
		int spacePos = question.lastIndexOf(" ", pos);
		if(pos == -1 || spacePos == -1) {
			return -1;
		}
		return Integer.parseInt(question.substring(spacePos+1, pos));
	}

	private void checkUserAnswer() {
		int num = 0;
		try{
			String s = questionAnswer.getText().trim();
			num = Integer.parseInt(s);
		}catch(NumberFormatException ex) {
			JOptionPane.showMessageDialog(myFrame,
					Constants.ERROR_INPUT_NO_INT,
					"Not an Integer",
					JOptionPane.ERROR_MESSAGE);
			questionAnswer.setText("");
			return;
		}
		compareToAnswer(num);
	}

	private void compareToAnswer(int num) {
		if(num == solution) {
			manPanel.displayMessage(Constants.CORRECT);
			fireWindowDone();
		}else{
			String response = Constants.ERROR_WRONG_ANSWER+"Please try again.";
			if(solution == -1) {
				response = Constants.ERROR_WRONG_ANSWER+"Try again.";
			}
			JOptionPane.showMessageDialog(myFrame,
					response,
					"Not Quite!",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private JRadioButton createRadioButton(ImageIcon selected, ImageIcon unselected, JPanel box, ButtonGroup tools) {
		JRadioButton temp = new JRadioButton(unselected);
		tools.add(temp);
		temp.setSelectedIcon(selected);
		controls.add(temp);
		box.add(temp);
		return temp;
	}

	private void fireWindowDone() {
		for(ManListener l:listeners) {
			l.manipWindowDone(null, this);
		}
	}
	
	private String addUnselectedPath(String s) {
		int pos = s.indexOf(".png");
		return s.substring(0, pos) + "unselected" + s.substring(pos);
	}

	public void addManListener(ManListener l) {
		listeners.add(l);
	}

	//@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			checkUserAnswer();
		}
	}

	//@Override
	public void keyReleased(KeyEvent arg0) {
	}

	//@Override
	public void keyTyped(KeyEvent arg0) {
	}
}
