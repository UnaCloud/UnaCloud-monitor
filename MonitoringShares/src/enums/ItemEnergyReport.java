package enums;

public enum ItemEnergyReport {
	HOSTNAME("Hostname"),
	TIME("Timestamp"),
	REGISTER_DATE("RegisterDate"),
	RDTSC("RDTSC"),
	ELAPSED_TIME("elapsedTime"),
	CPU_FRECUENCY("CPUFrequency"),
	PROCESSOR_POWER("processorPower"),
	ENERGY_JOULES("cumulativeProcessorEnergyJoules"),
	ENERGY_MHZ("cumulativeProcessorEnergyMhz"),
	IA_POWER("IAPower"),
	IA_ENERGY("cumulativeIAEnergy"),
	IA("cumulativeIA"),
	PACK_TEMP("packageTemperature"),
	PACK_HOT("packageHot"),
	PACK_POWER("packagePowerLimit");
	
	private String title;
	
	private ItemEnergyReport(String t) {
		title = t;
	}
	
	public String title(){return title;}
}
