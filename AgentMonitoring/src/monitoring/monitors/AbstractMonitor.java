package monitoring.monitors;

import monitoring.configuration.InterfaceSensorConfiguration;
import enums.MonitoringStatus;

/** 
 * @author Cesar
 * This class is the template for monitoring classes. There are three main task for all monitoring process; initial, monitoring, final
 * @param frecuency check frequency in seconds
 * @param windowSizeTime window check time size in milliseconds
 */
public abstract class AbstractMonitor implements Runnable{

	/**
	 * check frequency in seconds
	 */
	protected int frecuency;
	/**
	 * window check time size in seconds
	 */
	protected int windowSizeTime;
	
	/**
	 * file path to record data
	 */
	protected String recordPath;
	
	/**
	 * Status of monitoring process; Check MonitoringStatus Enum to more info
	 */
	private MonitoringStatus status;
	
	/**
	 * ID of sensor
	 */
	private String ID;
	
	/**
	 * Class to control variables to configure sensor
	 * @throws Exception
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
	public void toStop(){
		if(status==MonitoringStatus.RUNNING)status = MonitoringStatus.STOPPED;
		else if(status==MonitoringStatus.ERROR)status = MonitoringStatus.OFF;
	}
	private void toEnable(String record){
		if(!isDisable()){System.out.println(ID+" service is disable");return;}
		if(record==null||record.isEmpty()){
			System.err.println("There is not a record path configured");
			this.status = MonitoringStatus.DISABLE;
			return;
		}
		recordPath=record;
		this.status = MonitoringStatus.OFF;
	}
	private void offToInit(int frecuency, int window){		
		if(!isOff())return;
		if(updateVariables(frecuency, window))		
		    status = MonitoringStatus.INIT;		
	}
	protected void toDisable(){
		if(isOff())status = MonitoringStatus.DISABLE;
	}
	public void toOff(){
		if(isStopped())status = MonitoringStatus.OFF;
	}
	public void toError() {
		if(isDisable())return;
		this.status = MonitoringStatus.ERROR;
	}
	protected boolean updateVariables(int frecuency, int window){
		if(frecuency>0&&window>0&&frecuency<window){	
			this.frecuency = frecuency; 
		    this.windowSizeTime = window;
		    return true;		
		}	
		System.out.println("Frecuency is greater than window");
		return false;
	}
	public boolean updateSensor(int frecuency, int window){
		if(updateVariables(frecuency, window)){
			configuration.setFrecuency(frecuency);
			return true;
		}
		return false;
	}
	public void setRecordPath(String recordPath) {
		this.recordPath = recordPath;
	}	
	/**
     * Do initial task to control monitoring
     * @throws Exception 
     */
	public abstract void doInitial() throws Exception;
	/**
	 * Starts monitoring process
	**/
	public abstract void doMonitoring() throws Exception;
	/**
	 * Record data in Database if it is necessary
	 * @throws Exception 
	 */
	public abstract void doFinal() throws Exception;
	/**
	 * Unified send error method to communicate when process has failed	 * 
	 * Override this method if it is necessary for your sensor
	 */
	public void sendError(Exception e){
		toError();
	}
	/**
	 * check frequency in seconds
	 */
	public int getFrecuency() {
		return frecuency;
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
	public boolean isRunning(){
		return status==MonitoringStatus.RUNNING;
	}
	public boolean isStopped(){
		return status==MonitoringStatus.STOPPED;
	}
	public boolean isError(){
		return status==MonitoringStatus.ERROR;
	}
	public boolean isDisable(){
		return status==MonitoringStatus.DISABLE;
	}
	public boolean isReady(){
		return status==MonitoringStatus.INIT;
	}
	public boolean isOff(){
		return status==MonitoringStatus.OFF;
	}	
	public MonitoringStatus getStatus(){
		return status;
	}	
	public String getId(){
		return ID;
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
	public void configure() {
		toEnable(configuration.getRecordPath());
	}
	
}
