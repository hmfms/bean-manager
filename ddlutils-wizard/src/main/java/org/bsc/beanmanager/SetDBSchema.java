package org.bsc.beanmanager;

import static org.bsc.beanmanager.DDLWizardApplication.closeConnection;
import static org.bsc.beanmanager.DDLWizardApplication.getConnection;
import static org.bsc.beanmanager.DDLWizardConstants.CONNECTIONURL;
import static org.bsc.beanmanager.DDLWizardConstants.CONTINUEONERROR;
import static org.bsc.beanmanager.DDLWizardConstants.DATABASE_MODEL;
import static org.bsc.beanmanager.DDLWizardConstants.DBSCHEMA;
import static org.bsc.beanmanager.DDLWizardConstants.DRIVERCLASS;
import static org.bsc.beanmanager.DDLWizardConstants.DROPTABLES;
import static org.bsc.beanmanager.DDLWizardConstants.GENERATESQL;
import static org.bsc.beanmanager.DDLWizardConstants.PAGE2_STEP;
import static org.bsc.beanmanager.DDLWizardConstants.PASSWORD;
import static org.bsc.beanmanager.DDLWizardConstants.SQLFILE;
import static org.bsc.beanmanager.DDLWizardConstants.USER;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;
import org.jdesktop.application.Action;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPanelNavResult;

@SuppressWarnings("serial")
public class SetDBSchema extends WizardPage {


	class GenerateSchemaTask extends WizardPanelNavResult {

		@Override
        public void start(Map settings, ResultProgressHandle progress) {
            progress.setBusy("Generating...");

            final String driverClass = (String)settings.get(DRIVERCLASS);
            final String connectionUrl = (String)settings.get(CONNECTIONURL);
            final String user = (String)settings.get(USER);
            final String password = (String)settings.get(PASSWORD);

            final boolean dropTables = Boolean.TRUE.equals(settings.get(DROPTABLES));
            final boolean continueOnError = Boolean.TRUE.equals(settings.get(CONTINUEONERROR));
            final boolean generateSql = Boolean.TRUE.equals(settings.get(GENERATESQL));

            final java.io.File dbSchema = (java.io.File)settings.get( DBSCHEMA );

            Connection conn = null;

            try {

                Database db = new DatabaseIO().read( dbSchema );
                
                settings.put(DATABASE_MODEL, db);
                
                Platform platform = PlatformFactory.createNewPlatformInstance( driverClass , connectionUrl );

                if( generateSql ) {

                    String ddl = platform.getCreateTablesSql(db,
                            dropTables,
                            continueOnError );

                    FileWriter fw = new FileWriter( (java.io.File)settings.get(SQLFILE));
                    fw.write(ddl);
                    fw.close();

                }

                conn = getConnection( driverClass, connectionUrl, user, password);

                conn.setAutoCommit(true);

                platform.createTables( conn, db,  dropTables, continueOnError);

		//JOptionPane.showMessageDialog(SetDBSchema.this, "Database created succesfully!", "Message", JOptionPane.INFORMATION_MESSAGE);
                progress.finished(WizardPanelNavResult.PROCEED);

            }
            catch( Exception e ) {
/*
		JOptionPane.showMessageDialog(SetDBSchema.this,
                        String.format("Error on Database creation Database\n%s", e.getMessage()),
                        "Message", JOptionPane.ERROR_MESSAGE);
 */
                progress.failed(String.format("Error on Database creation\n%s", e.getMessage()), true /*canNavigateBack*/);
            }
            finally {
                    closeConnection(conn);
            }


        }
        
    }

    private static final String WIZARD_MESSAGE = "Select DB Schema File";

    public SetDBSchema() {
            super(PAGE2_STEP, "Generate Schema");


    }

    @Override
    protected void renderingPage() {
        super.renderingPage();

        setGenerateSQL(true);
        setDropTables(true);
        setContinueOnError(true);
}

    @Override
    public WizardPanelNavResult allowFinish(String stepName, Map settings, Wizard wizard) {
    	return new GenerateSchemaTask();
    }

    @Override
	public WizardPanelNavResult allowNext(String stepName, Map settings, Wizard wizard) {
        return new GenerateSchemaTask() ;
	}

	@Override
    protected String validateContents(Component component, Object event) {
        if( DDLWizardApplication.isEmpty(getSelectedFile()) ) {
            return WIZARD_MESSAGE;
        }
        
        return null;
    }

	public String getSelectedFile() {
	
		File dbSchema = getDbSchema();
		
		return (null!=dbSchema) ? dbSchema.getPath() : null;
	}
	
	public final boolean isDropTables() {
		return Boolean.TRUE.equals(getWizardDataMap().get( DROPTABLES));
	}


	public final void setDropTables(boolean dropTables) {
		putWizardData( DROPTABLES, dropTables);
		firePropertyChange(DROPTABLES, null, null);
	}

	public final boolean isContinueOnError() {
		return Boolean.TRUE.equals(getWizardData( CONTINUEONERROR));
	}


	public final void setContinueOnError(boolean continueOnError) {
		putWizardData( CONTINUEONERROR, continueOnError);
		firePropertyChange(CONTINUEONERROR, null, null);
	}


	public final boolean isGenerateSQL() {
		return Boolean.TRUE.equals(getWizardData( GENERATESQL));
	}

	public final void setGenerateSQL(boolean generateXML) {
		putWizardData( GENERATESQL, generateXML);
		firePropertyChange(GENERATESQL, null, null);
	}

	public final File getDbSchema() {
		return (File) getWizardData( DBSCHEMA);
	}

	public final void setDbSchema(File dbSchema) {
		putWizardData( DBSCHEMA, dbSchema);
		firePropertyChange("selectedFile", null, null);
	}

	public void setSQLFile( File value ) {
		putWizardData( SQLFILE, value);
		
	}
	public File getSQLFile() {
		return (File) getWizardData( SQLFILE);
	}
	
	
	@Action
	public void selectFile() {
		
		 JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter( "DB Schema (XML)", "xml");
		    chooser.setFileFilter(filter);
		    chooser.setCurrentDirectory( new File(System.getProperty("user.home")));
		    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		           
		    	File file = chooser.getSelectedFile();
		    	String path = file.getPath();
				
		    	setDbSchema( file );
			setSQLFile( new File( path.substring(0, path.lastIndexOf('.')).concat(".sql") ) );
		    	
		    }

	}

}
