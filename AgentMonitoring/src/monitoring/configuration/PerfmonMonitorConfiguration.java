package monitoring.configuration;
/**
 * 
 * @author CesarF
 * 
 * Class used as an interface to configure a Perfmon sensor
 *
 */
public abstract class PerfmonMonitorConfiguration implements InterfaceSensorConfiguration{

	public abstract String getCounterName();

	public abstract String[] getCounters();

	public abstract int getMaxFileSize();

}
