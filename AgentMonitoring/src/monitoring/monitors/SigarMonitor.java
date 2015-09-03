package monitoring.monitors;

import static monitoring.MonitoringConstants.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;

import monitoring.configuration.SigarConfigurationInterface;
import monitoring.sigar.LoaderDll;
import monitoring.sigar.MonitorReportGenerator;

/** 
 * @author Cesar
 * 
 * This class represent a process to monitoring CPU with Sigar library. To monitoring cpu, it uses the library Sigar by Hyperic and has three processes;
 * Do initial: to validate if there are files to be pickUp by system and rename those files, Do Monitoring: to sense cpu and record in a file, 
 * and Do final: to change the name and move them to pickUp folder
 */

public class SigarMonitor extends AbstractMonitor {

	private File currentFile;
	private String init;
	
	public SigarMonitor(String id, SigarConfigurationInterface configuration) throws Exception {
		super(id, configuration);		
	}
	    
	@Override
	protected void doInitial() throws Exception{
		 setLogFileForPickUp(ID);	
		 init = MonitorReportGenerator.getInstance().getInitialReport().toString();	    
	}
	
	@Override
	public void doMonitoring() throws Exception {		 
	     int localFrecuency = 1000*frequency;  
	     Date d = new Date();
	     d.setTime(d.getTime()+(windowSizeTime*1000));	
	     currentFile = new File(recordPath+File.separator+ID+SEPARATOR+df.format(new Date())+EXT);
	     PrintWriter pw = new PrintWriter(new FileOutputStream(currentFile,true),true);
	     if(init!=null){
	    	 pw.println(init);
	    	 init = null;
	     }
	     while(d.after(new Date())){  
	    	pw.println(MonitorReportGenerator.getInstance().getStateReport());
	        Thread.sleep(localFrecuency);
	     }
	     pw.close();
	}
	
	@Override
	public void doFinal() throws Exception{		
		setLogFileForPickUp(ID);
		System.out.println(new Date()+" end "+ID);
	}
	/**
	 * Override due to it is necessary load dll in path to execute Sigar
	 */
	@Override
	public void doConfiguration() throws Exception {
		String path = ((SigarConfigurationInterface) configuration).getDllPath(); 				
		if(path==null||path.isEmpty())throw new Exception("There is not a DLL path configured");
		else new LoaderDll(path).loadLibrary();		
	}


}