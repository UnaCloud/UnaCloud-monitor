package monitoring.monitors;

import static monitoring.MonitoringConstants.EXT;
import static monitoring.MonitoringConstants.PICKUP;
import static monitoring.MonitoringConstants.SEPARATOR;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

import com.losandes.utils.LocalProcessExecutor;

import monitoring.configuration.InterfaceSensorConfiguration;
import monitoring.configuration.PerfmonMonitorConfiguration;

public class PerfmonMonitor extends AbstractMonitor{
	
	private String counterName;
	private String[] counters;
	private int maxFileSizeMb;

	public PerfmonMonitor(String id, InterfaceSensorConfiguration conf)throws Exception {
		super(id, conf);
	}

	@Override
	protected void doInitial() throws Exception {		
		//TODO delete counter
		LocalProcessExecutor.executeCommand("logman stop "+counterName);
		String countersString = "";
		for (String c : counters)countersString.concat("\""+c+"\" ");
		LocalProcessExecutor.executeCommand("logman create counter "+ counterName +" -c "+ countersString +"-si "
						+ frecuency +" -max "+ maxFileSizeMb +" -f csv -o \""+ (recordPath+ID+SEPARATOR+df.format(new Date())) +"\"");	
		setLogFileForPickUp();
	}

	@Override
	protected void doMonitoring() throws Exception {
		LocalProcessExecutor.executeCommand("logman start "+counterName);
		Thread.sleep(windowSizeTime);
		LocalProcessExecutor.executeCommand("logman stop "+counterName);	
	}

	@Override
	protected void doFinal() throws Exception {
		setLogFileForPickUp();
	}	

	@Override
	protected void doConfiguration() throws Exception {
		String p = ((PerfmonMonitorConfiguration) configuration).getCounterName(); 				
		if(p==null||p.isEmpty())throw new Exception("There is not a perfomn counter name configured");
		counterName = p;
		String [] counts = ((PerfmonMonitorConfiguration) configuration).getCounters(); 				
		if(counts==null||counts.length==0)throw new Exception("There is not at least one counter configured");
		counters = counts;
		int maxSize = ((PerfmonMonitorConfiguration) configuration).getMaxFileSize(); 				
		if(p==null||p.isEmpty())throw new Exception("There is not a perfomn max size file configured");
		maxFileSizeMb = maxSize;
	}

	@Override
	protected void setLogFileForPickUp() {
		File folder = new File(recordPath);
		FilenameFilter filtro = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(ID);
			}
		};
		Date d = new Date();
		for (File file : folder.listFiles(filtro)) {						
			file.renameTo(new File(pickUpPath+PICKUP+SEPARATOR+file.getName()+SEPARATOR+df.format(d)+EXT));			
		}
	}

}
