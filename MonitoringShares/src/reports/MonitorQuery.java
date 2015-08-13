package reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;



import connection.MongoConnection;
import connection.MonitorDatabaseConnection;
import enums.ItemCPUMetrics;
import enums.ItemCPUReport;
import enums.ItemEnergyReport;
import utils.RefactorUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

public class MonitorQuery {
	
	private MonitorDatabaseConnection connection;
	
	public MonitorQuery(MonitorDatabaseConnection c) {
		this.connection = c;		
	}
	
	@SuppressWarnings("deprecation")
	public List<MonitorEnergyReport> getEnergyReportsByDate(Date start, Date end, String pm){
		List<MonitorEnergyReport> reports = new ArrayList<MonitorEnergyReport>();
		MongoConnection m = null;
		try {
			m = connection.generateConnection();		
			BasicDBObject orQuery = new BasicDBObject();
		    List<BasicDBObject> objects = new ArrayList<BasicDBObject>();
			BasicDBObject query = new BasicDBObject(ItemCPUReport.HOSTNAME.title(), pm.toLowerCase()).append("_id", new BasicDBObject("$gte",new ObjectId(start)).append("$lte", new ObjectId(end)));
			BasicDBObject query2 = new BasicDBObject(ItemCPUReport.HOSTNAME.title(), pm.toUpperCase()).append("_id", new BasicDBObject("$gte",new ObjectId(start)).append("$lte", new ObjectId(end)));
			objects.add(query2);objects.add(query);
			orQuery.put("$or", objects);
			DBCursor cursor = m.energyCollection().find(orQuery);
			try {
				while(cursor.hasNext()) {
					BasicDBObject obj = (BasicDBObject) cursor.next();
					reports.add(parseToEnergyReport(obj,new Date(obj.getObjectId("_id").getTime())));
				}
			} finally {
				 cursor.close();
			}
			m.close();
		} catch (Exception e) {
			if(m!=null)m.close();
		}
		return reports;
	}
	
	public List<MonitorReport> getCpuReportsByDate(Date start, Date end, String pm){
		List<MonitorReport> reports = new ArrayList<MonitorReport>();
		MongoConnection m = null;
		try {
			m = connection.generateConnection();
			BasicDBObject orQuery = new BasicDBObject();
		    List<BasicDBObject> objects = new ArrayList<BasicDBObject>();
			BasicDBObject query = new BasicDBObject(ItemCPUReport.HOSTNAME.title(), pm.toLowerCase()).append("_id", new BasicDBObject("$gte",new ObjectId(start)).append("$lte", new ObjectId(end)));
			BasicDBObject query2 = new BasicDBObject(ItemCPUReport.HOSTNAME.title(), pm.toUpperCase()).append("_id", new BasicDBObject("$gte",new ObjectId(start)).append("$lte", new ObjectId(end)));
			objects.add(query2);objects.add(query);
			orQuery.put("$or", objects);
			DBCursor cursor = m.cpuCollection().find(orQuery);
			try {
				while(cursor.hasNext()) {
					BasicDBObject obj = (BasicDBObject) cursor.next();
					reports.add(parseToCpuReport(obj));
				}
			} finally {
				 cursor.close();
			}
			m.close();
		} catch (Exception e) {
			if(m!=null)m.close();
		}
		return reports;
	}
	public MonitorInitialReport getCPUMetrics(String host){
		MongoConnection m = null;
		MonitorInitialReport mi = null;
		try {
			m = connection.generateConnection();
			BasicDBObject orQuery = new BasicDBObject();
		    List<BasicDBObject> objects = new ArrayList<BasicDBObject>();
			BasicDBObject query = new BasicDBObject(ItemCPUMetrics.HOSTNAME.title(), host.toLowerCase());
			BasicDBObject query2 = new BasicDBObject(ItemCPUMetrics.HOSTNAME.title(), host.toUpperCase());
			objects.add(query2);objects.add(query);
			orQuery.put("$or", objects);
			DBCursor cursor = m.infrastructureCollection().find(orQuery).sort(new BasicDBObject("_id",-1)).limit(1);
			if(cursor.hasNext()){
				BasicDBObject obj = (BasicDBObject) cursor.next();
				mi = parseToInitialReport(obj);
			}			
			m.close();
		} catch (Exception e) {
			if(m!=null)m.close();			
		}	
		return mi;
	}
	
	private MonitorInitialReport parseToInitialReport(BasicDBObject obj){
		MonitorInitialReport mon = new MonitorInitialReport();
		mon.setCoresPerSocket(obj.getInt(ItemCPUMetrics.CORES_X_SOCKETS.title()));
		mon.setcPUCores(obj.getInt(ItemCPUMetrics.CPU_CORES.title()));
		mon.setcPUMhz(obj.getString(ItemCPUMetrics.CPU_MHZ.title()));
		mon.setcPUModel(obj.getString(ItemCPUMetrics.CPU_MODEL.title()));
		mon.setcPUVendor(obj.getString(ItemCPUMetrics.CPU_VENDOR.title()));
		mon.setHardDiskFileSystem(obj.getString(ItemCPUMetrics.HD_FILESYSTEM.title()));
		mon.setHardDiskSpace(obj.getLong(ItemCPUMetrics.HD_SPACE.title()));
		mon.setHostname(obj.getString(ItemCPUMetrics.HOSTNAME.title()));
		mon.setNetworkMACAddress(obj.getString(ItemCPUMetrics.MAC.title()));
		mon.setOperatingSystemArchitect(obj.getString(ItemCPUMetrics.OS_ARQUITECTURE.title()));
		mon.setOperatingSystemName(obj.getString(ItemCPUMetrics.OS_NAME.title()));
		mon.setOperatingSystemVersion(obj.getString(ItemCPUMetrics.OS_VERSION.title()));
		mon.setrAMMemorySize(obj.getDouble(ItemCPUMetrics.RAM_SIZE.title()));
		mon.setSwapMemorySize(obj.getDouble(ItemCPUMetrics.SWAP_SIZE.title()));
		mon.setTimeLong(obj.getLong(ItemCPUMetrics.TIME_MILLI.title()));
		mon.setTimestString(obj.getString(ItemCPUMetrics.TIME.title()));
		mon.setTotalSockets(obj.getInt(ItemCPUMetrics.CPU_SOCKETS.title()));
		try {
			 mon.setMflops(obj.getDouble(ItemCPUMetrics.MFLOPS.title()));
			 mon.setTimeinSecs(obj.getDouble(ItemCPUMetrics.CPU_SECONDS.title()));
		} catch (Exception e) {
			 mon.setMflops(0);
			 mon.setTimeinSecs(0);
		}	
		try {
			mon.setNetworkGateway(obj.getString(ItemCPUMetrics.NET_GATEWAY.title()));
			mon.setNetworkInterface(obj.getString(ItemCPUMetrics.NET_INTERFACE.title()));
			mon.setNetworkIPAddress(obj.getString(ItemCPUMetrics.NET_IP.title()));
			mon.setNetworkNetmask(obj.getString(ItemCPUMetrics.NET_MASK.title()));
		} catch (Exception e) {
			mon.setNetworkGateway(null);
			mon.setNetworkInterface(null);
			mon.setNetworkIPAddress(null);
			mon.setNetworkNetmask(null);
		}
		return mon;
	}
	
	private MonitorEnergyReport parseToEnergyReport(BasicDBObject obj, Date objectId){
		MonitorEnergyReport mon = new MonitorEnergyReport();
		mon.setCPUFrequency(obj.getString(ItemEnergyReport.CPU_FRECUENCY.title()));
		mon.setCumulativeIA(obj.getString(ItemEnergyReport.IA.title()));
		mon.setCumulativeIAEnergy(obj.getString(ItemEnergyReport.IA_ENERGY.title()));
		try {
			if(obj.getString(ItemEnergyReport.ENERGY_JOULES.title())!=null)
			mon.setCumulativeProcessorEnergyJoules(obj.getString(ItemEnergyReport.ENERGY_JOULES.title()));
			else mon.setCumulativeProcessorEnergyJoules(obj.getString(ItemEnergyReport.ENERGY_JOULES.title()+" "));
			if(obj.getString(ItemEnergyReport.ENERGY_MHZ.title())!=null)
			mon.setCumulativeProcessorEnergyMhz(obj.getString(ItemEnergyReport.ENERGY_MHZ.title()));
			else mon.setCumulativeProcessorEnergyMhz(obj.getString(ItemEnergyReport.ENERGY_MHZ.title()+" "));
		} finally {	}		
		mon.setElapsedTime(obj.getString(ItemEnergyReport.ELAPSED_TIME.title()));
		mon.setHostName(obj.getString(ItemEnergyReport.HOSTNAME.title()));
		mon.setIAPower(obj.getString(ItemEnergyReport.IA_POWER.title()));
		mon.setPackageHot(obj.getString(ItemEnergyReport.PACK_HOT.title()));
		mon.setPackagePowerLimit(obj.getString(ItemEnergyReport.PACK_POWER.title()));
		mon.setPackageTemperature(obj.getString(ItemEnergyReport.PACK_TEMP.title()));
		mon.setProcessorPower(obj.getString(ItemEnergyReport.PROCESSOR_POWER.title()));
		mon.setRDTSC(obj.getString(ItemEnergyReport.RDTSC.title()));
		try {
			Long l = obj.getLong(ItemEnergyReport.REGISTER_DATE.title());
			mon.setRegisterDate(new Date(l));
		} catch (Exception e) {	
			mon.setRegisterDate(objectId);
		}		
		mon.setTime(obj.getString(ItemEnergyReport.TIME.title()));
		return mon;
	}
	private MonitorReport parseToCpuReport(BasicDBObject obj){
		MonitorReport mon = new MonitorReport();
		mon.setCpuCombined(obj.getDouble(ItemCPUReport.CPU_COMBINED.title()));
		mon.setCpuUser(obj.getDouble(ItemCPUReport.CPU_USER.title()));
		try {//Some data has an space at the end of NoCpuIdle
			mon.setNoCpuIdle(obj.getDouble(ItemCPUReport.NO_CPU_IDLE.title()));
		} catch (Exception e) {
			mon.setNoCpuIdle(obj.getDouble(ItemCPUReport.NO_CPU_IDLE.title()+" "));
		}		
		mon.setMemFreePercent(obj.getDouble(ItemCPUReport.MEM_FREE.title()));
		mon.setHardDiskFreeSpace(obj.getLong(ItemCPUReport.HD_FREE.title()));
		mon.setHardDiskUsedSpace(obj.getLong(ItemCPUReport.HD_USED.title()));
		mon.setHostName(obj.getString(ItemCPUReport.HOSTNAME.title()));
		mon.setCpuIdle(obj.getDouble(ItemCPUReport.CPU_IDLE.title()));
		mon.setTotalCpuIdleTime(obj.getLong(ItemCPUReport.TOTAL_IDLE.title()));
		mon.setCpuNice(obj.getDouble(ItemCPUReport.CPU_NICE.title()));
		mon.setTotalCpuNiceTime(obj.getLong(ItemCPUReport.TOTAL_NICE.title()));
		mon.setRamMemoryFree(obj.getDouble(ItemCPUReport.RAM_FREE.title()));
		mon.setRamMemoryUsed(obj.getDouble(ItemCPUReport.RAM_USED.title()));
		mon.setProcesses(RefactorUtils.refactorString(obj.getString(ItemCPUReport.PROCESSES.title())));
		mon.setNetRxBytes(obj.getLong(ItemCPUReport.NET_RX_BYTES.title()));
		mon.setNetRxErrors(obj.getLong(ItemCPUReport.NET_RX_ERRORS.title()));
		mon.setNetRxPackets(obj.getLong(ItemCPUReport.NET_RX_PACKETS.title()));
		mon.setNetSpeed(obj.getLong(ItemCPUReport.NET_SPEED.title()));
		mon.setSwapMemoryFree(obj.getDouble(ItemCPUReport.SWAP_FREE.title()));
		mon.setSwapMemoryPageIn(obj.getDouble(ItemCPUReport.SWAP_PAGE_IN.title()));
		mon.setSwapMemoryPageOut(obj.getDouble(ItemCPUReport.SWAP_PAGE_OUT.title()));
		mon.setSwapMemoryUsed(obj.getDouble(ItemCPUReport.SWAP_USED.title()));
		mon.setCpuSys(obj.getDouble(ItemCPUReport.CPU_SYS.title()));
		mon.setTotalCpuSysTime(obj.getLong(ItemCPUReport.TOTAL_SYS.title()));
		mon.setTimeLong(obj.getLong(ItemCPUReport.TIME_MILLI.title()));
		mon.setTimestString(obj.getString(ItemCPUReport.TIME.title()));
		mon.setNetTxBytes(obj.getLong(ItemCPUReport.NET_TX_BYTES.title()));
		mon.setNetTxErrors(obj.getLong(ItemCPUReport.NET_TX_ERRORS.title()));
		mon.setNetTxPackets(obj.getLong(ItemCPUReport.NET_TX_PACKETS.title()));
		mon.setCpuUptime(obj.getDouble(ItemCPUReport.UP_TIME.title()));
		mon.setMemUsedPercent(obj.getDouble(ItemCPUReport.MEM_USED.title()));
		mon.setTotalCpuUserTime(obj.getLong(ItemCPUReport.TOTAL_USER.title()));
		mon.setUserName(obj.getString(ItemCPUReport.USERNAME.title()));
		mon.setCpuWait(obj.getDouble(ItemCPUReport.CPU_WAIT.title()));
		mon.setTotalCpuWaitTime(obj.getLong(ItemCPUReport.TOTAL_WAIT.title()));
		return mon;
	}

}
