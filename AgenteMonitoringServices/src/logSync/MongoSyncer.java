package logSync;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import mongoDB.MongoConnection;

/**
 * Log syncer that syncs the entries in the range on the database side.
 * @author Emanuel
 *
 */
public class MongoSyncer extends Syncer{

	private final static String TIMESTAMP = "Timestamp";
	private final static String HOSTNAME = "Hostname";
	
	/**
	 * Collection where the entries will be synced
	 */
	private DBCollection SyncCollection;
	
	public MongoSyncer(LogFile[] logFiles, DateFormat timestampformat, MongoConnection connection) {
		super(logFiles, timestampformat);
		SyncCollection = connection.getSyncedCollection();
	}
	
	/**
	 * Creates a record wit the specified timestamp and hostname if one does not exist
	 * @param timestamp
	 * @param hostname
	 */
	public void createRecord(String timestamp, String hostname) {
		BasicDBObject basicRecord = new BasicDBObject();
		basicRecord.append(TIMESTAMP, timestamp);
		basicRecord.append(HOSTNAME, hostname);
		
		DBCursor cursor = SyncCollection.find(basicRecord).limit(1);
		
		if(cursor.count() == 0)
			SyncCollection.insert(basicRecord);
	}
	
	/**
	 * Adds newRecord to the record with the specified timestamp and hostname
	 * @param timestamp
	 * @param hostname
	 * @param newRecord
	 */
	public void addToRecord(String timestamp, String hostname, DBObject newRecord) {
		BasicDBObject query = new BasicDBObject();
		query.append(TIMESTAMP, timestamp);
		query.append(HOSTNAME, hostname);
	
		SyncCollection.update(query, new BasicDBObject().append("$set", newRecord));
	}
	
	@Override
	public void sync() {
		for (LogFile logFile : logFiles) {
			LogFileIterator<String[]> iterator = logFile.iterator();
			while(iterator.hasNext()) {
				String[] entry = iterator.next();
				String[] headers = logFile.getColumnNames();
				
				String hostname = iterator.getCurrentHostname();
				String timestamp = iterator.getCurrentTimestamp();
				
				try {
					if(LogFile.dateFormat.parse(timestamp).before(rangeStart) || LogFile.dateFormat.parse(timestamp).after(rangeFinish))
						continue;
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				
				BasicDBObject logFileEntry = new BasicDBObject(); 
				
				for (int i = 0; i < entry.length; i++)
					logFileEntry.append(headers[i], entry[i]);
				
				createRecord(timestamp, hostname);
				
				addToRecord(timestamp, hostname, logFileEntry);
			}
		}
	}
	
	@Override
	public void onRangeReset() {
	}
	
}
