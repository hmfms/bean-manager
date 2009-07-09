package org.bsc.beanmanager;

import java.awt.Cursor;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;
import org.jdesktop.application.Application;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Summary;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardController;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPanelProvider;
import org.swixml.jsr296.SwingApplication;


public class DDLWizardApplication extends SwingApplication {

	private SetJDBCInfoPage page1;
	private SetDBSchema page2;
	
	public static Connection getConnection( String driverClass, String connectionUrl, String user, String password ) throws Exception {
		try {
			Class.forName( driverClass );
		} catch (ClassNotFoundException e) {
			throw new Exception( "Error Loading Driver!");
		}

		Connection conn = null;
		
		try {
			
			conn = DriverManager.getConnection(connectionUrl, user, password );
			
		} catch (SQLException e) {
			throw new Exception( "Error opening connection !" );
		}
		
		return conn;
	}
	
	public static void closeConnection( Connection conn ) {
		if( null==conn ) return;
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Log
		}
	}
	
	
	private Object commit( Object result ) throws Exception {
		
		Connection conn = null;
		
		try {
			
        	Database db = new DatabaseIO().read( page2.getDbSchema() );

        	Platform platform = PlatformFactory.createNewPlatformInstance(page1.getDriverClass(), page1.getConnectionUrl());

        	if( page2.isGenerateSQL() ) {
            	String ddl = platform.getCreateTablesSql(db, page2.isDropTables(), page2.isContinueOnError());

                FileWriter fw = new FileWriter(page2.getSQLFile());
                fw.write(ddl);
                fw.close();
        		
        	}
            
			conn = getConnection( page1.getDriverClass(), page1.getConnectionUrl(), page1.getUser(), page1.getPasswd());
            
            conn.setAutoCommit(true);
                            		
            platform.createTables( conn, db,  page2.isDropTables(), page2.isContinueOnError());

		}
		finally {
			closeConnection(conn);
		}
		
		return result;
	}

	protected boolean cancel( Map<String,Object> settings ) {
		
		return true;
	}
	
    @Override
    protected void startup() {

       
       WizardPanelProvider provider = new  WizardPanelProvider( "Create DB Schema", 
    		   new String [] {"page1", "page2"}, 
    		   new String [] {"JDBC connection", "DB Schema"}) 
       {

            @SuppressWarnings("unchecked")
			@Override
            protected JComponent createPanel(WizardController controller, String id, Map parameters) {
            	JComponent result = null;

            	try {
                	if( "page1".equals(id) ) {
                		page1 = new SetJDBCInfoPage(controller, parameters);
                        result = render(page1, "SetJDBCInfoPage.xml");                		
                	}
                	if( "page2".equals(id)) {
                		page2 = new SetDBSchema(controller, parameters);
                		result = render( page2, "SetDBSchema.xml");
                	}
                	
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return result;
            }

            @SuppressWarnings("unchecked")
			@Override
            public boolean cancel(Map settings) {
                return true;
            }

            @SuppressWarnings("unchecked")
			@Override
            protected Object finish(Map settings) throws WizardException {
            	
				try {
					
					
					commit( settings );

					JOptionPane.showMessageDialog(null, "Database created succesfully!", "Message", JOptionPane.INFORMATION_MESSAGE);

            		final Map<String,Object> map = settings;
	                String [] summary = new String[ map.size() ];
	
	                int i=0;
	                for( Map.Entry<String,Object> e : map.entrySet() ) {
	                    summary[i++] = String.format("%s=%s", e.getKey(), e.getValue());
	                }
	                
	                return Summary.create( summary, settings );

				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
            	
            	return null;
            	
            }


       };

      Wizard wizard = provider.createWizard();
      
      
      Object result = WizardDisplayer.showWizard(wizard);

   }

	
	public static void main( String args[] ) {
		Application.launch(DDLWizardApplication.class, args);
	}
}
