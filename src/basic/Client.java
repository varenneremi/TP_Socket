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

		// Connection au serveur
		try {
			server = new Socket(serverHost, serverPort);
		} catch (UnknownHostException e) {
			System.err.println("Erreur dans le nom de serveur");
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}

		// Initialisation des Input et Output
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

	// Methode permettant de recevoir le fichier demande
	private void ecriture_fichier(DataInputStream dis) throws IOException {
		FileOutputStream fos;
		int n = 0;
		byte[] bFile;
		
		// Dossier dans lequel on stockera le fichier recu
		File folder = new File("Fichiers");
		
		// Si le dossier n'existe pas on le cree
		if(!folder.exists() || !folder.isDirectory()) {
			if(!folder.mkdir()) {
				System.err.println("Folder not created");
			}
		}
		
		// Fichier dans lequel on ecrit ce que l'o recoit du serveur
		File f = new File("Fichiers/" + file);
		
		// Si il n'existe pas on le cree
		if(!f.exists()) {
			if(!f.createNewFile()) {
				System.err.println("File not created");
			}
		}
		// Sinon on le supprime et on en cree un nouveau
		else {
			f.delete();
			f.createNewFile();
		}
		
		// Ouverture du fichier
		fos = new FileOutputStream(f);
		
		// Reception de la taille du fichier
		long tailleFic = dis.readLong();
		
		// Creation du buffer recevant les octets depuis le serveur
		if (tailleFic < SIZE) {
			bFile = new byte[(int) tailleFic];
		} else {
			bFile = new byte[SIZE];
		}
		
		// Reception du fichier
		while(tailleFic > 0) {
			n = dis.read(bFile);
			if(tailleFic < n) {
				fos.write(bFile, 0, (int) tailleFic);
			} else {
				fos.write(bFile);
			}
			tailleFic -= n;
		}
		
		// Fermeture du fichier
		fos.close();
	}

	// Methode permettant de recevoir des info du serveur
	private void lecture_rep(DataInputStream dis) throws IOException {
		int length = dis.readInt();
		byte[] bRep = new byte[length];
		dis.read(bRep, 0, length);
		String repServ = new String(bRep);
		
		// Si le message n'est pas "OK" alors c'est une erreur et on l'affiche au client
		if(!repServ.equals("OK")) {
			System.err.println(repServ);
			System.exit(-1);
		}
	}

	// Methode permettant d'envoyer la requete au serveur
	private void envoi_commande(DataOutputStream dos) throws IOException {
		String commande = "get " + file;
		byte[] bCom = commande.getBytes();
		dos.writeInt(commande.length());
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
