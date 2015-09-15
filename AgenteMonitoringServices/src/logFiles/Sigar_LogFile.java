package logFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import logSync.FileSyncer;
import logSync.LogFile;

public class Sigar_LogFile extends LogFile{

	public Sigar_LogFile(String pathToFiles) {
		super(pathToFiles, ",", 0, 0);
	}

	@Override
	protected File[] getLogFilesOnPath() {
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("sigar_");
			}
		};

		return new File(pathToFiles).listFiles(filter);
	}

	@Override
	protected String[] getColumnNamesOnLog(File file) {
		String[] resp = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String ln = reader.readLine();
			String[] tmp = ln.split(",");
			resp = new String[tmp.length-1];
			for (int i = 1; i < tmp.length; i++) {
				resp[i-1] = tmp[i].substring(0, tmp[i].indexOf('='));
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resp;
	}

	@Override
	protected Date getEntryDate(String entry, File file) {
		entry = entry.substring(entry.indexOf('[') + 1, entry.lastIndexOf(']'));
		String[] tmp = entry.split(",");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS");
		try {
			return df.parse(tmp[0].substring(tmp[0].indexOf('=')+1, tmp[0].length()));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected String[] getDataFromEntry(String entry) {
		entry = entry.substring(entry.indexOf('[') + 1, entry.lastIndexOf(']'));
		String[] tmp = entry.split(",");
		String[] resp = new String[tmp.length-1];
		for (int i = 1; i < resp.length; i++) {
			resp[i-1] = tmp[i].substring(tmp[i].indexOf('=')+1, tmp[i].length());
		}
		return resp;
	}
}
