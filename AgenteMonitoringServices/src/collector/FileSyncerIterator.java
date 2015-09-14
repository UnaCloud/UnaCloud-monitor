package collector;

import java.util.Iterator;

/**
 * 
 * @author Emanuel
 *
 */
public class FileSyncerIterator implements Iterator<String[]>{

	String[] nextEntry;
	FileSyncer fileSyncer;
	
	public FileSyncerIterator(FileSyncer fileSyncer) {
		this.fileSyncer = fileSyncer;
		fileSyncer.setFullTimeRange();
		nextEntry = fileSyncer.getNextEntry();
	}
	
	@Override
	public boolean hasNext() {
		return nextEntry != null;
	}

	@Override
	public String[] next() {
		String[] ret = nextEntry;
		nextEntry = fileSyncer.getNextEntry();
		return  ret;
	}

	@Override
	public void remove() {
	}

}
