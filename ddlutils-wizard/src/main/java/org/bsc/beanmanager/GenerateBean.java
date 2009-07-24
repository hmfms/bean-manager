package org.bsc.beanmanager;

import static org.bsc.beanmanager.DDLWizardConstants.GENERATE_BEAN;
import static org.bsc.beanmanager.DDLWizardConstants.PACKAGE_NAME;

import java.awt.Component;
import java.io.File;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdesktop.application.Action;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPanelNavResult;

@SuppressWarnings("serial")
public class GenerateBean extends WizardPage {

	class GenerateBeanTask extends WizardPanelNavResult {

		@Override
		public void start(Map wizardData, ResultProgressHandle progress) {
		}

	}
	
	public GenerateBean() {
		super("generate", "Generate Bean&BeanInfo");
	}

	@Override
	public WizardPanelNavResult allowFinish(String stepName, Map settings,	Wizard wizard) {
		return new GenerateBeanTask();
	}

	@Override
	protected void renderingPage() {
		super.renderingPage();
		
		setGenerateBean(true);
	}

	@Override
	protected String validateContents(Component component, Object event) {
		if( !isGenerateBean() ) return null;
		if( DDLWizardApplication.isEmpty( getOutputDir() )) return "Select Output directory";
		
		return null;
	}

	public final String getPackageName() {
		return (String) getWizardData(PACKAGE_NAME);
	}

	public final void setPackageName(String packageName) {
		super.putWizardData(PACKAGE_NAME, packageName);
		firePropertyChange(PACKAGE_NAME, null, null);
	}

	public final boolean isGenerateBean() {
		return Boolean.TRUE.equals(getWizardData(GENERATE_BEAN));
	}

	public final void setGenerateBean(boolean generateBean) {
		putWizardData(GENERATE_BEAN, generateBean);
		firePropertyChange(GENERATE_BEAN, null, null);
	}

	File outputDir;
	
	public final String getOutputDir() {
		return (String) getWizardData("outputDir");
	}

	public final void setOutputDir(String outputDir) {
		putWizardData("outputDir", outputDir);
		firePropertyChange("outputDir", null, null);
	}

	@Action
	public void selectOutputDir() {
		 JFileChooser chooser = new JFileChooser();
		    chooser.setCurrentDirectory( new File(System.getProperty("user.home")));
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		           
		    	outputDir = chooser.getSelectedFile();
		    	setOutputDir(outputDir.getPath());
				
		    }


	}

}
