package view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import controller.ClientController;
import model.Comm;
import model.CommandType;
import model.LineWrapTextPane;

public class ChatRoomClientGUI extends JFrame {

	private final int MEMBER_WIDTH = 120;
	private final int MEMBER_HEIGHT = 500;
	private final int WIDTH = 600;
	private final int HEIGHT = 800;
	private final int TYPE_WIDTH = 550;
	private final int TYPE_HEIGHT = 50;
	private final int LOG_WIDTH = 480;
	private final int LOG_HEIGHT = 550;
	private final int ENTER_WIDTH = 400;
	private final int ENTER_HEIGHT = 40;
	private final Font font = new Font("楷体", Font.PLAIN, 20);
	
	private Comm comm;
	protected String userName;
	private HashMap<String, JTextField> memberList;
	
	private JTextPane log;
	private ClientController controller;
	private JPanel content;
	private JScrollPane displayScrollPane;
	private JPanel typeField;
	private JTextField inputField;
	private JScrollPane memberScrollPane;
	private JPanel memberPane;
	private JPanel mainPane;
	private JPanel logPane;
	
	public ChatRoomClientGUI() {
		this.comm = new Comm();
		this.controller = new ClientController(this);
		this.comm.start();
		this.memberList = new HashMap<>();
		
		this.setSize(WIDTH, HEIGHT);
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("Chat Room");
		this.setLocationRelativeTo(null);

		content = new JPanel();
		content.setSize(WIDTH, HEIGHT);
		content.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
		content.setLayout(new BorderLayout());
		this.setContentPane(content);
		
		mainPane = new JPanel();
		mainPane.setBounds(3, 3, WIDTH, LOG_HEIGHT);
		mainPane.setLayout(new BorderLayout());

		log = new LineWrapTextPane();
		log.setPreferredSize(new Dimension(LOG_WIDTH - 30, LOG_HEIGHT - 30));
		log.setFont(font);
		log.setAlignmentX(Component.LEFT_ALIGNMENT);
		log.setMargin(new Insets(5, 5, 20, 5));
		log.setEditable(false);		

		
		displayScrollPane = new JScrollPane();
		displayScrollPane.setViewportView(log);
		displayScrollPane.setPreferredSize(new Dimension(LOG_WIDTH, LOG_HEIGHT));
//		displayScrollPane.setBounds(3, 3, LOG_WIDTH, LOG_HEIGHT);
		displayScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		displayScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		memberPane = new JPanel();
		memberPane.setMinimumSize(new Dimension(MEMBER_WIDTH, MEMBER_HEIGHT-100));
		memberPane.setPreferredSize(new Dimension(MEMBER_WIDTH, MEMBER_HEIGHT-100));
		memberPane.setLayout(new BoxLayout(memberPane, BoxLayout.Y_AXIS));
		
		memberScrollPane = new JScrollPane(memberPane);
		memberScrollPane.setMinimumSize(new Dimension(MEMBER_WIDTH, MEMBER_HEIGHT));
		memberScrollPane.setPreferredSize(new Dimension(MEMBER_WIDTH, MEMBER_HEIGHT));
		memberScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		memberScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		mainPane.add(displayScrollPane, BorderLayout.CENTER);
		mainPane.add(memberScrollPane, BorderLayout.EAST);
		content.add(mainPane, BorderLayout.CENTER);
		
		typeField = new JPanel();
		typeField.setSize(TYPE_WIDTH, TYPE_HEIGHT);
		typeField.setLayout(new FlowLayout());
		content.add(typeField, BorderLayout.SOUTH);
		
		inputField = new JTextField();
		inputField.setFont(font);
		inputField.setPreferredSize(new Dimension(ENTER_WIDTH, ENTER_HEIGHT));
		inputField.requestFocus();
		typeField.add(inputField);
	
		JButton send = new JButton("Send");
		send.setFont(font);
		send.setPreferredSize(new Dimension(100, 40));
		typeField.add(send);
		
		send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(userName + ":" +inputField.getText(), CommandType.MESSAGE);
			}
		});
		
		inputField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(userName + ": " +inputField.getText(), CommandType.MESSAGE);
			}
		});
		
	}
	
	public void start() {
		this.setVisible(true);
		while(true) {
			this.userName = JOptionPane.showInputDialog(this, "Please Enter Your Name");
			if(this.userName == null) System.exit(0);
			if(this.userName.equals("")) JOptionPane.showMessageDialog(this, "Name cannot be empty");
			else break;
		}

		sendMessage(userName, CommandType.ADD_MEMBER);
		inputField.requestFocus();

		new Thread(new Runnable() {

			@Override
			public void run() {
				receiveMessage();
			}

		}).start();
	}
	
	private void receiveMessage() {
		String receive = "";
		while(true) {
			synchronized(Comm.contentReceive) {
				try {
					while(Comm.contentReceive.size() == 0) {
						Comm.contentReceive.wait();
					}
					receive = Comm.contentReceive.poll();
					System.out.println("receve "+ receive);
					receive = controller.processReceive(receive);
					addLog(receive);
					Comm.contentReceive.notifyAll();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}	
			}				
		}
	}
	
	private void sendMessage(String content, CommandType type) {
		controller.sendMessage(content, type);
	}
	
	
	public void clearInputField() {
		inputField.setText("");
	}
	
	public void addMember(String userName) {
		JTextField newMember = new JTextField(userName);
		String currUserName = this.userName;
		newMember.setFont(font);
		newMember.setAlignmentX(Component.TOP_ALIGNMENT);
		memberPane.add(newMember);
		
		newMember.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2 && !userName.equals(currUserName)) {
					//TODO create a new UI for one-to-one chat
					System.out.println("Create a new UI but has not been implemented yet.");
				}
			}
		});
	}
	
	public void removeMember(String userName) {
		JTextField remove = memberList.get(userName);
		if(remove != null) {
			memberPane.remove(remove);
		}
	}
	
	public void addLog(String message) throws InvocationTargetException, InterruptedException {
		SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
		StyleConstants.setForeground(simpleAttributeSet, Color.GRAY);
		insert(message+System.lineSeparator(), simpleAttributeSet);
		log.moveCaretPosition(log.getDocument().getLength());
	}

	private void insert(String message, SimpleAttributeSet simpleAttributeSet) {
		Document doc = log.getDocument();
		try {
			doc.insertString(doc.getLength(), message, simpleAttributeSet);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	



}
