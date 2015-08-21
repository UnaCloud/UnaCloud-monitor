package monitoring.monitors;

import static monitoring.MonitoringConstants.*;

import java.io.File;
import java.util.Date;

import monitoring.configuration.PowerGadgetConfigurationInterface;

import com.losandes.utils.LocalProcessExecutor;

/** 
 * @author Cesar
 * This class represents a process to monitoring Energy in CPU. To monitoring energy, it uses the application Power Gadget by Intel in particular the application PowerLog3.0, and it has three process;
 * Do initial: to validate if there are files to record in database and delete those files, also stop the power log process in case it's running. Do Monitoring: execute the process PowerLog3.0.exe to sense energy and record it in a file, and Do final: to record en db
 * This class only work in Windows
 */

public class PowerGadgetMonitor extends AbstractMonitor {
	
	private String powerlogPath;
	private String fileName;
	
	public PowerGadgetMonitor(String id, PowerGadgetConfigurationInterface configuration) throws Exception {
		super(id, configuration);
	}
	@Override
	protected void doInitial() throws Exception {
		setLogFileForPickUp();
		LocalProcessExecutor.executeCommand("taskkill /I /MF PowerLog3.0.exe");
	}
	
	@Override
	public void doMonitoring() throws Exception {		
		fileName = recordPath+ID+SEPARATOR+df.format(new Date())+EXT;		
		LocalProcessExecutor.executeCommand(powerlogPath+" -resolution "+(frecuency*1000)+" -duration "+windowSizeTime+" -file "+fileName);		
	}

	@Override
	public void doFinal() throws Exception{
		setLogFileForPickUp();
		System.out.println(new Date()+(" finish "+ID));
	}
	/**
	 * Override due to it is necessary load dll in path to execute Sigar
	 */
	@Override
	public void doConfiguration() throws Exception{		
		String path = ((PowerGadgetConfigurationInterface) configuration).getPowerPath(); 				
		if(path==null||path.isEmpty())throw new Exception("There is not a PowerGadget path configured");
		else powerlogPath = path;
	}
	@Override
	protected void setLogFileForPickUp() {
		File folder = new File(recordPath);
		for (File file : folder.listFiles()) {
			if(file.isFile()&&file.getName().startsWith(ID)){
				file.renameTo(new File(pickUpPath+PICKUP+SEPARATOR+file.getName()+SEPARATOR+df.format(new Date())+EXT));
			}
		}
	}
}
