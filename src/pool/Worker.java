package pool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Worker extends Thread {

	final static int SIZE = 512;
	String folder;
	ClientBuffer cb;

	public Worker(String folder, ClientBuffer cb) {
		this.cb = cb;
		this.folder = folder;
	}

	public void run() {
		while (true) {
			InputStream is = null;
			DataInputStream dis = null;
			OutputStream os = null;
			DataOutputStream dos = null;

			// Recuperation du client dans le bufferClient
			Socket client = null;
			try {
				client = cb.get();
			} catch (InterruptedException e1) {
				e1.printStackTrace(System.err);
				System.exit(-1);
			}
			
			// Initialisation Input et Output
			try {
				is = client.getInputStream();
				dis = new DataInputStream(is);
				os = client.getOutputStream();
				dos = new DataOutputStream(os);
			} catch (IOException e) {
				e.printStackTrace(System.err);
				System.exit(-1);
			}

			// Lecture de la commande
			String[] arg = null;
			try {
				arg = lecture_commande(dis);
			} catch (IOException e2) {
				e2.printStackTrace(System.err);
				System.exit(-1);
			}

			process_request(arg, dos);
		}
	}

	private void process_request(String[] arg, DataOutputStream dos) {
		// Verification du nombre d'argument
		if (arg.length >= 2) {

			envoie_info("OK", dos);

			// Verification de la requete
			if (arg[0].equals("get")) {

				envoie_info("OK", dos);

				// Debut envoi du fichier
				String file = arg[1];
				lecture_fichier(file, dos);

			} else {
				envoie_info("Commande inconnue", dos);
			}
		} else {
			envoie_info("Pas assez d'arguments", dos);
		}
	}

	private void lecture_fichier(String file, DataOutputStream dos) {
		FileInputStream fis = null;
		try {
			// Ouverture du fichier à lire
			File f = new File(folder + "/" + file);
			fis = new FileInputStream(f);

			envoie_info("OK", dos);

			byte[] bFile = new byte[SIZE];

			// Envoi de la taille du fichier
			long tailleFic = f.length();
			dos.writeLong(tailleFic);

			// Envoi du fichier par paquets de 512 octets
			int n = 0;
			while ((n = fis.read(bFile)) >= 0) {
				dos.write(bFile);
			}

			// Fermeture du fichier
			fis.close();

		} catch (FileNotFoundException e) {
			envoie_info(file + " not found", dos);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}
	}

	private void envoie_info(String info, DataOutputStream dos) {
		byte[] bInfo = info.getBytes();
		try {
			dos.writeInt(info.length());
			dos.write(bInfo);
		} catch (IOException e1) {
			e1.printStackTrace(System.err);
			System.exit(-1);
		}
	}

	private String[] lecture_commande(DataInputStream dis) throws IOException {
		int length;
		String[] arg;

		length = dis.readInt();
		byte[] bCommande = new byte[length];
		dis.read(bCommande, 0, length);
		String commande = new String(bCommande);
		arg = commande.split(" ");

		return arg;
	}
}
