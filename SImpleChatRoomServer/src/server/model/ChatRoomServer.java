package server.model;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

import server.controller.ServerController;

public class ChatRoomServer {

	public void start() {
		try {
			ServerSocket server = new ServerSocket(9999);
			System.out.println("Chat Room Server Starts.");
			System.out.println("Ip : " + Inet4Address.getLocalHost().getHostAddress());
			ServerController controller = new ServerController();
			while(true) {
				Socket socket = server.accept();
				System.out.println("get connection from "+socket.getInetAddress());
				new Thread(new Communication(socket, controller)).start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	

}







