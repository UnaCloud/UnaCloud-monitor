package monitoring.configuration;

/**
 * 
 * @author CesarF
 * 
 * Class used as an interface to configure an OpenHardwareMonitor sensor
 *
 */
public abstract class OpenHardwareConfigurationInterface implements InterfaceSensorConfiguration{

	public abstract String getOpenHwProcess();
	
}
