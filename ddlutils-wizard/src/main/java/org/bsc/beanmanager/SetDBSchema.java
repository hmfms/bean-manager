package org.bsc.beanmanager;

import java.io.File;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdesktop.application.Action;
import org.netbeans.spi.wizard.WizardController;

@SuppressWarnings("serial")
public class SetDBSchema extends JPanel {
    
	private static final String WIZARD_MESSAGE = "Select DB Schema File";
	final WizardController wizardController;
    final Map<String,Object> parameters ;
    
	public SetDBSchema(WizardController wizardController,
			Map<String, Object> parameters) {
		super();
		this.wizardController = wizardController;
		this.parameters = parameters;
		
		setGenerateSQL(true);
		setDropTables(true);
		setContinueOnError(true);
		
		wizardController.setProblem(WIZARD_MESSAGE);
	}

	
	public final boolean isDataValid() {
		return (null != getDbSchema() );
	}


	public String getSelectedFile() {
	
		File dbSchema = getDbSchema();
		
		return (null!=dbSchema) ? dbSchema.getPath() : null;
	}
	
	public final boolean isDropTables() {
		return Boolean.TRUE.equals(parameters.get( "dropTables"));
	}


	public final void setDropTables(boolean dropTables) {
		parameters.put("dropTables", dropTables);
		firePropertyChange("dropTables", null, null);
	}

	public final boolean isContinueOnError() {
		return Boolean.TRUE.equals(parameters.get( "continueOnError"));
	}


	public final void setContinueOnError(boolean continueOnError) {
		parameters.put("continueOnError", continueOnError);
		firePropertyChange("continueOnError", null, null);
	}


	public final boolean isGenerateSQL() {
		return Boolean.TRUE.equals(parameters.get( "generateSQL"));
	}

	public final void setGenerateSQL(boolean generateXML) {
		parameters.put("generateSQL", generateXML);
	}

	public final File getDbSchema() {
		return (File) parameters.get( "dbSchema");
	}

	public final void setDbSchema(File dbSchema) {
		parameters.put("dbSchema", dbSchema);
		firePropertyChange("selectedFile", null, null);
	}

	public void setSQLFile( File value ) {
		parameters.put("sqlFile", value);
		
	}
	public File getSQLFile() {
		return (File) parameters.get( "sqlFile");
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
		    	
		    	wizardController.setProblem(null);
		    }
		    else {
				wizardController.setProblem(WIZARD_MESSAGE);
		    	
		    }

	}

}
