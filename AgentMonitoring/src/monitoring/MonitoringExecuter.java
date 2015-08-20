package monitoring;

import java.util.ArrayList;
import java.util.Date;

import monitoring.monitors.AbstractMonitor;

public class MonitoringExecuter extends Thread{

	ArrayList<AbstractMonitor> monitors = new ArrayList<AbstractMonitor>();
	@Override
	public void run() {	
		for (AbstractMonitor monitor : monitors) {
			try {
				monitor.doInitial();
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
	public void addMonitor(AbstractMonitor monitor){
		//TODO check that is not a equal monitoring added
		monitors.add(monitor);
	}
	public boolean isReady(){
		for (AbstractMonitor abstractMonitor : monitors) {
			if(abstractMonitor.isReady())return true;
		}
		return false;
	}

}
