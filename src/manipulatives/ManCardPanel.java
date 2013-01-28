package manipulatives;
// ManFrame.java
/**
 Hosts a ManPanel.
 Implements the UI logic for save/saveAs/open/quit
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
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
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import network.NetDelegate;
import network.NetHelper;
import basic.Constants;
import basic.GamePanel;
import cards.CardView;
import cards.TrickCard;
import deck.DeckView;
import extras.CardGameUtils;
import extras.Debug;
import extras.GameImages;
import extras.GameUtils;
import extras.ManPanelHelp;
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
	private boolean isMessingAround;
	
	private JPanel box;
	private JButton makePpl;
	private JButton drawFreely;
	private JButton drawLines;

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
	private JLabel numeratorLabel;
	private JLabel denominatorLabel;
	private JLabel peopleLabel;
	private JLabel numLineGraphic;
	private JPanel questionBox;
	private NetDelegate netRep;
	private boolean clickedShowedMeHow;
	private String answersTried;

	private DeckView deckPresented;
	private CardView cardPlayed;
	private GamePanel gPanel;
	private ManPanelHelp helpDemo;
	private AssetView cursor;
	private int numTimesWrong;
	private int maxPpl;
	
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
		
//		TeammateCard team = TeammateCardFactory.createCard(Constants.CHEAT_SHADOW_CARD);
//		DeckView dv = new DeckView(new PlayDeck(team), null);
//		ManCardPanel mcp = new ManCardPanel("What is 1/2 of 8?", 4, dv, null, null, null);
		ManCardPanel mcp = new ManCardPanel(fileToOpen);
		f.setLayout(new BorderLayout());
		f.add(mcp, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public ManCardPanel() {
		this(Constants.MAN_FRAME_DEFAULT_PLAY, null, null, null, null, false);
	}

	public ManCardPanel(String q, int answer, DeckView dv, CardView cv, NetDelegate nr, GamePanel gp, boolean firstTime) {
		this(q, answer, null, dv, cv, nr, gp, firstTime);
	}

	public ManCardPanel(String q, DeckView dv, CardView cv, NetDelegate nr, GamePanel gp, boolean firstTime) {
		this(q, 0, dv, cv, nr, gp, firstTime);
	}

	public ManCardPanel(File file) {
		this("Osvaldo, What is 1/2 (.5) of 10?", null, null, null, null, true);
	}

	// Creates a new ManFrame
	// If passed a non-null file, opens that file
	public ManCardPanel(String q, int answer, File file, DeckView dv, CardView cv, NetDelegate nr, GamePanel gp, boolean isFirstTime) {
		setLayout(new BorderLayout());
		myFrame = this;
		//solution = answer;
		question = q;
		solution = GameUtils.generateAnswer(q);
		assert(answer == solution);
		listeners = new ArrayList<ManListener>();
		controls = new ArrayList<JComponent>();
		userCanEdit = true;
		clickedShowedMeHow = false;
		netRep = nr;
		sendStartLogMessage();
		answersTried = "";
		manPanel = new ManPanel(800, 550, dv, cv, this);
		controls.add(manPanel);

		deckPresented = dv;
		cardPlayed = cv;
		gPanel = gp;
		numTimesWrong = 0;
		
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
		String qtext = stripQuestion(question);
		int numer = GameUtils.extractNumerator(qtext);
		int denom = GameUtils.extractDenominator(qtext);
		maxPpl = GameUtils.extractPeople(qtext);
		int prefixEndPos = GameUtils.extractNumeratorPos(qtext);
		int suffixStartPos = GameUtils.extractPplEndPos(qtext);
		String qprefix = qtext.substring(0, prefixEndPos);
		String endfix = qtext.substring(suffixStartPos);
		
		if(qtext.indexOf(".") == -1 && !isScratchPaper()) {
			questionLabel = new JLabel(qprefix, JLabel.CENTER);
			addLabelToMainQuestion(questionLabel, questionBox);
		
			numeratorLabel = new JLabel(""+numer, JLabel.CENTER);
			addLabelToMainQuestion(numeratorLabel, questionBox);
			
			JLabel divideLbl = new JLabel("/", JLabel.CENTER);
			addLabelToMainQuestion(divideLbl, questionBox);
		
			denominatorLabel = new JLabel(""+denom, JLabel.CENTER);
			addLabelToMainQuestion(denominatorLabel, questionBox);
		
			JLabel ofLbl = new JLabel(" of ", JLabel.CENTER);
			addLabelToMainQuestion(ofLbl, questionBox);
		
			peopleLabel = new JLabel(""+maxPpl, JLabel.CENTER);
			addLabelToMainQuestion(peopleLabel, questionBox);
		
			JLabel suffixLbl = new JLabel(endfix, JLabel.CENTER);
			addLabelToMainQuestion(suffixLbl, questionBox);
		}else if(!isScratchPaper()){
			String qtextprefix = qtext.substring(0, GameUtils.extractPplStartPos(qtext));
			questionLabel = new JLabel(qtextprefix, JLabel.CENTER);
			addLabelToMainQuestion(questionLabel, questionBox);
			numeratorLabel = new JLabel("", JLabel.CENTER);
			denominatorLabel = numeratorLabel;
			peopleLabel = new JLabel(""+maxPpl, JLabel.CENTER);
			addLabelToMainQuestion(peopleLabel, questionBox);
			JLabel suffixLbl = new JLabel(endfix, JLabel.CENTER);
			addLabelToMainQuestion(suffixLbl, questionBox);
		}else{
			questionLabel = numeratorLabel = denominatorLabel = peopleLabel = new JLabel("", JLabel.CENTER);
			maxPpl = 200;
		}
		createHorizontalGlue(questionBox, GLUE_NUMS/3);
		
		questionAnswer = new JTextField(3);
		questionAnswer.setFont(Constants.FONT_REG);
		questionAnswer.setToolTipText(Constants.TIP_USER_ANSWER);
		questionAnswer.setHorizontalAlignment(JTextField.CENTER);
		questionAnswer.setMaximumSize(questionAnswer.getPreferredSize());
		questionAnswer.setVisible(!isScratchPaper());
		questionAnswer.addKeyListener(this);
		askForFocus();
		questionBox.add(questionAnswer);
		controls.add(questionBox);

		createHorizontalGlue(questionBox, GLUE_NUMS/10);

		String buttonTxt = Constants.BTN_MAN_ANSWER;
		isMessingAround = question.equals(Constants.MAN_FRAME_DEFAULT_PLAY);
		if(isMessingAround) {
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
				if(!isMessingAround) {
					checkUserAnswer();
				}else{
					fireToggleManip();
				}
			}
		});

		notCoolManBtn = new JButton(Constants.MAN_FRAME_NO_ANSWER_BTN_TEXT);
		notCoolManBtn.setToolTipText(Constants.TIP_NOT_WHOLE);
		notCoolManBtn.setFont(Constants.FONT_REG);
		notCoolManBtn.setVisible(!isMessingAround);
		questionBox.add(notCoolManBtn);
		createHorizontalGlue(questionBox, GLUE_NUMS);
		controls.add(notCoolManBtn);
		notCoolManBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compareToAnswer(-1);
			}
		});
		add(questionBox, BorderLayout.NORTH);
		numLineGraphic = new JLabel(new ImageIcon(GameImages.getNormalNumberLine()));
		statusBox.setBackground(Color.WHITE);
//		statusBox.setVisible(Constants.SHOW_WORK_ON_COMPUTER || isMessingAround);
		statusBox.add(numLineGraphic);
		controls.add(numLineGraphic);

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
		clearScreenButton.setVisible(Constants.SHOW_WORK_ON_COMPUTER || isMessingAround);
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
		launchDemo.setVisible(Constants.SHOW_WORK_ON_COMPUTER && !isMessingAround && Constants.SHOW_ME_HOW_ENABLED);
		box.add(launchDemo);
		controls.add(launchDemo);
		launchDemo.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disableControls();
				clickedShowedMeHow = true;
				sendLogMessage("Help tried " + answersTried + "| " + manPanel.generateStatLine());
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
		drawLines.setSelected(true);
		manPanel.setPencilMode(false);
		drawLines.setVisible(Constants.SHOW_WORK_ON_COMPUTER || isMessingAround);
		drawLines.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setLineDrawMode();
			}
		});
		
		makePpl = createRadioButton(pplIcon, pplIconUn, box, tools, Constants.TIP_PPL);
//		makePpl.setSelected(true);	
//		manPanel.setPplMode(true);
		makePpl.setVisible(Constants.SHOW_WORK_ON_COMPUTER || isMessingAround);
		makePpl.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPplMode();
			}
		});
		
		drawFreely = createRadioButton(pencilIcon, pencilIconUn, box, tools, Constants.TIP_PENCIL);
//		drawFreely.setSelected(true);
//		manPanel.setPencilMode(true);
		drawFreely.setVisible(Constants.SHOW_WORK_ON_COMPUTER || isMessingAround);
		drawFreely.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFreeDrawMode();
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
		
		final JButton drawButton = createDebugButton(box, "draw people");
		drawButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int num = Integer.parseInt(questionAnswer.getText());
				manPanel.clearAll();
				manPanel.drawPeople(num, 4);
			}
		});
		
		final JButton circleButton = createDebugButton(box, "drawShaded");
		circleButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int num = Integer.parseInt(questionAnswer.getText());
				manPanel.clearAll();
				for(int i = 0; i < 2; i++) {
					manPanel.addACircle(i, manPanel.calculateTheta(num), manPanel.calculateLineLength(), manPanel.getCenter(), num);
//					manPanel.addAnArc(i, manPanel.calculateTheta(num));
				}
			}
		});
		
		final JButton showHelp = createDebugButton(box, "help animation");
		showHelp.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				launch1stTimeAnimation();
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
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1) {
					Debug.println("mancard pressed" + (e.getX()-launchDemo.getWidth()) + ", " + (e.getY()-notCoolManBtn.getHeight()));
					manPanel.manPanelMousePressed(e.getX()-launchDemo.getWidth(), e.getY()-notCoolManBtn.getHeight(), e.getButton());
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1){
					manPanel.manPanelMouseReleased(e.getX()-launchDemo.getWidth(), e.getY()-notCoolManBtn.getHeight());
				}
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if(e.getModifiers() == MouseEvent.BUTTON1_MASK) {
					Debug.println("mancard dragged" + (e.getX()-launchDemo.getWidth()) + ", " + (e.getY()-notCoolManBtn.getHeight()));
					manPanel.manPanelMouseDragged(e.getX()-launchDemo.getWidth(), e.getY()-notCoolManBtn.getHeight(), e.getButton());
				}
			}
		});

		manPanel.setVisible(Constants.SHOW_WORK_ON_COMPUTER || isMessingAround);
		deckViewShown = new ManDeckViewPanel(dv, cv);
		deckViewShown.setPreferredSize(new Dimension(Constants.ORIG_CARD_WIDTH, getHeight()));
		add(deckViewShown, BorderLayout.EAST);

		changeElemsToColor(Constants.COLOR_SKIN_TONE, Constants.OPTION_WEST);
		changeElemsToColor(Constants.MANIP_CENTER_BACKGROUND, Constants.OPTION_CENTER);
		changeElemsToColor(Constants.COLOR_SKIN_TONE, Constants.OPTION_NORTH);
		changeElemsToColor(Constants.COLOR_SKIN_TONE, Constants.OPTION_EAST);
		
		if(ManPanelUtils.isShadowOnly(dv, cv)) {
			changeElemsToColor(Constants.MANIP_SHADOW_LEFT_BACKGROUND, Constants.OPTION_WEST);
			changeElemsToColor(Constants.MANIP_SHADOW_LEFT_BACKGROUND, Constants.OPTION_NORTH);
			changeElemsToColor(Constants.MANIP_SHADOW_LEFT_BACKGROUND, Constants.OPTION_EAST);
			changeElemsToColor(Constants.MANIP_SHADOW_CENTER_BACKGROUND, Constants.OPTION_CENTER);
		}
		setVisible(true);
		if(isFirstTime && Constants.GIVE_USERS_INSTRUCTIONS) {
			launch1stTimeAnimation();
		}
	}
	
	public void launch1stTimeAnimation() {
		manPanel.clearAll();
		cursor = new AssetView(GameImages.getMouseCursor());
		cursor.setShouldUseImage(true);
		helpDemo = new ManPanelHelp(myFrame, cursor);
		manPanel.doAdd(cursor);
	}
	
	public void setInitialHelp() {
		manPanel.displayMessage(Constants.MAN_FRAME_HELP_INIT);
	}
	
	public void showAnswerSection() {
		manPanel.displayMessage(Constants.MAN_FRAME_HELP_ANSWER);
	}
	
	public void showNotAWholeNumberSection() {
		manPanel.displayMessage(Constants.MAN_FRAME_HELP_NOT_WHOLE);
	}
	
	public void showPencilHelp() {
		manPanel.displayMessage(Constants.MAN_FRAME_HELP_PENCIL);
	}
	
	public void setPplMode() {
		manPanel.setPplMode(true);
		drawLines.setSelected(false);
		makePpl.setSelected(true);
		drawFreely.setSelected(false);
	}
	
	public void setFreeDrawMode() {
		manPanel.setPencilMode(true);
		drawLines.setSelected(false);
		makePpl.setSelected(false);
		drawFreely.setSelected(true);
	}
	
	public void setLineDrawMode() {
		manPanel.setPencilMode(false);
		drawLines.setSelected(true);
		makePpl.setSelected(false);
		drawFreely.setSelected(false);
	}
	
	private boolean isScratchPaper() {
		return question.equals(Constants.MAN_FRAME_DEFAULT_PLAY);
	}
	
	private void addLabelToMainQuestion(JLabel label, JPanel p) {
		label.setFont(Constants.FONT_REG);
		p.add(label);
	}

	public void disableControls() {
		userCanEdit = false;
		setControls(userCanEdit);
	}
	
	private void setControls(boolean canControl) {
		for(JComponent jc:controls) {
			jc.setEnabled(canControl);
		}
		numLineGraphic.setEnabled(true);
		manPanel.setMouseClicks(canControl);
	}
	
	private void createHorizontalGlue(JPanel jp, int numTimes) {
		for(int i = 0; i < numTimes; i++) {
			jp.add(Box.createHorizontalGlue());
		}
	}

	public void enableControls() {
		userCanEdit = true;
		setControls(userCanEdit);
	}
	
	public void reset() {
		setLineDrawMode();
		manPanel.clearAll();
	}
	
	public JLabel createDebugLabel(JPanel b, String label) {
		JLabel l = new JLabel(label);
		l.setVisible(Constants.DEBUG_MODE);
		b.add(l);
		return l;
	}
	
	public JButton createDebugButton(JPanel p, String label) {
		JButton b = new JButton(label);
		b.setVisible(Constants.DEBUG_MODE);
		p.add(b);
		return b;
	}
	
	public DeckView getDeckAffected() {
		return deckPresented;
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
	
	public void startLine(int x, int y) {
		manPanel.createActiveLine(new DoublePoint(x, y), new DoublePoint(x, y));
	}
	
	public void modifyLine(int x, int y) {
		manPanel.changeActiveLine(new DoublePoint(x, y));
	}
	
	public void addAManip(int x, int y) {
		manPanel.doAdd(x, y);
	}
	
	public void endLine(int x, int y) {
		manPanel.displayMessage(Constants.MAN_FRAME_HELP_PUT_PPL);
	}
	
	public void startFreeDraw(int x, int y) {
		manPanel.createActivePencil(new DoublePoint(x, y), new DoublePoint(x, y));
	}
	
	public void modifyFreeDraw(int x, int y) {
		manPanel.changeActivePencil(new DoublePoint(x, y));
	}
	
	public void endFreeDraw(int x, int y) {
		manPanel.displayMessage("");
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
		int num = GameUtils.extractNumerator(question);
		int numPos = q.indexOf(""+num+"/");
		int denPos = q.indexOf(" of ");
		leftPart = q.substring(0, numPos);
		rightPart = q.substring(denPos);
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
			Debug.println(cardPlayed.getCard() + " will determine stinkiness");
			isStink = ((TrickCard) cardPlayed.getCard()).isStink();
		}else if(deckPresented != null){
			Debug.println("cardPlayed was null so " + deckPresented.getTrickOnTop().getCard() + " will determine stinkiness");
			isStink = ((TrickCard) deckPresented.getTrickOnTop().getCard()).isStink();
		}
		manPanel.launchResultAnimation(ppl, num, den, solution, isStink);
	}
	
	public void launchShadowResultAnimation() {
		int num = GameUtils.extractNumerator(question);
		int den = GameUtils.extractDenominator(question);
		int ppl = GameUtils.extractPeople(question);
		manPanel.launchShadowResultAnimation(ppl, num, den, solution);
	}

	private void checkUserAnswer() {
		int num = 0;
		String s = questionAnswer.getText().trim();
		try{
			num = Integer.parseInt(s);
		}catch(NumberFormatException ex) {
			JOptionPane.showMessageDialog(myFrame,
					Constants.ERROR_INPUT_NO_INT,
					Constants.ERROR_NOT_AN_INTEGER,
					JOptionPane.ERROR_MESSAGE);
			questionAnswer.setText("");
			sendLogMessage("Oops tried " + s + "| " + manPanel.generateStatLine());
			return;
		}
		compareToAnswer(num);
	}
	
	private void compareToAnswer(int num) {
		if(num == solution) {
			manPanel.displayMessage(Constants.CORRECT);
			String c = (clickedShowedMeHow)? "y": "n";
			sendLogMessage("Done tried " + answersTried + "| " + manPanel.generateStatLine() + "| shownHow? " + c);
			disableControls();
			if(solution != -1 && legalMove() && !ManPanelUtils.isShadowOnly(deckPresented, cardPlayed)) {
				launchResultAnimation();
			}else if(ManPanelUtils.isShadowOnly(deckPresented, cardPlayed)) {
				launchShadowResultAnimation();
			}else{
				fireWindowDone();
			}
		}else{
			String response = Constants.ERROR_WRONG_ANSWER + Constants.SENTENCE_SEP + Constants.ERROR_TRY_ONCE_MORE;
			String prefix = ", ";
			if(answersTried.length() == 0) {
				response = Constants.ERROR_WRONG_ANSWER + Constants.SENTENCE_SEP + Constants.ERROR_TRY_AGAIN;
				prefix = "";
			}
			answersTried += prefix + num;
			numTimesWrong++;
			if(numTimesWrong >= Constants.NUM_WRONG_B4_SHOW_ME) {
				manPanel.displayMessage(Constants.MAN_MSG_SHOW_ME_HELP);
			}
			//Want to here fire off a message that you tried and got the wrong answer.
			sendLogMessage("Oops tried " + num + "| " + manPanel.generateStatLine());
			JOptionPane.showMessageDialog(myFrame,
					response,
					Constants.ERROR_NOT_QUITE,
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/* Strategy is:
	 * Check if it's a computer.  Computer will always send a legal move
	 * We now know it's a human player, make sure they are placing it correctly
	 * (ie they aren't placing a stink bomb on their own deck)
	 * Finally check if it's fractionally possible to play that card.
	 */
	private boolean legalMove() {
		boolean cardPlayedByHuman = cardPlayed != null;
		if(!cardPlayedByHuman) return true;  //computer always makes a legal move
		return GameUtils.legalHumanCardMove(deckPresented, cardPlayed);
	}
	
//	public void paintComponent(Graphics g) {
//		super.paintComponent(g);
//		if(cursor != null) {
//			cursor.drawObject(g);
//		}
//	}

	public void windowFinished() {
		fireWindowDone();
	}
	
	private JButton createRadioButton(ImageIcon selected, ImageIcon unselected, JPanel box, ButtonGroup tools, String tipText) {
		JButton temp = new JButton(unselected);
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
	
	public boolean canAddManips(int numManips, DeckView deck, CardView card) {
		if(ManPanelUtils.isShadowOnly(deck, card)) {
			return true;
		}
		return numManips < maxPpl;
	}
	
	private String addUnselectedPath(String s) {
		int pos = s.indexOf(".png");
		return s.substring(0, pos) + "unselected" + s.substring(pos);
	}

	public void addManListener(ManListener l) {
		listeners.add(l);
	}
	
	private void sendLogMessage(String str) {
		int commapos = question.indexOf(",");
		if(commapos == -1) {
			return;
		}
		String name = question.substring(0, commapos);
		NetHelper.logMessage(netRep, name, str);
	}
	
	private void sendStartLogMessage() {
		int commapos = question.indexOf(",");
		if(commapos == -1) {
			return;
		}
		String rest = question.substring(commapos+1);
		sendLogMessage("Started" + rest);
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
	
	private void resetLabel(JLabel label) {
		label.setForeground(Constants.DEFAULT_BUTTON_TEXT_COLOR);
		label.setOpaque(false);
		label.setFont(Constants.FONT_REG);
	}
	
	private void highlightLabel(JLabel label) {
		label.setForeground(Constants.LOUD_BUTTON_TEXT_COLOR);
		label.setOpaque(true);
		label.setBackground(Color.WHITE);
		label.setFont(Constants.FONT_LARGE);
	}
	
	public void fireDenomExplained() {
		highlightLabel(denominatorLabel);
		highlightNumberLine();
	}
	
	public void fireNumerExplained() {
		resetLabel(peopleLabel);
		highlightLabel(numeratorLabel);
		highlightNumberLine();
	}
	
	public void firePplExplained() {
		resetLabel(denominatorLabel);
		highlightLabel(peopleLabel);
		resetNumberLine();
	}
	
	public void fireExplainDone() {
		resetLabel(denominatorLabel);
		resetLabel(numeratorLabel);
		resetLabel(peopleLabel);
		resetNumberLine();
	}
	
	private void resetNumberLine() {
		if(GameUtils.isQuestionDecimalQuestion(question)) {
			numLineGraphic.setIcon(new ImageIcon(GameImages.getNormalNumberLine()));
			repaint();
		}
	}
	
	private void highlightNumberLine() {
		if(GameUtils.isQuestionDecimalQuestion(question)) {
			numLineGraphic.setIcon(new ImageIcon(GameImages.getHighlightedNumberLine(GameUtils.extractNumerator(question), GameUtils.extractDenominator(question))));
			repaint();
		}
	}
	
	private boolean isDecimal() {
		return !(denominatorLabel == null || denominatorLabel.getText().length() <= 0);
	}
	
//  These are older versions of methods that are no longer used
//	private void populateWithNPeople(int ppl, int den) {
//		String s = Constants.MAN_HELP_PLACE_PREFIX + ppl + Constants.MAN_HELP_PLACE_SUFFIX;
//		manPanel.drawPeople(ppl, den);
//		drawMessage(s);
//	}
//
//	private void circleNGroups(int num, int den, int answer) {
//		if(answer == -1) {
//			String s = Constants.MAN_MSG_NOT_COOL_MAN;
//			drawMessage(s);
//			return;
//		}
//		String s = Constants.MAN_HELP_CIRCLE_PREFIX + num + Constants.MAN_HELP_CIRCLE_SUFFIX;
//		drawMessage(s);
//		manPanel.drawOvals(num, den);
//	}
//
//	private void drawMessage(String s) {
//		manPanel.displayMessage(s);
//	}
}

