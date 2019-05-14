package controller;

import javax.swing.SwingUtilities;

import model.Comm;
import model.JSonDoc;
import model.EncryptType;
import model.Encryption;
import view.ChatRoomClientGUI;
import model.CommandType;
import model.Decryption;

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
		default:
			break;
		}
		synchronized (Comm.contentSend) {
			Comm.contentSend.add(doc.toJson());
			Comm.contentSend.notifyAll();
		}
	}

	public void receiveMessage() {
		
	}

	public String processReceive(String receive) {
		JSonDoc doc = JSonDoc.parse(receive);
		String command = doc.getString("command");
		CommandType commandType = CommandType.valueOf(command);
		String result = receive;
		switch(commandType) {
		case MESSAGE:
			result = Decryption.AESDecode(doc.getString("message"));
			break;
		case ADD_MEMBER:
			break;
		case REMOVE_MEMBER:
			break;
			default:
				break;
		}
		return result;
	}
}
