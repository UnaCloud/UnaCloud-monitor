package logSync;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * Iterable class that returns all the entries in a set of logs that were recorded at the same second
 * @author Emanuel Krivoy

 */
public class FileSyncer extends Syncer implements Iterable<String[]>{

	/**
	 * Final, and synced, log file
	 */
	private File syncedFile;
	
	/**
	 * String used to separate value in the final log file
	 */
	private String separator;
	
	/**
	 * Next entry in the iterator 
	 */
	private Calendar nextEntry;

	/**
	 * Creates a new file syncer that tries to sync the given logs
	 * @param logFiles log file to sync
	 * @param timestampformat format of the timestamp that will be written on the first column of the synced file
	 * @param syncedFile path to synced file
	 * @param separator string used to separate the values in the synced file
	 */
	public FileSyncer(LogFile[] logFiles, DateFormat timestampformat, File syncedFile, String separator) {
		super(logFiles,timestampformat);
		this.syncedFile = syncedFile;
		this.separator = separator;
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
	 * @throws Exception
	 */
	private void saveToFile() throws Exception {
		PrintWriter writer = new PrintWriter(syncedFile);
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

	@Override
	public void sync() {
		try {
			saveToFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onRangeReset() {
		nextEntry = null;
	}
	
	/**
	 * Returns an iterator for the synced entries on the currently set time range
	 * @return
	 */
	@Override
	public Iterator<String[]> iterator() {
		return new FileSyncerIterator(this);
	}
}
