package reports;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import enums.ItemCPUReport;


public class MonitorReport{

	Timestamp timest;
	long timeLong;
	String userName;
	double cpuUptime;
	//cpuIdle,noCpuIdle,cpuUser,cpuSys,cpuNice,cpuWait,cpuCombined;
//	long totalCpuUserTime,totalCpuSysTime,totalCpuNiceTime,totalCpuWaitTime,totalCpuIdleTime;
	double ramMemoryFree,ramMemoryUsed;
	double memFreePercent,memUsedPercent;
	double swapMemoryFree,swapMemoryPageIn,swapMemoryPageOut,swapMemoryUsed;
	long hardDiskFreeSpace;
	long hardDiskUsedSpace;
	long netRxBytes;
	long netTxBytes;
	long netSpeed;
	long netRxErrors;
	long netTxErrors;
	long netRxPackets;
	long netTxPackets;
   // String processes;
    
    public MonitorReport() {
	}
    
	public MonitorReport(Timestamp timest, long timeLong, String userName, double uptime,/* double idle, double d, double cPuser, double sys, double nice, double wait, double combined, long user, long sys0, long nice0, long wait0, long idle0,*/double rAMMemoryFree, double rAMMemoryUsed, double freePercent, double usedPercent, double swapMemoryFree, double swapMemoryPageIn, double swapMemoryPageOut, double swapMemoryUsed, long hardDiskFreeSpace, long hardDiskUsedSpace, long rxBytes, long txBytes, long speed, long rxErrors, long txErrors, long rxPackets, long txPackets/*,String processes*/){
		
		this.timest = timest;
		this.timeLong = timeLong;
		this.userName = userName;
		this.cpuUptime = uptime;
//		this.cpuIdle = idle;
//		this.noCpuIdle = d;
//		cpuUser = cPuser;
//		this.cpuSys = sys;
//		this.cpuNice = nice;
//		this.cpuWait = wait;
//		this.cpuCombined = combined;
//		this.totalCpuUserTime = user;
//		this.totalCpuSysTime = sys0;
//		this.totalCpuNiceTime = nice0;
//		this.totalCpuWaitTime = wait0;
//		this.totalCpuIdleTime = idle0;
		this.ramMemoryFree = rAMMemoryFree;
		this.ramMemoryUsed = rAMMemoryUsed;
		this.memFreePercent = freePercent;
		this.memUsedPercent = usedPercent;
		this.swapMemoryFree = swapMemoryFree;
		this.swapMemoryPageIn = swapMemoryPageIn;
		this.swapMemoryPageOut = swapMemoryPageOut;
		this.swapMemoryUsed = swapMemoryUsed;
		this.hardDiskFreeSpace = hardDiskFreeSpace;
		this.hardDiskUsedSpace = hardDiskUsedSpace;
		this.netRxBytes = rxBytes;
		this.netTxBytes = txBytes;
		this.netSpeed = speed;
		this.netRxErrors = rxErrors;
		this.netTxErrors = txErrors;
		this.netRxPackets = rxPackets;
		this.netTxPackets = txPackets;
   //     this.processes=processes;
	}
	
	public MonitorReport(String line) throws ParseException {		
			
		line = line.replace("MonitorReport [", "").replace("]", "");
		//processes = line.substring(line.indexOf("{")+1, line.indexOf("}"));
		//line = line.substring(0,line.indexOf(","+ItemCPUReport.PROCESSES.title()));
    	String [] elements = line.split(",");
    	for (String elem : elements) {
			String [] components = elem.split("=");			
			if(components[0].trim().equals(ItemCPUReport.TIME.title())){
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			    Date parsedDate = dateFormat.parse(components[1]);
			    timest = new java.sql.Timestamp(parsedDate.getTime());
			}
			else if(components[0].trim().equals(ItemCPUReport.TIME_MILLI.title())) timeLong = Long.parseLong(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.USERNAME.title())) userName = components[1];
			else if(components[0].trim().equals(ItemCPUReport.UP_TIME.title())) cpuUptime = Double.parseDouble(components[1]);
//			else if(components[0].trim().equals(ItemCPUReport.CPU_IDLE.title())) cpuIdle = Double.parseDouble(components[1]);
//			else if(components[0].trim().equals(ItemCPUReport.NO_CPU_IDLE.title())) noCpuIdle = Double.parseDouble(components[1]);
//			else if(components[0].trim().equals(ItemCPUReport.CPU_USER.title())) cpuUser = Double.parseDouble(components[1]);
//			else if(components[0].trim().equals(ItemCPUReport.CPU_SYS.title())) cpuSys = Double.parseDouble(components[1]);
//			else if(components[0].trim().equals(ItemCPUReport.CPU_NICE.title())) cpuNice = Double.parseDouble(components[1]);
//			else if(components[0].trim().equals(ItemCPUReport.CPU_WAIT.title())) cpuWait = Double.parseDouble(components[1]);
//			else if(components[0].trim().equals(ItemCPUReport.CPU_COMBINED.title())) cpuCombined = Double.parseDouble(components[1]);
//			else if(components[0].trim().equals(ItemCPUReport.TOTAL_USER.title())) totalCpuUserTime = Long.parseLong(components[1]);
//			else if(components[0].trim().equals(ItemCPUReport.TOTAL_SYS.title())) totalCpuSysTime = Long.parseLong(components[1]);
//			else if(components[0].trim().equals(ItemCPUReport.TOTAL_NICE.title())) totalCpuNiceTime = Long.parseLong(components[1]);
//			else if(components[0].trim().equals(ItemCPUReport.TOTAL_WAIT.title())) totalCpuWaitTime = Long.parseLong(components[1]);
//			else if(components[0].trim().equals(ItemCPUReport.TOTAL_IDLE.title())) totalCpuIdleTime = Long.parseLong(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.RAM_FREE.title())) ramMemoryFree = Double.parseDouble(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.RAM_USED.title())) ramMemoryUsed = Double.parseDouble(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.MEM_FREE.title())) memFreePercent = Double.parseDouble(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.MEM_USED.title())) memUsedPercent = Double.parseDouble(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.SWAP_FREE.title())) swapMemoryFree = Double.parseDouble(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.SWAP_PAGE_IN.title())) swapMemoryPageIn = Double.parseDouble(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.SWAP_PAGE_OUT.title())) swapMemoryPageOut = Double.parseDouble(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.SWAP_USED.title())) swapMemoryUsed = Double.parseDouble(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.HD_FREE.title())) hardDiskFreeSpace = Long.parseLong(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.HD_USED.title())) hardDiskUsedSpace = Long.parseLong(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.NET_RX_BYTES.title())) netRxBytes = Long.parseLong(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.NET_TX_BYTES.title())) netTxBytes = Long.parseLong(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.NET_SPEED.title())) netSpeed = Long.parseLong(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.NET_RX_ERRORS.title())) netRxErrors = Long.parseLong(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.NET_TX_ERRORS.title())) netTxErrors = Long.parseLong(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.NET_RX_PACKETS.title())) netRxPackets = Long.parseLong(components[1]);
			else if(components[0].trim().equals(ItemCPUReport.NET_TX_PACKETS.title())) netTxPackets = Long.parseLong(components[1]);
		}
	}
	public Timestamp getTimest() {
		return timest;
	}

	public void setTimest(Timestamp timest) {
		this.timest = timest;
	}
	
	public void setTimestString(String t){
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);	  
	    Date parsedDate;
		try {
			parsedDate = dateFormat.parse(t);
			timest = new java.sql.Timestamp(parsedDate.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}	

	public long getTimeLong() {
		return timeLong;
	}

	public void setTimeLong(long timeLong) {
		this.timeLong = timeLong;
	}
	public double getCpuUptime() {
		return cpuUptime;
	}

	public void setCpuUptime(double cpuUptime) {
		this.cpuUptime = cpuUptime;
	}

//	public double getCpuIdle() {
//		return cpuIdle;
//	}
//
//	public void setCpuIdle(double cpuIdle) {
//		this.cpuIdle = cpuIdle;
//	}
//
//	public double getNoCpuIdle() {
//		return noCpuIdle;
//	}
//
//	public void setNoCpuIdle(double noCpuIdle) {
//		this.noCpuIdle = noCpuIdle;
//	}
//
//	public double getCpuUser() {
//		return cpuUser;
//	}
//
//	public void setCpuUser(double cpuUser) {
//		this.cpuUser = cpuUser;
//	}
//
//	public double getCpuSys() {
//		return cpuSys;
//	}
//
//	public void setCpuSys(double cpuSys) {
//		this.cpuSys = cpuSys;
//	}
//
//	public double getCpuNice() {
//		return cpuNice;
//	}
//
//	public void setCpuNice(double cpuNice) {
//		this.cpuNice = cpuNice;
//	}
//
//	public double getCpuWait() {
//		return cpuWait;
//	}
//
//	public void setCpuWait(double cpuWait) {
//		this.cpuWait = cpuWait;
//	}
//
//	public double getCpuCombined() {
//		return cpuCombined;
//	}
//
//	public void setCpuCombined(double cpuCombined) {
//		this.cpuCombined = cpuCombined;
//	}
//
//	public long getTotalCpuUserTime() {
//		return totalCpuUserTime;
//	}
//
//	public void setTotalCpuUserTime(long totalCpuUserTime) {
//		this.totalCpuUserTime = totalCpuUserTime;
//	}
//
//	public long getTotalCpuSysTime() {
//		return totalCpuSysTime;
//	}
//
//	public void setTotalCpuSysTime(long totalCpuSysTime) {
//		this.totalCpuSysTime = totalCpuSysTime;
//	}
//
//	public long getTotalCpuNiceTime() {
//		return totalCpuNiceTime;
//	}
//
//	public void setTotalCpuNiceTime(long totalCpuNiceTime) {
//		this.totalCpuNiceTime = totalCpuNiceTime;
//	}
//
//	public long getTotalCpuWaitTime() {
//		return totalCpuWaitTime;
//	}
//
//	public void setTotalCpuWaitTime(long totalCpuWaitTime) {
//		this.totalCpuWaitTime = totalCpuWaitTime;
//	}
//
//	public long getTotalCpuIdleTime() {
//		return totalCpuIdleTime;
//	}
//
//	public void setTotalCpuIdleTime(long totalCpuIdleTime) {
//		this.totalCpuIdleTime = totalCpuIdleTime;
//	}

	public double getRamMemoryFree() {
		return ramMemoryFree;
	}

	public void setRamMemoryFree(double ramMemoryFree) {
		this.ramMemoryFree = ramMemoryFree;
	}

	public double getRamMemoryUsed() {
		return ramMemoryUsed;
	}

	public void setRamMemoryUsed(double ramMemoryUsed) {
		this.ramMemoryUsed = ramMemoryUsed;
	}

	public double getMemFreePercent() {
		return memFreePercent;
	}

	public void setMemFreePercent(double memFreePercent) {
		this.memFreePercent = memFreePercent;
	}

	public double getMemUsedPercent() {
		return memUsedPercent;
	}

	public void setMemUsedPercent(double memUsedPercent) {
		this.memUsedPercent = memUsedPercent;
	}

	public double getSwapMemoryFree() {
		return swapMemoryFree;
	}

	public void setSwapMemoryFree(double swapMemoryFree) {
		this.swapMemoryFree = swapMemoryFree;
	}

	public double getSwapMemoryPageIn() {
		return swapMemoryPageIn;
	}

	public void setSwapMemoryPageIn(double swapMemoryPageIn) {
		this.swapMemoryPageIn = swapMemoryPageIn;
	}

	public double getSwapMemoryPageOut() {
		return swapMemoryPageOut;
	}

	public void setSwapMemoryPageOut(double swapMemoryPageOut) {
		this.swapMemoryPageOut = swapMemoryPageOut;
	}

	public double getSwapMemoryUsed() {
		return swapMemoryUsed;
	}

	public void setSwapMemoryUsed(double swapMemoryUsed) {
		this.swapMemoryUsed = swapMemoryUsed;
	}

	public long getHardDiskFreeSpace() {
		return hardDiskFreeSpace;
	}

	public void setHardDiskFreeSpace(long hardDiskFreeSpace) {
		this.hardDiskFreeSpace = hardDiskFreeSpace;
	}

	public long getHardDiskUsedSpace() {
		return hardDiskUsedSpace;
	}

	public void setHardDiskUsedSpace(long hardDiskUsedSpace) {
		this.hardDiskUsedSpace = hardDiskUsedSpace;
	}

	public long getNetRxBytes() {
		return netRxBytes;
	}

	public void setNetRxBytes(long netRxBytes) {
		this.netRxBytes = netRxBytes;
	}

	public long getNetTxBytes() {
		return netTxBytes;
	}

	public void setNetTxBytes(long netTxBytes) {
		this.netTxBytes = netTxBytes;
	}

	public long getNetSpeed() {
		return netSpeed;
	}

	public void setNetSpeed(long netSpeed) {
		this.netSpeed = netSpeed;
	}

	public long getNetRxErrors() {
		return netRxErrors;
	}

	public void setNetRxErrors(long netRxErrors) {
		this.netRxErrors = netRxErrors;
	}

	public long getNetTxErrors() {
		return netTxErrors;
	}

	public void setNetTxErrors(long netTxErrors) {
		this.netTxErrors = netTxErrors;
	}

	public long getNetRxPackets() {
		return netRxPackets;
	}

	public void setNetRxPackets(long netRxPackets) {
		this.netRxPackets = netRxPackets;
	}

	public long getNetTxPackets() {
		return netTxPackets;
	}

	public void setNetTxPackets(long netTxPackets) {
		this.netTxPackets = netTxPackets;
	}

//	public String getProcesses() {
//		return processes;
//	}
//
//	public void setProcesses(String processes) {
//		this.processes = processes;
//	}

	@Override
	public String toString() {
		return "MonitorReport ["+ItemCPUReport.TIME.title()+"=" + timest+","
				+ItemCPUReport.TIME_MILLI.title()+"=" +timeLong+ ","
				+ItemCPUReport.USERNAME.title()+"="+ userName + ","
				+ItemCPUReport.UP_TIME.title()+"="+ cpuUptime + ","
//				+ItemCPUReport.CPU_IDLE.title()+"=" + cpuIdle + ","
//				+ItemCPUReport.NO_CPU_IDLE.title()+"=" + noCpuIdle+","
//				+ItemCPUReport.CPU_USER.title()+"=" + cpuUser + ","
//				+ItemCPUReport.CPU_SYS.title()+"=" + cpuSys + ","
//				+ItemCPUReport.CPU_NICE.title()+"=" + cpuNice+ ","
//				+ItemCPUReport.CPU_WAIT.title()+"=" + cpuWait + ","
//				+ItemCPUReport.CPU_COMBINED.title()+"=" + cpuCombined + ","
//				+ItemCPUReport.TOTAL_USER.title()+"=" + totalCpuUserTime + ","
//				+ItemCPUReport.TOTAL_SYS.title()+"=" + totalCpuSysTime + ","
//				+ItemCPUReport.TOTAL_NICE.title()+"=" + totalCpuNiceTime + ","
//				+ItemCPUReport.TOTAL_WAIT.title()+"=" + totalCpuWaitTime + ","
//				+ItemCPUReport.TOTAL_IDLE.title()+"=" + totalCpuIdleTime + ","
				+ItemCPUReport.RAM_FREE.title()+"=" + ramMemoryFree + ","
				+ItemCPUReport.RAM_USED.title()+"=" + ramMemoryUsed + ","
				+ItemCPUReport.MEM_FREE.title()+"=" + memFreePercent + ","
				+ItemCPUReport.MEM_USED.title()+"=" + memUsedPercent + ","
				+ItemCPUReport.SWAP_FREE.title()+"=" + swapMemoryFree+ ","
				+ItemCPUReport.SWAP_PAGE_IN.title()+"=" + swapMemoryPageIn+ ","
				+ItemCPUReport.SWAP_PAGE_OUT.title()+"=" + swapMemoryPageOut+ ","
				+ItemCPUReport.SWAP_USED.title()+"=" + swapMemoryUsed + ","
				+ItemCPUReport.HD_FREE.title()+"=" + hardDiskFreeSpace + ","
				+ItemCPUReport.HD_USED.title()+"=" + hardDiskUsedSpace + ","
				+ItemCPUReport.NET_RX_BYTES.title()+"=" + netRxBytes + ","
				+ItemCPUReport.NET_TX_BYTES.title()+"=" + netTxBytes + ","
				+ItemCPUReport.NET_SPEED.title()+"=" + netSpeed + ","
				+ItemCPUReport.NET_RX_ERRORS.title()+"=" + netRxErrors	+ ","
				+ItemCPUReport.NET_TX_ERRORS.title()+"=" + netTxErrors + ","
				+ItemCPUReport.NET_RX_PACKETS.title()+"=" + netRxPackets+ ","
				+ItemCPUReport.NET_TX_PACKETS.title()+"=" + netTxPackets
	//			+","
	//			+ItemCPUReport.PROCESSES.title()+"={" + processes + "}
				+"]";
	}
	
	public static String getHead(){
		return ItemCPUReport.TIME.title()+"," + ItemCPUReport.TIME_MILLI.title()+ "," + ItemCPUReport.USERNAME.title()+ ","
				+ ItemCPUReport.HOSTNAME.title() + ","+ ItemCPUReport.UP_TIME.title()+"," 
//				+ ItemCPUReport.CPU_IDLE.title() + "," + ItemCPUReport.NO_CPU_IDLE.title() + ","
//				+ ItemCPUReport.CPU_USER.title()+"," + ItemCPUReport.CPU_SYS.title() + "," + ItemCPUReport.CPU_NICE.title() + "," 
//				+ ItemCPUReport.CPU_WAIT.title()+"," + ItemCPUReport.CPU_COMBINED.title() + "," + ItemCPUReport.TOTAL_USER.title() + ","
//				+ ItemCPUReport.TOTAL_SYS.title() + "," + ItemCPUReport.TOTAL_NICE.title() + ","+ ItemCPUReport.TOTAL_WAIT.title() + "," 
//				+ ItemCPUReport.TOTAL_IDLE.title() + ","
				+ ItemCPUReport.RAM_FREE.title()+"," + ItemCPUReport.RAM_USED.title()+ ","
				+ ItemCPUReport.MEM_FREE.title() + ","+ ItemCPUReport.MEM_USED.title() + "," + ItemCPUReport.SWAP_FREE.title()+ ","
				+ ItemCPUReport.SWAP_PAGE_IN.title()+ "," + ItemCPUReport.SWAP_PAGE_OUT.title()+ "," + ItemCPUReport.SWAP_USED.title()+ "," 
				+ ItemCPUReport.HD_FREE.title() + ","+ ItemCPUReport.HD_USED.title() + ","
				+ ItemCPUReport.NET_RX_BYTES.title() + "," + ItemCPUReport.NET_TX_BYTES.title() + ","+ ItemCPUReport.NET_SPEED.title() + "," 
				+ ItemCPUReport.NET_RX_ERRORS.title() + "," + ItemCPUReport.NET_TX_ERRORS.title()+ "," + ItemCPUReport.NET_RX_PACKETS.title() + "," 
				+ ItemCPUReport.NET_TX_PACKETS.title()	
	//			+ "," + ItemCPUReport.PROCESSES.title()+"(Name;VirtualMemorySize;ResidentMemorySize;CPUPercentage)"
				;
	}
	public String getLine(){
		return  timest+ "," + timeLong+ ","	+ userName + ","
				+ cpuUptime + "," 
//				+ cpuIdle + "," + noCpuIdle+ "," 
//				+ cpuUser + "," + cpuSys + "," + cpuNice+ "," 
//				+ cpuWait + ", " + cpuCombined + ","+ totalCpuUserTime + "," 
//				+ totalCpuSysTime + "," + totalCpuNiceTime + ","
//				+ totalCpuWaitTime + "," + totalCpuIdleTime + ","
				+ ramMemoryFree + "," + ramMemoryUsed+ "," 
				+ memFreePercent + "," + memUsedPercent + "," + swapMemoryFree+ "," 
				+ swapMemoryPageIn+ "," + swapMemoryPageOut	+ "," + swapMemoryUsed + ","
				+ hardDiskFreeSpace + ","+ hardDiskUsedSpace + ","  
				+ netRxBytes + ","	+ netTxBytes + "," + netSpeed + ","
				+ netRxErrors+ "," + netTxErrors + "," + netRxPackets+ ","
				+ netTxPackets 
//				+ "," + processes
				;
	}
}
