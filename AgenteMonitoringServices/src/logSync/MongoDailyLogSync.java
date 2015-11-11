package logSync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import logFiles.OpenHardware_LogFile;
import logFiles.Perfmon_LogFile;
import logFiles.PowerGadget_LogFile;
import logFiles.Sigar_LogFile;
import mongoDB.MongoConnection;
import collector.AgentFileCollector;

/**
 * Format specific class thats syncs the daily log from 4 sensors: power gadget, perfmon, sigar and open hardware monitor
 * @author Emanuel Krivoy
 */
public class MongoDailyLogSync {

	private String daily_folders_path; 
	private SimpleDateFormat dateFormat;

	public MongoDailyLogSync() throws Exception {
		config();

		Properties prop = new Properties();
		InputStream inputStream = new FileInputStream(new File("AgentFileCollector.properties"));
		prop.load(inputStream);

		daily_folders_path = prop.getProperty(AgentFileCollector.SAVE_PATH);
		dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

		FilenameFilter dailyFileFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d");
			}
		};

		FilenameFilter machineFileFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toUpperCase().startsWith("ISC");
			}
		};

		File workRoot = new File(daily_folders_path);
		System.out.println("DailySyncer START");
		for (File dailyFolder : workRoot.listFiles(dailyFileFilter)) {
			for (File machine : dailyFolder.listFiles(machineFileFilter)) {
				//Hasn't been processed before
				if(!(new File(machine.getPath() + File.separator + ".MongoDailySynced").exists())) {
					try {
						System.out.println("Syncing " + dailyFolder.getName() + "_" + machine.getName());
						//compatibility(new File(machine.getPath()+File.separator+"openHardware"+File.separator));
						LogFile[] logFiles = new LogFile[4];
						logFiles[0] = new OpenHardware_LogFile(machine+File.separator+"openHardware");
						logFiles[1] = new Sigar_LogFile(machine+File.separator+"sigar");
						logFiles[2] = new Perfmon_LogFile(machine+File.separator+"perfmon");
						logFiles[3] = new PowerGadget_LogFile(machine+File.separator+"powerGadget");
						
						
						MongoConnection connection = new MongoConnection();
						MongoSyncer syncer = new MongoSyncer(logFiles, dateFormat, connection);
						syncer.sync();
						new File(machine.getPath() + File.separator + ".MongoDailySynced").createNewFile();
						System.out.println("Synced " + dailyFolder.getName() + "_" + machine.getName());
					} catch(Exception e) {
						System.out.println("ERROR (" + dailyFolder.getName()+"_"+machine.getName() + ")");
						e.printStackTrace();
						continue;
					}
				}
			}
		}
		System.out.println("DailySyncer END");
	}

	//TODO delete
	private void compatibility(File path) {
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.contains("00-00-00-000");
			}
		};

		File[] toFix = path.listFiles(filter);
		for (File file : toFix) {
			file.renameTo(new File(path.getPath() + File.separator + file.getName().replace("00-00-00-000", getOpenHardwareStartDate(file))));
		}
	}

	//TODO delete
	private String getOpenHardwareStartDate(File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			reader.readLine(); reader.readLine();

			String entry = reader.readLine();
			String[] tmp = entry.split(",");
			SimpleDateFormat initialFormat = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");
			SimpleDateFormat finalFormat = new SimpleDateFormat("kk-mm-ss-000");

			Date entryDate = initialFormat.parse(tmp[0]);
			reader.close();
			return finalFormat.format(entryDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates the log file
	 */
	private static void config(){
		try {
			//Create agent log file
			PrintStream ps=new PrintStream(new FileOutputStream("MongoDailySyncerLog.txt",true),true){
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

	public static void main(String[] args) throws Exception {
		new MongoDailyLogSync();
	}

}

