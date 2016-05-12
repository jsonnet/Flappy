package networking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetHandler extends Thread {

	private Socket incoming;
	private int client;
	private NetServer server;

	public NetHandler(Socket i, int c, NetServer serv) {
		this.incoming = i;
		this.client = c;
		this.server = serv;
	}

	@Override
	public synchronized void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(this.incoming.getInputStream()));
			PrintWriter out = new PrintWriter(this.incoming.getOutputStream(), true);
			// Der Server meldet sich beim Client
			out.println(this.server.melde(this.client));
			boolean done = false;
			while (!done) {
				// The server waits for an input
				String str = in.readLine();
				if (str == null)
					done = true;
				else if (str.equals("BYE")) {
					done = true;
					this.server.broadcast("I am disconnecting", this.client);
					this.server.disconnect(this.client);
				}
				// The server broadcasts the input of the client
				else
					this.server.broadcast(str, this.client);
			}
			this.incoming.close();
		} catch (Exception e) {
			System.out.println("Error:" + e);
		}
	}

}