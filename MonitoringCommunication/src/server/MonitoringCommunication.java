package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import jdk.nashorn.internal.parser.JSONParser;
import monitoring.MonitoringController;

public class MonitoringCommunication extends Thread{

	private int port;
	private MonitoringController controller;
	private ServerSocket serverSocket;
	private BufferedReader reader;
	private OutputStream out; 

	public MonitoringCommunication(int port, MonitoringController controller) throws IOException {
		this.port = port;
		this.controller = controller;
		serverSocket = new ServerSocket(port);
	}

	@Override
	public void run() {
		while(true) {
			try {
				Socket clientSocket = serverSocket.accept();
				reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				out = clientSocket.getOutputStream();
				String[] command = reader.readLine().split(" ");

				switch(command[0].toUpperCase()) {

				case "GET":
					getFile(command[1]);
					break;

				case "QUERY":
					query();
					break;

				case "EXECUTE":
					execute(command[1]);
					break;

				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void getFile(String services) {

		if(services.toUpperCase().equals("ALL")) {

		} else {
			String[] names = services.split(",");
			for (String name : names) {

			}
		}

	}

	private void query() {
		PrintWriter writer = new PrintWriter(out);
		String[] services = {"hola", "que", "mas"}; //controller.getServicesNames();

		JSONObject response = new JSONObject();
		response.append("size",services.length);
		response.append("services", new JSONArray(services));
		writer.println(response);
	}

	//TODO
	private void execute(String command) {

	}

	//TODO
	private List<File> getPickUpFiles() {
		return null;

	}

	//TODO
	private List<File> getPickUpFIles(String service) {
		return null;

	}

	//TODO
	private boolean sendFile(File file) {
		return false;

	}


}
