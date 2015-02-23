package tutorial;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import network.GClientInterface;
import network.SimpleGameClientThread;

public class SimpleGameClientGUI extends JFrame implements GClientInterface, KeyListener {
	private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader read = null;
    private SimpleGameClientThread clientThread = null;
	private Thread thread = null;
	private JTextField line;
	private JTextField chatInput;
	private JTextArea chatHistory;
	private JLabel chatLabel;
	private JLabel lobbyLabel;
	private JList jLobbyPeople;
	private DefaultListModel lobbyPeople;
	private String myName = null;
	private String oppoName = null;
	private JButton start;
	private JPanel windows;
	private String currentLayout = null;
	private JPanel lobbyPanel;
	private JPanel chatPanel;
	private String chatSent;
	
	private static final String LOBBY_PANEL = "Lobby Panel";
	private static final String CHAT_PANEL = "Chat Panel";
	
	public SimpleGameClientGUI() {
		setTitle("GameClient GUI");
		
		windows = new JPanel(new CardLayout());
		
		lobbyPeople = new DefaultListModel();
		lobbyPanel = new JPanel(new BorderLayout());
		createLobbyPanel(lobbyPanel);
		
		chatPanel = new JPanel(new BorderLayout());
		createChatPanel(chatPanel);
		
		windows.add(lobbyPanel, LOBBY_PANEL);
		windows.add(chatPanel, CHAT_PANEL);
		add(windows);
		initialize();
		askNameAndSend("What is you name?");
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
		box.add(start);
		start.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String challenger = (String) jLobbyPeople.getSelectedValue(); 
				out.println(challenger);
				moveToChat();
				lobbyLabel.setText("Please wait while we check with " + challenger);
			}
		});
		
		jLobbyPeople = new JList(lobbyPeople);
		jLobbyPeople.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane listScroller = new JScrollPane(jLobbyPeople);
		listScroller.setPreferredSize(new Dimension(250, 180));
		
		center.add(listScroller);
		
		lobbyLabel = new JLabel("Click Connect to Server To Start");
		upperBox.add(lobbyLabel);
		line = new JTextField(10);
		box.add(line);
		line.addKeyListener(this);
	}
	
	private void createChatPanel(JPanel parentPanel) {
		JPanel upperBox = new JPanel();
		upperBox.setLayout(new BoxLayout(upperBox, BoxLayout.X_AXIS));
		parentPanel.add(upperBox, BorderLayout.NORTH);
		
		JPanel center = new JPanel();
		parentPanel.add(center, BorderLayout.CENTER);
		
		JPanel lowerBox = new JPanel();
		lowerBox.setLayout(new BoxLayout(lowerBox, BoxLayout.X_AXIS));
		parentPanel.add(lowerBox, BorderLayout.SOUTH);
		
		JButton chat = new JButton("Send");
		lowerBox.add(chat);
		chat.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println(chatInput.getText());
				chatInput.setText("");
			}
		});
		
		chatHistory = new JTextArea(20, 18);
		JScrollPane listScroller = new JScrollPane(chatHistory);
		listScroller.setPreferredSize(new Dimension(350, 130));
		
		center.add(listScroller);
		
		chatLabel = new JLabel("Chatting now");
		upperBox.add(chatLabel);
		chatInput = new JTextField(15);
		lowerBox.add(chatInput);
		chatInput.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				String chatText = chatInput.getText();
				checkTextField(e, chatInput, myName + " chatted: " + chatText);
				if(enterTyped(e)) {
					//chatHistory.append(myName + ": " + chatText + "\n");
				}
			}
			
			public void keyReleased(KeyEvent e) { }
			public void keyTyped(KeyEvent e) { }
		});
	}
	
	private void askNameAndSend(String question) {
		String s = (String)JOptionPane.showInputDialog(this, question, "Name", JOptionPane.QUESTION_MESSAGE, null, null, "");
		setTitle(s);
		out.println(s);
	}
	
	public static void main(String[] args) {
		new SimpleGameClientGUI();
	}
	
	private void initialize() {
        try {
            socket = new Socket("127.0.0.1", 1604);
            out = new PrintWriter(socket.getOutputStream(), true);
            start();
       } catch (UnknownHostException e) {
            System.err.println("Don't know about host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection");
            System.exit(1);
        }
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
    				out.println(fromUser);
    			}else{
    				thread = null;
    			}
    		}catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	closeAll();
    }
    
    public void handleMsg(String fromServer) {
    	if(myName == null) {
    		System.out.println(fromServer);
    		if(!fromServer.startsWith(".error")) {
    			moveToLobby();
                myName = getTitle();
    			pack();
    			setVisible(true);
    			setDefaultCloseOperation(EXIT_ON_CLOSE);
    		}else{
    			askNameAndSend("Names taken! Please choose another name");
    			return;
    		}
    	}
    	String name = "";
    	if(fromServer.length() > 0 && fromServer.charAt(0) == '.') {
    		name = extractName(fromServer);
    	}
    	if(fromServer.startsWith(".add")) {
    		if(!name.equalsIgnoreCase(myName) && !lobbyPeople.contains(name)) {
    			lobbyPeople.addElement(name);
    		}
    	} else if(fromServer.startsWith(".remove")) {
    		lobbyPeople.removeElement(name);
    	} else if(fromServer.startsWith(".start")) {
    		lobbyLabel.setText("playing against " + name);
    		oppoName = name;
    		lobbyPeople.removeAllElements();
    		moveToChat();
    	} else if(fromServer.startsWith(".oppoleft")) {
    		lobbyLabel.setText(name + "unexpectedly quit");
    		moveToLobby();
    		//more lobby stuff or restart lobby make it show up again
    	} else if(fromServer.startsWith(".play")) {
    		oppoName = name;
    	} else {
    		if(inLobby()) {
    			lobbyLabel.setText(fromServer);
    		} else {
    			if(fromServer.startsWith("Message:")) {
    				chatHistory.append(chatSent);
    			}
    			chatHistory.append(fromServer + "\n");
    		}
    	}
    }
    
    private void moveToLobby() {
    	switchToLayout(LOBBY_PANEL);
    }
    
    private void moveToChat() {
    	switchToLayout(CHAT_PANEL);
    }
    
    private void switchToLayout(String layoutName) {
    	CardLayout cl = (CardLayout) windows.getLayout();
    	cl.show(windows, layoutName);
    	currentLayout = layoutName;
    }
    
    private boolean inChat() {
    	return inLayout(CHAT_PANEL);
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
				out.println(fromUser);
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
}
