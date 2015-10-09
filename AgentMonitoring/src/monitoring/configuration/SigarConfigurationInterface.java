package monitoring.configuration;
/**
 * 
 * @author CesarF
 * 
 * Class used as an interface to configure a Sigar sensor
 *
 */
public abstract class SigarConfigurationInterface implements InterfaceSensorConfiguration{	
	
	public abstract String getDllPath();
	
	public abstract String[] getHeaders();

}
