package client.view;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import client.controller.ClientController;
import client.model.Comm;
import client.model.CommandType;
import client.model.LineWrapTextPane;

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
	private HashMap<String, Vector<String>> memberList;
	private Vector<Vector<String>> memberData;
	
	private JTextPane log;
	private ClientController controller;
	private JPanel content;
	private JScrollPane displayScrollPane;
	private JPanel typeField;
	private JTextField inputField;
	private JScrollPane memberScrollPane;
	private JTable memberTable;
	private JPanel mainPane;
	
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
		displayScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		displayScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		Vector<String> colName = new Vector<>();
		colName.add("Member");
		memberData = new Vector<>();
		
		memberTable = new JTable(new DefaultTableModel(memberData, colName) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
		cr.setHorizontalAlignment(JLabel.CENTER);
		memberTable.setDefaultRenderer(Object.class, cr);
		memberTable.setFillsViewportHeight(true);
		memberTable.setMinimumSize(new Dimension(MEMBER_WIDTH, MEMBER_HEIGHT-100));
		memberTable.setPreferredSize(new Dimension(MEMBER_WIDTH, MEMBER_HEIGHT-100));
		memberTable.setLayout(new BoxLayout(memberTable, BoxLayout.Y_AXIS));
		
		memberScrollPane = new JScrollPane(memberTable);
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
				sendMessage(userName + ":" +inputField.getText(), CommandType.MESSAGE);
			}
		});
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	sendMessage(userName, CommandType.UNREGISTER);
		    	System.exit(0);
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
		controller.receiveMessage();
	}
	
	private void sendMessage(String content, CommandType type) {
		controller.sendMessage(content, type);
	}
	
	
	public void clearInputField() {
		inputField.setText("");
	}
	
	public void addMember(String userName) {
		Vector<String> member = new Vector<>();
		member.add(userName);
		memberData.add(member);
		memberList.put(userName, member);
		memberTable.updateUI();
		
		SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
		StyleConstants.setForeground(simpleAttributeSet, Color.LIGHT_GRAY);
		StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_CENTER);
		insert(userName+" joins Chat Room."+System.lineSeparator(), simpleAttributeSet, true);
		
//		JTextField newMember = new JTextField(userName);
//		String currUserName = this.userName;
//		newMember.setFont(font);
//		newMember.setAlignmentX(Component.TOP_ALIGNMENT);
//		memberTable.add(newMember);
//		
//		newMember.addMouseListener(new MouseAdapter() {
//			
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				if(e.getClickCount() == 2 && !userName.equals(currUserName)) {
//					//TODO create a new UI for one-to-one chat
//					System.out.println("Create a new UI but has not been implemented yet.");
//				}
//			}
//		});
	}
	
	public void removeMember(String userName) {
		Vector<String> member;
		if(memberList.containsKey(userName)) {
			member = memberList.get(userName);
		}else return;
		memberData.remove(member);
		memberTable.updateUI();
		
		SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
		StyleConstants.setForeground(simpleAttributeSet, Color.LIGHT_GRAY);
		StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_CENTER);
		insert(userName+" leaves Chat Room."+System.lineSeparator(), simpleAttributeSet, true);
		
	}
	
	public void addLog(String message) throws InvocationTargetException, InterruptedException {
		SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
		StyleConstants.setForeground(simpleAttributeSet, Color.BLACK);
		insert(message+System.lineSeparator(), simpleAttributeSet, false);
	}

	private void insert(String message, SimpleAttributeSet simpleAttributeSet, boolean alignCenter) {
		StyledDocument styledDocument = log.getStyledDocument();
		int offset = styledDocument.getLength();
		try {
			styledDocument.insertString(offset, message, simpleAttributeSet);
			if(alignCenter) {
				styledDocument.setParagraphAttributes(offset, message.length(), simpleAttributeSet, false);
			}
			
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		log.moveCaretPosition(log.getDocument().getLength());
	}
	
	public String getUserName() {
		return this.userName;
	}



}