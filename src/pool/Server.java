package pool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

	final static int BUFFCLIENT_SIZE = 10;
	final static int NB_WORKERS = 4;
	int port;
	String folder;
	
	public Server(int port, String folder) {
		this.folder = folder;
		this.port = port;
	}
	
	private void run() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		ClientBuffer cb = new ClientBuffer(BUFFCLIENT_SIZE);
		
		for (int i = 0; i < NB_WORKERS; i++) {
			Worker w = new Worker(folder, cb);
			w.setDaemon(true);
			w.start();
		}
		
		while (true) {
			
			Socket client = null;
			// Connexion avec le client
			try {
				client = server.accept();
				
				// Ajout de la socket client dans le bufferClient
				cb.put(client);
			} catch (IOException e) {
				e.printStackTrace(System.err);
				System.exit(-1);
			} catch (InterruptedException e) {
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
