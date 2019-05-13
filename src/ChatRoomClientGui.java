import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatRoomClientGui extends JFrame {

	private final int WIDTH = 400;
	private final int HEIGHT = 600;
	private final int PANE_WIDTH = 400;
	private final int PANE_HEIGHT = 600;
	private final int TYPE_WIDTH = 400;
	private final int TYPE_HEIGHT = 50;
	private final int LOG_WIDTH = 380;
	private final int LOG_HEIGHT = 480;
	private final int ENTER_WIDTH = 250;
	private final int ENTER_HEIGHT = 40;
	private final Font font = new Font("楷体", Font.PLAIN, 20);
	private String userName;
	private JTextArea log;
	
	private ChatRoomClient client;
	
	public ChatRoomClientGui() {
		this.client = new ChatRoomClient();
		this.client.start();
		
		this.setSize(WIDTH, HEIGHT);
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("Chat Room");
		this.setLocationRelativeTo(null);

		JPanel content = new JPanel();
		content.setSize(PANE_WIDTH, PANE_HEIGHT);
		content.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
		content.setLayout(new BorderLayout());
		this.setContentPane(content);

		log = new JTextArea();
		log.setBounds(5, 5, LOG_WIDTH - 30, LOG_HEIGHT - 30);
		log.setFont(font);
		log.setEditable(false);
		log.setLineWrap(true);
		
		JScrollPane jsp = new JScrollPane(log);
		jsp.setBounds(3, 3, LOG_WIDTH, LOG_HEIGHT);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		content.add(jsp, BorderLayout.CENTER);
		

		
		JPanel typeField = new JPanel();
		typeField.setSize(TYPE_WIDTH, TYPE_HEIGHT);
		typeField.setLayout(new FlowLayout());
		content.add(typeField, BorderLayout.SOUTH);
		
		JTextField enter = new JTextField();
		enter.setFont(font);
		enter.setPreferredSize(new Dimension(ENTER_WIDTH, ENTER_HEIGHT));
		typeField.add(enter);
		
		JButton send = new JButton("Send");
		send.setFont(font);
		send.setPreferredSize(new Dimension(100, 40));
		typeField.add(send);
		
		send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (ChatRoomClient.contentSend) {
					ChatRoomClient.contentSend.add(userName + ": " +enter.getText());
					enter.setText("");
					ChatRoomClient.contentSend.notifyAll();
				}
			}
		});
		
		enter.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (ChatRoomClient.contentSend) {
					ChatRoomClient.contentSend.add(userName + ": " +enter.getText());
					enter.setText("");
					ChatRoomClient.contentSend.notifyAll();
				}
			}
		});
		
	}
	
	public void start() {
		this.setVisible(true);
		this.userName = JOptionPane.showInputDialog(this, "Please Enter Your Name");
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
				synchronized(ChatRoomClient.contentReceive) {
					try {
						while(ChatRoomClient.contentReceive.size() == 0) {
							ChatRoomClient.contentReceive.wait();
						}
						log.setCaretPosition(log.getText().length());
						log.append(ChatRoomClient.contentReceive.poll() + System.lineSeparator());
						ChatRoomClient.contentReceive.notifyAll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}				
			}
			}
			
		}).start();
	}
	
	public static void main(String[] args) {
		ChatRoomClientGui gui = new ChatRoomClientGui();
		gui.start();
	}

}
