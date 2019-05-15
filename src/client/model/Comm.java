package client.model;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Comm {
	
	public static Queue<String> contentSend = new LinkedList<>();
	public static Queue<String> contentReceive = new LinkedList<>();
	
	
	public void start() {
		try {
			Socket socket = new Socket("13.239.115.187", 9999);
			new Thread(new ClientSend(socket)).start();
			new Thread(new ClientReceive(socket)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	
	
}





