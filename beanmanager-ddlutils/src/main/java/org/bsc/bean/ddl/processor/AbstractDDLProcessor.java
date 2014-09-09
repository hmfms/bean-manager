package org.bsc.bean.ddl.processor;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.model.Database;
import org.bsc.bean.ddl.DDLUtil;

public abstract class AbstractDDLProcessor extends javax.annotation.processing.AbstractProcessor {

    protected void info( String msg ) {
        //logger.info(msg);
        processingEnv.getMessager().printMessage(Kind.NOTE, msg );
    }

    protected void warn( String msg ) {
        //logger.warning(msg);
        processingEnv.getMessager().printMessage(Kind.WARNING, msg );
    }
    
    protected void warn( String msg, Throwable t ) {
        //logger.log(Level.WARNING, msg, t );
        processingEnv.getMessager().printMessage(Kind.WARNING, msg );
    }

    protected void error( String msg ) {
        //logger.severe(msg);
        processingEnv.getMessager().printMessage(Kind.ERROR, msg );
    }
    
    protected void error( String msg, Throwable t ) {
        //logger.log(Level.SEVERE, msg, t );
        processingEnv.getMessager().printMessage(Kind.ERROR, msg );
    }

    protected void createLocalDatabase( Database db, String target )  {
    		
    		final String local_db_driver = "org.apache.derby.jdbc.EmbeddedDriver";
    		final String local_db_connection = String.format("jdbc:derby:%s;create=true",target);
    			 
    		try {
				Class.forName(local_db_driver);
			} catch (ClassNotFoundException e) {
				warn( String.format("driver class [%s] not found!", local_db_driver ));
				return;
			}
    		     
    		java.sql.Connection conn = null;
    		
    		try {
				conn = DriverManager.getConnection(local_db_connection);

				conn.setAutoCommit(true);
			
				Platform platform = PlatformFactory.createNewPlatformInstance(local_db_driver, local_db_connection);
  			  
				platform.createTables( conn, db,  true /*drop tables */, true /* continue on error */ );

				DriverManager.getConnection(String.format("jdbc:derby:%s;shutdown=true",target));
				
    		} catch (SQLException e) {
				warn( String.format("error getting connection from [%s]!", local_db_connection ));
				return;
			}
    		finally {
    			if( conn!=null )
					try {
						conn.close();
						info("connection closed");
					} catch (SQLException e) {
						warn( "error closing connection", e);
					}
    		}

    			
    	
    }
    
    protected void generateSQL( Database db, String driver, String connectionUrl) {
        
        final Filer filer = processingEnv.getFiler();

    	String sql = DDLUtil.generateSQL(db, driver, connectionUrl);
        
		try {
	        FileObject res = filer.createResource(StandardLocation.SOURCE_OUTPUT, "sql", "createDB.sql", (javax.lang.model.element.Element)null);

	        java.io.Writer w = res.openWriter();
	        
	        w.write(sql);
	        
	        w.close();
		} catch (IOException e) {
			warn( String.format("error writing output file" ), e );
			
			System.err.println( sql );
		}
    	
    }
}
