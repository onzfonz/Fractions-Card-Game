package network;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import pebblebag.PebbleListener;
import tutorial.SimpleGameClientThread;
import basic.Constants;
import basic.FYIMessage;
import cards.CardGamePanel;
import extras.Debug;

public class GameClientGUI extends JFrame implements GClientInterface, KeyListener, NetDelegate, WindowListener {
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader read = null;
	private SimpleGameClientThread clientThread = null;
	private Thread thread = null;
	private JTextField line;
	private JLabel lobbyLabel;
	private JList jLobbyPeople;
	private DefaultListModel lobbyPeople;
	private String myName = null;
	private String oppoName = null;
	private JButton start;
	private JPanel windows;
	private String currentLayout = null;
	private JPanel lobbyPanel;
	private JPanel gamePanel;
	private String chatSent;
	private CardGamePanel game;
	private FYIMessage alertMsg;

	private static final String LOBBY_PANEL = "Lobby Panel";
	private static final String GAME_PANEL = "Game Panel";

	public GameClientGUI() {
		setTitle("GameClient GUI");

		windows = new JPanel(new CardLayout());

		lobbyPeople = new DefaultListModel();
		lobbyPanel = new JPanel(new BorderLayout());
		createLobbyPanel(lobbyPanel);

		gamePanel = new JPanel(new BorderLayout());
		createGamePanel(gamePanel);

		windows.add(lobbyPanel, LOBBY_PANEL);
		windows.add(gamePanel, GAME_PANEL);
		if(Constants.NETWORK_MODE && !Constants.DEBUG_MODE) {
			alertMsg = new FYIMessage(null, Constants.INFO_STARTING);
		}
		add(windows);
		initialize();
		askNamesAndSend("", "DPlayer1 & DPlayer2");
		if(!Constants.NETWORK_MODE) {
			makeWindowShowUp();
			moveToGame();
		}else{
			if(alertMsg != null) {
				alertMsg.killMessage();
			}
		}
	}

	private void createLobbyPanel(JPanel parentPanel) {
		JPanel upperBox = new JPanel();
		upperBox.setLayout(new BoxLayout(upperBox, BoxLayout.X_AXIS));
		parentPanel.add(upperBox, BorderLayout.NORTH);

		JPanel center = new JPanel();
		parentPanel.add(center, BorderLayout.CENTER);

		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
		parentPanel.add(box, BorderLayout.SOUTH);

		start = new JButton("Play");
		start.setFont(Constants.FONT_REG);
		box.add(start);
		if(Constants.NETWORK_MODE) {
			start.addActionListener( new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					String challenger = (String) jLobbyPeople.getSelectedValue(); 
					if(challenger != null) {
						moveToGame();
						sendToServer(challenger);
						lobbyLabel.setText("Please wait while we check with " + challenger);
					}
				}
			});
		}

		jLobbyPeople = new JList(lobbyPeople);
		jLobbyPeople.setFont(Constants.FONT_REG);
		jLobbyPeople.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane listScroller = new JScrollPane(jLobbyPeople);
		listScroller.setPreferredSize(new Dimension(Constants.PANEL_WIDTH-Constants.PEBBLE_BAG_THRESHOLD, Constants.PANEL_HEIGHT-Constants.PEBBLE_BAG_THRESHOLD));

		center.add(listScroller);

		lobbyLabel = new JLabel(Constants.CONNECT_SERVER);
		lobbyLabel.setFont(Constants.FONT_REG);
		upperBox.add(lobbyLabel);
		//Commenting out the textbox at the bottom
		/*line = new JTextField(10);
		box.add(line);
		line.addKeyListener(this);*/
	}

	private void createGamePanel(JPanel parentPanel) {
		game = new CardGamePanel(this);
		parentPanel.add(game);
	}

	private void askNamesAndSend(String prefix, String names) {
		String id = names;
		if(!Constants.DEBUG_MODE) {
			String s = (String)JOptionPane.showInputDialog(this, prefix + Constants.INFO_NET_ASK_NAME, "Name", JOptionPane.QUESTION_MESSAGE, null, null, "");

			if(s.equalsIgnoreCase("exit")) {
				System.exit(0);
			}
			while(s == null || s.equals("") || containsIllegalThings(s)) {
				s = (String)JOptionPane.showInputDialog(this, Constants.INFO_ERR_REAL_NAME, "Name", JOptionPane.QUESTION_MESSAGE, null, null, "");
			}
			String partner = (String)JOptionPane.showInputDialog(this, Constants.INFO_NET_ASK_PARTNER, "Partner", JOptionPane.QUESTION_MESSAGE, null, null, "");
			id = s + " & " + partner;
			if(partner == null || partner.equals("")) {
				id = s;
			}
		}
		setTitle(id);
		game.titleUpdated(id);
		sendToServer(id);
	}

	private boolean containsIllegalThings(String name) {
		boolean ill = false;
		ill = ill || name.contains("&");
		return ill;
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }


		new GameClientGUI();
	}

	private void initialize() {
		if(!Constants.NETWORK_MODE) return;
		boolean noAddress = true;
		int i = 0;

		while(true) {
			try {
				//			InetAddress addr = InetAddress.getByName(Constants.SERVER_ADDR);
				//			socket = new Socket(addr, Constants.SOCKET_PORT);
				//			socket = new Socket(Constants.SERVER_IP, Constants.SOCKET_PORT);
				socket = getSocketMethod(i);
				if(socket == null) {
					System.exit(1);
				}
				out = new PrintWriter(socket.getOutputStream(), true);
				start();
				return;
			} catch (UnknownHostException e) {
				System.err.println("Don't know about host");
				//System.exit(1);
			} catch (IOException e) {
				System.err.println("Couldn't get I/O for the connection");
				//System.exit(1);
			}
			i++;
		}
	}

	private Socket getSocketMethod(int timesAttempted) throws IOException {
		String input;
		int ind;
		if(Constants.DEBUG_MODE && timesAttempted == 0) {
			return new Socket(Constants.LOCALHOST, Constants.SOCKET_PORT);
		}
		String[] ipAddrs = { Constants.LOCAL_SERVER_IP2, Constants.SERVER_IP, Constants.SERVER_ADDR, Constants.SERVER_IP_STANFORD, Constants.LOCALHOST};
		switch(timesAttempted) {
		case 0: return new Socket(Constants.LOCAL_SERVER_IP, Constants.SOCKET_PORT);
		case 1: ind = JOptionPane.showOptionDialog(this, Constants.INFO_PICK_SERVER, "IP", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, ipAddrs, Constants.LOCAL_SERVER_IP2);
		if(ind != JOptionPane.CLOSED_OPTION) {
			input = ipAddrs[ind]; break;
		}
		case 2: input = JOptionPane.showInputDialog(Constants.INFO_ERR_SERVER_404);
		break;
		default: return null;
		}

		return new Socket(input, Constants.SOCKET_PORT);
	}

	/* In the future we don't really want this input here...*/
	public void start() throws IOException {
		read = new BufferedReader(new InputStreamReader(System.in));
		if(thread == null) {
			clientThread = new SimpleGameClientThread(this, socket);
		}
	}

	public void run() {
		System.out.println("Starting:");
		while(thread != null) {
			try {
				String fromUser = read.readLine();
				if(fromUser != null) {
					System.out.println("Client: " + fromUser);
					sendToServer(fromUser);
				}else{
					thread = null;
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		closeAll();
	}

	public void makeWindowShowUp() {
		myName = getTitle();
		pack();
		setVisible(true);
		addWindowListener(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	public void handleMsg(String fromServer) {
		if(Constants.DEBUG_MODE){
			System.out.println("Received: " + fromServer);
		}
		if(myName == null) {
			System.out.println(fromServer);
			if(!fromServer.startsWith(".error")) {
				moveToLobby();
				makeWindowShowUp();
			}else{
				askNamesAndSend(Constants.INFO_ERR_NAMES_TAKEN, "DPlayer 3 & DPlayer4");
				return;
			}
		}
		String name = "";
		if(fromServer.length() > 0 && fromServer.charAt(0) == '.') {
			name = extractName(fromServer);
		}
		if(fromServer.startsWith(Constants.NET_CMD_ADD)) {
			if(!name.equalsIgnoreCase(myName) && !lobbyPeople.contains(name)) {
				lobbyPeople.addElement(name);
			}
		} else if(fromServer.startsWith(Constants.NET_CMD_REMOVE)) {
			lobbyPeople.removeElement(name);
		} else if(fromServer.startsWith(Constants.NET_CMD_START)) {
			lobbyLabel.setText("playing against " + name);
			oppoName = name;
			lobbyPeople.removeAllElements();
			moveToGame();
			sendToServer(Constants.NET_CMD_READY_TO_START);
		} else if(fromServer.startsWith(Constants.NET_CMD_QUIT)) {
			lobbyLabel.setText(Constants.INFO_NET_ERR_LOST_CONN + name);
			game.resetPanel(true);
			moveToLobby();
			//more lobby stuff or restart lobby make it show up again
		} else if(fromServer.startsWith(Constants.NET_CMD_PLAY)) {
			oppoName = name;
		} else if(fromServer.startsWith(Constants.NET_CMD_GAME)) {
			game.parseTextInput(name);
			//code here for sending play commmand
		} else {
			if(inLobby()) {
				lobbyLabel.setText(fromServer);
			} else {
				//    			if(fromServer.startsWith("Message:")) {
				//    				chatHistory.append(chatSent);
				//    			}
				//    			chatHistory.append(fromServer + "\n");
			}
		}
	}

	private void moveToLobby() {
		switchToLayout(LOBBY_PANEL);
	}

	private void moveToGame() {
		switchToLayout(GAME_PANEL);
		game.beginGame();
	}

	private void switchToLayout(String layoutName) {
		CardLayout cl = (CardLayout) windows.getLayout();
		cl.show(windows, layoutName);
		currentLayout = layoutName;
	}

	private boolean inGame() {
		return inLayout(GAME_PANEL);
	}

	private boolean inLobby() {
		return inLayout(LOBBY_PANEL);
	}

	private boolean inLayout(String layoutName) {
		return currentLayout.equalsIgnoreCase(layoutName);
	}

	private String extractName(String serverMsg) {
		int namePos = serverMsg.indexOf(" ") + 1;
		return serverMsg.substring(namePos);
	}

	private void sendToServer(String msg) {
		if(Constants.NETWORK_MODE) {
			out.println(msg);
		}
	}

	public void closeAll() {
		try {
			if(out != null) out.close();
			if(read != null) read.close();
			if(socket != null) socket.close();
			clientThread.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	private boolean enterTyped(KeyEvent ke) {
		return ke.getKeyCode() == KeyEvent.VK_ENTER;
	}

	public void checkTextField(KeyEvent ke, JTextField field, String additional) {
		if(enterTyped(ke)) {
			String fromUser = field.getText();
			if(fromUser != null) {
				System.out.println(additional);
				sendToServer(fromUser);
				field.setText("");
			}else{
				thread = null;
			}
		}
	}

	public void keyPressed(KeyEvent arg0) {

		checkTextField(arg0, line, "Client: " + line.getText());
	}

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void sendCommand(String cmd, String args) {
		String msg = cmd + Constants.CMD_SEP + args;
		Debug.println("Sending: " + msg);
		sendToServer(msg);
	}

	public void sendShakedCommand(String cmd, String args, PebbleListener l) {
		game.setChipObserver(l);
		sendCommand(cmd, args);
	}

	public void sendShakingCommand(String cmd, String args) {
		sendCommand(cmd, args);
	}

	public String getFrameTitle() {
		return getTitle();
	}

	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowClosing(WindowEvent arg0) {
		int option = 0;
		if(inGame() && !Constants.DEBUG_MODE) {
			option = JOptionPane.showConfirmDialog(this, Constants.INFO_ASK_B4_CLOSING, "Exit?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		}
		if(option == 0) {
			System.exit(0);
		}
	}

	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}
}
