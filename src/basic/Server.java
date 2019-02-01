package basic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
		InputStream is = null;
		DataInputStream dis = null;
		OutputStream os = null;
		DataOutputStream dos = null;

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
			} catch (IOException e) {
				e.printStackTrace(System.err);
				System.exit(-1);
			}
			System.out.println("Client" + client.getInetAddress() + " connected");

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

			// Verification du nombre d'argument
			if (arg.length >= 2) {

				String info = "OK";
				byte[] bInfo = info.getBytes();
				try {
					dos.write(info.length());
					dos.write(bInfo);
				} catch (IOException e1) {
					e1.printStackTrace(System.err);
					System.exit(-1);
				}

				// Verification de la requete
				if (arg[0].equals("get")) {
					// envoi du fichier

					try {
						dos.write(info.length());
						dos.write(bInfo);
					} catch (IOException e1) {
						e1.printStackTrace(System.err);
						System.exit(-1);
					}

					String file = arg[1];
					lecture_fichier(file, dos);

				} else {
					String errCom = "Commande inconnue";
					byte[] send = errCom.getBytes();
					try {
						dos.writeInt(errCom.length());
						dos.write(send);
					} catch (IOException e) {
						e.printStackTrace(System.err);
						System.exit(-1);
					}
				}
			} else {
				String errArg = "Pas assez d'argument";
				byte[] send = errArg.getBytes();
				try {
					dos.writeInt(errArg.length());
					dos.write(send);
				} catch (IOException e) {
					e.printStackTrace(System.err);
					System.exit(-1);
				}
			}

		}
	}

	private void lecture_fichier(String file, DataOutputStream dos) {
		FileInputStream fis = null;
		try {
			File f = new File(folder + "/" + file);
			fis = new FileInputStream(f);

			String info = "OK";
			byte[] bInfo = info.getBytes();
			dos.write(info.length());
			dos.write(bInfo);

			byte[] bFile = new byte[SIZE];

			long tailleFic = f.length();
			dos.writeLong(tailleFic);

			int n = 0;
			while ((n = fis.read(bFile)) >= 0) {
				dos.write(bFile);
			}
			
		} catch (FileNotFoundException e) {
			String errFile = file + "not found";
			byte[] send = errFile.getBytes();
			try {
				dos.writeInt(errFile.length());
				dos.write(send);
			} catch (IOException e1) {
				e.printStackTrace(System.err);
				System.exit(-1);
			}
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace(System.err);
				System.exit(-1);
			}
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
