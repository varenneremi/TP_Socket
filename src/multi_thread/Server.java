package multi_thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	final static int SIZE = 512;
	int port;
	String folder;

	public Server(int port, String folder) {
		this.port = port;
		this.folder = folder;
	}

	private void run() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		while (true) {
			Socket client = null;
			// Connexion avec le client
			try {
				client = server.accept();
				Worker w = new Worker(client, folder);
				w.start();
			} catch (IOException e) {
				e.printStackTrace(System.err);
				System.exit(-1);
			}
			System.out.println("Client" + client.getInetAddress() + " connected");

		}

	}

	public static void main(String[] args) {
		int port = 0;
		String folder = null;
		if (args.length != 2) {
			System.err.println("Mauvais argument");
			System.exit(-1);
		} else {
			port = Integer.parseInt(args[0]);
			folder = args[1];
		}

		Server s = new Server(port, folder);

		s.run();
	}

}
