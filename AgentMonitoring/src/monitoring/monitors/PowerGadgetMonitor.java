package monitoring.monitors;

import static monitoring.MonitoringConstants.*;

import java.io.File;
import java.io.FilenameFilter;
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
	private String exeName;
	
	public PowerGadgetMonitor(String id, PowerGadgetConfigurationInterface configuration) throws Exception {
		super(id, configuration);
	}
	@Override
	protected void doInitial() throws Exception {
		setLogFileForPickUp();
		//"PowerLog3.0.exe"
		LocalProcessExecutor.killProcess(exeName);
	}
	
	@Override
	public void doMonitoring() throws Exception {		
		fileName = recordPath+ID+SEPARATOR+df.format(new Date())+EXT;		
		LocalProcessExecutor.executeCommand(powerlogPath+exeName+" -resolution "+(frecuency*1000)+" -duration "+windowSizeTime+" -file "+fileName);		
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
		String p = ((PowerGadgetConfigurationInterface) configuration).getPowerPath(); 				
		if(p==null||p.isEmpty())throw new Exception("There is not a PowerGadget path configured");
		else powerlogPath = p;
		p = ((PowerGadgetConfigurationInterface) configuration).getExeName(); 				
		if(p==null||p.isEmpty())throw new Exception("There is not a PowerGadget path configured");
		else exeName = p;
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
