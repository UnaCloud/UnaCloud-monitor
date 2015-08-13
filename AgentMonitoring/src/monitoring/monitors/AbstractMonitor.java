package monitoring.monitors;

import connection.MonitorDatabaseConnection;
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
	 * Time to reduces the first execution
	 */
	protected long reduce;
	/**
	 * Status of monitoring process; Check MonitoringStatus Enum to more info
	 */
	private MonitoringStatus status;
	/**
	 * Connection to DB from Agent
	 */
	protected MonitorDatabaseConnection connection;
	
	protected AbstractMonitor(MonitorDatabaseConnection con) throws Exception {		
		status = MonitoringStatus.DISABLE;
		connection = con;
	}
	
	@Override
	public void run() {
		if(status==MonitoringStatus.INIT)
		try {
			if(recordPath!=null){
				status = MonitoringStatus.RUNNING;
				System.out.println("Lets work for "+windowSizeTime);
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
	public void toEnable(String record)throws Exception{
		if(!isDisable())return;
		if(record==null)throw new Exception("record path is missing");
		recordPath=record;
		this.status = MonitoringStatus.OFF;
	}
	public void offToInit(int frecuency, int window, int time){
		if(!isOff())return;
		if(updateVariables(frecuency, window, time))		
		    status = MonitoringStatus.INIT;		
	}
	public void toDisable(){
		if(isOff())status = MonitoringStatus.DISABLE;
	}
	public void toOff(){
		if(isStopped())status = MonitoringStatus.OFF;
	}
	public void toError() {
		this.status = MonitoringStatus.ERROR;
	}
	public boolean updateVariables(int frecuency, int window, int time){
		if(frecuency>0&&window>0&&frecuency<window){	
			this.frecuency = frecuency; 
		    this.windowSizeTime = window;
			this.reduce = time;
		    return true;		
		}	
		System.out.println("Frecuency is greater than window");
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
	 * Unified send error method to communicate when process has failed
	 */
	public abstract void sendError(Exception e);
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
	public long getReduce() {
		return reduce;
	}
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
}
