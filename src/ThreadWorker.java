

import java.net.*;
import java.io.*;

public class ThreadWorker extends Thread {
	// client socket to handle
	private Socket clientSocket;
	private File testFile;
	private int firstPart;
	private int secondPart;
	private int contentLength;
	
	// get client socket to handle when creating instance
	public ThreadWorker(Socket soc) {
		clientSocket = soc;
	}
	
	// override the thread method
	public void run() {
		long nCurrentWorkerID = Thread.currentThread().getId();
		//System.out.println("worker " + nCurrentWorkerID + ": handling req...");
	
		try {
			// Get the HTTP request content
			BufferedReader inFromClient;
			inFromClient = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));
			char reqBuf[] = new char[1024];
			inFromClient.read(reqBuf);
			String strReq = new String(reqBuf);
			strReq.trim();
			
			System.out.println(strReq);
			
			// what should we do here? parse req!
			String[] arrHttpContent = strReq.split(" ");
			String strMethod = arrHttpContent[0];
			String strTarget = arrHttpContent[1];
			//Check arrHttpContent
				
				/**
				for(int i = 0;i<arrHttpContent.length;i++){
					System.out.println("TheadWorker/arrHttpContent: The " +i+ "th String is " +arrHttpContent[i]);
				}
				**/
				
				
			
			if (strTarget.equals("/favicon.ico")) {
				//System.out.println("browser is requesting icon, forget it!");
				return;// do nothing
			}
			if(strTarget.equals("/test.mp4")) {
				testFile = new File("test.mp4");
				if(!testFile.exists()){
					System.out.println("Test File does not exist!");
					return;
				} else if(!testFile.isFile()) {
					System.out.println("Test File is not a file!");
					return;
				}
				//if test.mp4 request has range
				if(arrHttpContent[3].contains("byte")){
					//System.out.println("TheadWorker/arrHttpContent: Contains the char sequence byte");
					String range = arrHttpContent[3].substring(6);
					range = range.split("\n")[0];
					range = range.replace("\n", "").replace("\r", "");
					String[] parts = range.split("-");
					
					firstPart = Integer.parseInt(parts[0]);
					//System.out.println("TheadWorker/arrHttpContent: " + firstPart);
					parts[1] = parts[1].split("\n")[0];
					secondPart = Integer.parseInt(parts[1]);
					//System.out.println("TheadWorker/arrHttpContent: " + secondPart);
					
					//System.out.println("ThreadWorker/SplitingRange: The firstPart is " +firstPart+ "and the secondPart is " +secondPart);
					contentLength = secondPart - firstPart + 1;
					//There are total 3 types of invalid range inputs
						//1. fistError - the secondPart is smaller than firstPart
						//2. secondError - secondPart exceeds the fileSize
						//3. thirdError - smallerPart goes below 0
					if(firstError() || secondError() || thridError())
					{
						String html = "HTTP/1.1 406 Requested Range Not Satisfiable\n"
								+ "Content-Type: video/mp4\n"
								+ "Connection: keep-alive\n"
								+ "Server:Apache/1.3.0 (unix)\n"
								+ "Content-disposition: attachment; filename=test.mp4\n"
								+ "Accept-Ranges: bytes\n"
								+ "Content-Range: bytes " + 0 + "-" + testFile.length() + "\n\n";
						DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
						outToServer.writeBytes(html); // send out html
						outToServer.flush();
						clientSocket.close(); // close the socket;
						return;
					}
					String html = "HTTP/1.1 206 Partial Content\n"
								+ "Content-Type: video/mp4\n"
								+ "Content-Length:" + contentLength + "\n" 
								+ "Connection: keep-alive\n"
								+ "Server:Apache/1.3.0 (unix)\n"
								+ "Content-disposition: attachment; filename=test.mp4\n"
								+ "Accept-Ranges: bytes\n"
								+ "Content-Range: bytes " + firstPart+ "-" +secondPart+ "/" + testFile.length() + "\n\n";
					DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
					outToServer.writeBytes(html); // send out html
					outToServer.flush();
					clientSocket.close(); // close the socket;
				} else {
					//if test.mp4 request does not have range
					String content = "";
					String html = "HTTP/1.1 200 OK\n"
								+ "Server:Apache/1.3.0 (unix)\n"
								+ "Content-Length:" + testFile.length() + "\n" 
								+ "Content-disposition: attachment; filename=test.mp4\n"
								+ "Content-Type: video/mp4\n" 
								+ "Accept-Ranges: bytes\n\n";
					DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
					outToServer.writeBytes(html); // send out html
					outToServer.flush();
					clientSocket.close(); // close the socket;
				}
				return;
			}
			
			// send resp to client
			String content = "<html><head><title>test</title></head><body>COMP 4621 Project Report<br>Student Name: Kim Jihyok<br>Student ID: 10565979</body></html>";
			String html = "HTTP/1.1 200 OK\n"
						+ "Connection:close\n"
						+ "Date: Mon, 23 Feb 2009 14:23:00 GMT\n" 
						+ "Server:Apache/1.3.0 (unix)\n"
						+ "Content-Length:" + content.length() + "\n" 
						+ "Content-Type: text/html\n\n" + content;
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(html); // send out html
			outToServer.flush();
			clientSocket.close(); // close the socket;
			//System.out.println("worker " + Thread.currentThread().getId() + ": resp is sent.");
		} catch (IOException e) {
			e.printStackTrace(); 
		} 
	}

	private boolean thridError() {
		//1. fistError - the secondPart is smaller than firstPart
		if(secondPart < firstPart){
			return true;
		}
		return false;
	}

	private boolean secondError() {
		//2. secondError - secondPart exceeds the fileSize
		if(secondPart > testFile.length()){
			return true;
		}
		return false;
	}

	private boolean firstError() {
		//3. thirdError - smallerPart goes below 0
		if(firstPart < 0){
			return true;
		}
		return false;
	}
}
