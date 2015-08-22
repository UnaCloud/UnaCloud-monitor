package monitoring.monitors;

import com.losandes.utils.LocalProcessExecutor;

import monitoring.configuration.OpenHardwareConfigurationInterface;
import monitoring.configuration.InterfaceSensorConfiguration;

/** 
 * @author CesarF and FiveDots
 * This class represents a sensor process to monitoring cpu performance. It uses the application Open Hardware Monitor. 
 * It has three process; * Do initial: kill process openHardware, then it validates if there are files to be pickUp by system and rename then. 
 * Do Monitoring: execute the process OpenHardwareMonitor, and Do final: rename last file to be pick up.
 */
public class OpenHardwareMonitor extends AbstractMonitor{

	private String openHardwareProcess;

	private final String LOG = "OpenHardwareMonitorLog";
	
	public OpenHardwareMonitor(String id, InterfaceSensorConfiguration conf)	throws Exception {
		super(id, conf);
	}

	@Override
	protected void doInitial() throws Exception {		
		LocalProcessExecutor.killProcess(openHardwareProcess);	
		setLogFileForPickUp(LOG);
	}

	@Override
	protected void doMonitoring() throws Exception {
		LocalProcessExecutor.createProcess(recordPath+openHardwareProcess);
		Thread.sleep(windowSizeTime*1000);
		LocalProcessExecutor.killProcess(openHardwareProcess);	
	}

	@Override
	protected void doFinal() throws Exception {
		setLogFileForPickUp(LOG);
	}

	@Override
	protected void doConfiguration() throws Exception {
		String p = ((OpenHardwareConfigurationInterface) configuration).getOpenHwProcess(); 				
		if(p==null||p.isEmpty())throw new Exception("There is not an openhardware process configured");
		openHardwareProcess = p;
	}	

}
