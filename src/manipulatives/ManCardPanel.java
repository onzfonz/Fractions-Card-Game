package manipulatives;
// ManFrame.java
/**
 Hosts a ManPanel.
 Implements the UI logic for save/saveAs/open/quit
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import basic.Constants;
import cards.CardView;
import deck.DeckView;
import extras.CardGameUtils;
import extras.Debug;
import extras.RandomGenerator;

public class ManCardPanel extends JPanel implements KeyListener, ManPanelListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -36920913712532509L;
	private static RandomGenerator rgen = RandomGenerator.getInstance();
	private ManCardPanel myFrame;
	private int solution;
	private ArrayList<ManListener> listeners;
	private String question;
	private boolean userCanEdit;
	private ArrayList<JComponent> controls;
	private JTextField questionAnswer;
	private boolean isPlaying;
	
	private JPanel box;
	private JRadioButton makePpl;
	private JRadioButton drawFreely;
	private JRadioButton drawLines;

	private ManPanel manPanel;
	private ManDeckViewPanel deckViewShown;
	private File saveFile; // the last place we saved, or null
	private JButton clearScreenButton;
	private JButton launchDemo;
	private String currentArea;
	private JPanel manBox;
	private JButton questionBtn;
	private JButton notCoolManBtn;
	private JLabel questionLabel;
	private JPanel questionBox;
	
	private static final int GLUE_NUMS = 10;
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }

		File fileToOpen = null;

		// Notice command line arg filename
		if (args.length != 0) {
			fileToOpen = new File(args[0]);
		}

		JFrame f = new JFrame("ManCardPanel Test");
		ManCardPanel mcp = new ManCardPanel(fileToOpen);
		f.setLayout(new BorderLayout());
		f.add(mcp, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public ManCardPanel() {
		this(Constants.MAN_FRAME_DEFAULT_PLAY, null, null);
	}

	public ManCardPanel(String q, int answer, DeckView dv, CardView cv) {
		this(q, answer, null, dv, cv);
	}

	public ManCardPanel(String q, DeckView dv, CardView cv) {
		this(q, 0, dv, cv);
	}

	public ManCardPanel(File file) {
		this("Osvaldo, What is 1/3 of 51?", null, null);
	}

	// Creates a new ManFrame
	// If passed a non-null file, opens that file
	public ManCardPanel(String q, int answer, File file, DeckView dv, CardView cv) {
		setLayout(new BorderLayout());
		myFrame = this;
		//solution = answer;
		question = q;
		solution = generateAnswer(q);
		assert(answer == solution);
		listeners = new ArrayList<ManListener>();
		controls = new ArrayList<JComponent>();
		userCanEdit = true;
		manPanel = new ManPanel(800, 550, this);
		manPanel.setToolTipText(Constants.TIP_MANIP_AREA);
		controls.add(manPanel);
		if (file != null) manPanel.open(file);

		manBox = new JPanel();
		manBox.setLayout(new BorderLayout());
		manBox.add(manPanel, BorderLayout.CENTER);
		add(manBox, BorderLayout.CENTER);

		box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
		add(box, BorderLayout.WEST);

		JPanel statusBox = new JPanel();
		statusBox.setLayout(new BoxLayout(statusBox, BoxLayout.X_AXIS));
		manBox.add(statusBox, BorderLayout.SOUTH);

		questionBox = new JPanel();
		questionBox.setLayout(new BoxLayout(questionBox, BoxLayout.X_AXIS));

		createHorizontalGlue(questionBox, GLUE_NUMS);
		questionLabel = new JLabel(stripQuestion(question), JLabel.CENTER);
		questionLabel.setFont(Constants.FONT_REG);
		questionBox.add(questionLabel);
		
		createHorizontalGlue(questionBox, GLUE_NUMS/3);
		
		questionAnswer = new JTextField(3);
		questionAnswer.setFont(Constants.FONT_REG);
		questionAnswer.setToolTipText(Constants.TIP_USER_ANSWER);
		questionAnswer.setHorizontalAlignment(JTextField.CENTER);
		questionAnswer.setMaximumSize(questionAnswer.getPreferredSize());
		questionAnswer.addKeyListener(this);
		askForFocus();
		questionBox.add(questionAnswer);
		controls.add(questionBox);

		createHorizontalGlue(questionBox, GLUE_NUMS/10);

		String buttonTxt = Constants.BTN_MAN_ANSWER;
		isPlaying = question.equals(Constants.MAN_FRAME_DEFAULT_PLAY);
		if(isPlaying) {
			buttonTxt = Constants.BTN_BACK_TO_GAME;
		}
		questionBtn = new JButton(buttonTxt);
		questionBtn.setToolTipText(Constants.TIP_ANSWER);
		questionBtn.setFont(Constants.FONT_REG);
		questionBox.add(questionBtn);
		createHorizontalGlue(questionBox, GLUE_NUMS/5);
		controls.add(questionBtn);
		questionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!isPlaying) {
					checkUserAnswer();
				}else{
					fireToggleManip();
				}
			}
		});

		notCoolManBtn = new JButton(Constants.MAN_FRAME_NO_ANSWER_BTN_TEXT);
		notCoolManBtn.setToolTipText(Constants.TIP_NOT_WHOLE);
		notCoolManBtn.setFont(Constants.FONT_REG);
		notCoolManBtn.setVisible(!isPlaying);
		questionBox.add(notCoolManBtn);
		createHorizontalGlue(questionBox, GLUE_NUMS);
		controls.add(notCoolManBtn);
		notCoolManBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compareToAnswer(-1);
			}
		});
		add(questionBox, BorderLayout.NORTH);
		JLabel addMany = new JLabel(new ImageIcon(CardGameUtils.getCardImageViaFilename("numberline-825.png")));
		statusBox.setBackground(Color.WHITE);
		statusBox.add(addMany);
		controls.add(addMany);

		//		JButton shufButton = new JButton("Shuffle Objects");
		//		box.add(shufButton);
		//		controls.add(shufButton);
		//		shufButton.addActionListener( new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				manPanel.shuffle();
		//			}
		//		});

		clearScreenButton = new JButton(Constants.BTN_MAN_CLEAR);
		clearScreenButton.setToolTipText(Constants.TIP_CLEAR);
		clearScreenButton.setFont(Constants.FONT_SMALL);
		box.add(clearScreenButton);
		controls.add(clearScreenButton);
		clearScreenButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manPanel.clearAll();
			}
		});

		launchDemo = new JButton(Constants.BTN_MAN_HELP);
		launchDemo.setToolTipText(Constants.TIP_SHOW);
		launchDemo.setFont(Constants.FONT_SMALL);
		launchDemo.setVisible(!isPlaying);
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

		drawLines = createRadioButton(lineIcon, lineIconUn, box, tools, Constants.TIP_LINE);
		//drawLines.setSelected(true);
		drawLines.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manPanel.setPencilMode(false);
			}
		});
		
		makePpl = createRadioButton(pplIcon, pplIconUn, box, tools, Constants.TIP_PPL);
		makePpl.setSelected(true);
		manPanel.setPplMode(true);
		makePpl.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manPanel.setPplMode(true);
			}
		});
		
		drawFreely = createRadioButton(pencilIcon, pencilIconUn, box, tools, Constants.TIP_PENCIL);
		drawFreely.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manPanel.setPencilMode(true);
			}
		});
		
		final JLabel redLabel = createDebugLabel(box, "r");
		final JSlider redSlider = createDebugSlider(box, "Red", 0, 255, 128);
		redSlider.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				Color curColor = getBackgroundColorForBase();
				int blue = curColor.getBlue();
				int green = curColor.getGreen();
				Color tempColor = new Color(redSlider.getValue(), green, blue);
				//gamePanel.setBackground(tempColor);
				changeElemsToColor(tempColor, currentArea);
				redLabel.setText("" + redSlider.getValue());
			}
		});

		final JLabel greenLabel = createDebugLabel(box, "g");
		final JSlider greenSlider = createDebugSlider(box, "Green", 0, 255, 128);
		greenSlider.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				Color curColor = getBackgroundColorForBase();
				int blue = curColor.getBlue();
				int red = curColor.getRed();
				Color tempColor = new Color(red, greenSlider.getValue(), blue);
				//gamePanel.setBackground(tempColor);
				changeElemsToColor(tempColor, currentArea);
				greenLabel.setText("" + greenSlider.getValue());
			}
		});

		final JLabel blueLabel = createDebugLabel(box, "b");
		final JSlider blueSlider = createDebugSlider(box, "Blue", 0, 255, 128);
		blueSlider.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				Color curColor = getBackgroundColorForBase();
				int red = curColor.getRed();
				int green = curColor.getGreen();
				Color tempColor = new Color(red, green, blueSlider.getValue());
				//gamePanel.setBackground(tempColor);
				changeElemsToColor(tempColor, currentArea);
				blueLabel.setText("" + blueSlider.getValue());
			}
		});
		
		currentArea = Constants.OPTION_WEST;
		String[] elems = {Constants.OPTION_WEST, Constants.OPTION_NORTH, Constants.OPTION_CENTER, Constants.OPTION_SOUTH, Constants.OPTION_EAST, Constants.OPTION_OUTER};
		final JComboBox areaOption = new JComboBox(elems);
		areaOption.setVisible(Constants.SHOW_COLOR_SLIDERS);
		box.add(areaOption);
		areaOption.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentArea = (String) areaOption.getSelectedItem();
			}
		});

		deckViewShown = new ManDeckViewPanel(dv, cv);
		deckViewShown.setPreferredSize(new Dimension(Constants.ORIG_CARD_WIDTH, getHeight()));
		add(deckViewShown, BorderLayout.EAST);

		changeElemsToColor(Constants.COLOR_SKIN_TONE, Constants.OPTION_WEST);
		changeElemsToColor(Constants.MANIP_CENTER_BACKGROUND, Constants.OPTION_CENTER);
		changeElemsToColor(Constants.COLOR_SKIN_TONE, Constants.OPTION_NORTH);
		changeElemsToColor(Constants.COLOR_SKIN_TONE, Constants.OPTION_EAST);
		setVisible(true);
	}

	public void disableControls() {
		userCanEdit = false;
		for(JComponent jc:controls) {
			jc.setEnabled(false);
		}
	}
	
	private void createHorizontalGlue(JPanel jp, int numTimes) {
		for(int i = 0; i < numTimes; i++) {
			jp.add(Box.createHorizontalGlue());
		}
	}

	public void enableControls() {
		userCanEdit = true;
		for(JComponent jc:controls) {
			jc.setEnabled(true);
		}
	}
	
	public JLabel createDebugLabel(JPanel b, String label) {
		JLabel l = new JLabel(label);
		l.setVisible(Constants.DEBUG_MODE);
		b.add(l);
		return l;
	}
	
	private Color getBackgroundColorForBase() {
		if(currentArea.equals(Constants.OPTION_WEST)) {
			return box.getBackground();
		}else if(currentArea.equals(Constants.OPTION_NORTH)) {
			return questionBox.getBackground();
		}else if(currentArea.equals(Constants.OPTION_CENTER)) {
			return manPanel.getBackground();
		}else if(currentArea.equals(Constants.OPTION_EAST)){
			return deckViewShown.getBackground();
		}
		return box.getBackground();
	}
	
	private void changeElemsToColor(Color tempColor, String dir) {
		if(dir.equals(Constants.OPTION_WEST)||dir.equals(Constants.OPTION_OUTER)) {
			box.setBackground(tempColor);
			drawLines.setBackground(tempColor);
			makePpl.setBackground(tempColor);
			drawFreely.setBackground(tempColor);
			clearScreenButton.setBackground(tempColor);
			launchDemo.setBackground(tempColor);
		}
		if(dir.equals(Constants.OPTION_NORTH)||dir.equals(Constants.OPTION_OUTER)) {
			notCoolManBtn.setBackground(tempColor);
			questionBtn.setBackground(tempColor);
			questionBox.setBackground(tempColor);
			questionLabel.setBackground(tempColor);
			//questionAnswer.setBackground(tempColor);
		}
		if(dir.equals(Constants.OPTION_SOUTH)) {
			
		}
		if(dir.equals(Constants.OPTION_CENTER)) {
			manPanel.setBackground(tempColor);
		}
		if(dir.equals(Constants.OPTION_EAST)||dir.equals(Constants.OPTION_OUTER)) {
			deckViewShown.setBackground(tempColor);
		}
	}
	
	public JSlider createDebugSlider(JPanel b, String title, int low, int high, int reg) {
		JLabel sliderTitle = new JLabel(title);
		sliderTitle.setVisible(Constants.SHOW_COLOR_SLIDERS);
		b.add(sliderTitle);
		JSlider temp = new JSlider(low, high, reg);
		b.add(temp);
		temp.setVisible(Constants.SHOW_COLOR_SLIDERS);
		return temp;
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
			fireWindowDone();
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
			l.manipPanelClosed(this);
		}
	}
	
	private void fireToggleManip() {
		for(ManListener l:listeners) {
			l.toggleManipView();
		}
	}
	
	public String getQuestion() {
		return question;
	}
	
	public void askForFocus() {
		questionAnswer.requestFocusInWindow();
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

