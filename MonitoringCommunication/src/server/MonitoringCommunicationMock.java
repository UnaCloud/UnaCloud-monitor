package server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import monitoring.MonitoringConstants;

import monitoring.MonitoringController;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class MonitoringCommunicationMock extends Thread{

	private final int RESEND_TRIES = 3;

	private MonitoringController controller;
	private ServerSocket serverSocket;
	private BufferedReader reader;
	private OutputStream out; 

	public MonitoringCommunicationMock(int port, MonitoringController controller) throws IOException {
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

				case MonitoringConstants.GET:
					getFile(command[1]);
					break;

				case MonitoringConstants.QUERY:
					query();
					break;

				case MonitoringConstants.EXECUTE:
					execute(command[1]);
					break;

				}

				reader.close();
				out.close();
				clientSocket.close();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void getFile(String services) throws Exception {
		PrintWriter writer = new PrintWriter(out);
		if(services.toUpperCase().equals(MonitoringConstants.ALL)) {
			for(File file : controller.getPickupFiles()) {
				fileProtocol(file, writer);
			}
		} else {
			String[] names = services.split(MonitoringConstants.SERVICE_NAME_SEPARATOR);
			for (String name : names) {
				for(File file : controller.getPickupFiles(name)) {
					fileProtocol(file, writer);
				}
			}
		}

		writer.close();
	}

	private void query() {
		PrintWriter writer = new PrintWriter(out);
		String[] services = {"STF1", "STF2"};

		JSONObject response = new JSONObject();
		response.append("size",services.length);
		response.append("services", new JSONArray(services));
		writer.println(response);
	}

	//TODO
	private void execute(String command) {

	}


	private void fileProtocol(File file, PrintWriter writer) throws Exception{
		int tries;
		for (tries = 0; tries < RESEND_TRIES; tries++) {


			writer.println(MonitoringConstants.FILE_NAME+MonitoringConstants.COMMS_SEPARATOR+file.getName()+"-"+file.length()+"-Bytes");
			writer.flush();

			if(!reader.readLine().toUpperCase().startsWith(MonitoringConstants.COMMS_OK)) {
				continue;
			}

			sendFile(file);

			if(!reader.readLine().toUpperCase().startsWith(MonitoringConstants.COMMS_OK)) {
				continue;
			}

			writer.println(MonitoringConstants.FILE_HASH+MonitoringConstants.COMMS_SEPARATOR+Base64.encode(getHash(file)));
			writer.flush();

			if(!reader.readLine().toUpperCase().startsWith(MonitoringConstants.COMMS_OK)) {
				continue;
			}

			controller.sendFileToDone(file);
			break;
		}

		if(tries == 3) {
			writer.println(MonitoringConstants.COMMS_ERROR + "-Retries exceeded");
			writer.flush();
			throw new Exception("Couldn't send all files");
		}
	}

	//TODO buffer size?
	private void sendFile(File file) throws IOException {

		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

		int count = 0;
		byte[] buffer = new byte[1024*1024];
		while ((count = in.read(buffer)) > 0) {
			out.write(buffer, 0, count);
			out.flush();
		}

		in.close();
	}

	private byte[] getHash(File file) {
		try {
			MessageDigest md = MessageDigest.getInstance(MonitoringConstants.HASH_ALGORITHM);
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

			int count = 0;
			byte[] buffer = new byte[1024*1024];
			while ((count = in.read(buffer)) > 0) {
				md.update(buffer,0,count);
			}

			in.close();

			return md.digest();

		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	public static void main(String[] args) throws IOException {
		new MonitoringCommunicationMock(7856, null).start();
	}
}
