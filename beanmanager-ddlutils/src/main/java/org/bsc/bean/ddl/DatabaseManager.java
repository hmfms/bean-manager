package org.bsc.bean.ddl;

import static org.bsc.bean.ddl.DDLUtil.fromBeanInfoToTable;

import java.beans.BeanInfo;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;

public class DatabaseManager {

	private static final String CONFIG_FILE = "config.properties";

	public static java.util.Properties loadConfiguration( java.io.InputStream is ) throws IOException {
		if( null==is ) throw new IllegalArgumentException( "input strean is null!");
		java.util.Properties p = new java.util.Properties();
		
		p.load(is);
		
		return p;
	}

	public static java.util.Properties loadConfiguration() throws IOException {
	
		java.util.Properties p = new java.util.Properties();
		
		java.io.InputStream is = DatabaseManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
		p.load(is);
		
		return p;
	}
	
	public static String getProperty( java.util.Properties p, String name ) {
	
		String result = p.getProperty( name );
		if( null==result ) throw new IllegalArgumentException( String.format( "argument [%s] doesn't exist in configuration!", name));
	
		return result;
	}
	
	public static String getPassword( java.util.Properties p, String name ) {
		String result = getProperty(p, name);
		
		if( result.startsWith("*") ) {
			
			result = JOptionPane.showInputDialog( null, "DB Password ", "Table creation", JOptionPane.QUESTION_MESSAGE );
		
		}
		
		return result;
	}
	
	/**
	 * @param args
	 */
	public static void createDatabase( java.util.Properties p, String name, BeanInfo ...beanInfos  ) {

		
		String jdbc_driver = getProperty( p, name.concat("jdbc.driver"));
		
        String connectionUrl = 
        	java.text.MessageFormat.format( getProperty(p, name.concat("jdbc.url") ), System.getProperty("user.home") );

        System.out.printf( "conection url=%s\n", connectionUrl);

        Connection conn = null;
        
        try {
			
            Class.forName(jdbc_driver.trim());
            
            conn = DriverManager.getConnection(connectionUrl, getProperty(p, name.concat("jdbc.user")),getPassword(p, name.concat("jdbc.pass")) );
            
            conn.setAutoCommit(true);
            
            Database db = new Database(); 
        	
            for( BeanInfo bi : beanInfos ) {
            	Table table = fromBeanInfoToTable( bi );

            	//	Index index = new  org.apache.ddlutils.model.NonUniqueIndex();

            	db.addTable( table );
            }
        	Platform platform = PlatformFactory.createNewPlatformInstance(jdbc_driver, connectionUrl);
		
        	platform.createTables( conn, db,  true /*drop tables */, true /* continue on error */ );
    		
            
		} catch (Exception e) {
			e.printStackTrace();
		}
        finally {
        	if( null!=conn )
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
        }
		
	
	}
	
}
