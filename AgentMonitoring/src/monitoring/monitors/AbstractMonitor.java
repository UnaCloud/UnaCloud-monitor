package monitoring.monitors;

import static monitoring.MonitoringConstants.EXT;
import static monitoring.MonitoringConstants.PICKUP;
import static monitoring.MonitoringConstants.SEPARATOR;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import monitoring.configuration.InterfaceSensorConfiguration;
import enums.MonitoringStatus;

/** 
 * @author CesarF
 * This class is the template for monitoring classes. There are three main task for all monitoring process; initial, monitoring, final

 */
public abstract class AbstractMonitor implements Runnable{

	/**
	 * record frequency in seconds
	 */
	protected int frequency;
	/**
	 * window check time size in seconds
	 */
	protected int windowSizeTime;
	
	/**
	 * file path to record data
	 */
	protected String recordPath;
	/**
	 * file path to save pickup files
	 */
	protected String pickUpPath;
	/**
	 * Status of monitoring process; Check MonitoringStatus Enum to more info
	 */
	private MonitoringStatus status;
	
	/**
	 * ID of sensor
	 */
	protected String ID;	 
	
	/**
	 * Date format to manage files
	 */
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss-SSS");
	
	/**
	 * Class to control variables to configure sensor
	 */
	protected InterfaceSensorConfiguration configuration;		
	
	protected AbstractMonitor(String id, InterfaceSensorConfiguration conf) throws Exception {	
		ID = id;
		status = MonitoringStatus.DISABLE;
		this.configuration=conf;
	}
	
	@Override
	public void run() {
		if(status==MonitoringStatus.INIT)
		try {
			if(recordPath!=null){
				status = MonitoringStatus.RUNNING;
				System.out.println("Let's work for "+windowSizeTime);
				doMonitoring();
				doFinal();
				if(status==MonitoringStatus.RUNNING)status=MonitoringStatus.INIT;
			}		
		} catch (Exception e) {
			e.printStackTrace();
			sendError(e);
		}		
	}
	/**
	 * It validates if sensor is ready and call doInitial
	 * @throws Exception in case of error in doInitial method
	 */
	public void doInit() throws Exception{
		if(isReady())doInitial();
	}
	
	/**
     * Do initial task to control monitoring
     * @throws Exception 
     */
	protected abstract void doInitial() throws Exception;
	
	/**
	 * Starts monitoring process
	**/
	protected abstract void doMonitoring() throws Exception;
	
	/**
	 * Record data in Database if it is necessary
	 * @throws Exception 
	 */
	protected abstract void doFinal() throws Exception;	
	
	/**
	 * Move all sensor files to pickUp folder. 
	 * All files that start with name parameter are renamed with word PICKUP_ at beginning and move them to pickup folder
	 */
	protected void setLogFileForPickUp(final String startFileName){
		File folder = new File(recordPath);		
		FilenameFilter filtro = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(startFileName);
			}
		};
		Date d = new Date();
		for (File file : folder.listFiles(filtro)) {
			String fileName = file.getName().replace(EXT, "");
			file.renameTo(new File(pickUpPath+PICKUP+SEPARATOR+fileName+SEPARATOR+df.format(d)+EXT));
		}
	}
	
	/**
	 * Method used to configure your sensor, throws exception to disable sensor
	 */
	protected abstract void doConfiguration() throws Exception;
	/**
	 * Unified send error method to communicate when process has failed	 * 
	 * Override this method if it is necessary for your sensor
	 */
	public void sendError(Exception e){
		toError();
	}

	/**
	 * Method used to configure sensor running time
	 * Override this method if it is necessary for your sensor
	 * @throws Exception
	 */
	public void init(int time) {
		offToInit(configuration.getFrecuency(), time);
	}
	/**
	 * Method used to auto-configure monitoring tool
	 * Override this method if it is necessary for your sensor
	 * @throws Exception
	 */
	public void configure(String pickupPath) {
		try {
			doConfiguration();
			toEnable(configuration.getRecordPath(),pickupPath);
		} catch (Exception e) {
			System.out.println("Error in "+ID+" configuration");
			System.out.println(e.getMessage());
		}		
	}	
	/**
	 * To change sensor status: RUNNING to STOP, ERROR to OFF. 
	 * In case sensor has another status it does not change it
	 */
	public void toStop(){
		if(status==MonitoringStatus.RUNNING)status = MonitoringStatus.STOPPED;
		else if(status==MonitoringStatus.ERROR)status = MonitoringStatus.OFF;
	}
	/**
	 * To change sensor status from DISABLE to OFF
	 * the pickup folder path and record folder path can't be neither empty or null
	 * @param record
	 * @param pickUp
	 * @throws Exception
	 */
	private void toEnable(String record, String pickUp) throws Exception{
		if(!isDisable()){System.out.println(ID+" service is disable");return;}
		if(record==null||record.isEmpty()||pickUp==null||pickUp.isEmpty()){
			this.status = MonitoringStatus.DISABLE;
			throw new Exception("There is not a record path or a pickup path configured");
		}
		recordPath=record;
		pickUpPath = pickUp;
		this.status = MonitoringStatus.OFF;
	}
	/**
	 * To change sensor status from OFF to INIT
	 * Window time size must be greater than frequency
	 * @param frecuency
	 * @param window
	 */
	private void offToInit(int frequency, int window){		
		if(!isOff())return;
		if(updateVariables(frequency, window))		
		    status = MonitoringStatus.INIT;		
	}
	/**
	 * To change sensor status from OFF to DISABLE 
	 */
	protected void toDisable(){
		if(isOff())status = MonitoringStatus.DISABLE;
	}
	/**
	 * To change sensor status from STOP to OFF
	 */
	public void toOff(){
		if(isStopped())status = MonitoringStatus.OFF;
	}
	/**
	 * To change sensor status from any status (except DISABLE) to ERROR.
	 */
	public void toError() {
		if(isDisable())return;
		this.status = MonitoringStatus.ERROR;
	}
	/**
	 * To update frequency and window time size
	 * Frequency does not must be greater than window time size
	 * @param frequency: in seconds
	 * @param window: in seconds
	 * @return true in case variables are valid
	 */
	protected boolean updateVariables(int frequency, int window){
		if(frequency>0&&window>0&&frequency<window){	
			this.frequency = frequency; 
		    this.windowSizeTime = window;
		    return true;		
		}	
		System.out.println("Frecuency is greater than window");
		return false;
	}
	/**
	 * Public access to modify frequency and window time size
	 * @param frequency
	 * @param window
	 * @return
	 */
	public boolean updateSensor(int frequency, int window){
		if(updateVariables(frequency, window)){
			configuration.setFrecuency(frequency);
			return true;
		}
		return false;
	}
		
	/**
	 * check frequency in seconds
	 */
	public int getFrequency() {
		return frequency;
	}
	/** 
	 * @return size time in seconds
	 */
	public int getWindowSizeTime() {
		return windowSizeTime;
	}
	/**
	 * @return path to record logs
	 */
	public String getRecordPath() {
		return recordPath;
	}
	/**
	 * Modify the record path
	 * @param recordPath
	 */
	public void setRecordPath(String recordPath) {
		this.recordPath = recordPath;
	}	
	/**
	 * 
	 * @return true sensor status is RUNNING
	 */
	public boolean isRunning(){
		return status==MonitoringStatus.RUNNING;
	}
	/**
	 * 
	 * @return true sensor status is STOPPED
	 */
	public boolean isStopped(){
		return status==MonitoringStatus.STOPPED;
	}
	/**
	 * 
	 * @return true sensor status is ERROR
	 */
	public boolean isError(){
		return status==MonitoringStatus.ERROR;
	}
	/**
	 * 
	 * @return true sensor status is DISABLE
	 */
	public boolean isDisable(){
		return status==MonitoringStatus.DISABLE;
	}
	/**
	 * 
	 * @return true sensor status is INIT
	 */
	public boolean isReady(){
		return status==MonitoringStatus.INIT;
	}
	/**
	 * 
	 * @return true sensor status is OFF
	 */
	public boolean isOff(){
		return status==MonitoringStatus.OFF;
	}	
	/**
	 * 
	 * @return sensor status
	 */
	public MonitoringStatus getStatus(){
		return status;
	}	
	/**
	 * 
	 * @return sensor ID
	 */
	public String getId(){
		return ID;
	}
}
