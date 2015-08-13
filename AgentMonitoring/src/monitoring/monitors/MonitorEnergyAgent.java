package monitoring.monitors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import physicalmachine.Network;
import reports.MonitorEnergyReport;

import com.losandes.utils.LocalProcessExecutor;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;

import connection.MongoConnection;
import connection.MonitorDatabaseConnection;
import enums.ItemEnergyReport;


/** 
 * @author Cesar
 * This class represents a process to monitoring Energy in CPU. To monitoring energy, it uses the application Power Gadget by Intel in particular the application PowerLog3.0, and it has three process;
 * Do initial: to validate if there are files to record in database and delete those files, also stop the power log process in case it's running. Do Monitoring: execute the process PowerLog3.0.exe to sense energy and record it in a file, and Do final: to record en db
 * This class only work in Windows
 */

public class MonitorEnergyAgent extends AbstractMonitor {
	
	private String powerlogPath;
	
	public MonitorEnergyAgent(MonitorDatabaseConnection con) throws Exception {
		super(con);
	}
	@Override
	public void doInitial() throws Exception {
		if(isReady())LocalProcessExecutor.executeCommand("taskkill /IMF PowerLog3.0.exe");
	}

	@Override
	public void doMonitoring() throws Exception {
		//C:\\Program Files\\Intel\\Power Gadget 3.0\\PowerLog3.0.exe
		checkFile();
		if(reduce>windowSizeTime)reduce= 0;
		LocalProcessExecutor.executeCommand(powerlogPath+" -resolution "+(frecuency*1000)+" -duration "+(windowSizeTime-reduce)+" -file "+recordPath);
		if(reduce>0)reduce=0;
	}

	@Override
	public void doFinal() throws Exception{
		recordData();
		cleanFile(new File(recordPath));
		System.out.println(new Date()+"Termine energia");
	}
	
	private void checkFile() throws Exception{
		File f = new File(recordPath);
		if(f.exists()){
			if(f.length()>0){
				recordData();
				cleanFile(f);
			}
		}		
	}
	private void recordData() throws Exception{	
		BufferedReader bf = new BufferedReader(new FileReader(new File(recordPath)));
		String line = null;
		ArrayList<MonitorEnergyReport> reports = new ArrayList<MonitorEnergyReport>();
		Date registerDate = new Date();
		String hostname = Network.getHostname();
		while((line=bf.readLine())!=null&&!line.isEmpty()){
			if(!line.startsWith("System")){
				MonitorEnergyReport m = new MonitorEnergyReport(line);
				m.setRegisterDate(registerDate);
				m.setHostName(hostname);
				reports.add(m);
			}
		}
		bf.close();
		if(reports.size()>0){
			MongoConnection con = connection.generateConnection();	 
			saveReports(reports, con);
			con.close();
		}	  	
	}
	
	
	private void saveReports(ArrayList<MonitorEnergyReport> reports, MongoConnection db) {
		BulkWriteOperation builder = db.energyCollection().initializeOrderedBulkOperation();		
		
		 for (MonitorEnergyReport statusReport : reports)if(statusReport!=null){			
			BasicDBObject doc = new BasicDBObject(ItemEnergyReport.HOSTNAME.title(),statusReport.getHostName())
			.append(ItemEnergyReport.TIME.title(), statusReport.getTime())
			.append(ItemEnergyReport.REGISTER_DATE.title(), statusReport.getRegisterDate().getTime())
			.append(ItemEnergyReport.RDTSC.title(), statusReport.getRDTSC())
			.append(ItemEnergyReport.ELAPSED_TIME.title(), statusReport.getElapsedTime())
			.append(ItemEnergyReport.CPU_FRECUENCY.title(), statusReport.getCPUFrequency())
			.append(ItemEnergyReport.PROCESSOR_POWER.title(), statusReport.getProcessorPower())
			.append(ItemEnergyReport.ENERGY_JOULES.title(), statusReport.getCumulativeProcessorEnergyJoules())
			.append(ItemEnergyReport.ENERGY_MHZ.title(), statusReport.getCumulativeProcessorEnergyMhz())
			.append(ItemEnergyReport.IA_POWER.title(), statusReport.getIAPower())
			.append(ItemEnergyReport.IA_ENERGY.title(), statusReport.getCumulativeIAEnergy())
			.append(ItemEnergyReport.IA.title(), statusReport.getCumulativeIA())
			.append(ItemEnergyReport.PACK_TEMP.title(), statusReport.getPackageTemperature())
			.append(ItemEnergyReport.PACK_HOT.title(), statusReport.getPackageHot())
			.append(ItemEnergyReport.PACK_POWER.title(),statusReport.getPackagePowerLimit());
			builder.insert(doc);
       }				
		System.out.println("Insert energy: "+builder.execute().getInsertedCount());
	}

	public String getPowerlogPath() {
		return powerlogPath;
	}
	
	public void setPowerlogPath(String powerlogPath) {
		this.powerlogPath = powerlogPath;
	}

	@Override
	public void sendError(Exception e) {
		toError();
	}
	
	private void cleanFile(File f) throws FileNotFoundException{
		if(f.exists()){
			PrintWriter writer = new PrintWriter(f);
			writer.print("");
			writer.close();			
		}
    }

	public void addEnergyPath(String path) {
		if(!isReady())return;
		if(path==null||path.isEmpty())toDisable();
		else setPowerlogPath(path);
	}
}
