package monitoring.monitors;

import static monitoring.MonitoringConstants.EXT;
import static monitoring.MonitoringConstants.PICKUP;
import static monitoring.MonitoringConstants.SEPARATOR;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

import com.losandes.utils.LocalProcessExecutor;
import com.losandes.utils.MySystem;

import monitoring.configuration.InterfaceSensorConfiguration;
import monitoring.configuration.PerfmonMonitorConfiguration;

/** 
 * @author CesarF and FiveDots
 * This class represents a sensor process to monitoring cpu performance. It uses the application Perfmon on windows OS. 
 * It has three process; * Do initial: stop and delete the Logman counter (in case it is running), configure new Logman counter, and validates if there are files to be pickUp by system and rename then. 
 * Do Monitoring: execute Logman counter, and Do final: rename last file to be pick up.
 */
public class PerfmonMonitor extends AbstractMonitor{
	
	private String counterName;
	private String[] counters;
	private int maxFileSizeMb;
	private String countersString;

	public PerfmonMonitor(String id, InterfaceSensorConfiguration conf)throws Exception {
		super(id, conf);
	}

	@Override
	protected void doInitial() throws Exception {	
		LocalProcessExecutor.executeCommand("logman stop "+counterName);
		LocalProcessExecutor.executeCommand("logman delete "+counterName);
		countersString = "";
		for (String c : counters)countersString += ("\""+c+"\" ");		
		setLogFileForPickUp(ID);
	}

	@Override
	protected void doMonitoring() throws Exception {
		LocalProcessExecutor.executeCommand("logman create counter "+ counterName +" -c "+ countersString +"-si "
				+ frequency +" -max "+ maxFileSizeMb +" -f csv -o \""+ (recordPath+ID+SEPARATOR+df.format(new Date())) +"\"");	
		LocalProcessExecutor.executeCommand("logman start "+counterName);
		Thread.sleep(windowSizeTime*1000);
		LocalProcessExecutor.executeCommand("logman stop "+counterName);	
		LocalProcessExecutor.executeCommand("logman delete "+counterName);
	}

	@Override
	protected void doFinal() throws Exception {
		setLogFileForPickUp(ID);
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
	
	/**
	 * Override to remove Perfomn properties in file name
	 */
	@Override
	protected void setLogFileForPickUp(final String startFileName) {
		File folder = new File(recordPath);		
		FilenameFilter filtro = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(startFileName);
			}
		};
		Date d = new Date();
		for (File file : folder.listFiles(filtro)) {
			String dateInit = file.getName().split(SEPARATOR)[1];			
			file.renameTo(new File(pickUpPath+PICKUP+SEPARATOR+ID+SEPARATOR+MySystem.getHostname()+SEPARATOR+dateInit+SEPARATOR+df.format(d)+EXT));
		}
	}

}
