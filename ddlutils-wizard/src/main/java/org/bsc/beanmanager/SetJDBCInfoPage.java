package org.bsc.beanmanager;

import static org.bsc.beanmanager.DDLWizardConstants.CONNECTIONURL;
import static org.bsc.beanmanager.DDLWizardConstants.DRIVERCLASS;
import static org.bsc.beanmanager.DDLWizardConstants.PAGE1_STEP;
import static org.bsc.beanmanager.DDLWizardConstants.PASSWORD;
import static org.bsc.beanmanager.DDLWizardConstants.USER;
import static org.bsc.beanmanager.DDLWizardConstants.*;

import java.awt.Component;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;

import org.jdesktop.application.Action;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPanelNavResult;

@SuppressWarnings("serial")
public class SetJDBCInfoPage extends WizardPage {
    
    public static final String DESCRIPTION = "JDBC connection";
	
	private static final String SELECT_DRIVER_MESSAGE = "please select the JDBC Driver";

	static class JDBCInfo {
	
		final Class<?> driver;
		final String connectionUrl;
		final String name ;
		
		public JDBCInfo( String name,  Class<?> driver, String connectionUrl) {
			super();
			this.name = name;
			this.driver = driver;
			this.connectionUrl = connectionUrl;
		}

		@Override
		public String toString() {
			return name;
		}

		public final String getDriver() {
			return driver.getName();
		}

		public final String getConnectionUrl() {
			return connectionUrl;
		}
		
		
	}

        class OpenConnectionTask extends WizardPanelNavResult {

            @Override
            public void start(Map wizardData, ResultProgressHandle progress) {

                progress.setBusy("Connecting...");

                Connection conn = null;

				try {
		
					conn = DDLWizardApplication.getConnection( wizardData );
		
				} catch (Exception e) {
					//JOptionPane.showMessageDialog(SetJDBCInfoPage.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
					progress.failed (e.getMessage(), true);
					return;
				}
				finally {
					DDLWizardApplication.closeConnection(conn);
				}

				progress.finished(WizardPanelNavResult.PROCEED);

            }

        }
	
    List<JDBCInfo> supportedDrivers = new ArrayList<JDBCInfo>(3);

    JComboBox cmbDriver;
 
    /**
     * 
     */
	public SetJDBCInfoPage() {
		super( PAGE1_STEP,DESCRIPTION);
		
		
		supportedDrivers.add( new JDBCInfo( "<Select Driver>", null, null) ); 
		supportedDrivers.add( new JDBCInfo( "MySql Driver", com.mysql.jdbc.Driver.class,  "jdbc:mysql://mysql_server:3306/db_name" ));
		//supportedDrivers.add( new JDBCInfo( "MySql Driver", com.mysql.jdbc.Driver.class,  "jdbc:mysql://192.168.2.101:3306/aacc" ));
		supportedDrivers.add( new JDBCInfo( "Oracle Driver", 
                                                    oracle.jdbc.driver.OracleDriver.class,
                                                    "jdbc:oracle:thin:@//oracle_server:oracle_port/oracle_db_name" ));
		supportedDrivers.add( new JDBCInfo( "MSSQL Driver", 
                                                    com.microsoft.sqlserver.jdbc.SQLServerDriver.class,
                                                    "jdbc:sqlserver://SQL_SERVER:1433;databaseName=DB_NAME;integratedSecurity=false;" ));
		supportedDrivers.add( new JDBCInfo( "Derby DB [Embed]", 
                                                     org.apache.derby.jdbc.EmbeddedDriver.class,
                                                     "jdbc:derby:DB_PATH/DB_NAME;create=false" ));
		
	}

    @Override
	protected void renderingPage() {
    	
    	setCreateDB(true);
	}

	@Override
    public WizardPanelNavResult allowBack(String stepName, Map settings, Wizard wizard) {
        return super.allowBack(stepName, settings, wizard);
    }

    @Override
    public WizardPanelNavResult allowFinish(String stepName, Map settings, Wizard wizard) {
        return super.allowFinish(stepName, settings, wizard);
    }

    @Override
    public WizardPanelNavResult allowNext(String stepName, Map settings, Wizard wizard) {
        return new OpenConnectionTask();
    }

    @Override
    protected String validateContents(Component component, Object event) {
        if( cmbDriver==null || cmbDriver.getSelectedIndex()<=0 ) {
            return SELECT_DRIVER_MESSAGE;
        }

        if( DDLWizardApplication.isEmpty( getConnectionUrl() )) {
            return "Connection Url is required!";
        }
        
        return null;
    }


	public final boolean isCreateDB() {
		return Boolean.TRUE.equals( getWizardData(CREATEDB));
	}

	public final void setCreateDB(boolean createDB) {
		putWizardData(CREATEDB, createDB);
		firePropertyChange(CREATEDB, null, null);
	}

	public final boolean isFromDB() {
		return Boolean.TRUE.equals( getWizardData(FROMDB));
	}

	public final void setFromDB(boolean fromDB) {
		putWizardData(FROMDB, fromDB);
		firePropertyChange(FROMDB, null, null);
	}

	public final String getDriverClass() {
		return (String)getWizardData(DRIVERCLASS);
	}

	public final void setDriverClass(String driverClass) {
                putWizardData(DRIVERCLASS, driverClass);
		firePropertyChange(DRIVERCLASS, null, null);
	}

	public final String getConnectionUrl() {
		return (String)getWizardData(CONNECTIONURL);
	}

	public final void setConnectionUrl(String connectionUrl) {
		putWizardData( CONNECTIONURL, connectionUrl);
		firePropertyChange(CONNECTIONURL, null, null);
	}

	public final String getUser() {
		return (String)getWizardData(USER);
	}

	public final void setUser(String user) {
		putWizardData( USER, user);
	}

	public final String getPasswd() {
		return (String)getWizardData(PASSWORD);
	}

	public final void setPasswd(String passwd) {
		putWizardData( PASSWORD, passwd);
	}

	public final List<JDBCInfo> getSupportedDrivers() {
		return supportedDrivers;
	}
	
	public boolean isDataValid() {
		return (cmbDriver!=null && cmbDriver.getSelectedIndex()>0) && (null!=getConnectionUrl());
	}
	
	@Action
	public void selectDriver() {

		int selectedIndex = cmbDriver.getSelectedIndex();
		if( selectedIndex > 0 ) {
			JDBCInfo selectedInfo = supportedDrivers.get(selectedIndex);
			
			setDriverClass(selectedInfo.getDriver());
			setConnectionUrl(selectedInfo.getConnectionUrl());
			
		}
		else {
			setDriverClass("");
			setConnectionUrl("");
		}
		
	}

}
