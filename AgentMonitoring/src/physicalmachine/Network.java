package physicalmachine;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.cmd.Shell;
import org.hyperic.sigar.cmd.SigarCommandBase;

import com.losandes.utils.LocalProcessExecutor;

import static com.losandes.utils.Constants.*;

/**
 *  Responsible for obtaining network information
 *  Not completed for Mac operating system (networkInterface, networkNetmask, networkGateway)
 */
public class Network extends SigarCommandBase {
	
	/**
	 * network properties
	 */
    private String networkInterface;
    private String networkHostname;
    private String networkIPAddress;
    private String networkMACAddress;
    private String networkNetmask;
    private String networkGateway;

    public Network(Shell shell) {
        super(shell);
    }

    public Network() {
        super();
    }
    
    /**
     * obtains network interface and sets properties
     */
    public void output(String[] args) throws SigarException {
        
        InetAddress ip;
        String ip2=null;
        String interfaz=null;
		try {
			ip = InetAddress.getLocalHost();   
			//System.out.println("Current IP address : " + ip.getHostAddress());
                        //System.out.println("Current IP address : " + ip.getHostName());
                        ip2=ip.getHostAddress();
		} catch (UnknownHostException e) { 
			e.printStackTrace();
		}
        //System.out.println(this.sigar.getNetInterfaceList().length);
        String[] interfaces=this.sigar.getNetInterfaceList();
        int i=0;
        while (i<interfaces.length)
        {
            //System.out.println(interfaces[i].toString());
            NetInterfaceConfig config1 = this.sigar.getNetInterfaceConfig(interfaces[i].toString());
            //System.out.println(config1.getType());
            //System.out.println(config1.getName());
            //System.out.println(config1.getAddress());
            String ip1=config1.getAddress();
            if ( ip1.equals(ip2))
            {
                interfaz=interfaces[i].toString();
            }       
            //System.out.println(config1.getMetric());
            //System.out.println(config1.getMtu());
            //System.out.println("/////////////");
            i++;
        }
        
        NetInterfaceConfig config = this.sigar.getNetInterfaceConfig(interfaz);
        networkInterface = config.getName();
        networkIPAddress = config.getAddress();
        networkMACAddress = config.getHwaddr();
        networkNetmask = config.getNetmask();
        org.hyperic.sigar.NetInfo info = this.sigar.getNetInfo();
        networkHostname = info.getHostName();
        networkGateway = info.getDefaultGateway();
    }
    
    static String hostname=null;
    /**
     * Responsible for obtaining the hostname
     * @return
     */
    public static String getHostname() {
    	if(hostname!=null)return hostname;
    	hostname=LocalProcessExecutor.executeCommandOutput("hostname").trim();
    	return hostname;
    }

    /**
     * Responsible for getting IP address
     * @return
     */
    public String getIpAddress() {
        String result = "";
        try {
            java.net.InetAddress inetAdd = java.net.InetAddress.getByName(getHostname());
            result = inetAdd.getHostAddress();
        } catch (UnknownHostException ex) {
            result = ERROR_MESSAGE + "getting the IP address: " + ex.getMessage();
            System.err.println(result);
        }
        return result;
    }

    /**
     * Responsible for getting the ifconfig -a output
     * @return
     */
    public String getIpconfig() {
        return LocalProcessExecutor.executeCommandOutput("ifconfig","-a").trim();
    }

    /**
     * Responsible for getting the MAC address
     * @return
     */
    public String getMacAddress() {
        String result = ERROR_MESSAGE + "getting the MAC address";
        String[] aux = getIpconfig().split("\\n");
        for (int i = 0; i < aux.length; i++) {
            if (aux[i].toLowerCase().contains("ether")) {
                aux[i] = aux[i].replace("ether", "").trim();
                result = aux[i];
            }
        }
        return result;
    }

    /**
     * Responsible for setting all the network information
     */
    public void getNetworkInfo(String opeSys) {
        if (opeSys.toLowerCase().contains("mac")) {
            networkInterface = "inet";
            networkHostname = getHostname();
            networkIPAddress = getIpAddress();
            networkMACAddress = getMacAddress();
            networkNetmask = "255.255.255.0";
            networkGateway = "157.253.202.1";
        } else {
            try {
                output(new String[0]);
            } catch (SigarException ex) {
                System.err.println(ERROR_MESSAGE + "getting the networking information: " + ex.getMessage());
            }
        }
    }

    /**
     * @return the networkInterface
     */
    public String getNetworkInterface() {
        return networkInterface;
    }

    /**
     * @return the networkHostname
     */
    public String getNetworkHostname() {
        return networkHostname;
    }

    /**
     * @return the networkIPAddress
     */
    public String getNetworkIPAddress() {
        return networkIPAddress;
    }

    /**
     * @return the networkMACAddress
     */
    public String getNetworkMACAddress() {
        return networkMACAddress;
    }

    /**
     * @return the networkNetmask
     */
    public String getNetworkNetmask() {
        return networkNetmask;
    }

    /**
     * @return the networkGateway
     */
    public String getNetworkGateway() {
        return networkGateway;
    }
}//end of Network

