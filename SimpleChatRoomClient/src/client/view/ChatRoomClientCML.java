package client.view;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatRoomClientCML {
	
	
	
	public void start() {

	}

	
	public static void main(String[] args) {
		try {
			Socket socket = new Socket("3.104.38.136", 9999);
			new Thread(new ClientCMLSend(socket)).start();
			new Thread(new ClientCMLReceive(socket)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}


class ClientCMLSend implements Runnable{
	private Socket socket;
	
	public ClientCMLSend(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		PrintWriter pWriter = null;
		BufferedWriter bw;
		String content;
		String userName = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try {
			pWriter = new PrintWriter(socket.getOutputStream());
			bw = new BufferedWriter(pWriter);
			System.out.print("Please enter your name: ");
			userName = br.readLine();
			while(true) {
				content = br.readLine();
				if(content.equals("q")) break;
				bw.write(userName + ": " + content + System.lineSeparator());
				bw.flush();
			}
			socket.close();
			System.out.println("Connection stops.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}


class ClientCMLReceive implements Runnable {

	private Socket socket;
	
	public ClientCMLReceive(Socket socket) {
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
				System.out.println(str);
			}
		} catch (IOException e) {
			
		} finally {
			
		}
		
	}
	
}
