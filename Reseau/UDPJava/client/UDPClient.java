package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Scanner;

public class UDPClient {

	private static final String EXIT = "exit";
	private static final String INADDR_ANY = "0.0.0.0";
	private static final int PORT = 8080;
	private static final int SIZE = 500;
	private static DatagramSocket socket;
	private static InetSocketAddress cli;
	
	private static void init() {
			try {
				socket = new DatagramSocket();
			} catch (SocketException e) {
				e.printStackTrace();
			}
			cli = new InetSocketAddress(INADDR_ANY, PORT);
	}
	
	private static void receiveUdp(DatagramPacket toReceive) {
		try {
			socket.receive(toReceive);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void sendUdp(DatagramPacket toSend) {
		try {
			socket.send(toSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		init();
		Scanner sc = new Scanner(System.in);
		DatagramPacket toReceive = new DatagramPacket(new byte[SIZE], SIZE, cli);
		DatagramPacket toSend = new DatagramPacket(new byte[SIZE], SIZE, cli);
		for(;;) {
			toSend.setData(sc.nextLine().getBytes());
			if((EXIT.compareTo(new String(toSend.getData()))) == 0) {
				break;
			}
			sendUdp(toSend);
			receiveUdp(toReceive);
			System.out.println(new String(toReceive.getData()));
		}
		socket.close();
		sc.close();
	}
}
