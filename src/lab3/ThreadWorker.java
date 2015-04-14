package lab3;

import java.net.Socket;
import java.net.*;
import java.io.*;

public class ThreadWorker extends Thread {
	// client socket to handle
	private Socket clientSocket;
	
	// get client socket to handle when creating instance
	public ThreadWorker(Socket soc) {
		clientSocket = soc;
	}
	
	// override the thread method
	public void run() {
		long nCurrentWorkerID = Thread.currentThread().getId();
		System.out.println("worker " + nCurrentWorkerID + ": handling req...");
	
		try {
			// Get the HTTP request content
			BufferedReader inFromClient;
			inFromClient = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));
			char reqBuf[] = new char[1024];
			inFromClient.read(reqBuf);
			String strReq = new String(reqBuf);
			strReq.trim();
			System.out.println("work " + nCurrentWorkerID + ": req content = " + strReq);
			
			// what should we do here? parse req!
			String[] arrHttpContent = strReq.split(" ");
			String strMethod = arrHttpContent[0];
			String strTarget = arrHttpContent[1];
			if (strTarget.equals("/favicon.ico")) {
				System.out.println("browser is requesting icon, forget it!");
				return;// do nothing
			}
			
			// send resp to client
			String content = "<html><head><title>test</title></head><body>welcome to my homepage, <a href='lab3.pdf'>pdf</a></body></html>";
			String html = "HTTP/1.1 200 OK\n Connection:close\n Date: Mon, 23 Feb 2009 14:23:00 GMT\n"
			+ "Server:Apache/1.3.0 (unix)\n Content-Length:"
			+ content.length() + "\n"
			+ "Content-Type: text/html\n\n" + content;
			DataOutputStream outToServer = new DataOutputStream(
			clientSocket.getOutputStream());
			outToServer.writeBytes(html); // send out html
			outToServer.flush();
			clientSocket.close(); // close the socket;
			System.out.println("worker " + Thread.currentThread().getId() + ": resp is sent.");
		} catch (IOException e) {
			e.printStackTrace(); 
		} 
	}
}
