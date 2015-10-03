package mongoDB;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
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
			
			for(File syncedFile : getSyncedFiles())
				recordFile(syncedFile);
			
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns all the log files recursively present in the save path
	 * @return File array with all the synced log files
	 */
	private File[] getSyncedFiles() {
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
			
			for(File f : current.listFiles(dirFilter))
				queue.add(f);
			
			for(File f : current.listFiles(syncedFileFilter))
				resp.add(f);
		}
		
		return (File[]) resp.toArray();
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

}
