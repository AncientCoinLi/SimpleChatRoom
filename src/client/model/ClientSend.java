package client.model;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

class ClientSend implements Runnable{
	private Socket socket;
	
	public ClientSend(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		PrintWriter pWriter = null;
		BufferedWriter bw;
		String content;

		try {
			pWriter = new PrintWriter(socket.getOutputStream());
			bw = new BufferedWriter(pWriter);

			while(true) {
				synchronized (Comm.contentSend) {
					while(Comm.contentSend.size() == 0) {
						Comm.contentSend.wait();
					}
					content = Comm.contentSend.poll();
					bw.write(content + System.lineSeparator());
					bw.flush();
					Comm.contentSend.notifyAll();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
