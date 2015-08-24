package monitoring.sigar;

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
import com.losandes.utils.MySystem;

import reports.MonitorInitialReport;
import reports.MonitorReport;

public class MonitorReportGenerator extends SigarCommandBase {
	
	private static MonitorReportGenerator instance;
	public static synchronized MonitorReportGenerator getInstance(){		
		if(instance==null)instance=new MonitorReportGenerator();
		return instance;
	}
    /**
     * Refactor. This method execute the initialReport method 
     * but if DLL is not load call loader and call again initialReport 
     * @return
     */
    public MonitorInitialReport getInitialReport(){
    	try {
			return generateInitialReport();
		} catch (SigarException e) {
			e.printStackTrace();
		}
    	return null;
    }
	/**
	 * Generates initial monitoring report info
	 * @return initial report
	 * @throws SigarException 
	 */
    private MonitorInitialReport generateInitialReport() throws SigarException {
    	//This code war removed from State Report because generate use percentage in computer.
    	monitoring.sigar.linpackJava.Linpack CPUMflops = new monitoring.sigar.linpackJava.Linpack();
        CPUMflops.run_benchmark();
        PhysicalMachine monitor = new PhysicalMachine();
        java.util.Date date;
        date = new Date();
        java.sql.Timestamp timest = new java.sql.Timestamp(date.getTime());
        // com.sun.security.auth.module.NTSystem NTSystem = new
        // com.sun.security.auth.module.NTSystem();
        // Evalua una sola CPU
        CpuInfo[] infos;
        infos = this.sigar.getCpuInfoList();
        RepetitionCounter cpuModel = new RepetitionCounter();
        RepetitionCounter cpuVendor = new RepetitionCounter();
        RepetitionCounter cpuMhz = new RepetitionCounter();

        for (CpuInfo cpu : infos) {
            cpuModel.add(cpu.getModel());
            cpuVendor.add(cpu.getVendor());
            cpuMhz.add("" + cpu.getMhz());

        }
        org.hyperic.sigar.CpuInfo CPU1 = infos[0];
        return new MonitorInitialReport(timest,date.getTime(),
                MySystem.getHostname().toUpperCase(), CPUMflops.getMflops(),
                CPUMflops.getTimeinSecs(),
                monitor.operatingSystem.getOperatingSystemName(),
                monitor.operatingSystem.getOperatingSystemVersion(),
                monitor.operatingSystem.getOperatingSystemArchitect(),
                cpuModel.toString(), cpuVendor.toString(),
                monitor.cpu.getCPUCores(), CPU1.getTotalSockets(),
                cpuMhz.toString(), CPU1.getCoresPerSocket(),
                monitor.memory.getRAMMemorySize(),
                monitor.memory.getSwapMemorySize(),
                monitor.hardDisk.getHardDiskSpace(),
                monitor.hardDisk.getHardDiskFileSystem(),
                monitor.network.getNetworkMACAddress(),
	            monitor.network.getNetworkIPAddress(),
	            monitor.network.getNetworkInterface(),
	            monitor.network.getNetworkNetmask(),
	            monitor.network.getNetworkGateway());
    }
    
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
    
    public MonitorReport getStateReport(){
    	try {
			return generateStateReport();
		} catch (SigarException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    
    /**
     * generates a physical machine state report
     * @return report collected
     * @throws SigarException 
     */
    private  MonitorReport generateStateReport() throws SigarException {        
        PhysicalMachine monitor = new PhysicalMachine();
        Date date = new Date();
        java.sql.Timestamp timest = new java.sql.Timestamp(date.getTime());

        String processes = "";
        long[] pids = instance.sigar.getProcList();
        for (long id : pids) {
            try {
            	String[] processName = instance.sigar.getProcExe(id).getName().split("\\\\");
                processes += "(name:"+processName[processName.length - 1] + "; virtualMemorySize:"+instance.sigar.getProcMem(id).getSize()+"; residentMemorySize:"+instance.sigar.getProcMem(id).getResident()+"; cpuPercentage:"+instance.sigar.getProcCpu(id).getPercent()+")"+(id==pids[pids.length-1]?"":",");
            } catch (Exception ex) {
            }
        }
        Cpu cpu = instance.sigar.getCpu();
        CpuPerc CPU2 = instance.sigar.getCpuPerc();
        Uptime UPTIME = instance.sigar.getUptime();
        Mem MEM = instance.sigar.getMem();
        NetInterfaceStat NET = instance.sigar
                .getNetInterfaceStat(monitor.network.getNetworkInterface());
        return new MonitorReport(timest,date.getTime(),  
        		OperatingSystem.getUserName(),MySystem.getHostname().toUpperCase(),
                UPTIME.getUptime(), CPU2.getIdle() * 100,
                (100 - (CPU2.getIdle() * 100)), CPU2.getUser() * 100,
                CPU2.getSys() * 100, CPU2.getNice() * 100,
                CPU2.getWait() * 100, CPU2.getCombined() * 100, cpu.getUser(),
                cpu.getSys(), cpu.getNice(), cpu.getWait(), cpu.getIdle(),
                monitor.memory.getRAMMemoryFree(),
                monitor.memory.getRAMMemoryUsed(), MEM.getFreePercent(),
                MEM.getUsedPercent(), monitor.memory.getSwapMemoryFree(),
                monitor.memory.getSwapMemoryPageIn(),
                monitor.memory.getSwapMemoryPageOut(),
                monitor.memory.getSwapMemoryUsed(),
                monitor.hardDisk.getHardDiskFreeSpace(),
                monitor.hardDisk.getHardDiskUsedSpace(), NET.getRxBytes(),
                NET.getTxBytes(), NET.getSpeed(), NET.getRxErrors(),
                NET.getTxErrors(), NET.getRxPackets(), NET.getTxPackets(), processes);
    
    }

    @Override
    public void output(String[] arg0) throws SigarException {
        // TODO Auto-generated method stub
    }
}
