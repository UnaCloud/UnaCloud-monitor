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
import org.hyperic.sigar.ProcStat;
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
		if(headersInitial.contains(ItemCPUMetrics.MFLOPS.title())||headersInitial.contains(ItemCPUMetrics.CPU_SECONDS.title())){
			CPUMflops = new monitoring.sigar.linpackJava.Linpack();
			CPUMflops.run_benchmark();
		}    	
		PhysicalMachine monitor = new PhysicalMachine();
		CpuInfo[] infos = this.sigar.getCpuInfoList();
		for (int i = 0; i < headersInitial.size(); i++) {
			if(headersInitial.get(i).equals(ItemCPUMetrics.CORES_X_SOCKETS.title())){
				org.hyperic.sigar.CpuInfo CPU1 = infos[0];
				result+=CPU1.getCoresPerSocket();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.CPU_CORES.title())){
				result+= monitor.cpu.getCPUCores();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.CPU_MHZ.title())){
				RepetitionCounter cpuMhz = new RepetitionCounter();
				for (CpuInfo cpu : infos)cpuMhz.add("" + cpu.getMhz());
				result+=cpuMhz.toString();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.CPU_MODEL.title())){
				RepetitionCounter cpuModel = new RepetitionCounter();
				for (CpuInfo cpu : infos)cpuModel.add(cpu.getModel());
				result+= cpuModel.toString();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.CPU_SECONDS.title())){
				result+=CPUMflops.getTimeinSecs();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.CPU_SOCKETS.title())){
				org.hyperic.sigar.CpuInfo CPU1 = infos[0];
				result+=CPU1.getTotalSockets();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.CPU_VENDOR.title())){
				RepetitionCounter cpuVendor = new RepetitionCounter();
				for (CpuInfo cpu : infos)cpuVendor.add(cpu.getVendor());
				result+=cpuVendor.toString();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.HD_FILESYSTEM.title())){
				result+=monitor.hardDisk.getHardDiskFileSystem();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.HD_SPACE.title())){
				result+=monitor.hardDisk.getHardDiskSpace();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.MAC.title())){
				result+= monitor.network.getNetworkMACAddress();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.MFLOPS.title())){
				result+=CPUMflops.getMflops();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.NET_GATEWAY.title())){
				result+=monitor.network.getNetworkGateway();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.NET_INTERFACE.title())){
				result+=monitor.network.getNetworkInterface();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.NET_IP.title())){
				result+=monitor.network.getNetworkIPAddress();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.NET_MASK.title())){
				result+=monitor.network.getNetworkNetmask();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.OS_ARQUITECTURE.title())){
				result+=monitor.operatingSystem.getOperatingSystemArchitect();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.OS_NAME.title())){
				result+=monitor.operatingSystem.getOperatingSystemName();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.OS_VERSION.title())){
				result+=monitor.operatingSystem.getOperatingSystemVersion();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.RAM_SIZE.title())){
				result+=monitor.memory.getRAMMemorySize();
			}else if(headersInitial.get(i).equals(ItemCPUMetrics.SWAP_SIZE.title())){
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
		for(int i = 0; i < headers.size(); i++) {

			if(headers.get(i).equals(ItemCPUReport.PROCESSES_GENERAL.title()))
				head += ":#MULTI#:Idle#:Running#:Sleeping#:Stopped#:Zombie#:"+ItemCPUReport.PROCESSES_GENERAL.title();
			else if(headers.get(i).equals(ItemCPUReport.PROCESSES_DETAIL.title()))
				head += ":#MULTI#:ExeName#:User#:ResidentMemory#:Priority#:Processor#:State#:Threads#:CpuPercent#:StartTime#:"+ItemCPUReport.PROCESSES_DETAIL.title();
			else
				head += headers.get(i);

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
			if(headers.get(i).equals(ItemCPUReport.CPU_COMBINED.title())){
				result+=cpuPercentage.getCombined() * 100;
			}else if(headers.get(i).equals(ItemCPUReport.CPU_IDLE.title())){
				result+=cpuPercentage.getIdle() * 100;
			}else if(headers.get(i).equals(ItemCPUReport.CPU_NICE.title())){
				result+=cpuPercentage.getNice() * 100;
			}else if(headers.get(i).equals(ItemCPUReport.CPU_SYS.title())){
				result+=cpuPercentage.getSys() * 100;
			}else if(headers.get(i).equals(ItemCPUReport.CPU_USER.title())){
				result+=cpuPercentage.getUser() * 100;
			}else if(headers.get(i).equals(ItemCPUReport.CPU_WAIT.title())){
				result+=cpuPercentage.getWait() * 100;
			}else if(headers.get(i).equals(ItemCPUReport.HD_FREE.title())){
				result+=monitor.hardDisk.getHardDiskFreeSpace();
			}else if(headers.get(i).equals(ItemCPUReport.HD_USED.title())){
				result+=monitor.hardDisk.getHardDiskUsedSpace();
			}else if(headers.get(i).equals(ItemCPUReport.MEM_FREE.title())){
				result+=MEM.getFreePercent();
			}else if(headers.get(i).equals(ItemCPUReport.MEM_USED.title())){
				result+= MEM.getUsedPercent();
			}else if(headers.get(i).equals(ItemCPUReport.NET_RX_BYTES.title())){
				result+=NET.getRxBytes();
			}else if(headers.get(i).equals(ItemCPUReport.NET_RX_ERRORS.title())){
				result+=NET.getRxErrors();
			}else if(headers.get(i).equals(ItemCPUReport.NET_RX_PACKETS.title())){
				result+=NET.getRxPackets();
			}else if(headers.get(i).equals(ItemCPUReport.NET_SPEED.title())){
				result+=NET.getSpeed();
			}else if(headers.get(i).equals(ItemCPUReport.NET_TX_BYTES.title())){
				result+= NET.getTxBytes();
			}else if(headers.get(i).equals(ItemCPUReport.NET_TX_ERRORS.title())){
				result+= NET.getTxErrors();
			}else if(headers.get(i).equals(ItemCPUReport.NET_TX_PACKETS.title())){
				result+=NET.getTxPackets();
			}else if(headers.get(i).equals(ItemCPUReport.NO_CPU_IDLE.title())){
				result+=(100 - (cpuPercentage.getIdle() * 100));
			}else if(headers.get(i).equals(ItemCPUReport.PROCESSES_GENERAL.title())){
				ProcStat stat = instance.sigar.getProcStat();
				result += stat.getIdle() + ":" + stat.getRunning() + ":" + stat.getSleeping() + ":" + stat.getStopped() + ":" + stat.getZombie();
			}else if(headers.get(i).equals(ItemCPUReport.PROCESSES_DETAIL.title())){
				String processes = "";
				long[] pids = instance.sigar.getProcList();
				for (long id : pids) {
					try {
						//processes += "(name:"+processName[processName.length - 1] + "; virtualMemorySize:"+instance.sigar.getProcMem(id).getSize()+"; residentMemorySize:"+instance.sigar.getProcMem(id).getResident()+"; cpuPercentage:"+instance.sigar.getProcCpu(id).getPercent()+")"+(id==pids[pids.length-1]?"":",");
						processes += getProcessData(id, id==pids[pids.length-1]);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				result+=processes;
			}else if(headers.get(i).equals(ItemCPUReport.RAM_FREE.title())){
				result+=monitor.memory.getRAMMemoryFree();
			}else if(headers.get(i).equals(ItemCPUReport.RAM_USED.title())){
				result+=monitor.memory.getRAMMemoryUsed();
			}else if(headers.get(i).equals(ItemCPUReport.SWAP_FREE.title())){
				result+=monitor.memory.getSwapMemoryFree();
			}else if(headers.get(i).equals(ItemCPUReport.SWAP_PAGE_IN.title())){
				result+=monitor.memory.getSwapMemoryPageIn();
			}else if(headers.get(i).equals(ItemCPUReport.SWAP_PAGE_OUT.title())){
				result+=monitor.memory.getSwapMemoryPageOut();
			}else if(headers.get(i).equals(ItemCPUReport.SWAP_USED.title())){
				result+= monitor.memory.getSwapMemoryUsed();
			}else if(headers.get(i).equals(ItemCPUReport.TOTAL_IDLE.title())){
				result+=cpu.getIdle();
			}else if(headers.get(i).equals(ItemCPUReport.TOTAL_NICE.title())){
				result+=cpu.getNice();
			}else if(headers.get(i).equals(ItemCPUReport.TOTAL_SYS.title())){
				result+=cpu.getSys();
			}else if(headers.get(i).equals(ItemCPUReport.TOTAL_USER.title())){
				result+=cpu.getUser();
			}else if(headers.get(i).equals(ItemCPUReport.TOTAL_WAIT.title())){
				result+=cpu.getWait();
			}else if(headers.get(i).equals(ItemCPUReport.UP_TIME.title())){
				Uptime UPTIME = instance.sigar.getUptime();
				result+=UPTIME.getUptime();
			}else if(headers.get(i).equals(ItemCPUReport.USERNAME.title())){
				result+=OperatingSystem.getUserName();
			}
			if(i!=headers.size()-1)result+=",";
		}
		return result;
	}

	private String getProcessData(long id, boolean last) {
		String data = "";

		double cpuPerc;
		long residentMemory;
		String user;
		String exe;

		try {
			cpuPerc = instance.sigar.getProcCpu(id).getPercent();
			residentMemory = instance.sigar.getProcMem(id).getResident(); 
		} catch (Exception e) {
			return data;
		}

		try {
			user = instance.sigar.getProcCredName(id).getUser();
		} catch(Exception e) {
			user = "none";
		}

		try {
			exe = instance.sigar.getProcExe(id).getName();
			String[] temp = instance.sigar.getProcExe(id).getName().split("\\\\");
			exe = temp[temp.length-1];
		} catch(Exception e) {
			exe = "none";
		}

		//The process is owned by human user and no exceptions were thrown OR ((it is owned by SYSTEM OR an exception was thrown) AND the process has an impact in either memory or cpu)
		if(!(user.equals("SYSTEM") || user.equals("None")) || cpuPerc > 1e-8 || residentMemory > 0) {
			try {
			data += exe +
					":"+user +
					":"+residentMemory +
					":"+instance.sigar.getProcState(id).getPriority() +
					":"+instance.sigar.getProcState(id).getProcessor() +
					":"+instance.sigar.getProcState(id).getState() +
					":"+instance.sigar.getProcState(id).getThreads() +
					":"+cpuPerc +
					":"+instance.sigar.getProcTime(id).getStartTime() +
					(last?"":";");  
			
			} catch(Exception e) {
				return "";
			}
		}


		return data;
	}

	@Override
	public void output(String[] arg0) throws SigarException {
		// TODO Auto-generated method stub
	}
}
