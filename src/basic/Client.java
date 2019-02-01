package basic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	final static int SIZE = 512;
	String serverHost;
	int serverPort;
	String file;

	public Client(String serverHost2, int serverPort2, String file2) {
		serverHost = serverHost2;
		serverPort = serverPort2;
		file = file2;
	}

	private void run() {
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

		try {
			envoi_commande(dos);
			lecture_rep(dis);
			lecture_rep(dis);
			lecture_rep(dis);
			ecriture_fichier(dis);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}		

	}

	private void ecriture_fichier(DataInputStream dis) throws IOException {
		FileOutputStream fos;
		int n = 0;
		byte[] bFile = new byte[SIZE];
		
		File folder = new File("Fichiers");
		if(!folder.exists() || !folder.isDirectory()) {
			if(!folder.mkdir()) {
				System.err.println("Folder not created");
			}
		}
		
		
		File f = new File("Fichiers/" + file);
		if(!f.exists()) {
			if(!f.createNewFile()) {
				System.err.println("File not created");
			}
		} else {
			f.delete();
			f.createNewFile();
		}
		
		fos = new FileOutputStream(f);
		
		long tailleFic = dis.readLong();
		
		while(tailleFic > 0) {
			n = dis.read(bFile);
			fos.write(bFile);
			tailleFic -= n;
		}
		
		fos.close();
	}

	private void lecture_rep(DataInputStream dis) throws IOException {
		int length = dis.readInt();
		byte[] bRep = new byte[length];
		dis.read(bRep, 0, length);
		String repServ = new String(bRep);
		
		if(!repServ.equals("OK")) {
			System.err.println(repServ);
			System.exit(-1);
		}
	}

	private void envoi_commande(DataOutputStream dos) throws IOException {
		String commande = "get " + file;
		byte[] bCom = commande.getBytes();
		dos.write(commande.length());
		dos.write(bCom);
	}

	public static void main(String[] args) {
		String serverHost = null;
		int serverPort = 0;
		String file = null;

		if (args.length != 3) {
			System.err.println("Mauvais argument");
			System.exit(-1);
		} else {
			serverHost = args[0];
			serverPort = Integer.parseInt(args[1]);
			file = args[2];
		}

		Client c = new Client(serverHost, serverPort, file);

		c.run();
	}
}
