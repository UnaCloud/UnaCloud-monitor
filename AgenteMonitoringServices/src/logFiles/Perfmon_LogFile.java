package logFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import logSync.LogFile;

public class Perfmon_LogFile extends LogFile{

	public Perfmon_LogFile(String pathToFiles) {
		super(pathToFiles, ",", 0, 2);
	}

	@Override
	protected File[] getLogFilesOnPath() {
		FilenameFilter filter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("perfmon_");
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
				resp[i-1] = tmp[i].substring(1, tmp[i].length()-1);
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
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss.SSS");

		try {
			tmp[entryDatePosition] = tmp[entryDatePosition].substring(1, tmp[entryDatePosition].length()-1); 
			return df.parse(tmp[entryDatePosition]);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected String[] getDataFromEntry(String entry) {
		String[] temp  = entry.split(valueSeparator);
		String[] ans = new String[temp.length-1];
		int ansIndex = 0;
		for (int i = 0; i < temp.length; i++) {
			if(i == entryDatePosition)
				continue;
			ans[ansIndex]  = temp[i].substring(1, temp[i].length()-1);
			ansIndex++;
		}
		return ans;
	}
}
