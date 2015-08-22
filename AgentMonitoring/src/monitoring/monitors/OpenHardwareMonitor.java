package monitoring.monitors;

import static monitoring.MonitoringConstants.EXT;
import static monitoring.MonitoringConstants.PICKUP;
import static monitoring.MonitoringConstants.SEPARATOR;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

import com.losandes.utils.LocalProcessExecutor;

import monitoring.configuration.OpenHardwareConfigurationInterface;
import monitoring.configuration.InterfaceSensorConfiguration;

public class OpenHardwareMonitor extends AbstractMonitor{

	private String openHardwareProcess;

	private final String LOG = "OpenHardwareMonitorLog";
	
	public OpenHardwareMonitor(String id, InterfaceSensorConfiguration conf)	throws Exception {
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
		Thread.sleep(windowSizeTime*1000);
		LocalProcessExecutor.killProcess(openHardwareProcess);	
	}

	@Override
	protected void doFinal() throws Exception {
		setLogFileForPickUp();
	}

	@Override
	protected void setLogFileForPickUp() {
		File folder = new File(recordPath);
		FilenameFilter filtro = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(LOG);
			}
		};
		Date d = new Date();
		for (File file : folder.listFiles(filtro)) {
			file.renameTo(new File(pickUpPath+PICKUP+SEPARATOR+file.getName()+SEPARATOR+df.format(d)+EXT));
		}	
	}

	@Override
	protected void doConfiguration() throws Exception {
		String p = ((OpenHardwareConfigurationInterface) configuration).getOpenHwProcess(); 				
		if(p==null||p.isEmpty())throw new Exception("There is not an openhardware process configured");
		openHardwareProcess = p;
	}	

}
