package monitoring.monitors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;

import monitoring.configuration.AbstractSigarConfiguration;
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

	
	public SigarMonitor(String id, AbstractSigarConfiguration configuration) throws Exception {
		super(id, configuration);		
	}
	    
	@Override
	public void doInitial() throws Exception{
		if(isReady()){
			 File f = new File(recordPath);
			 PrintWriter pw = new PrintWriter(new FileOutputStream(f,true),true);
		     pw.println(MonitorReportGenerator.getInstance().getInitialReport());
		     pw.close();
		}	     
	}
	
	@Override
	public void doMonitoring() throws Exception {
		 checkFile();
	     int localFrecuency = 1000*frecuency;  
	     Date d = new Date();
	     d.setTime(d.getTime()+(windowSizeTime*1000));	
	     File f = new File(recordPath);
	     PrintWriter pw = new PrintWriter(new FileOutputStream(f,true),true);
	     while(d.after(new Date())){  
	    	pw.println(MonitorReportGenerator.getInstance().getStateReport());
	        Thread.sleep(localFrecuency);
	     }
	     pw.close();
	}
	
	@Override
	public void doFinal() throws Exception{
		cleanFile();
		System.out.println(new Date()+" end cpu");
	}

	@Override
	public void sendError(Exception e) {
		toError();
	}	
	
	private void checkFile() throws Exception{
		File f = new File(recordPath);
		if(!f.exists())f.createNewFile();
		else{
			if(f.length()>0){
				cleanFile();
			}
		}
	}	
	 
	private void cleanFile() throws FileNotFoundException{
		File f = new File(recordPath);
		PrintWriter writer = new PrintWriter(f);
		writer.print("");
		writer.close();
	}
	 
	@Override
	public void configure() {
		try {
			String path = ((AbstractSigarConfiguration) configuration).getDllPath(); 				
			if(path==null||path.isEmpty())System.err.println("There is not a DLL path configured");
			else{
				new LoaderDll(path).loadLibrary();
				super.configure();
			}				
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

}