package lab3;
import java.net.*;
import java.io.*;

public class WebServer {
	
	
	public static void main(String[] args) throws IOException {
		// create server socket
		int nPort = 8090;
		// run the server;
		ServerSocket svrSocket = new ServerSocket(nPort); 
		
		// keep listening on port 8090, serve for each req
		Socket socket = null;
		while (true) {
			System.out.println("Server is ready for requst...");
			
			// block until TCP connection is established
			socket = svrSocket.accept();
			
			
			// Get the HTTP request content
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream() ) );
			char reqBuf [] = new char [1024];
			inFromClient.read(reqBuf);
			String strReq = new String(reqBuf);
			strReq.trim();
			System.out.println("Req: \n"+strReq);
			// what should we do here? parse req!
			String [] arrHttpContent = strReq.split(" ");
			String strMethod = arrHttpContent[0];
			String strTarget = arrHttpContent[1];
			
			if (strTarget.equals("//favicon.ico") ) {
				System.out.println("browser is requesting icon, forget it!");
				continue;//do nothing 
			}
			
			// send resp to client
			String content = "<html><head><title>test</title></head><body>COMP 4621 Project Report<br>Student Name: Kim Jihyok<br>Student ID: 10565979</body></html>";
			String html = 	"HTTP/1.1 200 OK\n Connection:close\n Date: Mon, 23 Feb 2009 14:23:00 GMT\n" 
							+ "Server:Apache/1.3.0 (unix)\n Content-Length:" + content.length() + "\n" + "Content- Type: text/html\n\n" + content;
			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
			outToServer.writeBytes(html); // send out html
			// clear up
			outToServer.flush();
			socket.close();
			System.out.println("Resp has been sent.");
		} 
	}	
}
