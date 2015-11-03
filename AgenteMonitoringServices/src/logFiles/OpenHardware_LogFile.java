package logFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import logSync.LogFile;

public class OpenHardware_LogFile extends LogFile{

	public OpenHardware_LogFile(String pathToFiles) {
		super(pathToFiles, ",", 0, 2);
	}

	@Override
	public File[] getLogFilesOnPath() {
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
		/*String resp = "/intelcpu/0/load/1,/intelcpu/0/load/2,/intelcpu/0/load/3,/intelcpu/0/load/4,/intelcpu/0/load/0,/intelcpu/0/temperature/0,/intelcpu/0/temperature/1,/intelcpu/0/temperature/2,/intelcpu/0/temperature/3,/intelcpu/0/temperature/4,/intelcpu/0/clock/1,/intelcpu/0/clock/2,/intelcpu/0/clock/3,/intelcpu/0/clock/4,/intelcpu/0/power/0,/intelcpu/0/power/1,/intelcpu/0/power/2,/intelcpu/0/power/3,/intelcpu/0/clock/0,/ram/load/0,/ram/data/0,/ram/data/1,/hdd/0/temperature/0,/hdd/0/load/0,/hdd/1/load/0";
		return resp.split(",");*/
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
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");

		try {
			return df.parse(tmp[entryDatePosition]);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
