package monitoring.monitors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;

import monitoring.configuration.AbstractPowerGadgetConfiguration;

import com.losandes.utils.LocalProcessExecutor;

/** 
 * @author Cesar
 * This class represents a process to monitoring Energy in CPU. To monitoring energy, it uses the application Power Gadget by Intel in particular the application PowerLog3.0, and it has three process;
 * Do initial: to validate if there are files to record in database and delete those files, also stop the power log process in case it's running. Do Monitoring: execute the process PowerLog3.0.exe to sense energy and record it in a file, and Do final: to record en db
 * This class only work in Windows
 */

public class PowerGadgetMonitor extends AbstractMonitor {
	
	private String powerlogPath;
	
	public PowerGadgetMonitor(String id, AbstractPowerGadgetConfiguration configuration) throws Exception {
		super(id, configuration);
	}
	@Override
	public void doInitial() throws Exception {
		if(isReady())LocalProcessExecutor.executeCommand("taskkill /I /MF PowerLog3.0.exe");
	}

	@Override
	public void doMonitoring() throws Exception {
		//C:\\Program Files\\Intel\\Power Gadget 3.0\\PowerLog3.0.exe
		checkFile();
		LocalProcessExecutor.executeCommand(powerlogPath+" -resolution "+(frecuency*1000)+" -duration "+windowSizeTime+" -file "+recordPath);
		
	}

	@Override
	public void doFinal() throws Exception{
		cleanFile(new File(recordPath));
		System.out.println(new Date()+(" finish power gadget"));
	}
	
	private void checkFile() throws Exception{
		File f = new File(recordPath);
		if(f.exists()){
			if(f.length()>0){
				cleanFile(f);
			}
		}		
	}
	
	private void cleanFile(File f) throws FileNotFoundException{
		if(f.exists()){
			PrintWriter writer = new PrintWriter(f);
			writer.print("");
			writer.close();			
		}
    }

	@Override
	public void configure() {		
		String path = ((AbstractPowerGadgetConfiguration) configuration).getPowerPath(); 				
		if(path==null||path.isEmpty())System.err.println("There is not a PowerGadget path configured");
		else{
			powerlogPath = path;
			super.configure();
		}		
	}
}
