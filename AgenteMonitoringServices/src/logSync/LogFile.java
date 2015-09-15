package logSync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * Responds to time queries over a set of log files.
 * The file must follow this name format: sensorName_hostName_logCreationDate_logFinishDate_downloadDate
 * @author Emanuel Krivoy
 */
public abstract class LogFile {

	/**
	 * Path where the log files are stored
	 */
	protected String pathToFiles;
	/**
	 * String or regex that defines the separator of values inside the log
	 */
	protected String valueSeparator;
	/**
	 * 0-indexed position of the timestamp in a log entry
	 */
	protected int entryDatePosition;

	/**
	 * Saves the last file that was queried
	 */
	private File fileCache;
	/**
	 * Saves the date of the las entry that was queried
	 */
	private Date entryCache;
	/**
	 * Saves the position of the last queried entry
	 */
	private BufferedReader readerCache;	

	/**
	 * Set of log files where the queried entries might be
	 */
	private File[] logFiles;

	/**
	 * Date format used in the log file's name
	 */
	protected final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss-SSS");

	/**
	 * Number of data fields
	 */
	private int numberOfFields;
	
	private int logStartLine;
	/**
	 * Initializes the log file with the specified parameters
	 * @param pathToFiles Path where the log files are stored
	 * @param valueSeparator String or regex that defines the separator of values inside the log
	 * @param datePosition 0-indexed position of the timestamp in a log entry
	 */
	public LogFile(String pathToFiles, String valueSeparator, int datePosition, int logStartLine) {
		this.pathToFiles = pathToFiles;
		this.valueSeparator = valueSeparator;
		this.entryDatePosition = datePosition;
		this.logStartLine = logStartLine;
		
		logFiles = getLogFilesOnPath();	
		
		numberOfFields = getColumnNames().length;
	}

	/**
	 * Returns the names of the data fields
	 * @return String array with the headers
	 */
	public String[] getColumnNames() {
		return getColumnNamesOnLog(logFiles[0]);
	}

	/**
	 * Returns the data that was logged at date or null if it isn't in the log files
	 * @param date Entry's time stamp, it will be considered up to seconds precision
	 * @return String array with data or null if the entry couldn't be found
	 * @throws Exception 
	 */
	public String[] getDataAtSecond(Date date) throws Exception {
		BufferedReader reader = null;
		
		if(fileCache!= null && dateInFileRange(date, fileCache) && entryCache.before(date))
			reader = readerCache;
		else{
			File file = getFileThatContainsEntry(date);
			if(file == null)
				return null;

			try {
				reader = new BufferedReader(new FileReader(file));
				for (int i = 0; i < this.logStartLine; i++) {
					reader.readLine();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}			
		}

		String entry;
		while((entry = reader.readLine()) != null) {
			Date entryDate = getEntryDate(entry, fileCache);
			entryDate = truncateMilis(entryDate);
			date = truncateMilis(date);
			entryCache = (Date) date.clone(); 
			if(entryDate.equals(date)) {
				readerCache = reader;
				return getDataFromEntry(entry);
			}
			//we went past the queried date
			if(entryDate.after(date)) {
				fileCache = null;
				return null;
			}
		}
		return null;
	}

	/**
	 * Return the log file that might contain the entry
	 * @param entryDate
	 * @return The file object with the possible entry
	 */
	private File getFileThatContainsEntry(Date entryDate) {
		for (File file : logFiles) 
			if(dateInFileRange(entryDate, file)){ 
				fileCache = file;
				return file;
			}
		return null;
	}

	/**
	 * Checks if the date falls inside the logging range of the file
	 * @param date
	 * @param file
	 * @return true if this date is in range, false if not
	 */
	private boolean dateInFileRange(Date date, File file) {
		return getLogStart(file).getTime() <= date.getTime() && date.getTime() <= getLogFinish(file).getTime();
	}

	/**
	 * Returns the start date of the log
	 * @param file log file 
	 * @return
	 */
	public static Date getLogStart(File file) {
		try {
			return dateFormat.parse(file.getName().split("_")[2]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the finish date of the log
	 * @param file log file 
	 * @return
	 */
	public static Date getLogFinish(File file) {
		try {
			return dateFormat.parse(file.getName().split("_")[3]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sets the specified date's milis to 0
	 * @param date
	 * @return
	 */
	private Date truncateMilis(Date date) {
		Calendar cal  = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Returns the data in the specified entry, without the timestamp
	 * @param entry
	 * @return
	 */
	protected String[] getDataFromEntry(String entry) {
		String[] temp  = entry.split(valueSeparator);
		String[] ans = new String[temp.length-1];
		int ansIndex = 0;
		for (int i = 0; i < temp.length; i++) {
			if(i == entryDatePosition)
				continue;
			ans[ansIndex]  = temp[i];
			ansIndex++;
		}
		return ans;
	}
	
	/**
	 * Sorts the log files from the earliest start date to the latest
	 * @return sorted file array
	 */
	public File[] getFilesSortedByStartDate() {
		Comparator<File> comp = new Comparator<File>() {
			
			@Override
			public int compare(File o1, File o2) {
				return (getLogStart(o1).after(getLogStart(o2))?1:-1);
			}
		};
		Arrays.sort(logFiles, comp);
		return logFiles;
	}
	
	/**
	 * Sorts the log files from the earliest finish date to the latest
	 * @return sorted file array
	 */
	public File[] getFilesSortedByFinishDate() {
		Comparator<File> comp = new Comparator<File>() {
			
			@Override
			public int compare(File o1, File o2) {
				return (getLogFinish(o1).after(getLogFinish(o2))?1:-1);
			}
		};
		Arrays.sort(logFiles, comp);
		return logFiles;
	}
	
	/**
	 * Returns the number of data fields in the log, not counting the timestamp
	 * @return number of data fields
	 */
	public int getNumberOfDataFields() {
		return numberOfFields;
	}
	
	/**
	 * Returns the earliest <i>file<i> start date in the log file pool
	 * @return the earliest possible entry date
	 */
	public Date getEarliestPossibleEntry() {
		return truncateMilis(getLogStart(getFilesSortedByStartDate()[0]));
	}
	/**
	 * Returns the earliest <i>file<i> finish date in the log file pool
	 * @return the earliest possible entry date
	 */
	public Date getLatestPossibleEntry() {
		File[] files = getFilesSortedByFinishDate();
		return truncateMilis(getLogFinish(files[files.length-1]));
	}
	/**
	 * Returns the files that are part of the log file set
	 * @return
	 */
	protected abstract File[] getLogFilesOnPath();
	/**
	 * Returns the names of the data fields in the specified log file
	 * @param file
	 * @return String array with the headers
	 */
	protected abstract String[] getColumnNamesOnLog(File file);
	/**
	 * Returns the date of a log entry, given its timestamp and the file it was found on
	 * @param dateField
	 * @param file
	 * @return Date object representing the time the entry was logged
	 */
	protected abstract Date getEntryDate(String entry, File file);
	
}
