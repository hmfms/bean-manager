package org.bsc.beanmanager;

import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdesktop.application.Action;
import org.netbeans.spi.wizard.WizardController;

@SuppressWarnings("serial")
public class SetJDBCInfoPage extends JPanel {
	
	private static final String DATA_IS_VALID = "dataValid";
	private static final String WIZARD_MESSAGE = "please select the JDBC Driver";

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
	
    final WizardController wizardController;
    final Map<String,Object> parameters ;
    
    List<JDBCInfo> supportedDrivers = new ArrayList<JDBCInfo>(3);
    
    JComboBox cmbDriver;
    
	public SetJDBCInfoPage(WizardController wizardController, Map<String, Object> parameters) {
		super();
		this.wizardController = wizardController;
		this.parameters = parameters;
		
		
		supportedDrivers.add( new JDBCInfo( "<Select Driver>", null, null) ); 
		supportedDrivers.add( new JDBCInfo( "Oracle Driver", 
											oracle.jdbc.driver.OracleDriver.class, 
											"dbc:oracle:thin:@//oracle_server:oracle_port/oracle_db_name" ));
		supportedDrivers.add( new JDBCInfo( "MSSQL Driver", 
											com.microsoft.sqlserver.jdbc.SQLServerDriver.class, 
											"jdbc:sqlserver://SQL_SERVER:1433;databaseName=DB_NAME;integratedSecurity=false;" ));
		supportedDrivers.add( new JDBCInfo( "Derby DB [Embed]", 
											org.apache.derby.jdbc.EmbeddedDriver.class, 
											"jdbc:derby:DB_PATH/DB_NAME;create=false" ));
		
        wizardController.setProblem( WIZARD_MESSAGE);
	}

	public final String getDriverClass() {
		return (String)parameters.get("driverClass");
	}

	public final void setDriverClass(String driverClass) {
		parameters.put("driverClass", driverClass);
		firePropertyChange("driverClass", null, null);
	}

	public final String getConnectionUrl() {
		return (String)parameters.get("connectionUrl");
	}

	public final void setConnectionUrl(String connectionUrl) {
		parameters.put("connectionUrl", connectionUrl);
		firePropertyChange("connectionUrl", null, null);
		firePropertyChange(DATA_IS_VALID, null, null);
	}

	public final String getUser() {
		return (String)parameters.get("user");
	}

	public final void setUser(String user) {
		parameters.put("user", user);
	}

	public final String getPasswd() {
		return (String)parameters.get("passwd");
	}

	public final void setPasswd(String passwd) {
		parameters.put("passwd", passwd);
	}

	public final List<JDBCInfo> getSupportedDrivers() {
		return supportedDrivers;
	}

	public boolean isDataValid() {
		return (cmbDriver!=null && cmbDriver.getSelectedIndex()>0) && (null!=getConnectionUrl());
	}
	
	@Action
	public void selectDriver( ActionEvent ev ) {
		int index = cmbDriver.getSelectedIndex();
		if( index > 0 ) {
			JDBCInfo selectedInfo = supportedDrivers.get(index);
			
			setDriverClass(selectedInfo.getDriver());
			setConnectionUrl(selectedInfo.getConnectionUrl());
			
	        wizardController.setProblem( "Test Connection");
			
		}
		else {
			setDriverClass("");
			setConnectionUrl("");
	        wizardController.setProblem( WIZARD_MESSAGE);
		}
		firePropertyChange(DATA_IS_VALID, null, null);
		
	}

	@Action( enabledProperty=DATA_IS_VALID)
	public void test() {
		Connection conn = null;
		
		try {
			
			conn = DDLWizardApplication.getConnection( getDriverClass(), getConnectionUrl(), getUser(), getPasswd() );
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		finally {
			DDLWizardApplication.closeConnection(conn);
		}
		
		wizardController.setProblem(null);
	}
}
