package reports;

import java.util.Date;

import enums.ItemEnergyReport;

public class MonitorEnergyReport {
	
	
	private String time,RDTSC,elapsedTime,CPUFrequency,processorPower,
	cumulativeProcessorEnergyJoules,cumulativeProcessorEnergyMhz,
	IAPower,cumulativeIAEnergy,cumulativeIA,packageTemperature,
	packageHot,packagePowerLimit, hostName;	
	private Date registerDate;
	
	public MonitorEnergyReport(){
		
	}
	
	public MonitorEnergyReport(String line){
		String[] data = line.split(",");
		time = data[0].trim();
		RDTSC = data[1].trim();
		elapsedTime = data[2].trim();
		CPUFrequency = data[3].trim();
		processorPower = data[4].trim();
		cumulativeProcessorEnergyJoules = data[5].trim();
		cumulativeProcessorEnergyMhz = data[6].trim();
		IAPower = data[7].trim();
		cumulativeIAEnergy = data[8].trim();
		cumulativeIA = data[9].trim();
		packageTemperature = data[10].trim();
		packageHot = data[11].trim();
		packagePowerLimit = data[12].trim();
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String ti) {
		time = ti;
	}
	public String getRDTSC() {
		return RDTSC;
	}
	public void setRDTSC(String rDTSC) {
		RDTSC = rDTSC;
	}
	public String getElapsedTime() {
		return elapsedTime;
	}
	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	public String getCPUFrequency() {
		return CPUFrequency;
	}
	public void setCPUFrequency(String cPUFrequency) {
		CPUFrequency = cPUFrequency;
	}
	public String getProcessorPower() {
		return processorPower;
	}
	public void setProcessorPower(String processorPower) {
		this.processorPower = processorPower;
	}
	public String getCumulativeProcessorEnergyJoules() {
		return cumulativeProcessorEnergyJoules;
	}
	public void setCumulativeProcessorEnergyJoules(
			String cumulativeProcessorEnergyJoules) {
		this.cumulativeProcessorEnergyJoules = cumulativeProcessorEnergyJoules;
	}
	public String getCumulativeProcessorEnergyMhz() {
		return cumulativeProcessorEnergyMhz;
	}
	public void setCumulativeProcessorEnergyMhz(String cumulativeProcessorEnergyMhz) {
		this.cumulativeProcessorEnergyMhz = cumulativeProcessorEnergyMhz;
	}
	public String getIAPower() {
		return IAPower;
	}
	public void setIAPower(String iAPower) {
		IAPower = iAPower;
	}
	public String getCumulativeIAEnergy() {
		return cumulativeIAEnergy;
	}
	public void setCumulativeIAEnergy(String cumulativeIAEnergy) {
		this.cumulativeIAEnergy = cumulativeIAEnergy;
	}
	public String getCumulativeIA() {
		return cumulativeIA;
	}
	public void setCumulativeIA(String cumulativeIA) {
		this.cumulativeIA = cumulativeIA;
	}
	public String getPackageTemperature() {
		return packageTemperature;
	}
	public void setPackageTemperature(String packageTemperature) {
		this.packageTemperature = packageTemperature;
	}
	public String getPackageHot() {
		return packageHot;
	}
	public void setPackageHot(String packageHot) {
		this.packageHot = packageHot;
	}
	public String getPackagePowerLimit() {
		return packagePowerLimit;
	}
	public void setPackagePowerLimit(String packagePowerLimit) {
		this.packagePowerLimit = packagePowerLimit;
	}
	public Date getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	@Override
	public String toString() {
		return "MonitorEnergyReport [time=" + time + ", RDTSC=" + RDTSC
				+ ", elapsedTime=" + elapsedTime + ", CPUFrequency="
				+ CPUFrequency + ", processorPower=" + processorPower
				+ ", cumulativeProcessorEnergyJoules="
				+ cumulativeProcessorEnergyJoules
				+ ", cumulativeProcessorEnergyMhz="
				+ cumulativeProcessorEnergyMhz + ", IAPower=" + IAPower
				+ ", cumulativeIAEnergy=" + cumulativeIAEnergy
				+ ", cumulativeIA=" + cumulativeIA + ", packageTemperature="
				+ packageTemperature + ", packageHot=" + packageHot
				+ ", packagePowerLimit=" + packagePowerLimit + ", hostName="
				+ hostName + ", registerDate=" + registerDate + "]";
	}

	public static String getHead() {
		return ItemEnergyReport.TIME.title() + "," + ItemEnergyReport.RDTSC.title()+ "," 
				+ ItemEnergyReport.ELAPSED_TIME.title() + ","+ ItemEnergyReport.CPU_FRECUENCY.title()+ "," 
				+ ItemEnergyReport.PROCESSOR_POWER.title()+ ","+ ItemEnergyReport.ENERGY_JOULES.title()+","
				+ ItemEnergyReport.ENERGY_MHZ.title()+ "," + ItemEnergyReport.IA_POWER.title()+ "," 
				+ ItemEnergyReport.IA_ENERGY.title()+ "," + ItemEnergyReport.IA.title()+ ","
				+ ItemEnergyReport.PACK_TEMP.title() + "," + ItemEnergyReport.PACK_HOT.title()	+ "," 
				+ ItemEnergyReport.PACK_POWER.title() + ","+ ItemEnergyReport.HOSTNAME.title()+ "," 
				+ ItemEnergyReport.REGISTER_DATE.title() ;
	}
	public String getLine() {
		return   time + "," + RDTSC	+ "," 
				+ elapsedTime + ","	+ CPUFrequency + "," 
				+ processorPower+ ","+ cumulativeProcessorEnergyJoules+ ","
				+ cumulativeProcessorEnergyMhz + "," + IAPower+ "," 
				+ cumulativeIAEnergy+ "," + cumulativeIA + ","
				+ packageTemperature + "," + packageHot	+ "," 
				+ packagePowerLimit + ","+ hostName + "," + 
				registerDate ;
	}
	
	
}
