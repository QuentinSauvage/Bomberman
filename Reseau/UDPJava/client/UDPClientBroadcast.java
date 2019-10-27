package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class UDPClientBroadcast {
	private DatagramSocket socket;
	private DatagramPacket send;
	private DatagramPacket receive;
	private InetAddress host;
	private List<InetAddress> listOfInetAddress;
	private static final String ANSWER_SEARCH_SERVER = "JE SUIS LA RÉPUBLIQUE MOI";
	private static final String SEARCH_SERVER = "NE TOUCHEZ PAS MONSIEUR MÉLENCHON";
	private static final int PORT=8080;
	private static final int SIZE=500;
		
	public UDPClientBroadcast(String hostname) throws SocketException, UnknownHostException {
		socket=new DatagramSocket();
		host = InetAddress.getByName(hostname);
		socket.setBroadcast(true);
		listOfInetAddress = new ArrayList<InetAddress>();
		send=new DatagramPacket(SEARCH_SERVER.getBytes(), SEARCH_SERVER.getBytes().length,host, PORT);
		receive=new DatagramPacket(new byte[SIZE], SIZE, host, PORT);
	}
	
	public List<InetAddress> searchServer() {
		try {
			listOfInetAddress.clear();
			socket.send(send);
			socket.setSoTimeout(1000);
			
			while(true) {
				socket.receive(receive);
				byte[] msgServer = receive.getData();
				String response = new String(msgServer, 0, receive.getLength());
				System.out.println(response);
				if(ANSWER_SEARCH_SERVER.equals(response)) {
					System.out.println("IL L'ÉTRANGLE");
					listOfInetAddress.add(receive.getAddress());
				}
			}
		} catch (IOException e) {
			if(listOfInetAddress.isEmpty()) {
				System.err.println("Pas de serveur");
			}
			socket.close();
		}
		return listOfInetAddress;
	}
	
	public static void main(String[] args) {
		try {
			UDPClientBroadcast client = new UDPClientBroadcast("255.255.255.255");
			client.searchServer();
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
