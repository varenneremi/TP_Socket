package tpSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class Server {
	
	ServerSocket server ; 
	int port;
	
	
	public Server(int p) {
		try {
		this.port = p;
		server = new ServerSocket(p);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void main () throws IOException {
		while(true) {
			Socket client = server.accept();
			System.out.println("Client" + client.getInetAddress() + "connected");
			
			InputStream is = client.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			int length = dis.readInt();
			byte [] b = new byte[length];
			String cName = new String(b);
			
			OutputStream os = client.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			String hello = "Hello " + cName; 
			dos.writeInt(hello.length());
			dos.writeChars(hello);
		}
	}
	
	
		
}
