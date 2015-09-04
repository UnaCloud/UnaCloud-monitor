package monitoring;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import monitoring.configuration.ControllerConfiguration;
import monitoring.monitors.AbstractMonitor;
import enums.MonitoringStatus;
import static monitoring.MonitoringConstants.*;

/**
 * 
 * @author CesarF
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
	private MonitoringExecuter executor;	

	/**
	 * Path to folder where the files to be picked up are stored
	 */
	private String pickUpPath;
	
	/**
	 * Path to folder were file that were picked up are temporarily stored
	 */
	private String donePath;
	
	/**
	 * 
	 * @param configurationClass: configuration interface used to configurate controller
	 */
	public MonitoringController(ControllerConfiguration configurationClass){
		cm = configurationClass;
		pickUpPath = cm.getPickUpPath();
		donePath = cm.getDonePath();
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
	 * Asks to executor if thread is running
	 * @return
	 */
	public boolean monitoringIsRunning(){
		return executor!=null&&executor.isAlive();
	}
	/**
	 * Method to enabled services using the configuration class
	 * @throws Exception
	 */
	public void configureServices(){		
		System.out.println("Config monitoring services");			
		TreeMap<String, Boolean> sensorStates = cm.getStateSensors(); 		 
		for (AbstractMonitor monitor : tools.values()){
			System.out.println(monitor.getId()+" added");
			if(sensorStates.get(monitor.getId())!=null&&sensorStates.get(monitor.getId()))monitor.configure(pickUpPath);	
		}
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
	 * Enabled all services which has its name in parameter array
	 * @param services
	 */
	public void prepareAllServices(){	
		System.out.println("Start monitoring services");		
		int time = cm.getMonitoringTime();
		for (AbstractMonitor monitor : tools.values()) 
			monitor.init(time);		
	}
	/**
	 * Method to execute services which are configured and ready to run. 
	 * To understand services status check MonitoringStatus Enum
	 */
	public void startServices(){
		if(executor==null||!executor.isAlive())executor = new MonitoringExecuter();
		for (AbstractMonitor monitor : tools.values()) {
			if(monitor.isReady())executor.addMonitor(monitor);	
		}
		if(!executor.isAlive())executor.start();	
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
				tools.get(service).configure(pickUpPath);
				cm.enableSensor(service);
			}
		}		
	}
	/**
	 * Update services referenced in array, if all services referenced can be updated, it update process control time
	 * @param frecuency: in seconds
	 * @param windowSize: in seconds
	 * @param services: name services
	 */
	public void updateServices(int frecuency, int windowSize, String[] services){
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
	 * @param service: name of service
	 * @return status of a service
	 */
	public MonitoringStatus getStatusService(String service){
		if(tools.get(service)!=null)return tools.get(service).getStatus();	
		return null;
	}
	/**
	 * @return services names in controller list
	 */
	public String[] getServicesNames() {
		return tools.keySet().toArray(new String[1]);
	}
	/**
	 * @return list of files of all services to be pickup by system
	 */
	public File[] getPickupFiles(){
		File pickups = new File(pickUpPath);
		
		FilenameFilter filtro = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(PICKUP+SEPARATOR);
			}
		};
		
		return pickups.listFiles(filtro);
	}
	/**
	 * 
	 * @param service
	 * @return list of files of a particular service to be pick up by system
	 */
	public File[] getPickupFiles(final String service){
		File pickups = new File(pickUpPath);
		
		FilenameFilter filtro = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(PICKUP+SEPARATOR+service);
			}
		};
		
		return pickups.listFiles(filtro);
	}
	/**
	 * 
	 * @param file
	 * @return move a file to done folder.
	 */
	public boolean sendFileToDone(final File file){
	    try {
	    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss-SSS");
	    	String newName = file.getName().substring(file.getName().indexOf(SEPARATOR), file.getName().length()).replace(EXT, "");
	    	file.renameTo(new File(donePath+DONE+newName+SEPARATOR+df.format(new Date())+EXT));		
	    	return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public File getPickPath() {
		return new File(pickUpPath);
	}
}
