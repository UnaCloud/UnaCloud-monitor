package collector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import client.MonitoringClient;

public class AgentFileCollector {

	private static String NET_ADDRESSES = "net_addresses";
	private static String PORT = "port";

	private static String TEMP_PATH = "temp_path";
	private static String SAVE_PATH = "file_save";

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

		for (int i = 0; i < addresses.length; i++) {
			new MonitoringClient(addresses[i], port, tempPath).getFiles();
			System.out.println((i+1)+"/"+addresses.length+" completed hosts");
		}

		saveFiles(prop.getProperty(TEMP_PATH), prop.getProperty(SAVE_PATH));

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
	 * Saves the temp files to the saved files directory using a punctual file structure
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
			file.renameTo(savedFile);
		}
	}

	private static String getFileHostName(File file) {
		return file.getName().split("_")[2];
	}

	private static String getFileStartDate(File file) {
		return file.getName().split("_")[3].substring(0, 10);
	}

	private static String getFileSensorName(File file) {
		return file.getName().split("_")[1];
	}

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

	public static void config(){
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
