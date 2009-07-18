package org.bsc.beanmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import javax.swing.JOptionPane;

import org.jdesktop.application.Application;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Summary;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;
import org.swixml.jsr296.SwingApplication;


public class DDLWizardApplication extends SwingApplication {

        public static boolean isEmpty( String value ) {

            if( value==null ) return true;
            if( value.length()==0) return true;
            if( value.trim().length()==0 ) return true;

            return false;
        }

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
	
	
    @Override
    protected void startup() {
	SetJDBCInfoPage page1 = null;
	SetDBSchema page2 = null;


        try {
            page1 = render(new SetJDBCInfoPage(), "SetJDBCInfoPage.xml");
            page2 = render( new SetDBSchema(), "SetDBSchema.xml");
        } catch (Exception ex) {

            JOptionPane.showMessageDialog(null, "Fatal Error on render component!", "Error", JOptionPane.ERROR_MESSAGE);
            exit();

        }

        Wizard wizard = WizardPage.createWizard( new WizardPage[] { page1, page2 }, new WizardResultProducer(){

            public Object finish(Map settings) throws WizardException {


                final Map<String,Object> map = settings;
                String [] summary = new String[ map.size() ];
                        int i=0;
                for( Map.Entry<String,Object> e : map.entrySet() ) {
                    summary[i++] = String.format("%s=%s", e.getKey(), e.getValue());
                }

                return Summary.create( summary, settings );

            }

            public boolean cancel(Map wizardData) {
                exit();
                return true;
            }
         
        });

 
      
      /* Object result = */WizardDisplayer.showWizard(wizard);

   }

	
	public static void main( String args[] ) {
		Application.launch(DDLWizardApplication.class, args);
	}
}
