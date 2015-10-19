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

public class MonitoringCommunication extends Thread{

	private final int RESEND_TRIES = 3;

	private MonitoringController controller;
	private ServerSocket serverSocket;
	private BufferedReader reader;
	private OutputStream out; 

	public MonitoringCommunication(int port, MonitoringController controller) throws IOException {
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
				String ln = reader.readLine();
				String[] command = ln.split(" ");

				System.out.println("Monitoring communication: " + ln);
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

	/**
	 * Offers all the ready to be picked files from all or a list of services.
	 * @param services List with MonitoringConstants.SERVICE_NAME_SEPARATOR separated values or the MonitoringConstants.ALL command.
	 * @throws Exception
	 */
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

	/**
	 * Prints all the available services into the output stream.
	 * The message is JSON formatted.
	 */
	private void query() {
		PrintWriter writer = new PrintWriter(out);
		String[] services = controller.getServicesNames();

		JSONObject response = new JSONObject();
		response.put("size", services.length);
		response.put("services", new JSONArray(services));
		writer.println(response);
		writer.flush();
	}

	/**
	 * Executes the given command.
	 * Commands are defined in the MonitoringConstants class.
	 * @param command
	 */
	private void execute(String command) {
		if(command.toUpperCase().equals(MonitoringConstants.EXECUTE_DELETE_DONE)) {
			controller.deleteDone();
		}
	}


	/**
	 * Helper method that pushes a file to the client.
	 * @param file File to be uploaded
	 * @param writer PrintWriter that handles the communication with the client
	 * @throws Exception Throws an exception if the connection resets or the file cannot be sent within the retry limit. 
	 */
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

	/**
	 * Utilitary method that pushes a file into a stream.
	 * @param file
	 * @throws IOException
	 */
	private void sendFile(File file) throws IOException {

		FileInputStream in = new FileInputStream(file);

		int count = 0;
		byte[] buffer = new byte[1024*8];
		while ((count = in.read(buffer)) > 0) {
			out.write(buffer, 0, count);
			out.flush();
		}

		in.close();
	}

	/**
	 * Returns the cryptographic digest of a file for integrity checks.
	 * @param file
	 * @return byte array with the digest
	 */
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
		new MonitoringCommunication(720, null).start();
	}
}
