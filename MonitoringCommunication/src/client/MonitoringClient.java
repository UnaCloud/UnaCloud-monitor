package client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import monitoring.MonitoringConstants;

public class MonitoringClient {

	private String netAddress;
	private int port;

	private Socket socket;
	private PrintWriter writer;

	private String fileSavePath;

	public MonitoringClient(String netAddress, int port, String fileSavePath) {
		this.netAddress = netAddress;
		this.port = port;
		this.fileSavePath = fileSavePath;
	}

	/**
	 * Queries the available services in the monitor
	 * @return An array with all the available services' IDs
	 */
	public String[] query()	{
		System.out.println("QUERY");
		try {
			establishConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			writer.println(MonitoringConstants.QUERY);
			writer.flush();
			String json = reader.readLine();
			System.out.println(json);
			JSONObject response = new JSONObject(json);

			String[] services = new String[response.getInt("size")];
			JSONArray responseArray = response.getJSONArray("services");

			for (int i = 0; i < services.length; i++) {
				services[i] = responseArray.getString(i);
			}

			closeConnection();

			return services;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Pulls all available files from the monitor
	 * @return Downloaded File objects
	 */
	public List<File> getFiles() {
		try {
			establishConnection();
			writer.println(MonitoringConstants.GET + " " + MonitoringConstants.ALL);
			writer.flush();

			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			List<File> downloadedFiles = fileProtocol(reader);

			return downloadedFiles;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Pulls all available files from the specified services
	 * @param services Requested services' IDs
	 * @return Downloaded File objects
	 */
	public List<File> getFiles(String[] services) {
		try {
			establishConnection();
			String servicesString = "";

			for (String s : services) {
				servicesString += s;
			}

			writer.println(MonitoringConstants.GET + " " +servicesString);
			writer.flush();

			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			List<File> downloadedFiles = fileProtocol(reader);

			return downloadedFiles;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Helper method to run the file download protocol
	 * @param reader Stream from where messages and files will be read
	 * @return List of downloaded files
	 * @throws IOException If there are problems with the reader
	 */
	private List<File> fileProtocol(BufferedReader reader) throws IOException {
		String ln;
		ArrayList<File> downloadedFiles = new ArrayList<File>();

		while((ln = reader.readLine()) != null && ln.startsWith(MonitoringConstants.FILE_NAME)) {
			try {
				String[] response = ln.split(MonitoringConstants.COMMS_SEPARATOR);

				String fileName = response[1];
				long filesize = Long.parseLong(response[2]);

				File file = new File(fileSavePath + File.pathSeparator + fileName);

				writer.println(MonitoringConstants.COMMS_OK);
				writer.flush();
				
				System.out.println("DOWNLOADING: " + fileName);
				
				downloadFile(file, filesize);
				downloadedFiles.add(file);

				writer.println(MonitoringConstants.COMMS_OK);
				writer.flush();

				String localHash = Base64.encode(getHash(file));

				response = reader.readLine().split(MonitoringConstants.COMMS_SEPARATOR);
				
				
				if(response[1].equals(localHash))
					writer.println(MonitoringConstants.COMMS_OK);
				else 
					writer.println(MonitoringConstants.COMMS_ERROR + MonitoringConstants.COMMS_SEPARATOR + "Hashes dont match.");
				writer.flush();
				
				System.out.println("DONWLOADED: " + fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return downloadedFiles;
	}

	/**
	 * Downloads the current file from the input stream and saves it in the specified file.
	 * @param file
	 * @throws IOException 
	 */
	private void downloadFile(File file, long fileSize) throws IOException {

		FileOutputStream fo = new FileOutputStream(file);
		BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

		int count = 0;
		byte[] buffer = new byte[1024*1024];
		while (count < fileSize) {
			count += in.read(buffer);
			fo.write(buffer, 0, count);
			fo.flush();
		}

		fo.close();

	}

	private void establishConnection() throws UnknownHostException, IOException {
		socket = new Socket(netAddress, port);
		writer = new PrintWriter(socket.getOutputStream());
	}

	private void closeConnection() throws IOException {
		writer.close();
		socket.close();
	}

	/**
	 * Returns the hash digest of the specified file
	 * @param file
	 * @return hash signature
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

	public static void main(String[] args) throws Exception {
		//new MonitoringClient("localhost", 8080).query();
		new MonitoringClient("localhost", 8080, "test\\").getFiles();
		//		Socket socket = new Socket("localhost", 7856);
		//		PrintWriter writer = new PrintWriter(socket.getOutputStream());
		//		writer.println("GET ALL");
		//		writer.flush();
		//		
		//		FileOutputStream fo = new FileOutputStream(new File("lalal.txt"));
		//		BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
		//		
		//		int count = 0;
		//		byte[] buffer = new byte[1024*1024];
		//		while ((count = in.read(buffer)) > 0) {
		//			fo.write(buffer, 0, count);
		//			fo.flush();
		//		}
		//		
		//		writer.println("OK");
		//		fo.close();
		//		socket.close();
	}

}
