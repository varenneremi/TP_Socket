package babyStep;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	public static void main(String[] args) {
		String serverHost = "localHost";
		int serverPort = 1234;
		Socket server = null;
		InputStream is = null;
		DataInputStream dis = null;
		OutputStream os = null;
		DataOutputStream dos = null;
		
		try {
			server = new Socket(serverHost, serverPort);
		} catch (UnknownHostException e) {
			System.err.println("Erreur dans le nom de serveur");
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}
		
		try {
			is = server.getInputStream();
			dis = new DataInputStream(is);
			
			os = server.getOutputStream();
			dos = new DataOutputStream(os);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}
				
		String name = "test";
		
		try {
			dos.writeInt(name.length());
			byte[] send = name.getBytes();
			dos.write(send);
			
			int length = dis.readInt();
			byte[] b = new byte[length];
			dis.read(b, 0, length);
			String hello = new String(b);
			System.out.println(hello);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}
	}
}
