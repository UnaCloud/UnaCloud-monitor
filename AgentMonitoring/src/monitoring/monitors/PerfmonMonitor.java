package monitoring.monitors;

import static monitoring.MonitoringConstants.SEPARATOR;
import java.util.Date;

import com.losandes.utils.LocalProcessExecutor;

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

	public PerfmonMonitor(String id, InterfaceSensorConfiguration conf)throws Exception {
		super(id, conf);
	}

	@Override
	protected void doInitial() throws Exception {	
		LocalProcessExecutor.executeCommand("logman stop "+counterName);
		LocalProcessExecutor.executeCommand("logman delete "+counterName);
		String countersString = "";
		for (String c : counters)countersString.concat("\""+c+"\" ");
		LocalProcessExecutor.executeCommand("logman create counter "+ counterName +" -c "+ countersString +"-si "
						+ frequency +" -max "+ maxFileSizeMb +" -f csv -o \""+ (recordPath+ID+SEPARATOR+df.format(new Date())) +"\"");	
		setLogFileForPickUp(ID);
	}

	@Override
	protected void doMonitoring() throws Exception {
		LocalProcessExecutor.executeCommand("logman start "+counterName);
		Thread.sleep(windowSizeTime*1000);
		LocalProcessExecutor.executeCommand("logman stop "+counterName);	
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

}
