package monitoring.exceptions;

public class MonitoringException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8314764322052944962L;
	
	@Override
	public String getMessage() {
		return "MonitorConfiguration class has not been modified in ControlMonitoring singleton class";
	}

}
