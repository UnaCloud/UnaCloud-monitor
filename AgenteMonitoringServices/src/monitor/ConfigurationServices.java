package monitor;

import java.io.InputStream;
import java.util.Properties;
import java.util.TreeMap;

import monitoring.configuration.ControllerConfiguration;
import monitoring.configuration.OpenHardwareConfigurationInterface;
import monitoring.configuration.PerfmonMonitorConfiguration;
import monitoring.configuration.PowerGadgetConfigurationInterface;
import monitoring.configuration.SigarConfigurationInterface;

public class ConfigurationServices {
	
	OpenHardwareConfigurationInterface ohConfig;
	SigarConfigurationInterface sigarConfig;
	PerfmonMonitorConfiguration perfomConfig;
	PowerGadgetConfigurationInterface powerConfig;
	ControllerConfiguration controllerConfig;
	static Properties prop;
	
	public ConfigurationServices() {
		try {
			prop = new Properties();
			String propFileName = "config.properties";
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			prop.load(inputStream);
			
			controllerConfig = new ControllerConfiguration() {
				
				@Override
				public void setMonitoringTime(int window) {	}
				
				@Override
				public TreeMap<String, Boolean> getStateSensors() {
					String servicesString = prop.getProperty("SERVICES");
					String[] services = servicesString.split(",");
					TreeMap<String, Boolean> tree = new TreeMap<String, Boolean>();
					for (String service : services) {
						String[] data = service.split(":");
						tree.put(data[0], data[1].toUpperCase().equals("TRUE")?true:false);
					}
					return tree;
				}
				
				@Override
				public int getMonitoringTime() {					
					return Integer.parseInt(prop.getProperty("MONITORING_TIME"));
				}
				
				@Override
				public void enableSensor(String service) {}
			};
			
			ohConfig =  new OpenHardwareConfigurationInterface() {
				
				@Override
				public void setFrecuency(int frecuency) {}
				
				@Override
				public String getRecordPath() {					
					return prop.getProperty("OH_PATH");
				}				
				@Override
				public int getFrecuency() {					
					return Integer.parseInt(prop.getProperty("FRECUENCY"));
				}				
				@Override
				public String getOpenHwProcess() {					
					return prop.getProperty("OH_PROCESS");
				}
			};
			
			sigarConfig = new SigarConfigurationInterface() {
				
				@Override
				public void setFrecuency(int frecuency) {}
				
				@Override
				public String getRecordPath() {					
					return prop.getProperty("SIGAR_RECORD_PATH");
				}
				
				@Override
				public int getFrecuency() {
					return Integer.parseInt(prop.getProperty("FRECUENCY"));
				}
				
				@Override
				public String getDllPath() {					
					return prop.getProperty("DLL_PATH");
				}
			};
			
			perfomConfig = new  PerfmonMonitorConfiguration() {
				
				@Override
				public void setFrecuency(int frecuency) {}
				
				@Override
				public String getRecordPath() {					
					return prop.getProperty("PM_RECORD_PATH");
				}
				
				@Override
				public int getFrecuency() {
					return Integer.parseInt(prop.getProperty("FRECUENCY"));
				}
				
				@Override
				public int getMaxFileSize() {					
					return Integer.parseInt(prop.getProperty("PM_MAX_SIZE"));
				}
				
				@Override
				public String[] getCounters() {
					String counters = prop.getProperty("COUNTERS");					
					return counters.split("\t");
				}
				
				@Override
				public String getCounterName() {
					return prop.getProperty("COUNTER_NAME");
				}
			};
			
			powerConfig = new PowerGadgetConfigurationInterface() {
				
				@Override
				public void setFrecuency(int frecuency) {	}
				
				@Override
				public String getRecordPath() {
					return prop.getProperty("PG_RECORD_PATH");
				}
				
				@Override
				public int getFrecuency() {
					return Integer.parseInt(prop.getProperty("FRECUENCY"));
				}
				
				@Override
				public String getPowerPath() {
					return prop.getProperty("PG_RECORD_PATH");
				}

				@Override
				public String getExeName() {					
					return prop.getProperty("PG_EXE_NAME");
				}
			};
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
