package monitoring;

import java.util.TreeMap;

import monitoring.configuration.ControllerConfiguration;
import monitoring.monitors.AbstractMonitor;
import enums.MonitoringStatus;

/**
 * 
 * @author Cesar
 *
 * This class allows to control a list of monitoring services. 
 * first of all set values and connection variable 
 */

public class MonitoringController {	

	/**
	 * Group of monitoring sensors 
	 */
	private TreeMap<String, AbstractMonitor> tools = new TreeMap<String, AbstractMonitor>();
	
	/**
	 * Configuration class
	 */
	private ControllerConfiguration cm;

	/**
	 * Execute in a cycle all services enabled
	 */
	private MonitoringExecuter c;	
	
	public MonitoringController(ControllerConfiguration configurationClass){
		cm = configurationClass;
	}
	/**
	 * Adding monitor to controller
	 * @param monitor
	 */
	public void addMonitoringTool(AbstractMonitor monitor){
		if(monitor==null)return;
		tools.put(monitor.getId(), monitor);
	}
	/**
	 * Method to enabled services using the configuration class
	 * @throws Exception
	 */
	public void configureServices(){		
		System.out.println("Config monitoring services");			
		TreeMap<String, Boolean> sensorStates = cm.getStateSensors(); 		 
		for (AbstractMonitor monitor : tools.values())
			if(sensorStates.get(monitor.getId())!=null&&sensorStates.get(monitor.getId()))monitor.configure(null);	
	}	
	/**
	 * Enabled services which has its name in parameter array
	 * @param services
	 */
	public void prepareServices(String... services){	
		System.out.println("Start monitoring services");		
		int time = cm.getMonitoringTime();
		for (String string : services) 
			if(tools.get(string)!=null)tools.get(string).init(time);		
	}
	/**
	 * Method to execute services which are configured and ready to run. 
	 * To understand services status check MonitoringStatus Enum
	 */
	public void startServices(){
		if(c==null)c = new MonitoringExecuter();
		for (AbstractMonitor monitor : tools.values())
			if(monitor.isReady())c.addMonitor(monitor);					
		if(!c.isAlive())c.start();	
	}
	/**
	 * Stop services referenced in array
	 * @param services
	 */
	public void stopServices(String... services){
		System.out.println("Stop monitoring service");
		for (String string : services) 
			if(tools.get(string)!=null)tools.get(string).toStop();			
	}
	/**
	 * enable services referenced in array
	 * @param services
	 */
	public void enabledServices(String... services) {	
		System.out.println("Enable monitoring service");
		for (String service : services) {
			if(tools.get(service)!=null){
				tools.get(service).configure(null);
				cm.enableSensor(service);
			}
		}		
	}
	/**
	 * Update services referenced in array, if all services referenced can be updated, it update process control time
	 * @param frecuency
	 * @param windowSize
	 * @param services
	 */
	public void updateServices(int frecuency, int windowSize, String... services){
		if(windowSize<=0||frecuency<=0)return;
		System.out.println("Update monitoring service");		
		for (String service : services) {
			if(tools.get(service)!=null){
				if(!tools.get(service).updateSensor(frecuency, windowSize))return;				
			}
		}	
		//TODO: cancel previous changes in case of some service can't be updated
		cm.setMonitoringTime(windowSize);		
	}
	/**
	 * Return the status of a service
	 * @param service: name of service
	 * @return
	 */
	public MonitoringStatus getStatusService(String service){
		if(tools.get(service)!=null)return tools.get(service).getStatus();	
		return null;
	}
	
	public String[] getServicesNames() {
		return tools.keySet().toArray(new String[1]);
	}
}
