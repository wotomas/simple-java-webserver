
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer_mt {
	public static void main(String[] args) throws IOException {
		// create server socket
		int nPort = 8090;
		
		// run the server;
		ServerSocket svrSocket = new ServerSocket(nPort); 
		
		// keep listening on port 8090, serve for each req
		Socket clientSocket = null;
		while (true) {
			System.out.println("Server is ready for new requst...");
			
			// block until TCP connection is established
			clientSocket = svrSocket.accept();
			//System.out.println("Server get a requst, assign to worker...");
			
			// handle the rest by thread worker
			ThreadWorker worker = new ThreadWorker(clientSocket);
			worker.start();
		}
	}
}
