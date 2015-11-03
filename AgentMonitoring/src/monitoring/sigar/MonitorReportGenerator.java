package monitoring.sigar;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import monitoring.sigar.physicalmachine.PhysicalMachine;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Uptime;
import org.hyperic.sigar.cmd.SigarCommandBase;

import com.losandes.utils.OperatingSystem;

import enums.ItemCPUMetrics;
import enums.ItemCPUReport;

/**
 * This class use sigar library. It generates a log of monitoring values based in a list of variables set by user.
 * 
 * @author CesarF
 *
 */
public class MonitorReportGenerator extends SigarCommandBase {
	
	private ArrayList<String> headers;
	private ArrayList<String> headersInitial;
	
	private static MonitorReportGenerator instance;
	public static synchronized MonitorReportGenerator getInstance(){		
		if(instance==null)instance=new MonitorReportGenerator();
		return instance;
	}
	/**
	 * Set the headers that will use the sensor.
	 * @param heads
	 */
    public void setHeaders(String[] heads){    	
    	headers = new ArrayList<String>();
    	for(String head: heads)if(!head.isEmpty())headers.add(head);
    }
    /**
	 * Set the headers that will use the sensor.
	 * @param heads
	 */
    public void setInitialHeaders(String[] heads){    	
    	headersInitial = new ArrayList<String>();
    	for(String head: heads)if(!head.isEmpty())headersInitial.add(head);
    }
    /**
     * Return the list of headers of initial report separated by comma
     * @return
     */
    public String getHeadInitial(){
    	String head = ItemCPUMetrics.TIME.title()+","+ItemCPUMetrics.TIME_MILLI.title()+",";
    	for (int i = 0; i < headersInitial.size(); i++) {
    		head+=headersInitial.get(i);
			if(i!=headersInitial.size()-1)head+=",";
		}
    	return head;
    }
	/**
	 * Generates initial monitoring report info
	 * @return initial report as a String for log
	 * @throws SigarException 
	 */
    public String generateInitialReport() throws SigarException {
    	java.util.Date date = new Date();
        java.sql.Timestamp timest = new java.sql.Timestamp(date.getTime());        
        String result = timest+","+date.getTime()+",";
        
    	monitoring.sigar.linpackJava.Linpack CPUMflops = null;
    	if(headersInitial.contains(ItemCPUMetrics.MFLOPS.name())||headersInitial.contains(ItemCPUMetrics.CPU_SECONDS.name())){
    		CPUMflops = new monitoring.sigar.linpackJava.Linpack();
            CPUMflops.run_benchmark();
    	}    	
    	PhysicalMachine monitor = new PhysicalMachine();
    	CpuInfo[] infos = this.sigar.getCpuInfoList();
    	for (int i = 0; i < headersInitial.size(); i++) {
    		if(headersInitial.get(i).equals(ItemCPUMetrics.CORES_X_SOCKETS.name())){
    			org.hyperic.sigar.CpuInfo CPU1 = infos[0];
    			result+=CPU1.getCoresPerSocket();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.CPU_CORES.name())){
    			result+= monitor.cpu.getCPUCores();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.CPU_MHZ.name())){
    			 RepetitionCounter cpuMhz = new RepetitionCounter();
    			 for (CpuInfo cpu : infos)cpuMhz.add("" + cpu.getMhz());
    			 result+=cpuMhz.toString();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.CPU_MODEL.name())){
    			RepetitionCounter cpuModel = new RepetitionCounter();
    	        for (CpuInfo cpu : infos)cpuModel.add(cpu.getModel());
    			result+= cpuModel.toString();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.CPU_SECONDS.name())){
    			result+=CPUMflops.getTimeinSecs();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.CPU_SOCKETS.name())){
    			org.hyperic.sigar.CpuInfo CPU1 = infos[0];
    			result+=CPU1.getTotalSockets();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.CPU_VENDOR.name())){
    			RepetitionCounter cpuVendor = new RepetitionCounter();
    	        for (CpuInfo cpu : infos)cpuVendor.add(cpu.getVendor());
    			result+=cpuVendor.toString();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.HD_FILESYSTEM.name())){
    			result+=monitor.hardDisk.getHardDiskFileSystem();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.HD_SPACE.name())){
    			result+=monitor.hardDisk.getHardDiskSpace();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.MAC.name())){
    			result+= monitor.network.getNetworkMACAddress();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.MFLOPS.name())){
    			result+=CPUMflops.getMflops();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.NET_GATEWAY.name())){
    			result+=monitor.network.getNetworkGateway();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.NET_INTERFACE.name())){
    			result+=monitor.network.getNetworkInterface();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.NET_IP.name())){
    			result+=monitor.network.getNetworkIPAddress();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.NET_MASK.name())){
    			result+=monitor.network.getNetworkNetmask();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.OS_ARQUITECTURE.name())){
    			result+=monitor.operatingSystem.getOperatingSystemArchitect();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.OS_NAME.name())){
    			result+=monitor.operatingSystem.getOperatingSystemName();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.OS_VERSION.name())){
    			result+=monitor.operatingSystem.getOperatingSystemVersion();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.RAM_SIZE.name())){
    			result+=monitor.memory.getRAMMemorySize();
    		}else if(headersInitial.get(i).equals(ItemCPUMetrics.SWAP_SIZE.name())){
    			result+=monitor.memory.getSwapMemorySize();
    		}
    		if(i!=headersInitial.size()-1)result+=",";
    	}
        return result;
        // com.sun.security.auth.module.NTSystem NTSystem = new
        // com.sun.security.auth.module.NTSystem();
        // Evalua una sola CPU
    }
    /**
     * 
     */
    private  class RepetitionCounter extends HashMap<String, Integer> {    	
		private static final long serialVersionUID = -5259022218756213835L;		
		/**
		 * adds new input
		 * @param input
		 */
		public void add(String input) {
            Integer n = get(input);
            if (n == null) {
                n = 0;
            }
            n++;
            put(input, n);
        }
		
        @Override
        public String toString() {
            String ret = null;
            for (java.util.Map.Entry<String, Integer> ent : entrySet()) {
                ret = (ret != null ? ";" : "") + ent.getKey() + " x" + ent.getValue();
            }
            return ret == null ? "" : ret;
        }
    }
    /**
     * Return the report head list for a log
     * @return
     */
    public String getReportHeaders(){
    	String head = ItemCPUReport.TIME.title()+","+ItemCPUReport.TIME_MILLI.title()+",";
    	for (int i = 0; i < headers.size(); i++) {
    		head+=headers.get(i);
			if(i!=headers.size()-1)head+=",";
		}
    	return head;
    }
    /**
     * generates a physical machine state report in csv log format
     * @return report collected
     * @throws SigarException 
     */
    public String generateStateReport() throws SigarException {  
    	Date date = new Date();
        java.sql.Timestamp timest = new java.sql.Timestamp(date.getTime());
    	String result =timest+","+date.getTime()+",";
        PhysicalMachine monitor = new PhysicalMachine();
        NetInterfaceStat NET = instance.sigar.getNetInterfaceStat(monitor.network.getNetworkInterface());
        Mem MEM = instance.sigar.getMem();
        CpuPerc cpuPercentage = instance.sigar.getCpuPerc();
        Cpu cpu = instance.sigar.getCpu();
    	for (int i = 0; i < headers.size(); i++) {
    		if(headers.get(i).equals(ItemCPUReport.CPU_COMBINED.name())){
    			result+=cpuPercentage.getCombined() * 100;
    		}else if(headers.get(i).equals(ItemCPUReport.CPU_IDLE.name())){
    			result+=cpuPercentage.getIdle() * 100;
    		}else if(headers.get(i).equals(ItemCPUReport.CPU_NICE.name())){
			    result+=cpuPercentage.getNice() * 100;
			}else if(headers.get(i).equals(ItemCPUReport.CPU_SYS.name())){
				result+=cpuPercentage.getSys() * 100;
			}else if(headers.get(i).equals(ItemCPUReport.CPU_USER.name())){
				result+=cpuPercentage.getUser() * 100;
			}else if(headers.get(i).equals(ItemCPUReport.CPU_WAIT.name())){
				result+=cpuPercentage.getWait() * 100;
			}else if(headers.get(i).equals(ItemCPUReport.HD_FREE.name())){
				result+=monitor.hardDisk.getHardDiskFreeSpace();
			}else if(headers.get(i).equals(ItemCPUReport.HD_USED.name())){
				result+=monitor.hardDisk.getHardDiskUsedSpace();
			}else if(headers.get(i).equals(ItemCPUReport.MEM_FREE.name())){
				result+=MEM.getFreePercent();
			}else if(headers.get(i).equals(ItemCPUReport.MEM_USED.name())){
				result+= MEM.getUsedPercent();
			}else if(headers.get(i).equals(ItemCPUReport.NET_RX_BYTES.name())){
				result+=NET.getRxBytes();
			}else if(headers.get(i).equals(ItemCPUReport.NET_RX_ERRORS.name())){
				result+=NET.getRxErrors();
			}else if(headers.get(i).equals(ItemCPUReport.NET_RX_PACKETS.name())){
				result+=NET.getRxPackets();
			}else if(headers.get(i).equals(ItemCPUReport.NET_SPEED.name())){
				result+=NET.getSpeed();
			}else if(headers.get(i).equals(ItemCPUReport.NET_TX_BYTES.name())){
				result+= NET.getTxBytes();
			}else if(headers.get(i).equals(ItemCPUReport.NET_TX_ERRORS.name())){
				result+= NET.getTxErrors();
			}else if(headers.get(i).equals(ItemCPUReport.NET_TX_PACKETS.name())){
				result+=NET.getTxPackets();
			}else if(headers.get(i).equals(ItemCPUReport.NO_CPU_IDLE.name())){
				result+=(100 - (cpuPercentage.getIdle() * 100));
			}else if(headers.get(i).equals(ItemCPUReport.PROCESSES.name())){
		        String processes = "";
		      //Otro head para los generales 1)
		      //TODO: procesos generales cuantos corren, cuantos son zombies
		        long[] pids = instance.sigar.getProcList();
		        for (long id : pids) {
		            try {
		            	//2)
		            	String[] processName = instance.sigar.getProcExe(id).getName().split("\\\\");
		                //processes += "(name:"+processName[processName.length - 1] + "; virtualMemorySize:"+instance.sigar.getProcMem(id).getSize()+"; residentMemorySize:"+instance.sigar.getProcMem(id).getResident()+"; cpuPercentage:"+instance.sigar.getProcCpu(id).getPercent()+")"+(id==pids[pids.length-1]?"":",");
		            	processes += "(name:"+processName[processName.length - 1] + 
		            			"; residentMemorySize:"+instance.sigar.getProcMem(id).getResident()+
		            			"; cpuPercentage:"+instance.sigar.getProcCpu(id).getPercent()+")"
		            			+(id==pids[pids.length-1]?"":",");
		            } catch (Exception ex) {
		            }//4)
		        }
		        result+=processes;
			}else if(headers.get(i).equals(ItemCPUReport.RAM_FREE.name())){
				result+=monitor.memory.getRAMMemoryFree();
			}else if(headers.get(i).equals(ItemCPUReport.RAM_USED.name())){
				result+=monitor.memory.getRAMMemoryUsed();
			}else if(headers.get(i).equals(ItemCPUReport.SWAP_FREE.name())){
				result+=monitor.memory.getSwapMemoryFree();
			}else if(headers.get(i).equals(ItemCPUReport.SWAP_PAGE_IN.name())){
				result+=monitor.memory.getSwapMemoryPageIn();
			}else if(headers.get(i).equals(ItemCPUReport.SWAP_PAGE_OUT.name())){
				result+=monitor.memory.getSwapMemoryPageOut();
			}else if(headers.get(i).equals(ItemCPUReport.SWAP_USED.name())){
				result+= monitor.memory.getSwapMemoryUsed();
			}else if(headers.get(i).equals(ItemCPUReport.TOTAL_IDLE.name())){
				result+=cpu.getIdle();
			}else if(headers.get(i).equals(ItemCPUReport.TOTAL_NICE.name())){
				result+=cpu.getNice();
			}else if(headers.get(i).equals(ItemCPUReport.TOTAL_SYS.name())){
				result+=cpu.getSys();
			}else if(headers.get(i).equals(ItemCPUReport.TOTAL_USER.name())){
				result+=cpu.getUser();
			}else if(headers.get(i).equals(ItemCPUReport.TOTAL_WAIT.name())){
				result+=cpu.getWait();
			}else if(headers.get(i).equals(ItemCPUReport.UP_TIME.name())){
				Uptime UPTIME = instance.sigar.getUptime();
				result+=UPTIME.getUptime();
			}else if(headers.get(i).equals(ItemCPUReport.USERNAME.name())){
				result+=OperatingSystem.getUserName();
			}
    		if(i!=headers.size()-1)result+=",";
    	}
        return result;
    }
    

    @Override
    public void output(String[] arg0) throws SigarException {
        // TODO Auto-generated method stub
    }
}
