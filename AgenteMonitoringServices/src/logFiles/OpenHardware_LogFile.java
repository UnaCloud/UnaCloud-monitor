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

import logSync.LogFile;

public class OpenHardware_LogFile extends LogFile{

	public OpenHardware_LogFile(String pathToFiles) {
		super(pathToFiles, ",", 0, 2);
	}

	@Override
	protected File[] getLogFilesOnPath() {
		FilenameFilter filter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("openHardware_");
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
				resp[i-1] = tmp[i];
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resp;
	}

	@Override
	protected Date getEntryDate(String entry, File file) {
		String[] tmp = entry.split(valueSeparator);
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");

		try {
			return df.parse(tmp[entryDatePosition]);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
