package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	private Socket socket;
	private static final int PORT = 1664;
	private InetAddress srvaddr;
	
	public TCPClient(String addr) {
		try {
			srvaddr = InetAddress.getByName(addr);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		try {
			socket = new Socket(srvaddr, PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter writer;
			writer = new PrintWriter(socket.getOutputStream(), true);
			writer.println("GET game/lect");
			System.out.println(reader.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TCPClient client = new TCPClient("localhost");
		client.run();
	}
}
