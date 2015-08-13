package physicalmachine;

import static com.losandes.utils.Constants.*;

import com.losandes.utils.OperatingSystem;

/**
 * Responsible for executing the physical machine state operations
 */
public class PhysicalMachine {

    public CPU cpu;
    public Memory memory;
    public HardDisk hardDisk;
    public Network network;
    public OperatingSystem operatingSystem;


    /**
     * Constructor method
     */
    public PhysicalMachine() {
        operatingSystem = new OperatingSystem();
        cpu = new CPU();
        memory = new Memory();
        hardDisk = new HardDisk();
        network = new Network();
        network.getNetworkInfo(operatingSystem.getOperatingSystemName());

    }

    /**
     * Responsible for getting the physical machine monitor variables
     * @return
     */
    public String monitorPhysicalMachine() {
        String monitor =
                //CPU
                cpu.getCPUModel() + MESSAGE_SEPARATOR_TOKEN
                + cpu.getCPUVendor() + MESSAGE_SEPARATOR_TOKEN
                + cpu.getCPUCores() + MESSAGE_SEPARATOR_TOKEN
                + cpu.getCPUMhz() + MESSAGE_SEPARATOR_TOKEN
                + cpu.getCPUidle()+ MESSAGE_SEPARATOR_TOKEN
                + cpu.getCPUUsed()+ MESSAGE_SEPARATOR_TOKEN
                //MEMORY
                + memory.getRAMMemorySize() + MESSAGE_SEPARATOR_TOKEN
                + memory.getSwapMemorySize() + MESSAGE_SEPARATOR_TOKEN
                + memory.getRAMMemoryFree() + MESSAGE_SEPARATOR_TOKEN
                + memory.getRAMMemoryUsed() + MESSAGE_SEPARATOR_TOKEN
                //HARD DISK
                + hardDisk.getHardDiskSpace() + MESSAGE_SEPARATOR_TOKEN
                + hardDisk.getHardDiskFileSystem() + MESSAGE_SEPARATOR_TOKEN
                + hardDisk.getHardDiskFreeSpace() + MESSAGE_SEPARATOR_TOKEN
                + hardDisk.getHardDiskUsedSpace() + MESSAGE_SEPARATOR_TOKEN
                //NETWORK
                + network.getNetworkHostname() + MESSAGE_SEPARATOR_TOKEN
                + network.getNetworkIPAddress() + MESSAGE_SEPARATOR_TOKEN
                + network.getNetworkMACAddress() + MESSAGE_SEPARATOR_TOKEN
                + network.getNetworkInterface() + MESSAGE_SEPARATOR_TOKEN
                + network.getNetworkNetmask()+ MESSAGE_SEPARATOR_TOKEN
                + network.getNetworkGateway() + MESSAGE_SEPARATOR_TOKEN
                //OPERATING SYSTEM
                + operatingSystem.getOperatingSystemName() + MESSAGE_SEPARATOR_TOKEN
                + operatingSystem.getOperatingSystemVersion() + MESSAGE_SEPARATOR_TOKEN
                + operatingSystem.getOperatingSystemArchitect() + MESSAGE_SEPARATOR_TOKEN
                + OperatingSystem.getUserName() + MESSAGE_SEPARATOR_TOKEN;        
        return monitor;
    }

}//end of PhysicalMachine

