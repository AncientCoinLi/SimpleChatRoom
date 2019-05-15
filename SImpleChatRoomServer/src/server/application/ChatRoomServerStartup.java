package server.application;

import server.model.ChatRoomServer;

public class ChatRoomServerStartup {

	public static void main(String[] args) {
		new ChatRoomServer().start();
	}

}
