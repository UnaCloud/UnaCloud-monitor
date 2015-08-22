package monitoring.configuration;

import java.util.TreeMap;

/**
 * 
 * @author CesarF
 * 
 * Class used as an interface to configure the sensor controller
 *
 */
public abstract class ControllerConfiguration{
	/** 
	 * @return map with list of services and boolean to represent if it is enabled o disabled
	 */
	public abstract TreeMap<String, Boolean> getStateSensors();
	/** 
	 * @return time that monitoring must run: number in seconds 
	 */
	public abstract int getMonitoringTime();
	/**
	 * 
	 * @param window: time to be modified in configuration
	 */
	public abstract void setMonitoringTime(int window);
	/** 
	 * @param service: service name to be enabled
	 */
	public abstract void enableSensor(String service);
	/** 
	 * @return pickUp directory path for logs
	 */
	public abstract String getPickUpPath();
	/**
	 * 
	 * @return done directory path
	 */
	public abstract String getDonePath();
}
