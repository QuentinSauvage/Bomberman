package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class UDPServer {
	
	private static final String ANSWER_SEARCH_SERVER = "JE SUIS LA RÉPUBLIQUE MOI";
	private static final String SEARCH_SERVER = "NE TOUCHEZ PAS MONSIEUR MÉLENCHON";
	private static final String PING = "PING";
	private static final String OK = "PONG";
	private static final String KO = "PAS PONG";
	private static final String INADDR_ANY = "0.0.0.0";
	private static final int PORT = 8080;
	private static final int SIZE = 500;
	private static DatagramSocket socket;
	private static InetSocketAddress srv;
	
	private static void bindSocket() {
		try {
			socket = new DatagramSocket(null);
			socket.bind(srv);
		} catch (SocketException e) {
			e.printStackTrace();
		}
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
		srv = new InetSocketAddress(INADDR_ANY, PORT);
		DatagramPacket datas = new DatagramPacket(new byte[SIZE], SIZE, srv);
		bindSocket();
		for(;;) {
			receiveUdp(datas);
			if(SEARCH_SERVER.compareTo(new String(datas.getData())) == 0) {
				datas.setData(ANSWER_SEARCH_SERVER.getBytes());
			} else {
				datas.setData(ANSWER_SEARCH_SERVER.getBytes());
			}
			System.out.println(new String(datas.getData()));
			sendUdp(datas);
		}
	}

}
