package monitoring.monitors;

import static monitoring.MonitoringConstants.EXT;
import static monitoring.MonitoringConstants.PICKUP;
import static monitoring.MonitoringConstants.SEPARATOR;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;

import com.losandes.utils.LocalProcessExecutor;
import com.losandes.utils.MySystem;

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

	private final String LOG = "OpenHardwareMonitorLog-";
	
	private final String TEMP = "OpenHMTemp";
	
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
	
	private void createDateTempFile() throws IOException{
		File f = new File(recordPath+File.separator+TEMP+SEPARATOR+df.format(new Date())+EXT);
		f.createNewFile();
	}
	
	private File getDateTempFile(){
		File folder = new File(recordPath);		
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(TEMP);
			}
		};
		File f = null;
		for(File file: folder.listFiles(filter)){f = file; break;}
		return f;
	}

	@Override
	protected void setLogFileForPickUp(final String startFileName){
		File folder = new File(recordPath);		
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(startFileName);
			}
		};
		Date d = new Date();
		File f = getDateTempFile();
		String tempName = f.getName().replace(EXT, "").replace(TEMP, "").replace(SEPARATOR, "");
		for (File file : folder.listFiles(filter)) {
			String fileName = file.getName().replace(EXT, "").replace(startFileName, "").replace(SEPARATOR, "");
			{
				if(tempName.startsWith(fileName))fileName=tempName;
			}
			file.renameTo(new File(pickUpPath+PICKUP+SEPARATOR+ID+SEPARATOR+MySystem.getHostname()+SEPARATOR+fileName+SEPARATOR+df.format(d)+EXT));
		}
		f.delete();
	}
}
