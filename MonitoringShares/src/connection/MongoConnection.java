package connection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class MongoConnection {
	private static final String ENERGY_COLLECTION = "EnergyMetrics";
	private static final String INFRASTRUCTURE_COLLECTION = "Infrastructure";
	private static final String CPU_COLLECTION = "CpuMetrics";
	
	private MongoClient client;
	private DB db;
	
	public MongoConnection(MongoClient mon, String database) {
		client = mon;
		db = client.getDB( database);
	}
	
	public DBCollection energyCollection(){
		return db.getCollection(ENERGY_COLLECTION);
	}
	public DBCollection infrastructureCollection(){
		return db.getCollection(INFRASTRUCTURE_COLLECTION);
	}
	public DBCollection cpuCollection(){
		return db.getCollection(CPU_COLLECTION);
	}
	
	public void close(){
		client.close();
	}
}
