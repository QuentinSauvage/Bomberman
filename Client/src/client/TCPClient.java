package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * TCP-related class.
 * @author quentin sauvage
 *
 */
public class TCPClient {
	private Socket socket;
	private static final int PORT = 1418;
	private InetAddress srvaddr;
	
	/**
	 * Creates a TCP connexion with the host.
	 * @param addr the host address.
	 */
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
	
	/**
	 * Send a json message to the server.
	 * @param datas the datas to send.
	 * @param shouldRead boolean that indicates if the answer should be read in this method.
	 * @return the answer of the server.
	 */
	public String sendJson(String datas, boolean shouldRead) {
		String res = new String();
		try {
			PrintWriter writer;
			writer = new PrintWriter(socket.getOutputStream(), true);
			writer.println(datas);
			if(shouldRead) {
				res = readAnswer();
			} else return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * Read the server answer.
	 * @return the sever answer.
	 */
	public String readAnswer() {
		BufferedReader reader;
		char res[] = new char[8192];
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			reader.read(res, 0, res.length);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return new String(res);
	}
}
