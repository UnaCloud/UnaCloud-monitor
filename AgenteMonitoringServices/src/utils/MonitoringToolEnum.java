package utils;

public enum MonitoringToolEnum {
	
	SIGAR("sigar"), OPEN_HARDWARE("openHardware"), PERFMON("perfmon"), POWER_GADGET("powerGadget");
	
	String name;
	
	private MonitoringToolEnum(String name) {
		this.name=name;
	}
	
	public static MonitoringToolEnum getTool(String name){
		if(name==SIGAR.name)return SIGAR;
		if(name==OPEN_HARDWARE.name)return OPEN_HARDWARE;
		if(name==PERFMON.name)return PERFMON;
		if(name==POWER_GADGET.name)return POWER_GADGET;
		return null;
	}
	
	public String getName(){
		return name;
	}
	
}
