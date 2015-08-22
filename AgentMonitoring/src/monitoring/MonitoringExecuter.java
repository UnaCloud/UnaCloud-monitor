package monitoring;

import java.util.ArrayList;
import java.util.Date;

import monitoring.monitors.AbstractMonitor;

/**
 * 
 * @author CesarF
 * Class to control execution of monitors. This executor runs all enabled services in a cycle.
 */
public class MonitoringExecuter extends Thread{

	/**
	 * List of AbstractMonitors to execute
	 */
	ArrayList<AbstractMonitor> monitors = new ArrayList<AbstractMonitor>();
	
	@Override
	public void run() {	
		for (AbstractMonitor monitor : monitors) {
			try {
				monitor.doInit();
			} catch (Exception e) {
				e.printStackTrace();
				monitor.toError();
			}
		}
		while(isReady()){
			try { 					
				System.out.println(new Date()+" Init monitor processes");
				ArrayList<Thread> processes = new ArrayList<Thread>();
				for (AbstractMonitor monitor : monitors)
					if(monitor.isReady())processes.add(new Thread(monitor));									
			    for (Thread thread : processes) thread.start();
				for (Thread thread : processes) thread.join();
				System.out.println(new Date()+" Finish monitor processes");
			} catch (Exception e) {
				e.printStackTrace();
				for (AbstractMonitor monitor : monitors)
					monitor.toError();					
			}				
		}	
		for (AbstractMonitor monitor : monitors)
			if(monitor.isStopped())monitor.toOff();
	}
	/**
	 * Add a unique abstracMonitor to executor. It method compare AbstractMonitor id´s to certify than a sensor running only one time.
	 * @param monitor to be added
	 * 
	 */
	public void addMonitor(AbstractMonitor monitor){
		for (AbstractMonitor abstractMonitor : monitors)
			if(monitor.getId().equals(abstractMonitor.getId()))return;		
		monitors.add(monitor);
	}
	/**
	 * 
	 * @return true in case there is at least one monitor ready to run.
	 */
	public boolean isReady(){
		for (AbstractMonitor abstractMonitor : monitors) {
			if(abstractMonitor.isReady())return true;
		}
		return false;
	}

}
