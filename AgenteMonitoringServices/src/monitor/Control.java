package monitor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

import server.MonitoringCommunication;
import utils.MonitoringToolEnum;
import monitoring.MonitoringController;
import monitoring.monitors.*;

public class Control {
	public static final int PORT = 720;
	
	public MonitoringController controller;

	public static void main(String[] args) {
		try {
			new Control();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	 
	public Control() {
		config();
		prepareServices();
	}
	
	public void prepareServices(){
		ConfigurationServices config = new ConfigurationServices();
		controller = new MonitoringController(config.controllerConfig);		
		try {
			controller.addMonitoringTool(new SigarMonitor(MonitoringToolEnum.SIGAR.getName(), config.sigarConfig));
			controller.addMonitoringTool(new PowerGadgetMonitor(MonitoringToolEnum.POWER_GADGET.getName(), config.powerConfig));
			controller.addMonitoringTool(new OpenHardwareMonitor(MonitoringToolEnum.OPEN_HARDWARE.getName(), config.ohConfig));
			controller.addMonitoringTool(new PerfmonMonitor(MonitoringToolEnum.PERFMON.getName(), config.perfomConfig));			
			MonitoringCommunication com = new MonitoringCommunication(PORT, controller);			
			controller.configureServices();
			controller.startServices();
			com.start();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void config(){
		try {
    		//Create agent log file
        	PrintStream ps=new PrintStream(new FileOutputStream("log.txt",true),true){
        		@Override
        		public void println(String x) {
        			super.println(new Date()+" "+x);
        		}
        		@Override
        		public void println(Object x) {
        			super.println(new Date()+" "+x);
        		}
        	};
			System.setOut(ps);
			System.setErr(ps);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}    	
	}

}
