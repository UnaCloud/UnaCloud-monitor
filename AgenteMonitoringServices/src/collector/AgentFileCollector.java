package collector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import client.MonitoringClient;

/**
 * Responsible for orchestrating file downloads and then organizing the obtained logs.
 * File name format should be PICK_sensorName_hostName_logCreationDate_logFinishDate
 * @author Emanuel Krivoy
 */
public class AgentFileCollector {

	public static String NET_ADDRESSES = "net_addresses";
	public static String PORT = "port";

	public static String TEMP_PATH = "temp_path";
	public static String SAVE_PATH = "file_save";

	public static void main(String[] args) throws Exception {
		 	config();
		Properties prop = new Properties();
		InputStream inputStream = new FileInputStream(new File("AgentFileCollector.properties"));
		prop.load(inputStream);

		String[] addresses = getNetAddresses(prop.getProperty(NET_ADDRESSES)); 
		int port = Integer.parseInt(prop.getProperty(PORT));
		String tempPath = prop.getProperty(TEMP_PATH);		
		System.out.println();
		for (String string : addresses) {
			System.out.println(string);
		}

		saveFiles(prop.getProperty(TEMP_PATH), prop.getProperty(SAVE_PATH));
		
		for (int i = 0; i < addresses.length; i++) {
			new MonitoringClient(addresses[i], port, tempPath).getFiles();
			saveFiles(prop.getProperty(TEMP_PATH), prop.getProperty(SAVE_PATH));
			System.out.println((i+1)+"/"+addresses.length+" completed hosts");
		}


	}

	/**
	 * Returns an array with each individual IP in a comma separated list that may include ranges
	 * @param addresses: a comma separated list of IPv4 addresses. Any element may be a range of the form "IPaddress1-IPaddress2"
	 * Example: "200.168.25.3, 198.162.20.25-198.162.20.50, 200.168.25.56"
	 * @return An array with the contained addresses
	 * @throws UnknownHostException: If an element doesn't have a correct IP format 
	 */
	public static String[] getNetAddresses(String addresses) throws UnknownHostException {
		String[] tmp  = addresses.split("\\s*,\\s*");

		ArrayList<String> resp = new ArrayList<String>();
		for (int i = 0; i < tmp.length; i++) {
			if(tmp[i].matches(".*\\s*-\\s*.*")) {
				String[] range = tmp[i].split("\\s*-\\s*");
				byte[] current = InetAddress.getByName(range[0]).getAddress();

				resp.add(range[0]);
				String lastAdded = range[0];
				while(!lastAdded.equals(range[1])) {
					for (int j = 3; j >= 0; j--) {
						current[j]++;
						if(current[j] == ((byte)255)) 
							current[j] = 1;
						else
							break;
					}
					lastAdded = InetAddress.getByAddress(current).getHostAddress();
					resp.add(lastAdded);
				}
			} else 
				resp.add(tmp[i]);
		}

		return resp.toArray(new String[1]);
	}

	/**
	 * Saves the temp files to the saved files directory using a specific file structure
	 * @param tempPath
	 * @param savePath
	 */
	public static void saveFiles(String tempPath, String savePath) {

		File temp =  new File(tempPath);
		File[] tempFiles = temp.listFiles();
		for (int i = 0; i < tempFiles.length; i++) {
			File file = tempFiles[i];
			String workingDate = getFileStartDate(file);
			String sensor = getFileSensorName(file);
			String hostName = getFileHostName(file);
			String newFileName = getNewNameFile(file);
			File savedFolder = new File(savePath+File.separator+ workingDate  +File.separator+ hostName +File.separator+ sensor);
			if(!savedFolder.exists())
				savedFolder.mkdirs();

			File savedFile = new File(savePath +File.separator+ workingDate +File.separator+ hostName +File.separator+ sensor +File.separator+ newFileName);
			try {
				Files.copy(file.toPath(), savedFile.toPath());
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Extracts the hosts unique name from the file name
	 * @param file
	 * @return hostname of the logged machine
	 */
	private static String getFileHostName(File file) {
		return file.getName().split("_")[2];
	}

	/**
	 * Extracts the start date of the log
	 * @param file
	 */
	private static String getFileStartDate(File file) {
		return file.getName().split("_")[3].substring(0, 10);
	}

	/**
	 * Extracts the ID of the tool used to create this log
	 * @param file
	 */
	private static String getFileSensorName(File file) {
		return file.getName().split("_")[1];
	}

	/**
	 * Returns the new name of the downloaded file. 
	 * The final format is sensorName_hostName_logCreationDate_logFinishDate_downloadDate
	 * @param file
	 */
	private static String getNewNameFile(File file) {
		String sansSuffix =file.getName().substring(0, file.getName().lastIndexOf('.'));
		String[] fileName = sansSuffix.split("_");
		String newName = fileName[1];
		for (int i = 2; i < fileName.length; i++) {
			newName += "_" + fileName[i];
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss-SSS");
		newName += "_" + df.format(new Date());
		newName += file.getName().substring(file.getName().lastIndexOf('.'), file.getName().length());
		return newName;
		
	}

	/**
	 * Creates the log file
	 */
	private static void config(){
		try {
			//Create agent log file
			PrintStream ps=new PrintStream(new FileOutputStream("log.txt",true),true){
				@Override
				public void println(String x) {
					super.println(new Date()+" "+x);
				}
				@Override
				public void println(Object x) {
					super.println(new Date()+" "+x);
				}
			};
			System.setOut(ps);
			System.setErr(ps);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}    	
	}
}
