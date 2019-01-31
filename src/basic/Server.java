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

	public static void main(String[] args) {
		ServerSocket server = null;
		int port = 1234;
		InputStream is = null;
		DataInputStream dis = null;
		OutputStream os = null;
		DataOutputStream dos = null;

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

			try {
				is = client.getInputStream();
				dis = new DataInputStream(is);
				os = client.getOutputStream();
				dos = new DataOutputStream(os);
			} catch (IOException e) {
				e.printStackTrace(System.err);
				System.exit(-1);
			}

			int length;
			String[] arg = null;
			String erreur;

			try {
				length = dis.readInt();
				byte[] bCommande = new byte[length];
				dis.read(bCommande, 0, length);
				String commande = new String(bCommande);
				arg = commande.split(" ");
			} catch (IOException e) {
				e.printStackTrace(System.err);
				System.exit(-1);
			}

			if (arg.length >= 2) {
				if (arg[0].equals("get")) {
					for (int i = 1; i < arg.length; i++) {
						String file = arg[i];

						FileInputStream fis = null;
						try {
							fis = new FileInputStream(new File(file));
							byte[] bFile = new byte[SIZE];

							int n = 0;
							while ((n = fis.read(bFile)) >= 0) {
								dos.write(SIZE);
								dos.write(bFile);
								bFile = new byte[SIZE];
							}
							dos.write(-1);
						} catch (FileNotFoundException e) {
							erreur = file + "not found";
							byte[] send = erreur.getBytes();
							try {
								dos.writeInt(erreur.length());
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
				} else {
					erreur = "Commande inconnue";
					byte[] send = erreur.getBytes();
					try {
						dos.writeInt(erreur.length());
						dos.write(send);
					} catch (IOException e) {
						e.printStackTrace(System.err);
						System.exit(-1);
					}
				}
			} else {
				erreur = "Pas assez d'argument";
				byte[] send = erreur.getBytes();
				try {
					dos.writeInt(erreur.length());
					dos.write(send);
				} catch (IOException e) {
					e.printStackTrace(System.err);
					System.exit(-1);
				}
			}

		}
	}

}
