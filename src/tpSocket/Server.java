package tpSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class Server {

	public static void main(String[] args) {

		ServerSocket server = null;
		int port = 1234;

		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		while (true) {
			Socket client = null;
			try {
				client = server.accept();
			} catch (IOException e) {
				e.printStackTrace(System.err);
				System.exit(-1);
			}
			System.out.println("Client" + client.getInetAddress() + " connected");

			InputStream is;
			try {
				is = client.getInputStream();
				DataInputStream dis = new DataInputStream(is);
				OutputStream os = client.getOutputStream();
				DataOutputStream dos = new DataOutputStream(os);
				
				int length = dis.readInt();
				byte[] b = new byte[length];
				dis.read(b, 0, length);
				String cName = new String(b);

				String hello = "Hello " + cName;
				byte[] send = hello.getBytes();
				dos.writeInt(hello.length());
				dos.write(send);
			} catch (IOException e) {
				e.printStackTrace(System.err);
				System.exit(-1);
			}
			
		}
	}
}
