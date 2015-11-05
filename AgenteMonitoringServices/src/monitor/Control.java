package monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Properties;

import monitoring.MonitoringController;
import monitoring.monitors.OpenHardwareMonitor;
import monitoring.monitors.PerfmonMonitor;
import monitoring.monitors.PowerGadgetMonitor;
import monitoring.monitors.SigarMonitor;
import server.MonitoringCommunication;
import utils.MonitoringToolEnum;

public class Control {
	public static final String PORT = "COMMUNICATIONS_PORT";
	public int port;

	public static MonitoringController controller;

	public static void main(String[] args) {

		try {
			new Control();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Control() {
		loadPort();
		config();
		prepareServices();
		compatibilityFileTransform(controller.getPickPath());
	}

	public void prepareServices(){
		final ConfigurationServices config = new ConfigurationServices();
		controller = new MonitoringController(config.controllerConfig);		
		try {
			controller.addMonitoringTool(new SigarMonitor(MonitoringToolEnum.SIGAR.getName(), config.sigarConfig));
			controller.addMonitoringTool(new PowerGadgetMonitor(MonitoringToolEnum.POWER_GADGET.getName(), config.powerConfig));
			controller.addMonitoringTool(new OpenHardwareMonitor(MonitoringToolEnum.OPEN_HARDWARE.getName(), config.ohConfig));
			controller.addMonitoringTool(new PerfmonMonitor(MonitoringToolEnum.PERFMON.getName(), config.perfomConfig));			
			MonitoringCommunication com = new MonitoringCommunication(port, controller);			
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


	public static void compatibilityFileTransform(File pickPath) {
		FilenameFilter ff = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("PICK_UP");
			}
		};
		for (File file : pickPath.listFiles(ff)) {
			String newName = file.getName();
			String[] tmp;
			newName = newName.replace("PICK_UP", "PICK");
			if(newName.contains("open_hardware")) {
				newName = newName.replace("open_hardware", "openHardware");
				tmp = newName.split("_");
				tmp[3] = tmp[3].substring(1, tmp[3].length());
				tmp[3] += "-00-00-00-000";
				newName = tmp[0];
				for (int i = 1; i < tmp.length; i++) {
					newName += "_" + tmp[i];
				}
			} else {
				newName = newName.replace("__", "_");
				if(newName.contains("power_gadget")) {
					newName = newName.replace("power_gadget", "powerGadget");
				}
				if(newName.contains("perfmon")) {
					newName = newName.replaceFirst("_\\d\\d\\d\\d\\d\\d_", "_");
				}
			}
			System.out.println(newName);
			file.renameTo(new File(pickPath + File.separator + newName));
		}

	}
	
	private void loadPort() {
		port = 720;
		
		Properties prop = new Properties();
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(new File("config.properties"));
			prop.load(inputStream);
			
			port = Integer.parseInt(prop.getProperty(PORT));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
