package monitoring;

/**
 * 
 * @author Cesar
 * 
 * this class represents parameters that all classes in monitoring need to get data with sigar library,
 * configurate monitoring and record data.
 *
 */
public abstract class MonitorConfiguration {
	
	public abstract boolean isMonitoringCpuEnable();
	public abstract boolean isMonitoringEnergyEnable();	
	public abstract String getLogCpuPath();
	public abstract String getLogEnergyPath();
	public abstract void disableCpuMonitoring();
	public abstract void disableEnergyMonitoring();
	public abstract int getCpuMonitorFrecuency();
	public abstract void setCpuMonitorFrecuency(int mF);
	public abstract int getEnergyMonitorFrecuency();
	public abstract void setEnergyMonitorFrecuency(int mE);
	public abstract int getCpuMonitorRegisterFrecuency();
	public abstract void setCpuMonitorRegisterFrecuency(int rC);
	public abstract int getEnergyMonitorRegisterFrecuency();
	public abstract void setEnergyMonitorRegisterFrecuency(int rE);
	public abstract String getPowerAppPath();
	public abstract void enableCpuMonitoring();
	public abstract void enableEnergyMonitoring();
	public abstract String getDataPath();
}
