package client.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.SwingUtilities;

import client.model.Network;
import client.model.CommandType;
import client.model.Decryption;
import client.model.EncryptType;
import client.model.Encryption;
import client.model.JSonDoc;
import client.view.ChatRoomClientGUI;

public class ClientController {
	private ChatRoomClientGUI client;
	private Network network;
	
	public void initialise(ChatRoomClientGUI client, Network network) {
		this.client = client;
		this.network = network;
	}

	public void sendMessage(String content, CommandType type) {
		JSonDoc doc = new JSonDoc();
		doc.append("command", type.toString());
		doc.append("username", client.getUserName());
		switch(type) {
		case MESSAGE:
			String cipherText = Encryption.AESEncode(content);
			doc.append("content", cipherText);
			doc.append("encrypt", EncryptType.AES.toString());
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {
					client.clearInputField();
				}

			});
			break;
		case UNREGISTER:
			break;
		case REGISTER:
			break;
		default:
			break;
		}
		network.sendMessage(doc.toJson());
	}


	public void processReceive(String receive) {
		JSonDoc doc = JSonDoc.parse(receive);
		String command = doc.getString("command");
		CommandType commandType = CommandType.valueOf(command);
		String userName = doc.getString("username");
		switch(commandType) {
		case MESSAGE:
			
			String message = Decryption.AESDecode(doc.getString("content"));
			try {
				client.addLog(message);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case ADD_MEMBER:
			client.addMember(userName);
			break;
		case REMOVE_MEMBER:
			client.removeMember(userName);
			break;
		case REGISTER_SUCCESS:
			client.registerSucceed();
			List<String> memberList = (List<String>) doc.get("memberList");
			client.initMemberList(memberList);
			break;
		case REGISTER_FAILURE:
			client.registerFail();
			break;
			default:
				break;
		}

	}
}
