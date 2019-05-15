package server.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import server.controller.ServerController;

public class Communication implements Runnable {
	private BufferedReader input;
	private BufferedWriter output;
	private Socket socket;
	private boolean isRunning;
	private ServerController controller;
	private String username;
	
	public Communication(Socket socket, ServerController controller) {
		this.controller = controller;
		this.socket = socket;
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		isRunning = true;
	}
	
	@Override
	public void run() {
		String receive = "";
		try {
			while(isRunning) {
				receive = input.readLine();
				System.out.println(receive);
				processInput(receive);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage() + System.lineSeparator() + socket.getInetAddress());
			controller.unregister(this.username);
			System.out.println(username + " quit.");
		}
	}
	
	private void processInput(String receive) {
		JSonDoc doc = JSonDoc.parse(receive);
		String command = doc.getString("command");
		CommandType type = CommandType.valueOf(command);
		String userName = doc.getString("username");
		switch(type) {
		case REGISTER:
			register(userName);
			break;
		case UNREGISTER:
			unregister(userName);
			break;
		case MESSAGE:
			controller.sendMessage(receive);
			break;

			default:
				break;
		}
	}

	public void sendMessage(String message) {
		try {
			output.append(message + System.lineSeparator());
			System.out.println("SEND " + message);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	private void unregister(String userName) {
		controller.unregister(userName);
	}

	private void register(String userName) {
		boolean status = controller.register(userName, this);
		if(status) {
			wrapMessage(userName, CommandType.REGISTER_SUCCESS);
			this.username = userName;
		}else {
			wrapMessage(userName, CommandType.REGISTER_FAILURE);
		}
	}
	
	private void wrapMessage(String username, CommandType type) {
		JSonDoc doc = new JSonDoc();
		doc.append("command", type.toString());
		doc.append("username", username);
		switch(type) {
		case REGISTER_FAILURE:
			doc.append("message", "Username exists.");
			break;
		case REGISTER_SUCCESS:
			doc.append("message", "register succeeds");
			ArrayList<String> members = new ArrayList<>(controller.getMemberList());
			doc.append("memberList", members);
			break;
			default:
				break;
		}
		sendMessage(doc.toJson());
		
	}

	public void stop() {
		try {
			isRunning = false;
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
