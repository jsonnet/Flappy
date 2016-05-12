package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetClient {

	public synchronized void startClient(String host) throws IOException {
		@SuppressWarnings("resource")
		Socket t = new Socket(host, 4444);
		BufferedReader from_server = new BufferedReader(new InputStreamReader(t.getInputStream()));
		PrintWriter to_server = new PrintWriter(t.getOutputStream(), true);
		boolean done = false;
		// Der Server meldet sich beim Client
		System.out.println(from_server.readLine());
		while (!done) {
			if (from_server.ready())
				try {
					while (from_server.ready())
						System.out.println(from_server.readLine());
				} catch (Exception e) {
				}
			String s = ""; // Input String
			to_server.println(s);
			if (s.equals("BYE"))
				done = true;
		}
	}

}
