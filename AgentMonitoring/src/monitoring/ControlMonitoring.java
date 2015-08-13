package monitoring;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import connection.MonitorDatabaseConnection;
import utils.LoaderDll;
import enums.MonitoringStatus;
import monitoring.monitors.AbstractMonitor;
import monitoring.monitors.MonitorCPUAgent;
import monitoring.monitors.MonitorEnergyAgent;

/**
 * 
 * @author Cesar
 *
 * This class allows to control Energy Monitoring and Performance Monitoring. 
 * first of all set values and connection variable 
 */

public abstract class ControlMonitoring {	

	private MonitorConfiguration values;
	private MonitorDatabaseConnection connection;	

	private MonitorCPUAgent mc;
	private MonitorEnergyAgent me;
	private Controller c;	
	
	public void setConfiguration(MonitorConfiguration configurationClass, MonitorDatabaseConnection con){
		values = configurationClass;
		connection = con;
	}
	public void initService() throws Exception{		
		System.out.println("Config monitoring service");
		try {
			mc = new MonitorCPUAgent(connection);		
			if(values.isMonitoringCpuEnable())mc.toEnable(values.getLogCpuPath());
		} catch (Exception e) {
			System.out.println(e.getMessage());			
			values.disableCpuMonitoring();
		}
		try {			
			me  = new MonitorEnergyAgent(connection);
			if(values.isMonitoringEnergyEnable())me.toEnable(values.getLogEnergyPath());			
		} catch (Exception e) {
			System.out.println(e.getMessage());		
			values.disableEnergyMonitoring();
		}
		startService(true, true);
	}	
	public void startService(boolean energy, boolean cpu){
		try {
			System.out.println("Start monitoring service");
			int time  = (int)(Math.random()*60*60);	
			if(cpu)
				if(values.isMonitoringCpuEnable()){
					new LoaderDll(values.getDataPath()).loadLibrary();
					mc.offToInit(values.getCpuMonitorFrecuency(),
							values.getCpuMonitorRegisterFrecuency(), time);
					
				}else System.out.println("Monitoring CPU is disable");
		    if(energy)
		    	if(values.isMonitoringEnergyEnable()){
		    		me.offToInit(values.getEnergyMonitorFrecuency(),
		    				values.getEnergyMonitorRegisterFrecuency(), time);
		    		me.addEnergyPath(values.getPowerAppPath());					
		    	}else System.out.println("Monitoring energy is disable");
		    if(c==null||!c.isAlive()){
		    	c = new Controller();
		    	c.addMonitor(mc);
		    	c.addMonitor(me);
		    	c.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			mc.toError();
			me.toError();
		}
	}

	public void stopService(boolean energy, boolean cpu){
		System.out.println("Stop monitoring service");
		if(energy)me.toStop();		
		if(cpu)mc.toStop();				
	}
	
	public void enabledService(boolean energy, boolean cpu) {	
		System.out.println("Enable monitoring service");
		if(cpu){			
			try {
				mc.toEnable(values.getLogCpuPath());
				values.enableCpuMonitoring();
			} catch (Exception e) {
				System.out.println("ERROR: "+e.getMessage());
			}			
		}
		if(energy){
			try {	
				me.toEnable(values.getLogEnergyPath());
				values.enableEnergyMonitoring();
			} catch (Exception e) {
				System.out.println("ERROR: "+e.getMessage());
			}			
		}		
	}
	
	public void disableService(boolean energy, boolean cpu){
//		if(cpu)if(mc.getStatus()==MonitoringStatus.OFF){
//			VariableManager.local.setBooleanValue("MONITORING_ENABLE_CPU", false);
//			mc.setStatus(MonitoringStatus.DISABLE);
//		}if(energy)if(me.getStatus()==MonitoringStatus.OFF){
//			VariableManager.local.setBooleanValue("MONITORING_ENABLE_ENERGY", false);
//			me.setStatus(MonitoringStatus.DISABLE);
//		}	
	}
	
	public void updateService(int frE, int frC, int wsCpu, int wsEnergy, boolean energy, boolean cpu){
		System.out.println("Update monitoring service");
		int time  = (int)(Math.random()*60*60);	
		if(energy){
			values.setEnergyMonitorFrecuency(frE);
			values.setEnergyMonitorRegisterFrecuency(wsEnergy);			
			String logEnergy = values.getLogEnergyPath();
			String path = values.getPowerAppPath();
			me.updateVariables(frE, wsEnergy, time);				
			me.setRecordPath(logEnergy);				
			me.setPowerlogPath(path);
		}
		if(cpu){
			values.setCpuMonitorFrecuency(frC);
			values.setCpuMonitorRegisterFrecuency(wsCpu);
			String logCpu = values.getPowerAppPath();
			mc.updateVariables(frC, wsCpu, time);				
			mc.setRecordPath(logCpu);
		}	
	}
	public MonitoringStatus getStatusEnergy(){
		return me==null?MonitoringStatus.DISABLE:me.getStatus();
	}
	public MonitoringStatus getStatusCpu(){
		return mc==null?MonitoringStatus.DISABLE:mc.getStatus();
	}
	
	private class Controller extends Thread{

		ArrayList<AbstractMonitor> monitors = new ArrayList<AbstractMonitor>();
		@Override
		public void run() {	
			for (AbstractMonitor monitor : monitors) {
				try {
					monitor.doInitial();
				} catch (Exception e) {
					e.printStackTrace();
					monitor.toError();
				}
			}
			while(isReady()){
				try { 					
					System.out.println(new Date()+" Init monitor processes");
					ArrayList<Thread> processes = new ArrayList<Thread>();
					for (AbstractMonitor monitor : monitors)
						if(monitor.isReady())processes.add(new Thread(monitor));									
				    for (Thread thread : processes) thread.start();
					for (Thread thread : processes) thread.join();
					System.out.println(new Date()+" Finish monitor processes");
				} catch (Exception e) {
					e.printStackTrace();
					for (AbstractMonitor monitor : monitors)
						monitor.toError();					
				}				
			}	
			for (AbstractMonitor monitor : monitors)
				if(monitor.isStopped())monitor.toOff();
		}
		public void addMonitor(AbstractMonitor monitor){
			monitors.add(monitor);
		}
		public boolean isReady(){
			for (AbstractMonitor abstractMonitor : monitors) {
				if(abstractMonitor.isReady())return true;
			}
			return false;
		}
	}
	public long getSpaceDirVMS(){
        try {
			long space = new File(values.getDataPath()).getFreeSpace();
			return space;
		} catch (Exception e) {
			return -1;
		}
    }
}
