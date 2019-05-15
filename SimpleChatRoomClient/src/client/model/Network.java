package client.model;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import client.controller.ClientController;

public class Network implements Runnable {
	
	private Socket socket;
	private BufferedWriter output;
	private BufferedReader input;
	private ClientController controller;

	
	public Network() {
		try {
			socket = new Socket("13.239.115.187", 9999);
			output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initialise(ClientController controller) {
		this.controller = controller;
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

	@Override
	public void run() {
		String receive = "";
		try {
			while(true) {
				receive = input.readLine();
				System.out.println(receive);
				if(receive == null) break;
				controller.processReceive(receive);		
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}





