package logSync;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import logFiles.OpenHardware_LogFile;
import logFiles.Perfmon_LogFile;
import logFiles.PowerGadget_LogFile;
import logFiles.Sigar_LogFile;

/**
 * Iterable class that returns all the entries in a set of logs that were recorded at the same second
 * @author Emanuel Krivoy

 */
public class FileSyncer implements Iterable<String[]>{

	/**
	 * Log files to sync
	 */
	private LogFile[] logFiles;
	/**
	 * Date format for the final log's timestamp 
	 */
	private DateFormat timestampformat;

	/**
	 * Next entry in the iterator 
	 */
	private Calendar nextEntry;
	/**
	 * Defines the start of the range to query
	 */
	private Date rangeStart;
	/**
	 * Defines the end date of the range to query
	 */
	private Date rangeFinish;

	/**
	 * Total field in the synced entries
	 */
	private int totalFields;

	/**
	 * Creates a new file syncer that tries to sync the given logs
	 * @param logFiles 
	 * @param timestampformat
	 */
	public FileSyncer(LogFile[] logFiles, DateFormat timestampformat) {
		this.logFiles = logFiles;
		this.timestampformat = timestampformat;

		totalFields = 0;
		for (LogFile logFile : logFiles) 
			totalFields += logFile.getNumberOfDataFields();
	}
	
	/**
	 * Set the time range where it should iterate the entries
	 * @param from range start
	 * @param to range end
	 */
	public void setTimeRange(Date from, Date to) {
		nextEntry = null;
		rangeStart = from;
		rangeFinish = to;
	}

	/**
	 * Sets the time range to the widest possible
	 */
	public void setFullTimeRange() {
		nextEntry = null;
		rangeStart = getEarliestPossibleEntry();
		rangeFinish = getLatestPossibleEntry();
	}

	/**
	 * Returns the next entry in the range 
	 * @return String array with the synced data or null if there are no more entries
	 */
	public String[] getNextEntry() {
		//Initializes calendar on current range
		if(nextEntry == null) {
			nextEntry = Calendar.getInstance();
			nextEntry.setTime(rangeStart);
		}

		while(nextEntry.getTime().before(rangeFinish) || nextEntry.getTime().equals(rangeFinish)) {

			String[] entry = new String[totalFields+1];
			//Sets entry timestamp
			entry[0] = timestampformat.format(nextEntry.getTime());

			int lastWrite = 1;
			boolean foundNull = false;

			for (LogFile logFile : logFiles) {
				try {
					String[] read = logFile.getDataAtSecond(nextEntry.getTime());
					//The entry for that second exists
					if(read != null) {
						for (int i = 0; i < read.length; i++) {
							entry[lastWrite] = read[i];
							lastWrite++;
						}

					} else {
						nextEntry.add(Calendar.SECOND, 1);
						foundNull = true;
						break;
					}

				} catch(Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			if(!foundNull) {
				nextEntry.add(Calendar.SECOND, 1);
				return entry;
			}
		}
		return null;
	}

	/**
	 * Saves the synced entries of the current range into the specified file, separating the values with the given separator
	 * @param File file where the final and synced log will be saved
	 * @param Separator value separator
	 * @throws Exception
	 */
	public void saveToFile(File file, String separator) throws Exception {
		PrintWriter writer = new PrintWriter(file);
		String[] columnNames = getColumnNames();
		writer.print(columnNames[0]);
		for (int i = 1; i < columnNames.length; i++) {
			writer.print(separator + columnNames[i]);
		}
		writer.println();
		writer.flush();
		
		for (String[] entry : this) {
			writer.print(entry[0]);
			for (int i = 1; i < entry.length; i++) {
				writer.print(separator + entry[i]);
			}
			writer.println();
			writer.flush();
		}

		writer.close();
	}

	/**
	 * Gets the earliest date where a common entry might be
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
	 * Gets the lates date where a common entry might be
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
	 * Returns a full range iterator for the synced entries
	 * @return
	 */
	@Override
	public Iterator<String[]> iterator() {
		return new FileSyncerIterator(this);
	}
	
	/**
	 * Returns the final column names, ordering all the headers of the provided log files
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
}
