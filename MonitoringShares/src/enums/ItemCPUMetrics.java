package enums;

public enum ItemCPUMetrics {
	
	HOSTNAME("Hostname"),
	TIME("Timestamp"),
	TIME_MILLI("TimeMilli"),
	OS_NAME("OSName"),
	OS_VERSION("OSVersion"),
	MFLOPS("MFlops"),		
	CPU_SECONDS("CPUTimeInSeconds"),	
	OS_ARQUITECTURE("OSArquitecture"),	
	CPU_MODEL("CPUModel"),
	CPU_VENDOR("CPUVendor"),
	CPU_CORES("CPUCores"),
	CPU_SOCKETS("CPUSockets"),
	CPU_MHZ("CpuMhz"),
	CORES_X_SOCKETS("CoresXSocket"),
	RAM_SIZE("RAMMemorySize"),
	SWAP_SIZE("SwapMemorySize"),
	HD_SPACE("HDSpace"),
	HD_FILESYSTEM("HDFileSystem"),
	MAC("MACAddress"),
	NET_IP("NetworkIpAddress"),
	NET_INTERFACE("NetworkInterface"),
	NET_MASK("NetworkNetMask"),
	NET_GATEWAY("NetworkGateway");
	
	private String title;	
	private ItemCPUMetrics(String t) {
		title = t;
	}
	public String title(){return title;}
}
