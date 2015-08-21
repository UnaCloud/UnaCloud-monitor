package monitoring.monitors;

import static monitoring.MonitoringConstants.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.Date;

import monitoring.configuration.SigarConfigurationInterface;
import monitoring.sigar.LoaderDll;
import monitoring.sigar.MonitorReportGenerator;

/** 
 * @author Cesar
 * 
 * This class represent a process to monitoring CPU with Sigar library. To monitoring cpu, it uses the library Sigar by Hyperic and has three processes;
 * Do initial: to validate if there are files to record in database and delete those files, Do Monitoring: to sense cpu and record in a file, and Do final: to record en db
 * This class has been only tested in Windows OS
 */

public class SigarMonitor extends AbstractMonitor {

	private File currentFile;
	
	public SigarMonitor(String id, SigarConfigurationInterface configuration) throws Exception {
		super(id, configuration);		
	}
	    
	@Override
	protected void doInitial() throws Exception{
		 setLogFileForPickUp();	
		 currentFile = new File(recordPath+File.separator+ID+df.format(new Date())+EXT);
		 PrintWriter pw = new PrintWriter(new FileOutputStream(
				 currentFile,true),true);
	     pw.println(MonitorReportGenerator.getInstance().getInitialReport());
	     pw.close();
	    
	}
	
	@Override
	public void doMonitoring() throws Exception {		 
	     int localFrecuency = 1000*frecuency;  
	     Date d = new Date();
	     d.setTime(d.getTime()+(windowSizeTime*1000));	
	     PrintWriter pw = new PrintWriter(new FileOutputStream(currentFile,true),true);
	     while(d.after(new Date())){  
	    	pw.println(MonitorReportGenerator.getInstance().getStateReport());
	        Thread.sleep(localFrecuency);
	     }
	     pw.close();
	}
	
	@Override
	public void doFinal() throws Exception{		
		setLogFileForPickUp();
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

	@Override
	protected void setLogFileForPickUp() {
		File folder = new File(recordPath);
		Date d = new Date();
		FilenameFilter filtro = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(ID);
			}
		};
		for (File file : folder.listFiles(filtro)) {
			file.renameTo(new File(pickUpPath+PICKUP+SEPARATOR+file.getName()+SEPARATOR+df.format(d)+EXT));
		}
	}

}