package manipulatives;
/*
 * ManFrame.java
 * -------------
 * This is only for testing purposes!  You don't actually use this.  You use CardGamePanel instead.
 */
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

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
import cards.TrickCard;
import deck.DeckView;
import extras.CardGameUtils;
import extras.Debug;
import extras.GameUtils;
import extras.RandomGenerator;

public class ManFrame extends JFrame implements KeyListener, ManPanelListener {
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
	private DeckView deckPresented;
	private CardView cardPlayed;

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
		this("2/3 of 51?", null, null);
	}

	// Creates a new ManFrame
	// If passed a non-null file, opens that file
	public ManFrame(String q, int answer, File file, DeckView dv, CardView cv) {
		setTitle("Manipulatives");
		myFrame = this;
		//solution = answer;
		question = q;
		solution = GameUtils.generateAnswer(q);
		assert(answer == solution);
		listeners = new ArrayList<ManListener>();
		controls = new ArrayList<JComponent>();
		userCanEdit = true;
		manPanel = new ManPanel(800, 600, this);
		manPanel.setToolTipText(Constants.TIP_MANIP_AREA);
		controls.add(manPanel);
		deckPresented = dv;
		cardPlayed = cv;

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
		questionAnswer.setToolTipText(Constants.TIP_USER_ANSWER);
		questionAnswer.setHorizontalAlignment(JTextField.CENTER);
		questionAnswer.addKeyListener(this);
		questionAnswer.requestFocusInWindow();
		questionBox.add(questionAnswer, BorderLayout.CENTER);
		controls.add(questionBox);

		JPanel questionButtons = new JPanel();
		questionButtons.setLayout(new BoxLayout(questionButtons, BoxLayout.X_AXIS));

		JButton questionBtn = new JButton(Constants.BTN_MAN_ANSWER);
		questionBtn.setToolTipText(Constants.TIP_ANSWER);
		questionBtn.setFont(Constants.FONT_REG);
		questionButtons.add(questionBtn);
		controls.add(questionBtn);
		questionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkUserAnswer();
			}
		});

		JButton notCoolManBtn = new JButton(Constants.MAN_FRAME_NO_ANSWER_BTN_TEXT);
		notCoolManBtn.setToolTipText(Constants.TIP_NOT_WHOLE);
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
		statusBox.setVisible(Constants.DEBUG_MODE);
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

		JButton clearScreenButton = new JButton(Constants.BTN_MAN_CLEAR);
		clearScreenButton.setToolTipText(Constants.TIP_CLEAR);
		clearScreenButton.setFont(Constants.FONT_SMALL);
		box.add(clearScreenButton);
		controls.add(clearScreenButton);
		clearScreenButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manPanel.clearAll();
			}
		});

		JButton launchDemo = new JButton(Constants.BTN_MAN_HELP);
		launchDemo.setToolTipText(Constants.TIP_SHOW);
		launchDemo.setFont(Constants.FONT_SMALL);
		box.add(launchDemo);
		controls.add(launchDemo);
		launchDemo.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disableControls();
				launchManipSimulation();
			}
		});

		ButtonGroup tools = new ButtonGroup();

		ImageIcon lineIcon = createImageIcon(Constants.PEN_ICON_IMG_PATH);
		ImageIcon pencilIcon = createImageIcon(Constants.LINE_ICON_IMG_PATH);
		ImageIcon pplIcon = createImageIcon(Constants.PPL_ICON_IMG_PATH);
		ImageIcon lineIconUn = createImageIcon(addUnselectedPath(Constants.PEN_ICON_IMG_PATH));
		ImageIcon pencilIconUn = createImageIcon(addUnselectedPath(Constants.LINE_ICON_IMG_PATH));
		ImageIcon pplIconUn = createImageIcon(addUnselectedPath(Constants.PPL_ICON_IMG_PATH));

		JRadioButton drawLines = createRadioButton(lineIcon, lineIconUn, box, tools, Constants.TIP_LINE);
		drawLines.setSelected(true);
		drawLines.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manPanel.setPencilMode(false);
			}
		});

		JRadioButton makePpl = createRadioButton(pplIcon, pplIconUn, box, tools, Constants.TIP_PPL);
		makePpl.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manPanel.setPplMode(true);
			}
		});

		JRadioButton drawFreely = createRadioButton(pencilIcon, pencilIconUn, box, tools, Constants.TIP_PENCIL);
		drawFreely.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manPanel.setPencilMode(true);
			}
		});

		JButton animateResultBtn = Debug.createDebugButton(box, "animate stinky");
		animateResultBtn.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disableControls();
				launchResultAnimation();
			}
		});

		ManDeckViewPanel deckViewShown = new ManDeckViewPanel(dv, cv);
		deckViewShown.setPreferredSize(new Dimension(Constants.ORIG_CARD_WIDTH, getHeight()));
		add(deckViewShown, BorderLayout.EAST);

		if(!Constants.DEBUG_MODE) {
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}else{
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

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

	public String stripQuestion(String pregunta) {
		int leftPar = pregunta.indexOf("(");
		int rightPar = pregunta.indexOf(")");
		if(leftPar == -1 || rightPar == -1) {
			return pregunta;
		}
		String leftPart = pregunta.substring(0, leftPar-1);
		String rightPart = pregunta.substring(rightPar+2);
		String decimal = pregunta.substring(leftPar+1, rightPar);
		int num = GameUtils.extractNumerator(question);
		int numPos = pregunta.indexOf(""+num);
		int denPos = pregunta.indexOf(" of ");
		leftPart = pregunta.substring(0, numPos);
		rightPart = pregunta.substring(denPos);
		return leftPart+decimal+rightPart;
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

	public void launchResultAnimation() {
		int num = GameUtils.extractNumerator(question);
		int den = GameUtils.extractDenominator(question);
		int ppl = GameUtils.extractPeople(question);
		boolean isStink = false;
		if(cardPlayed != null) {
			isStink = ((TrickCard) cardPlayed.getCard()).isStink();
		}
		manPanel.launchResultAnimation(ppl, num, den, solution, isStink);
	}

	private void populateWithNPeople(int ppl, int den) {
		String s = Constants.MAN_HELP_PLACE_PREFIX + ppl + Constants.MAN_HELP_PLACE_SUFFIX;
		manPanel.drawPeople(ppl, den);
		drawMessage(s);
	}

	private void circleNGroups(int num, int den, int answer) {
		if(answer == -1) {
			String s = Constants.MAN_MSG_NOT_COOL_MAN;
			drawMessage(s);
			return;
		}
		String s = Constants.MAN_HELP_CIRCLE_PREFIX + num + Constants.MAN_HELP_CIRCLE_SUFFIX;
		drawMessage(s);
		manPanel.drawOvals(num, den);
	}

	private void drawMessage(String s) {
		manPanel.displayMessage(s);
	}

	private void checkUserAnswer() {
		int num = 0;
		try{
			String s = questionAnswer.getText().trim();
			num = Integer.parseInt(s);
		}catch(NumberFormatException ex) {
			JOptionPane.showMessageDialog(myFrame,
					Constants.ERROR_INPUT_NO_INT,
					Constants.ERROR_NOT_AN_INTEGER,
					JOptionPane.ERROR_MESSAGE);
			questionAnswer.setText("");
			return;
		}
		compareToAnswer(num);
	}

	private void compareToAnswer(int num) {
		if(num == solution) {
			manPanel.displayMessage(Constants.CORRECT);
			disableControls();
			launchResultAnimation();
		}else{
			String response = Constants.ERROR_WRONG_ANSWER + Constants.SENTENCE_SEP + Constants.ERROR_TRY_ONCE_MORE;
			if(solution == -1) {
				response = Constants.ERROR_WRONG_ANSWER + Constants.SENTENCE_SEP + Constants.ERROR_TRY_AGAIN;
			}
			JOptionPane.showMessageDialog(myFrame,
					response,
					Constants.ERROR_NOT_QUITE,
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void windowFinished() {
		fireWindowDone();
	}

	private JRadioButton createRadioButton(ImageIcon selected, ImageIcon unselected, JPanel box, ButtonGroup tools, String tipText) {
		JRadioButton temp = new JRadioButton(unselected);
		temp.setToolTipText(tipText);
		tools.add(temp);
		temp.setSelectedIcon(selected);
		controls.add(temp);
		box.add(temp);
		return temp;
	}

	private ImageIcon createImageIcon(String path) {
		BufferedImage bi = CardGameUtils.getCardImage(path);
		return new ImageIcon(bi);
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

