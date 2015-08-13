/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.Arrays;

/**
 *
 * @author cesar
 */
public abstract class MonitorDatabaseConnection {	
	
	 protected String ip;
	 protected int port;
	 protected String name;
	 protected String user;
	 protected String password;
	 
	 /**
	 * Class Constructor
	 */
    public MonitorDatabaseConnection() {
    	callVariables();    	
    }
    /**
     * This method should be implemented in each factor to colaborate  
     */
    public abstract void callVariables();

	/**
     * Connects to monitoring database
     * @return connection done
     * @throws UnknownHostException if connection was not possible
     */
    public MongoConnection generateConnection() throws UnknownHostException {
    	MongoClient conexion ;      
        MongoCredential credential = MongoCredential.createCredential(user, name, password.toCharArray());
        ServerAddress address = new ServerAddress(ip, port);
        conexion = new MongoClient(address, Arrays.asList(credential));
        MongoConnection con = new MongoConnection(conexion,name);
        return con;
    }
}
