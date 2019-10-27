package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * UDP Broadcast-related class.
 * @author quentin sauvage
 *
 */
public class UDPClientBroadcast {
	private DatagramSocket socket;
	private DatagramPacket send;
	private DatagramPacket receive;
	private InetAddress host;
	private List<InetAddress> listOfInetAddress;
	private static final String ANSWER_SEARCH_SERVER = "i'm a bomberstudent server";
	private static final String SEARCH_SERVER = "looking for bomberstudent servers";
	private static final int PORT=3945;
	private static final int SIZE=500;
	
	/**
	 * Constructor.
	 * @param hostname should be the broadcast address.
	 */
	public UDPClientBroadcast(String hostname) {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		try {
			host = InetAddress.getByName(hostname);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		try {
			socket.setBroadcast(true);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		listOfInetAddress = new ArrayList<InetAddress>();
		send = new DatagramPacket(SEARCH_SERVER.getBytes(), SEARCH_SERVER.getBytes().length,host, PORT);
		receive = new DatagramPacket(new byte[SIZE], SIZE, host, PORT);
	}
	
	/**
	 * Search for bomberman servers.
	 * @return the list of bomberman servers.
	 */
	public List<InetAddress> searchServer() {
		try {
			listOfInetAddress.clear();
			socket.send(send);
			socket.setSoTimeout(1000);
			while(true) {
				socket.receive(receive);
				byte[] msgServer = receive.getData();
				String response = new String(msgServer, 0, receive.getLength());
				if(ANSWER_SEARCH_SERVER.equals(response)) {
					listOfInetAddress.add(receive.getAddress());
				}
			}
		} catch (IOException e) {
			socket.close();
		}
		return listOfInetAddress;
	}
}
