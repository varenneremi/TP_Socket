package pool;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ClientBuffer {

	// 3 Semaphore : 1 pour la protection des donnees (mutex), 1 pour representer
	// les cases pleines (notEmpty) et 1 pour les cases vides (notFull)
	private Semaphore mutex, notFull, notEmpty;
	private List<Socket> buf;
	int size;

	public ClientBuffer(int buffSize) {
		mutex = new Semaphore(1);
		notFull = new Semaphore(buffSize);
		notEmpty = new Semaphore(0);
		buf = new ArrayList<Socket>(buffSize);
		size = buffSize;
	}

	public void put(Socket s) throws InterruptedException {
		// On acquiert une case vide
		notFull.acquire();

		// on acquiert les donnees protegees
		mutex.acquire();
		buf.add(s);

		// on relache les donnees
		mutex.release();

		// On indique qu'une case est pleine
		notEmpty.release();
	}

	public Socket get() throws InterruptedException {
		// On acquiert une case pleine
		notEmpty.acquire();

		// On acquiert les donnees protegees
		mutex.acquire();
		Socket s = buf.remove(0);

		// On relache les donnees
		mutex.release();

		// On indique qu'une cases est vide
		notFull.release();

		return s;
	}
}
