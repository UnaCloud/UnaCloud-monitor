package logSync;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Abstract class the provides the basic methods for handling log files
 * @author Emanuel Krivoy

 */
public abstract class Syncer{

	/**
	 * Log files to sync
	 */
	protected LogFile[] logFiles;
	/**
	 * Date format for the final log's timestamp 
	 */
	protected DateFormat timestampformat;

	/**
	 * Defines the start of the range to query
	 */
	protected Date rangeStart;
	/**
	 * Defines the end date of the range to query
	 */
	protected Date rangeFinish;

	/**
	 * Total field in the synced entries
	 */
	protected int totalFields;

	/**
	 * Creates a new syncer that tries to sync the given logs
	 * @param logFiles log file to sync
	 * @param timestampformat format of the timestamp that will be written on the first column of the synced file
	 */
	public Syncer(LogFile[] logFiles, DateFormat timestampformat) {
		this.logFiles = logFiles;
		this.timestampformat = timestampformat;

		totalFields = 0;
		for (LogFile logFile : logFiles) 
			totalFields += logFile.getNumberOfDataFields();
		
		setFullTimeRange();
	}
	
	/**
	 * Set the current time range to be synced
	 * @param from range start
	 * @param to range end
	 */
	public void setTimeRange(Date from, Date to) {
		rangeStart = from;
		rangeFinish = to;
		onRangeReset();
	}

	/**
	 * Sets the current time range to the longest possible one
	 */
	public void setFullTimeRange() {
		rangeStart = getEarliestPossibleEntry();
		rangeFinish = getLatestPossibleEntry();
		onRangeReset();
	}

	/**
	 * Gets the earliest date where a common entry between the log files might be found
	 * @return The latest first start date of the log files
	 */
	public Date getEarliestPossibleEntry() {
		Date earliestPossible = logFiles[0].getEarliestPossibleEntry();
		for (LogFile logFile : logFiles) {
			Date curr = logFile.getEarliestPossibleEntry();
			if(curr.after(earliestPossible))
				earliestPossible = curr;
		}
		return earliestPossible;
	}

	/**
	 * Gets the lates date where a common entry between the log files might be found
	 * @return The earliest last finish date of the log files
	 */
	public Date getLatestPossibleEntry() {
		Date latestPossible = logFiles[0].getLatestPossibleEntry();
		for (LogFile logFile : logFiles) {
			Date curr = logFile.getLatestPossibleEntry();
			if(curr.before(latestPossible))
				latestPossible = curr;
		}
		return latestPossible;
	}
	
	/**
	 * Returns the final column names, concatenating all the headers of the provided log files
	 * @return String array with final column names
	 */
	public String[] getColumnNames() {
		int index = 1;
		String[] headers = new String[totalFields+1];
		headers[0] = "SyncedTime";
		for (LogFile logFile : logFiles) {
			String[] logHeaders = logFile.getColumnNames();
			for (int i = 0; i < logHeaders.length; i++) {
				headers[index] = logHeaders[i];
				index++;
			}
		}
		return headers;
	}
	
	/**
	 * Method that is called after every range reset. Should be overwritten as needed.
	 */
	protected void onRangeReset() {
	}
	
	/**
	 * Syncs the entries of the log files in the current time range 
	 */
	public abstract void sync();
}
