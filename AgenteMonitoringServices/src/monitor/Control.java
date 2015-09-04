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
	
	public static MonitoringController controller;

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
		final ConfigurationServices config = new ConfigurationServices();
		controller = new MonitoringController(config.controllerConfig);		
		try {
			controller.addMonitoringTool(new SigarMonitor(MonitoringToolEnum.SIGAR.getName(), config.sigarConfig));
			controller.addMonitoringTool(new PowerGadgetMonitor(MonitoringToolEnum.POWER_GADGET.getName(), config.powerConfig));
			controller.addMonitoringTool(new OpenHardwareMonitor(MonitoringToolEnum.OPEN_HARDWARE.getName(), config.ohConfig));
			controller.addMonitoringTool(new PerfmonMonitor(MonitoringToolEnum.PERFMON.getName(), config.perfomConfig));			
			MonitoringCommunication com = new MonitoringCommunication(PORT, controller);			
			controller.configureServices();//TODO join 		
			com.start();
			if(config.end>0){
				new Thread(){					
					public void sleepUntilInit(Date init, Date current){
						try {
							System.out.println("I will sleep "+(init.getTime()-current.getTime()));
							Thread.sleep(init.getTime()-current.getTime());	
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					@Override
					public void run() {
						while(true){
							Date d = new Date();							
							Date end = new Date(); end.setHours(config.end-1);end.setMinutes(55);end.setSeconds(0);
							Date init = new Date(); init.setHours(config.init);init.setMinutes(0);init.setSeconds(0);
							if(d.before(end)){
								System.out.println(d+" before end");
								if(d.before(init)){
									System.out.println("before init");
									sleepUntilInit(init, d);
								}
								controller.prepareAllServices();
								controller.startServices();	
								try {
									System.out.println("I will sleep "+(end.getTime()-d.getTime()));
									Thread.sleep(end.getTime()-d.getTime());		
									controller.stopServices(new String[]{
											MonitoringToolEnum.OPEN_HARDWARE.getName(),
											MonitoringToolEnum.PERFMON.getName(),
											MonitoringToolEnum.POWER_GADGET.getName(),
											MonitoringToolEnum.SIGAR.getName()});
									Thread.sleep(1000*60);
								} catch (InterruptedException e) {
									e.printStackTrace();
									System.exit(0);
								}
							}else{								
								init.setTime(init.getTime()+(1000*60*60*24));
								sleepUntilInit(init, d);
							}
						}
					}
				}.start();
			}else{
				controller.prepareAllServices();
				controller.startServices();	
			}
		} catch (Exception e) {
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
	
	//TODO
	public static void compatibilityFileTransform() {
		
	}

}
