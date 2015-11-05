package logSync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.Iterator;

/**
 * 
 * @author Emanuel
 * Iterator that give access to all the entries of all the log files
 */
public class LogFileIterator<T> implements Iterator<String[]>{

	/**
	 * LogFile object to iterate
	 */
	private LogFile logFile;
	
	/**
	 * Log files from the LogFile object
	 */
	private File[] logFiles;
	
	/**
	 * The next file to be loaded
	 */
	private int nextFile;
	
	/**
	 * Line number where the data starts at a log file
	 */
	private int logStartLine;
	
	private BufferedReader reader;
	
	private String currentEntry;
	private String nextEntry;
	
	public LogFileIterator(LogFile logFile) {
		
		this.logFile = logFile;
		this.logFiles = logFile.getFiles();
		this.logStartLine = logFile.getLogStartLine();
		
		nextFile = 0;
		
		try {
			loadNextFile();
			
			nextEntry = reader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean hasNext() {
		return nextEntry != null;
	}

	/**
	 * Returns the next's entry data
	 */
	@Override
	public String[] next() {
		currentEntry = nextEntry;

		try {
			nextEntry = reader.readLine();
			if(nextEntry == null) {
				loadNextFile();
				nextEntry = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return  logFile.getDataFromEntry(currentEntry);
	}

	/**
	 * Loads the next file if there is one
	 */
	private void loadNextFile() {
		if(nextFile < logFiles.length) {
			try {
				reader = new BufferedReader(new FileReader(logFiles[nextFile++]));
				for (int i = 0; i < logStartLine; i++) 
					reader.readLine();	
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void remove() {
	}

	/**
	 * Returns the correctly formatted timestamp of the current entry
	 * @return timestamp of the current entry
	 */
	public String getCurrentTimestamp() {
		Date timestamp = logFile.getEntryDate(currentEntry, logFiles[nextFile-1]);
		return LogFile.dateFormat.format(timestamp);
	}
	
	/**
	 * Returns the hostname of the machine that created of the current entry
	 * @return hostname fo the current entry
	 */
	public String getCurrentHostname() {
		return LogFile.getLogHostname(logFiles[nextFile-1]);
	}
}
