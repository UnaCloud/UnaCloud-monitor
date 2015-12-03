package monitoring.monitors;

import static monitoring.MonitoringConstants.EXT;
import static monitoring.MonitoringConstants.SEPARATOR;

import java.util.Date;

import monitoring.configuration.PowerGadgetConfigurationInterface;

import com.losandes.utils.LocalProcessExecutor;


/** 
 * @author Cesar
 * This class represents a process to monitoring Energy consumed by cpu. To monitoring energy, it uses the application Power Gadget by Intel in particular the application PowerLog. 
 * It has three process; * Do initial: to validate if there are files to be pickUp by system and rename those files, also stop the power log process in case it's running. 
 * Do Monitoring: execute the process PowerLog to sense energy and record it in a file, and Do final: rename last file be pick up.
 */

public class PowerGadgetMonitor extends AbstractMonitor {
	
	private String powerlogPath;
	private String fileName;
	private String exeName;
	
	public PowerGadgetMonitor(String id, PowerGadgetConfigurationInterface configuration) throws Exception {
		super(id, configuration);
	}
	@Override
	protected void doInitial() throws Exception {
		setLogFileForPickUp(ID);
		//"PowerLog3.0.exe"
		LocalProcessExecutor.killProcess(exeName);
	}
	
	@Override
	public void doMonitoring() throws Exception {		
		fileName = recordPath+ID+SEPARATOR+df.format(new Date())+EXT;	
		String[] cmdarray = {powerlogPath+exeName, "-resolution", ""+(frequency*1000), "-duration", ""+windowSizeTime, "-file", fileName};
		LocalProcessExecutor.executeCommand(cmdarray);		
	}

	@Override
	public void doFinal() throws Exception{
		setLogFileForPickUp(ID);
		System.out.println(new Date()+(" finish "+ID));
	}
	/**
	 * Override due to it is necessary load dll in path to execute Sigar
	 */
	@Override
	public void doConfiguration() throws Exception{		
		String p = ((PowerGadgetConfigurationInterface) configuration).getPowerPath(); 				
		if(p==null||p.isEmpty())throw new Exception("There is not a PowerGadget path configured");
		else powerlogPath = p;
		p = ((PowerGadgetConfigurationInterface) configuration).getExeName(); 				
		if(p==null||p.isEmpty())throw new Exception("There is not a PowerGadget path configured");
		else exeName = p;
	}
}
