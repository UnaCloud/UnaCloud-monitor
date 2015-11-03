package logFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import logSync.LogFile;

public class PowerGadget_LogFile extends LogFile{

	public PowerGadget_LogFile(String pathToFiles) {
		super(pathToFiles, ",", 0, 1);
	}

	@Override
	public File[] getLogFilesOnPath() {
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("powerGadget_");
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
	public Date getEntryDate(String entry, File file) {
		String[] tmp = entry.split(valueSeparator);
		SimpleDateFormat df = new SimpleDateFormat("kk:mm:ss:SSS");
		Calendar timestampCal = Calendar.getInstance();
		Calendar fileCal = Calendar.getInstance();
		try {
			timestampCal.setTime(df.parse(tmp[entryDatePosition]));
			fileCal.setTime(getLogStart(file));
			return mergeCalendars(timestampCal, fileCal);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Date mergeCalendars(Calendar timestampCal, Calendar fileCal) {
		fileCal.set(Calendar.HOUR_OF_DAY, timestampCal.get(Calendar.HOUR_OF_DAY));
		fileCal.set(Calendar.MINUTE, timestampCal.get(Calendar.MINUTE));
		fileCal.set(Calendar.SECOND, timestampCal.get(Calendar.SECOND));
		fileCal.set(Calendar.MILLISECOND, timestampCal.get(Calendar.MILLISECOND));
		return fileCal.getTime();
	}
}
