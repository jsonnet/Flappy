package networking;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class NetServer {

	// Client id
	private int client;
	// Number of all clients
	private int count;

	// All clients
	private ArrayList<Socket> clients;
	// All output of all clients
	private PrintWriter[] output;

	private Socket incoming;
	private String input;

	// Kontrollmeldung fuer den Client
	public synchronized String melde(int c) {
		return ("I'm pc " + c);
	}

	// Broadcasts the message from one client to all other connected clients
	public synchronized void broadcast(String s, int id) {
		this.input = s;
		this.client = id;
		for (int i = 0; i < this.count; i++)
			if ((this.clients.get(i) != null) && (i != id))
				this.output[i].println(s);
	}

	// If the limit not reached, a new client is added to the list, else it has to wait
	public synchronized int connect(Socket in) {
		int i = 0;
		try {
			while (this.count >= 10)
				this.wait();
			for (i = 0; this.clients.get(i) != null; i++);

			this.clients.add(in);
			this.output[i] = new PrintWriter(in.getOutputStream(), true);
			this.count++;
		} catch (Exception e) {
		}
		return i;
	}

	// The client is removed from the list
	public synchronized void disconnect(int id) {
		this.clients.remove(id);
		this.count--;
		this.notifyAll();
	}

	public NetServer() {
		// Set count of clients to 0
		this.count = 0;
		// Initialize both arrays
		this.clients = new ArrayList<Socket>();
		this.output = new PrintWriter[10];
	}

	public synchronized void startServer() throws IOException {
		NetServer chat = new NetServer();
		@SuppressWarnings("resource")
		ServerSocket s = new ServerSocket(4444);
		System.out.println(s.getInetAddress());
		boolean running = true;
		while (running) {
			Socket incoming = s.accept();
			System.out.println(incoming.getInetAddress());
			int id = chat.connect(incoming);
			new NetHandler(incoming, id, chat).start();
		}
	}

}
