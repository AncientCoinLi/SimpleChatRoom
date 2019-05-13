import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class ChatRoomClient {
	
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

	
	public static void main(String[] args) {
		
		ChatRoomClient client = new ChatRoomClient();
		client.start();
	}
	
	
	
}


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
				synchronized (ChatRoomClient.contentSend) {
					while(ChatRoomClient.contentSend.size() == 0) {
						ChatRoomClient.contentSend.wait();
					}
					content = ChatRoomClient.contentSend.poll();
					bw.write(content + System.lineSeparator());
					bw.flush();
					ChatRoomClient.contentSend.notifyAll();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}


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
				synchronized(ChatRoomClient.contentReceive) {
					ChatRoomClient.contentReceive.add(str);
					System.out.println(str+" "+ChatRoomClient.contentReceive.size());

					ChatRoomClient.contentReceive.notifyAll();
				}
			}
		} catch (IOException e) {
			
		} finally {
			
		}
		
	}
	
}
