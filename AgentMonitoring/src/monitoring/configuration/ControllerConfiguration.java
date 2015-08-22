package monitoring.configuration;

import java.util.TreeMap;

/**
 * 
 * @author Cesar
 * 
 * this class represents parameters that all classes in monitoring need to get data with sigar library,
 * configurate monitoring and record data.
 *
 */
public abstract class ControllerConfiguration{
	/**
	 * TODO: Documentation
	 * @return
	 */
	public abstract TreeMap<String, Boolean> getStateSensors();
	public abstract int getMonitoringTime();
	public abstract void setMonitoringTime(int window);
	public abstract void enableSensor(String service);
	public abstract String getPickUpPath();
	public abstract String getDonePath();
}
