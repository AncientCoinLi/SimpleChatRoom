package client.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingUtilities;

import cipher.Decryption;
import cipher.Encryption;
import client.model.Network;
import client.model.CommandType;
import client.model.EncryptType;
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
			EncryptType encrypt = client.getEncryptType();
			doc.append("encrypt", encrypt.toString());
			String cipherText = content;
			switch(encrypt) {
			case AES:
				cipherText = Encryption.AESEncode(content);
				doc.append("content", cipherText);
				break;
			case SHAMIR:
				HashMap<String, String> shamir = Encryption.shamirSign(client.getUserName(), content);
				JSonDoc shamirInfo = new JSonDoc();
				for(String key : shamir.keySet()) {
					shamirInfo.append(key, shamir.get(key));
				}
				doc.append("encryptInfo", shamirInfo);
				doc.append("content", content);
				break;
			case RSA:
				HashMap<String, String> rsa = Encryption.RSAEncode(content);
				JSonDoc rsaInfo = new JSonDoc();
				for(String key : rsa.keySet()) 
					if(!key.equals("cipherText")) rsaInfo.append(key, rsa.get(key));
				doc.append("encryptInfo", rsaInfo);
				doc.append("content", rsa.get("cipherText"));
				break;
				default:
					break;
			}

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
		String username = doc.getString("username");
		switch(commandType) {
		case MESSAGE:
			EncryptType encrypt = EncryptType.valueOf(doc.getString("encrypt"));
			String message = doc.getString("content");
			switch(encrypt) {
			case AES:
				message = Decryption.AESDecode(message);
				break;
			case SHAMIR:
				JSonDoc shamirInfo = (JSonDoc) doc.get("encryptInfo");
				BigInteger n = new BigInteger(shamirInfo.getString("n"));
				BigInteger e = new BigInteger(shamirInfo.getString("e"));
				BigInteger s = new BigInteger(shamirInfo.getString("s"));
				BigInteger t = new BigInteger(shamirInfo.getString("t"));
				if(Decryption.shamirValidate(message, username, n, e, s, t)) break;
				else return;
			case RSA:
				JSonDoc rsaInfo = (JSonDoc) doc.get("encryptInfo");
				BigInteger p = new BigInteger(rsaInfo.getString("p"));
				BigInteger q = new BigInteger(rsaInfo.getString("q"));
				BigInteger e2 = new BigInteger(rsaInfo.getString("e"));
				BigInteger c = new BigInteger(rsaInfo.getString("c"));
				message = Decryption.RSADecode(message, p, q, e2, c);
				break;
				default:
					break;
			}
			try {
				client.addLog(message);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case ADD_MEMBER:
			client.addMember(username);
			break;
		case REMOVE_MEMBER:
			client.removeMember(username);
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
