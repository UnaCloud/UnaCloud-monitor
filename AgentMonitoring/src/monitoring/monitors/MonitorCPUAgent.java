package monitoring.monitors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import connection.MongoConnection;
import connection.MonitorDatabaseConnection;
import enums.ItemCPUMetrics;
import enums.ItemCPUReport;
import reports.MonitorInitialReport;
import reports.MonitorReport;
import utils.MonitorReportGenerator;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;

/** 
 * @author Cesar
 * 
 * This class represent a process to monitoring CPU. To monitoring cpu, it uses the library Sigar by Hyperic and has three processes;
 * Do initial: to validate if there are files to record in database and delete those files, Do Monitoring: to sense cpu and record in a file, and Do final: to record en db
 * This class has been only tested in Windows OS
 */

public class MonitorCPUAgent extends AbstractMonitor {
	
	//private File f;
	
	public MonitorCPUAgent(MonitorDatabaseConnection con) throws Exception {
		super(con);		
	}
	
	@Override
	public void toEnable(String record) throws Exception {	
		super.toEnable(record);
		//f = new File(recordPath);
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
	     if(reduce>windowSizeTime)reduce= 0;
	     d.setTime(d.getTime()+(windowSizeTime*1000)-(reduce*1000));	
	     File f = new File(recordPath);
	     PrintWriter pw = new PrintWriter(new FileOutputStream(f,true),true);
	     while(d.after(new Date())){  
	    	pw.println(MonitorReportGenerator.getInstance().getStateReport());
	        Thread.sleep(localFrecuency);
	     }
	     if(reduce>0)reduce = 0;
	     pw.close();
	}
	
	@Override
	public void doFinal() throws Exception{
		recordData();
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
				recordData();
				cleanFile();
			}
		}
	}
	
	private void recordData() throws Exception{		
		File f = new File(recordPath);
		BufferedReader bf = new BufferedReader(new FileReader(f));
		String line = null;
		MonitorInitialReport initial = null; 
		ArrayList<MonitorReport> reports = new ArrayList<MonitorReport>();
		while((line=bf.readLine())!=null&&!line.isEmpty()){
			if(line.startsWith("MonitorInitialReport")){
				initial = new MonitorInitialReport(line);
			}else if(line.startsWith("MonitorReport")){
				reports.add(new MonitorReport(line));
			}
		}
		bf.close();
		if(reports.size()>0){
			if(initial!=null)saveInitialReport(initial, connection.generateConnection());
			saveReports(reports, connection.generateConnection());
			connection.generateConnection().close();
		}	  	
	}
	
	 private void saveInitialReport(MonitorInitialReport initialReport, MongoConnection db) throws UnknownHostException { 
		    BasicDBObject doc = (BasicDBObject) db.infrastructureCollection().findOne(new BasicDBObject(ItemCPUMetrics.HOSTNAME.title(),initialReport.getHostname()));
		    if(doc!=null&&compareInitialReport(initialReport, doc))doc = null; 		  
		    if(doc == null){
		    	doc = new BasicDBObject(ItemCPUMetrics.HOSTNAME.title(),initialReport.getHostname())
				.append(ItemCPUMetrics.TIME.title(),initialReport.getTimest())
				.append(ItemCPUMetrics.TIME_MILLI.title(),initialReport.getTimeLong())
				.append(ItemCPUMetrics.OS_NAME.title(), initialReport.getOperatingSystemName())
				.append(ItemCPUMetrics.OS_VERSION.title(), initialReport.getOperatingSystemVersion())
				.append(ItemCPUMetrics.OS_ARQUITECTURE.title(), initialReport.getOperatingSystemArchitect())				
				.append(ItemCPUMetrics.MFLOPS.title(), initialReport.getMflops())
				.append(ItemCPUMetrics.CPU_SECONDS.title(), initialReport.getTimeinSecs())
				.append(ItemCPUMetrics.CPU_MODEL.title(), initialReport.getcPUModel())
				.append(ItemCPUMetrics.CPU_VENDOR.title(), initialReport.getcPUVendor())
				.append(ItemCPUMetrics.CPU_CORES.title(), initialReport.getcPUCores())
				.append(ItemCPUMetrics.CPU_SOCKETS.title(), initialReport.getTotalSockets())
				.append(ItemCPUMetrics.CPU_MHZ.title(), initialReport.getcPUMhz())
				.append(ItemCPUMetrics.CORES_X_SOCKETS.title(), initialReport.getCoresPerSocket())
				.append(ItemCPUMetrics.RAM_SIZE.title(), initialReport.getrAMMemorySize())
				.append(ItemCPUMetrics.SWAP_SIZE.title(), initialReport.getSwapMemorySize())
				.append(ItemCPUMetrics.HD_SPACE.title(), initialReport.getHardDiskSpace())
				.append(ItemCPUMetrics.HD_FILESYSTEM.title(), initialReport.getHardDiskFileSystem())
				.append(ItemCPUMetrics.MAC.title(), initialReport.getNetworkMACAddress())
				.append(ItemCPUMetrics.NET_IP.title(), initialReport.getNetworkIPAddress())
				.append(ItemCPUMetrics.NET_INTERFACE.title(), initialReport.getNetworkInterface())
				.append(ItemCPUMetrics.NET_MASK.title(), initialReport.getNetworkNetmask())
				.append(ItemCPUMetrics.NET_GATEWAY.title(), initialReport.getNetworkGateway());       
			    System.out.println(db.infrastructureCollection().insert(doc).getN());
		    }				
	 }
	 
	 private void saveReports(ArrayList<MonitorReport>reports, MongoConnection db){
		 BulkWriteOperation builder = db.cpuCollection().initializeOrderedBulkOperation();
		 for (MonitorReport statusReport : reports)if(statusReport!=null){
			String[] pros = statusReport.getProcesses().split(",");
			List<BasicDBObject> listProcesses = new ArrayList<BasicDBObject>();
			for (String pro : pros) {
				pro = pro.replace("(", "").replace(")", "").trim();
				if(!pro.isEmpty()){
					BasicDBObject doc = new BasicDBObject();
					String[] values = pro.split(";");
					for (String val : values) {
						String[] cc = val.split(":");
						doc.append(cc[0], cc[1]);
					}
					listProcesses.add(doc);
				}				
			}
			BasicDBObject doc = new BasicDBObject(ItemCPUReport.HOSTNAME.title(),statusReport.getHostName())
			.append(ItemCPUReport.TIME.title(), statusReport.getTimest())
			.append(ItemCPUReport.TIME_MILLI.title(),statusReport.getTimeLong())
			.append(ItemCPUReport.USERNAME.title(), statusReport.getUserName())
			.append(ItemCPUReport.UP_TIME.title(), statusReport.getCpuUptime())
			.append(ItemCPUReport.CPU_IDLE.title(), statusReport.getCpuIdle())
			.append(ItemCPUReport.NO_CPU_IDLE.title(), statusReport.getNoCpuIdle())
			.append(ItemCPUReport.CPU_USER.title(), statusReport.getCpuUser())
			.append(ItemCPUReport.CPU_SYS.title(), statusReport.getCpuSys())
			.append(ItemCPUReport.CPU_NICE.title(), statusReport.getCpuNice())
			.append(ItemCPUReport.CPU_WAIT.title(), statusReport.getCpuWait())
			.append(ItemCPUReport.CPU_COMBINED.title(), statusReport.getCpuCombined())
			.append(ItemCPUReport.TOTAL_USER.title(),statusReport.getTotalCpuUserTime())
			.append(ItemCPUReport.TOTAL_SYS.title(), statusReport.getTotalCpuSysTime())
			.append(ItemCPUReport.TOTAL_NICE.title(), statusReport.getTotalCpuNiceTime())
			.append(ItemCPUReport.TOTAL_WAIT.title(), statusReport.getTotalCpuWaitTime())
			.append(ItemCPUReport.TOTAL_IDLE.title(), statusReport.getTotalCpuIdleTime())
			.append(ItemCPUReport.RAM_FREE.title(), statusReport.getRamMemoryFree())
			.append(ItemCPUReport.RAM_USED.title(), statusReport.getRamMemoryUsed())
			.append(ItemCPUReport.MEM_FREE.title(), statusReport.getMemFreePercent())
			.append(ItemCPUReport.MEM_USED.title(), statusReport.getMemUsedPercent())
			.append(ItemCPUReport.SWAP_FREE.title(), statusReport.getSwapMemoryFree())
			.append(ItemCPUReport.SWAP_PAGE_IN.title(), statusReport.getSwapMemoryPageIn())
			.append(ItemCPUReport.SWAP_PAGE_OUT.title(), statusReport.getSwapMemoryPageOut())
			.append(ItemCPUReport.SWAP_USED.title(), statusReport.getSwapMemoryUsed())
			.append(ItemCPUReport.HD_FREE.title(), statusReport.getHardDiskFreeSpace())
			.append(ItemCPUReport.HD_USED.title(), statusReport.getHardDiskUsedSpace())
			.append(ItemCPUReport.NET_RX_BYTES.title(),statusReport.getNetRxBytes())
			.append(ItemCPUReport.NET_TX_BYTES.title(), statusReport.getNetTxBytes())
			.append(ItemCPUReport.NET_SPEED.title(), statusReport.getNetSpeed())
			.append(ItemCPUReport.NET_RX_ERRORS.title(), statusReport.getNetRxErrors())
			.append(ItemCPUReport.NET_TX_ERRORS.title(), statusReport.getNetTxErrors())
			.append(ItemCPUReport.NET_RX_PACKETS.title(), statusReport.getNetRxPackets())
            .append(ItemCPUReport.NET_TX_PACKETS.title(), statusReport.getNetTxPackets())
            .append(ItemCPUReport.PROCESSES.title(),listProcesses);		
			builder.insert(doc);
        }		
		System.out.println("Insert: "+builder.execute().getInsertedCount());
	 }
	 
	 private void cleanFile() throws FileNotFoundException{
		File f = new File(recordPath);
		PrintWriter writer = new PrintWriter(f);
		writer.print("");
		writer.close();
	 }
	 /**
	  * Method to compare the current machine infrastructure with the information in db
	  * @param m1 current machine infrastructure
	  * @param object last machine infrastructure 
	  * @return
	  */
	 private boolean compareInitialReport(MonitorInitialReport m1, BasicDBObject object){		
		 if(!object.get(ItemCPUMetrics.OS_NAME.title()).equals(m1.getOperatingSystemName()))return true;
		 else if(!object.get(ItemCPUMetrics.OS_VERSION.title()).equals(m1.getOperatingSystemVersion()))return true;
		 else if(!object.get(ItemCPUMetrics.OS_ARQUITECTURE.title()).equals(m1.getOperatingSystemArchitect()))return true;
		 else if(!object.get(ItemCPUMetrics.CPU_MODEL.title()).equals(m1.getcPUModel()))return true;
		 else if(!object.get(ItemCPUMetrics.CPU_VENDOR.title()).equals(m1.getcPUVendor()))return true;
		 else if(!object.get(ItemCPUMetrics.CPU_CORES.title()).equals(m1.getcPUCores()))return true;
		 else if(!object.get(ItemCPUMetrics.CPU_SOCKETS.title()).equals(m1.getTotalSockets()))return true;
		 else if(!object.get(ItemCPUMetrics.CPU_MHZ.title()).equals(m1.getcPUMhz()))return true;
		 else if(!object.get(ItemCPUMetrics.CORES_X_SOCKETS.title()).equals(m1.getCoresPerSocket()))return true;
		 else if(!object.get(ItemCPUMetrics.RAM_SIZE.title()).equals(m1.getrAMMemorySize()))return true;
		 else if(!object.get(ItemCPUMetrics.SWAP_SIZE.title()).equals(m1.getSwapMemorySize()))return true;
		 else if(!object.get(ItemCPUMetrics.HD_SPACE.title()).equals(m1.getHardDiskSpace()))return true;
		 else if(!object.get(ItemCPUMetrics.HD_FILESYSTEM.title()).equals(m1.getHardDiskFileSystem()))return true;
		 else if(!object.get(ItemCPUMetrics.MAC.title()).equals(m1.getNetworkMACAddress()))return true;
		 else try {
			 if(!object.get(ItemCPUMetrics.MFLOPS.title()).equals(m1.getMflops()))return true;
			 else if(!object.get(ItemCPUMetrics.CPU_SECONDS.title()).equals(m1.getTimeinSecs()))return true;
			 else if(!object.get(ItemCPUMetrics.NET_IP.title()).equals(m1.getNetworkIPAddress()))return true;
			 else if(!object.get(ItemCPUMetrics.NET_INTERFACE.title()).equals(m1.getNetworkInterface()))return true;
			 else if(!object.get(ItemCPUMetrics.NET_MASK.title()).equals(m1.getNetworkNetmask()))return true;
			 else if(!object.get(ItemCPUMetrics.NET_GATEWAY.title()).equals(m1.getNetworkGateway()))return true;   
		 } catch (Exception e) {
			 return true;
		 }		
		 return false;
	 }
}