package monitoring.configuration;
/**
 * 
 * @author CesarF
 * 
 * Class used as an interface to configure any sensor
 *
 */
public interface InterfaceSensorConfiguration{
	public String getRecordPath();
	public int getFrecuency() ;
	public void setFrecuency(int frecuency);
}
