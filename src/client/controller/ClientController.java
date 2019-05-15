package client.controller;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import client.model.Comm;
import client.model.CommandType;
import client.model.Decryption;
import client.model.EncryptType;
import client.model.Encryption;
import client.model.JSonDoc;
import client.view.ChatRoomClientGUI;

public class ClientController {
	private ChatRoomClientGUI client;
	
	public ClientController(ChatRoomClientGUI client) {
		this.client = client;
	}
	
	public void start() {
	}

	public void sendMessage(String content, CommandType type) {
		JSonDoc doc = new JSonDoc();
		doc.append("command", type.toString());
		switch(type) {
		case MESSAGE:
			String cipherText = Encryption.AESEncode(content);
			doc.append("message", cipherText);
			doc.append("encrypt", EncryptType.AES.toString());
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {
					client.clearInputField();
				}
				
			});
			break;
		case ADD_MEMBER:
			doc.append("member", content);
			break;
		case REMOVE_MEMBER:
			doc.append("member", content);
			break;
		case REGISTER:
			break;
		default:
			break;
		}
		synchronized (Comm.contentSend) {
			Comm.contentSend.add(doc.toJson());
			Comm.contentSend.notifyAll();
		}
	}

	public void receiveMessage() {
		String receive = "";
		while(true) {
			synchronized(Comm.contentReceive) {
				try {
					while(Comm.contentReceive.size() == 0) {
						Comm.contentReceive.wait();
					}
					receive = Comm.contentReceive.poll();
					System.out.println("receve "+ receive);
					processReceive(receive);
					Comm.contentReceive.notifyAll();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			}				
		}
	}

	public void processReceive(String receive) {
		JSonDoc doc = JSonDoc.parse(receive);
		String command = doc.getString("command");
		CommandType commandType = CommandType.valueOf(command);
		String userName = "";
		switch(commandType) {
		case MESSAGE:
			String message = Decryption.AESDecode(doc.getString("message"));
			try {
				client.addLog(message);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case ADD_MEMBER:
			userName = doc.getString("member");
			client.addMember(userName);
			break;
		case REMOVE_MEMBER:
			userName = doc.getString("member");
			client.removeMember(userName);
			break;
			default:
				break;
		}

	}
}
