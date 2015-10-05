package mongoDB;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;

import collector.AgentFileCollector;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;

/**
 * Object that takes all the synced log files from the save_path
 * property of AgentFileCollector.properties and records them in 
 * a specified Mongo DB.
 * @author Emanuel Krivoy
 *
 */
public class SyncedFileMongoRecorder {

	private MongoConnection connection;

	/**
	 * Constructs the object and starts the database recording process
	 */
	public SyncedFileMongoRecorder() {
		try {
			connection = new MongoConnection();
			System.out.println("MongoDB connection established");
			for(File syncedFile : getSyncedFiles()) {
				System.out.println("Recording " + syncedFile.getName());
				recordFile(syncedFile);
				System.out.println("Recorded " + syncedFile.getName());
			}
			System.out.println("Recording completed");
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns all the log files recursively present in the save path
	 * @return File array with all the synced log files
	 */
	public static List<File> getSyncedFiles() {
		Properties prop = new Properties();
		try {
			InputStream inputStream = new FileInputStream(new File("AgentFileCollector.properties"));
			prop.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String filePath = prop.getProperty(AgentFileCollector.SAVE_PATH);

		File saveDir = new File(filePath);
		FilenameFilter syncedFileFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("_sync.csv");
			}
		};

		FileFilter dirFilter = new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		};

		Queue<File> queue = new LinkedList<File>(); 
		queue.add(saveDir);

		ArrayList<File> resp = new ArrayList<File>();

		while(!queue.isEmpty()) {
			File current = queue.poll();

			File[] dirChilds = current.listFiles(dirFilter);

			if(dirChilds != null) 
				for(File f : dirChilds)
					queue.add(f);

			File[] syncFiles = current.listFiles(syncedFileFilter);

			if(syncFiles != null)
				for(File f : syncFiles)
					resp.add(f);
		}

		return resp;
	}

	/**
	 * Pushes the data in the specified log file to the database
	 * @param syncedFile the file to be recorded in the database
	 */
	private void recordFile(File syncedFile) {
		String[] headers = getFileHeaders(syncedFile);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(syncedFile));
			BulkWriteOperation bulkOp = connection.getSyncedCollection().initializeOrderedBulkOperation();

			String line;
			while((line = reader.readLine()) != null) {
				String[] data = line.split(",");
				BasicDBObject record = new BasicDBObject();

				for(int i = 0; i < headers.length; i++) {
					record.append(headers[i], data[i]);
				}

				bulkOp.insert(record);
			}

			System.out.println("Inserted " + bulkOp.execute().getInsertedCount() + " records");
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("ERROR RECORDING SYNCED FILE");
			e.printStackTrace();
		}
	}

	/**
	 * Returns the column names present in the specified log file.
	 * These will be used as key names for the Mongo record
	 * @param syncedFile file from where to extract the headers
	 * @return String array with the column names
	 */
	private String[] getFileHeaders(File syncedFile) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(syncedFile));
			String[] headers = reader.readLine().split(",");
			reader.close();
			return headers;
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
			PrintStream ps=new PrintStream(new FileOutputStream("mongoLog.txt",true),true){
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

	public static void main(String[] args) {
		config();
		new SyncedFileMongoRecorder();

	}

}
