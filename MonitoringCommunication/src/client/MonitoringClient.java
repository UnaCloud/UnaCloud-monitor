package client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONObject;

import monitoring.MonitoringConstants;

public class MonitoringClient {

	private String NetAddress;
	private int port;
	
	private Socket socket;
	private PrintWriter writer;
	
	public MonitoringClient(String netAddress, int port) {
		NetAddress = netAddress;
		this.port = port;
	}

	/**
	 * Queries the available services in the monitor
	 * @return An array with all the available services' IDs
	 */
	public String[] query()	{
		try {
			establishConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			writer.println(MonitoringConstants.QUERY);
			JSONObject response = new JSONObject(reader.readLine());
			
			String[] services = new String[response.getInt("size")];
			JSONArray responseArray = response.getJSONArray("services");
			
			for (int i = 0; i < services.length; i++) {
				services[i] = responseArray.getString(i);
			}
			
			return services;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void establishConnection() throws UnknownHostException, IOException {
		socket = new Socket(NetAddress, port);
		writer = new PrintWriter(socket.getOutputStream());
	}
	
	private void closeConnection() throws IOException {
		writer.close();
		socket.close();
	}

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
