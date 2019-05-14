package application;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

public class ChatRoomServer {
	private static HashMap<String, Socket> allSocket = new HashMap<>();
	public static Queue<String> contentList = new LinkedList<>();

	public static void main(String[] args) {
		try {
			
			ServerSocket server = new ServerSocket(9999);
			System.out.println("Chat Room Server Starts.");
			System.out.println("Ip : " + Inet4Address.getLocalHost().getHostAddress());
			Random r = new Random();
			new Thread(new ServerSend(allSocket)).start();
			while(true) {
				Socket socket = server.accept();
				r.setSeed(System.currentTimeMillis());
				String serials = r.nextLong()+"";
				allSocket.put(serials, socket);
				new Thread(new ServerReceive(socket)).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}


class ServerReceive implements Runnable {
	private Socket socket;
	
	public ServerReceive(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		Scanner sc = null;
		String str = null;
		
		try {
			sc = new Scanner(socket.getInputStream());
			while(sc.hasNext()) {
				str = sc.nextLine();
				
				synchronized(ChatRoomServer.contentList) {
					ChatRoomServer.contentList.notifyAll();
					ChatRoomServer.contentList.add(str + System.lineSeparator());
				}
				System.out.println(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}

class ServerSend implements Runnable{
	private HashMap<String, Socket> allSocket;
	
	public ServerSend(HashMap<String, Socket> allSocket) {
		this.allSocket = allSocket;
	}
	@Override
	public void run() {
		PrintWriter pw;
		String str;
		
		try {
			while(true) {
				synchronized (ChatRoomServer.contentList) {
					while (ChatRoomServer.contentList.size() == 0) {
						ChatRoomServer.contentList.wait();
					}
					ChatRoomServer.contentList.notifyAll();
					str = ChatRoomServer.contentList.poll();
					for(String sock : allSocket.keySet()) {
						pw = new PrintWriter(allSocket.get(sock).getOutputStream());
						pw.write(str);
						pw.flush();
					}	
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			
		}
	}
	
}


