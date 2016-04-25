package networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import utility.LogHelper;

public class Networking extends Thread {

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private boolean runningServer = true;
	private boolean clientRunning = true;

	public Networking(int port, String serverName) throws IOException {
		if (serverName == null) {
			this.serverSocket = new ServerSocket(port);
			this.serverSocket.setSoTimeout(0);
		} else
			this.clientSocket = new Socket(serverName, port);
		// ServerName to InetAdress
	}

	private void runServer() throws IOException {
		while (this.runningServer) {
			LogHelper.log("Waiting for client on port " + this.serverSocket.getLocalPort() + "...");
			Socket server = this.serverSocket.accept();
			LogHelper.log("Just connected to " + server.getRemoteSocketAddress());
			DataInputStream in = new DataInputStream(server.getInputStream());
			LogHelper.log(in.readUTF());
			DataOutputStream out = new DataOutputStream(server.getOutputStream());
			out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress() + "\nGoodbye!");
			// TODO remove later
			server.close();
			break;
		}
	}

	private void runClient() throws IOException {
		while (this.clientRunning) {
			LogHelper.log("Connecting to " + this.clientSocket.getInetAddress() + " on port " + this.clientSocket.getLocalPort());
			Socket client = new Socket(this.clientSocket.getInetAddress(), this.clientSocket.getLocalPort());
			LogHelper.log("Just connected to " + client.getRemoteSocketAddress());
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeUTF("Hello from " + client.getLocalSocketAddress());
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			LogHelper.log("Server says " + in.readUTF());
			// TODO remove later
			client.close();
			break;
		}
	}

	public void runNet() {
		int port = 23456; // Choose good port
		try {
			Thread t = new Networking(port, null); // start server
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// (c) 2016 Joshua Sonnet
}
