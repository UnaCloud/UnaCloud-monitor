package monitoring.configuration;
/**
 * 
 * @author CesarF
 * 
 * Class used as an interface to configure a PowerGadget sensor
 *
 */
public abstract class PowerGadgetConfigurationInterface implements InterfaceSensorConfiguration{
	
	public abstract String getPowerPath() ;

	public abstract String getExeName();
}
