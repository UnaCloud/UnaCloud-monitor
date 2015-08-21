package monitoring.monitors;

import static monitoring.MonitoringConstants.EXT;
import static monitoring.MonitoringConstants.PICKUP;
import static monitoring.MonitoringConstants.SEPARATOR;

import java.io.File;
import java.util.Date;

import com.losandes.utils.LocalProcessExecutor;

import monitoring.configuration.OpenHardwareConfigurationInterface;
import monitoring.configuration.InterfaceSensorConfiguration;

public class OpenHardwareMonitor extends AbstractMonitor{

	private String openHardwareProcess;

	private final String log = "OpenHardwareMonitorLog";
	
	protected OpenHardwareMonitor(String id, InterfaceSensorConfiguration conf)	throws Exception {
		super(id, conf);
	}

	@Override
	protected void doInitial() throws Exception {		
		LocalProcessExecutor.killProcess(openHardwareProcess);	
		setLogFileForPickUp();
	}

	@Override
	protected void doMonitoring() throws Exception {
		LocalProcessExecutor.createProcess(recordPath+openHardwareProcess);
		Thread.sleep(windowSizeTime);
		LocalProcessExecutor.killProcess(openHardwareProcess);	
	}

	@Override
	protected void doFinal() throws Exception {
		setLogFileForPickUp();
	}

	@Override
	protected void setLogFileForPickUp() {
		File folder = new File(recordPath);
		Date d = new Date();
		for (File file : folder.listFiles()) {
			if(file.isFile()&&file.getName().startsWith(log)){
				file.renameTo(new File(pickUpPath+PICKUP+SEPARATOR+file.getName()+SEPARATOR+df.format(d)+EXT));
			}
		}	
	}

	@Override
	protected void doConfiguration() throws Exception {
		String p = ((OpenHardwareConfigurationInterface) configuration).getOpenHwProcess(); 				
		if(p==null||p.isEmpty())throw new Exception("There is not an openhardware process configured");
		openHardwareProcess = p;
	}	

}
