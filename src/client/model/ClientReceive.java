package client.model;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

class ClientReceive implements Runnable {

	private Socket socket;
	
	public ClientReceive(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		Scanner sc;
		String str;
		try {
			sc = new Scanner(socket.getInputStream());
			while(sc.hasNext()) {
				str = sc.nextLine();
				synchronized(Comm.contentReceive) {
					Comm.contentReceive.add(str);
					System.out.println(str+" "+Comm.contentReceive.size());
					Comm.contentReceive.notifyAll();
				}
			}
		} catch (IOException e) {
			
		} finally {
			
		}
		
	}
	
}
