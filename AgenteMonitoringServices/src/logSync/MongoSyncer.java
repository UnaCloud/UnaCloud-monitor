package logSync;

import java.text.DateFormat;
import java.util.Date;

import mongoDB.MongoConnection;

public class MongoSyncer extends Syncer{

	private MongoConnection connection;
	
	public MongoSyncer(LogFile[] logFiles, DateFormat timestampformat, MongoConnection connection) {
		super(logFiles, timestampformat);
		this.connection =  connection;
	}
	
	/**
	 * Checks whether an entry with the same timestamp has been recorded in the database
	 * @return true is an entry 
	 */
	public boolean entryExists(Date timestamp) {
		//TODO
		return true;
	}
	
	@Override
	public void sync() {
		// TODO Auto-generated method stub
		
	}
	
}
