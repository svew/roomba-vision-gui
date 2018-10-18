package comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RobotComm {
	
	private String hostname;
	private int port;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	public RobotComm(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		this.socket = null;
		this.out = null;
		this.in = null;
	}
	
	public void open() {
		if(socket != null) return;
		
		try {
			socket = new Socket(hostname, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
		
		} catch (Exception e) {
			e.printStackTrace();
			if(socket != null)
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			socket = null;
		}
	}
	
	public boolean hasInput() {
		if(socket == null) return false;
		
		try {
			return in.ready();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String getLine() {
		if(socket == null) return null;
		
		try {
			String line = in.readLine();
			if(!line.trim().isEmpty())
				System.out.println(line);
			return line;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void sendLine(String line) {
		if(socket == null) return;
		
		out.println(line);
	}
	
	public void close() {
		if(socket == null) return;
		
		out.close();
		try {
			in.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		socket = null;
	}
}
