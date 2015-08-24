package client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MonitoringTestClient {

	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("localhost", 7856);
		PrintWriter writer = new PrintWriter(socket.getOutputStream());
		writer.println("GET ALL");
		writer.flush();
		
		FileOutputStream fo = new FileOutputStream(new File("lalal.txt"));
		BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
		
		int count = 0;
		byte[] buffer = new byte[1024*1024];
		while ((count = in.read(buffer)) > 0) {
			fo.write(buffer, 0, count);
			fo.flush();
		}
		
		writer.println("OK");
		fo.close();
		socket.close();
	}
	
}
