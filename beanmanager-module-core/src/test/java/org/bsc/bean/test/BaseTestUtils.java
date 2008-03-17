/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.LogManager;
import org.bsc.bean.BeanManager;
import org.bsc.bean.BeanManagerFactory;
import org.bsc.bean.dyna.DynaManagedBeanInfo;

/**
 *
 * @author Sorrentino
 */
public class BaseTestUtils {

    public static void loadDriver() throws Exception {
        
            String driver = "org.apache.derby.jdbc.EmbeddedDriver";
            Class.forName(driver); 
        
    }
    
    public static void initLogger() throws Exception {
        
            LogManager.getLogManager().readConfiguration( TestBeanManager.class.getResourceAsStream("/logging.properties"));
        
    }
    
    public static Connection connect() throws Exception {
            
            String connectionURL = "jdbc:derby:./target/DerbyDB/testDB"; 

            Properties p = new Properties();
            p.setProperty("create", "true");
		
	    return DriverManager.getConnection(connectionURL, p); 
    }
    
    public static void executeCommands( Connection conn, String sql[] ) throws Exception  {
            Statement stmt = conn.createStatement();

            for( String command : sql )
                stmt.addBatch(command);

            stmt.executeBatch();

            stmt.close();
    }

    /**
     * 
     * @param conn
     * @throws java.sql.SQLException
     */
    public static void disconnect( Connection conn ) throws Exception {

            if( null!=conn ) {
                    conn.close();
            }


    }

}
