package enums;

public enum ItemCPUReport {
	
	TIME("Timestamp"),
	TIME_MILLI("TimeMilli"),
	USERNAME("Username"),
	UP_TIME("UpTime"),
	CPU_IDLE("CPUIdle"),	
	NO_CPU_IDLE("NoCPUIdle"),
	CPU_USER("CPUuser"),
	CPU_SYS("CPUsys"),
	CPU_NICE("CPUNice"),
	CPU_WAIT("CPUWait"),
	CPU_COMBINED("CPUCombined"),	
	TOTAL_USER("TotalUserTime"),
	TOTAL_SYS("TotalSysTime"),
	TOTAL_NICE("TotalNiceTime"),
	TOTAL_WAIT("TotalWaitTime"),
	TOTAL_IDLE("TotalIdleTime"),
	RAM_FREE("RamFree"),
	RAM_USED("RamUsed"),	
	MEM_FREE("MemFreePercent"),
	MEM_USED("MemUsedPercent"),
	SWAP_FREE("SwapMemoryFree"),
	SWAP_PAGE_IN("SwapMemoryPageIn"),
	SWAP_PAGE_OUT("SwapMemoryPageOut"),
	SWAP_USED("SwapMemoryUsed"),
	HD_FREE("HDFreeSpace"),
	HD_USED("HDUsedSpace"),
	NET_RX_BYTES("NetRXBytes"),
	NET_TX_BYTES("NetTxBytes"),
	NET_SPEED("NetSpeed"),
	NET_RX_ERRORS("NetRXErrors"),
	NET_TX_ERRORS("NetTxErrors"),
	NET_RX_PACKETS("NetRxPackets"),
	NET_TX_PACKETS("NetTxPackets"),
	PROCESSES("Processes");
	
	private String title;
	ItemCPUReport(String title){
		this.title = title;
	}
	
	public String title(){
		return title;
	}

}
