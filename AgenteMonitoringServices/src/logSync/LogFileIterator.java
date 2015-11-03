package logSync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * 
 * @author Emanuel
 * Iterator that give access to all the entries of all the log files
 */
public class LogFileIterator implements Iterator<String>{

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
	private String nextEntry;
	
	public LogFileIterator(File[] logFiles, int logStartLine) {
		
		this.logFiles = logFiles;
		this.logStartLine = logStartLine;
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

	@Override
	public String next() {
		String ret = nextEntry;

		try {
			nextEntry = reader.readLine();
			if(nextEntry == null) {
				loadNextFile();
				nextEntry = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return  ret;
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

}
