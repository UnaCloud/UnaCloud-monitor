package monitoring.configuration;

public abstract class PerfmonMonitorConfiguration implements InterfaceSensorConfiguration{

	public abstract String getCounterName();

	public abstract String[] getCounters();

	public abstract int getMaxFileSize();

}
