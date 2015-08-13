package reports;


import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import enums.ItemCPUMetrics;

/**
 * This class provides the attributes to encapsulate the result of a monitor
 * test. Among others, it includes cpu usage, memory usage and other
 * statistics
 *
 * @author GSot ans Cesar
 *
 */
public class MonitorInitialReport implements Serializable{

	private static final long serialVersionUID = -2738566841932331498L;
    private Timestamp timest;
    private long timeLong;
    private double mflops,timeinSecs;
    private String hostname;
    private String operatingSystemName;
    private String operatingSystemVersion;
    private String operatingSystemArchitect;
    private String cPUModel;
    private String cPUVendor;
    private int cPUCores;
    private int totalSockets;
    private String cPUMhz;
    private int coresPerSocket;
    private double rAMMemorySize;
    private double swapMemorySize;
    private long hardDiskSpace;
    private String hardDiskFileSystem;
    private String networkMACAddress;
    private String networkIPAddress;
    private String networkInterface;
    private String networkNetmask;
    private String networkGateway;
    
    public MonitorInitialReport() {
	}

    public MonitorInitialReport(Timestamp timest, Long time, String hostname, double mflops, double timeinSecs, String operatingSystemName, String operatingSystemVersion, String operatingSystemArchitect, String cPUModel, String cPUVendor, int cPUCores, int totalSockets, String cPUMhz, int coresPerSocket, double rAMMemorySize, double swapMemorySize, long hardDiskSpace, String hardDiskFileSystem, String networkMACAddress, String networkIPAddress, String networkInterface, String networkNetmask, String networkGateway) {
       // super(REGISTRATION_OPERATION,0);
        this.timest = timest;
        this.timeLong = time;
        this.hostname = hostname;
        this.operatingSystemName = operatingSystemName;
        this.operatingSystemVersion = operatingSystemVersion;
        this.operatingSystemArchitect = operatingSystemArchitect;
        this.cPUModel = cPUModel;
        this.cPUVendor = cPUVendor;
        this.cPUCores = cPUCores;
        this.totalSockets = totalSockets;
        this.cPUMhz = cPUMhz;
        this.coresPerSocket = coresPerSocket;
        this.rAMMemorySize = rAMMemorySize;
        this.swapMemorySize = swapMemorySize;
        this.hardDiskSpace = hardDiskSpace;
        this.hardDiskFileSystem = hardDiskFileSystem;
        this.networkMACAddress = networkMACAddress;
        this.timeinSecs = timeinSecs;
        this.mflops = mflops;
		this.networkIPAddress = networkIPAddress;
		this.networkInterface = networkInterface;
		this.networkNetmask = networkNetmask;
		this.networkGateway = networkGateway;
    }
    
    public MonitorInitialReport(String line) throws ParseException{
    	
    	line = line.replace("MonitorInitialReport [", "").replace("]", "");
    	String [] elements = line.split(",");
    	for (String elem : elements) {
			String [] components = elem.split("=");
			if(components[0].equals(ItemCPUMetrics.TIME.title())){
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			    Date parsedDate = dateFormat.parse(components[1]);
			    timest = new java.sql.Timestamp(parsedDate.getTime());
			}
			else if(components[0].equals(ItemCPUMetrics.TIME_MILLI.title())) timeLong = Long.parseLong(components[1]);
			else if(components[0].equals(ItemCPUMetrics.HOSTNAME.title())) hostname = components[1];
			else if(components[0].equals(ItemCPUMetrics.OS_NAME.title())) operatingSystemName = components[1];
			else if(components[0].equals(ItemCPUMetrics.OS_VERSION.title())) operatingSystemVersion = components[1];
			else if(components[0].equals(ItemCPUMetrics.OS_ARQUITECTURE.title())) operatingSystemArchitect = components[1];
			else if(components[0].equals(ItemCPUMetrics.CPU_MODEL.title())) cPUModel = components[1];
			else if(components[0].equals(ItemCPUMetrics.CPU_VENDOR.title())) cPUVendor = components[1];
			else if(components[0].equals(ItemCPUMetrics.CPU_CORES.title())) cPUCores = Integer.parseInt(components[1]);
			else if(components[0].equals(ItemCPUMetrics.CPU_MHZ.title())) cPUMhz = components[1];
			else if(components[0].equals(ItemCPUMetrics.CORES_X_SOCKETS.title()))coresPerSocket = Integer.parseInt(components[1]);
			else if(components[0].equals(ItemCPUMetrics.CPU_SOCKETS.title())) totalSockets = Integer.parseInt(components[1]);
			else if(components[0].equals(ItemCPUMetrics.RAM_SIZE.title())) rAMMemorySize = Double.parseDouble(components[1]);
			else if(components[0].equals(ItemCPUMetrics.SWAP_SIZE.title())) swapMemorySize = Double.parseDouble(components[1]);
			else if(components[0].equals(ItemCPUMetrics.HD_SPACE.title())) hardDiskSpace = Long.parseLong(components[1]);
			else if(components[0].equals(ItemCPUMetrics.HD_FILESYSTEM.title())) hardDiskFileSystem = components[1];
			else if(components[0].equals(ItemCPUMetrics.MAC.title())) networkMACAddress = components[1];
			else if(components[0].equals(ItemCPUMetrics.MFLOPS.title())) mflops = Double.parseDouble(components[1]);
			else if(components[0].equals(ItemCPUMetrics.CPU_SECONDS.title())) timeinSecs = Double.parseDouble(components[1]);
			else if(components[0].equals(ItemCPUMetrics.NET_IP.title())) networkIPAddress = components[1];
			else if(components[0].equals(ItemCPUMetrics.NET_INTERFACE.title())) networkInterface = components[1];
			else if(components[0].equals(ItemCPUMetrics.NET_MASK.title())) networkNetmask = components[1];
			else if(components[0].equals(ItemCPUMetrics.NET_GATEWAY.title())) networkGateway = components[1];
		}
    }


    public Timestamp getTimest() {
        return timest;
    }

    public void setTimest(Timestamp timest) {
        this.timest = timest;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getOperatingSystemName() {
        return operatingSystemName;
    }

    public void setOperatingSystemName(String operatingSystemName) {
        this.operatingSystemName = operatingSystemName;
    }

    public String getOperatingSystemVersion() {
        return operatingSystemVersion;
    }

    public void setOperatingSystemVersion(String operatingSystemVersion) {
        this.operatingSystemVersion = operatingSystemVersion;
    }

    public String getOperatingSystemArchitect() {
        return operatingSystemArchitect;
    }

    public void setOperatingSystemArchitect(String operatingSystemArchitect) {
        this.operatingSystemArchitect = operatingSystemArchitect;
    }

    public String getcPUModel() {
        return cPUModel;
    }

    public void setcPUModel(String cPUModel) {
        this.cPUModel = cPUModel;
    }

    public String getcPUVendor() {
        return cPUVendor;
    }

    public void setcPUVendor(String cPUVendor) {
        this.cPUVendor = cPUVendor;
    }

    public int getcPUCores() {
        return cPUCores;
    }

    public void setcPUCores(int cPUCores) {
        this.cPUCores = cPUCores;
    }

    public int getTotalSockets() {
        return totalSockets;
    }

    public void setTotalSockets(int totalSockets) {
        this.totalSockets = totalSockets;
    }

    public String getcPUMhz() {
        return cPUMhz;
    }

    public void setcPUMhz(String cPUMhz) {
        this.cPUMhz = cPUMhz;
    }

    public int getCoresPerSocket() {
        return coresPerSocket;
    }

    public void setCoresPerSocket(int coresPerSocket) {
        this.coresPerSocket = coresPerSocket;
    }

    public double getrAMMemorySize() {
        return rAMMemorySize;
    }

    public void setrAMMemorySize(double rAMMemorySize) {
        this.rAMMemorySize = rAMMemorySize;
    }

    public double getSwapMemorySize() {
        return swapMemorySize;
    }

    public void setSwapMemorySize(double swapMemorySize) {
        this.swapMemorySize = swapMemorySize;
    }

    public long getHardDiskSpace() {
        return hardDiskSpace;
    }

    public void setHardDiskSpace(long hardDiskSpace) {
        this.hardDiskSpace = hardDiskSpace;
    }

    public String getHardDiskFileSystem() {
        return hardDiskFileSystem;
    }

    public void setHardDiskFileSystem(String hardDiskFileSystem) {
        this.hardDiskFileSystem = hardDiskFileSystem;
    }

    public String getNetworkMACAddress() {
        return networkMACAddress;
    }

    public void setNetworkMACAddress(String networkMACAddress) {
        this.networkMACAddress = networkMACAddress;
    }
    
    public void setTimestString(String l){
    	SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
	    Date parsedDate;
		try {
			parsedDate = dateFormat.parse(l);
			timest = new java.sql.Timestamp(parsedDate.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	    
    }
    
    public long getTimeLong() {
		return timeLong;
	}
    
    public void setTimeLong(long timeLong) {
		this.timeLong = timeLong;
	}

	public double getMflops() {
		return mflops;
	}

	public void setMflops(double mflops) {
		this.mflops = mflops;
	}

	public double getTimeinSecs() {
		return timeinSecs;
	}

	public void setTimeinSecs(double timeinSecs) {
		this.timeinSecs = timeinSecs;
	}

	
	public String getNetworkIPAddress() {
		return networkIPAddress;
	}

	public void setNetworkIPAddress(String networkIPAddress) {
		this.networkIPAddress = networkIPAddress;
	}

	public String getNetworkInterface() {
		return networkInterface;
	}

	public void setNetworkInterface(String networkInterface) {
		this.networkInterface = networkInterface;
	}

	public String getNetworkNetmask() {
		return networkNetmask;
	}

	public void setNetworkNetmask(String networkNetmask) {
		this.networkNetmask = networkNetmask;
	}

	public String getNetworkGateway() {
		return networkGateway;
	}

	public void setNetworkGateway(String networkGateway) {
		this.networkGateway = networkGateway;
	}

	@Override
	public String toString() {
		return "MonitorInitialReport ["+ItemCPUMetrics.TIME.title()+"=" + timest
				+ ","+ItemCPUMetrics.TIME_MILLI.title()+"=" + timeLong 
				+ ","+ItemCPUMetrics.MFLOPS.title()+"=" + mflops
				+ ","+ItemCPUMetrics.CPU_SECONDS.title()+"=" + timeinSecs 
				+ ","+ItemCPUMetrics.HOSTNAME.title()+"=" + hostname
				+ ","+ItemCPUMetrics.OS_NAME.title()+"=" + operatingSystemName
				+ ","+ItemCPUMetrics.OS_VERSION.title()+"=" + operatingSystemVersion
				+ ","+ItemCPUMetrics.OS_ARQUITECTURE.title()+"=" + operatingSystemArchitect
				+ ","+ItemCPUMetrics.CPU_MODEL.title()+"=" + cPUModel 
				+ ","+ItemCPUMetrics.CPU_VENDOR.title()+"=" + cPUVendor
				+ ","+ItemCPUMetrics.CPU_CORES.title()+"=" + cPUCores 
				+ ","+ItemCPUMetrics.CPU_SOCKETS.title()+"=" + totalSockets
				+ ","+ItemCPUMetrics.CPU_MHZ.title()+"=" + cPUMhz 
				+ ","+ItemCPUMetrics.CORES_X_SOCKETS.title()+"=" + coresPerSocket
				+ ","+ItemCPUMetrics.RAM_SIZE.title()+"=" + rAMMemorySize 
				+ ","+ItemCPUMetrics.SWAP_SIZE.title()+"=" + swapMemorySize 
				+ ","+ItemCPUMetrics.HD_SPACE.title()+"=" + hardDiskSpace
				+ ","+ItemCPUMetrics.HD_FILESYSTEM.title()+"=" + hardDiskFileSystem
				+ ","+ItemCPUMetrics.MAC.title()+"=" + networkMACAddress 				
				+ ","+ItemCPUMetrics.NET_IP.title()+"=" + networkIPAddress
				+ ","+ItemCPUMetrics.NET_INTERFACE.title()+"=" + networkInterface
				+ ","+ItemCPUMetrics.NET_MASK.title()+"=" + networkNetmask
				+ ","+ItemCPUMetrics.NET_GATEWAY.title()+"=" + networkGateway +"]";
	}

}
