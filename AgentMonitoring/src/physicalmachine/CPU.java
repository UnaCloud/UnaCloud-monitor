package physicalmachine;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.SigarException;
public class CPU extends SigarWrapper {
	/**
	 * number of processors
	 */
    private int cPUCores;
    /**
     * CPU usage percentage 
     */
    private CpuPerc cpuPer;
    /**
     * sigar CPU representation
     */
    private Cpu cpu;
    /**
     * CPU info 
     */
    private org.hyperic.sigar.CpuInfo[] infos;
    
    /**
     * Class constructor
     */
    public CPU() {
    }

    /**
     * calculates CPU usage percentage
     * @return calculated cpuPerc
     */
    private CpuPerc getCpuPer() {
        if(cpuPer==null)try {
            cpuPer = sigar.getCpuPerc();
        } catch (SigarException ex) {
        }
        return cpuPer;
    }
    
    /**
     * obtains CPU info
     * @return sigar cpuinfo list
     */
    private CpuInfo[] getInfos() {
        if(infos==null){
            try {
                infos = sigar.getCpuInfoList();
            } catch (SigarException ex) {
            }
        }
        return infos;
    }

    /**
     * Calculates idle CPU
     * @return the CPUidle
     */
    public String getCPUidle() {
        getCpuPer();
        double d = cpuPer.getIdle();
        return format.format(d);
    }

    /**
     * Obtains CPU vendor
     * @return the CPUVendor
     */
    public String getCPUVendor() {
        getInfos();
        String h=infos.length>0?infos[0].getVendor():"";
        for(int e=1;e<infos.length;e++)h+=";"+infos[e].getVendor();
        return h;
    }

    /**
     * Obtains CPU model
     * @return the CPUModel
     */
    public String getCPUModel() {
        getInfos();
        String h=infos.length>0?infos[0].getModel():"";
        for(int e=1;e<infos.length;e++)h+=";"+infos[e].getModel();
        return h;
    }

    /**
     * Obtains CPU mHz
     * @return the CPUMhz
     */
    public String getCPUMhz() {
        getInfos();
        String h=infos.length>0?""+infos[0].getMhz():"";
        for(int e=1;e<infos.length;e++)h+=";"+infos[e].getMhz();
        return h;
    }

    /**
     * gets cpu cores
     * @return the CPUCores
     */
    public int getCPUCores() {
        return cPUCores;
    }

    /**
     * calculates used CPU
     * @return the cPuUsed
     */
    public String getCPUUsed() {
        getCpuPer();
        double d = cpuPer.getIdle();
        return format.format(100-d);
    }
    
    /**
     * Obtains the total system cpu user time
     * @return cpu user time
     */
    public Long getCPUUserTime() {
        getCPUCores();
        return cpu.getUser();
    }
    
    /**
     * Obtains the total sytem cpu time
     * @return cpu total time
     */
    public Long getCPUTotalTime() {
        getCPUCores();
        return cpu.getTotal();
    }   
    
}//end of CPU

