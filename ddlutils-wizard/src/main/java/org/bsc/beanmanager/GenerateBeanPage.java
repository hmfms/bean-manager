package org.bsc.beanmanager;

import static org.bsc.beanmanager.BeanGeneratorUtils.generateBean;
import static org.bsc.beanmanager.BeanGeneratorUtils.generateBeanInfo;
import static org.bsc.beanmanager.DDLWizardConstants.DATABASE_MODEL;
import static org.bsc.beanmanager.DDLWizardConstants.GENERATE_BEAN;
import static org.bsc.beanmanager.DDLWizardConstants.PACKAGE_NAME;

import java.awt.Component;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.jdesktop.application.Action;
import org.jdesktop.observablecollections.ObservableCollections;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPanelNavResult;
import org.swixml.jsr295.BindingUtils;


@SuppressWarnings("serial")
public class GenerateBeanPage extends WizardPage {

	class GenerateBeanTask extends WizardPanelNavResult {

		@Override
		public void start(Map wizardData, ResultProgressHandle progress) {
            progress.setBusy("Generating...");
            
            Database db = (Database) wizardData.get(DATABASE_MODEL);
            
            try {
            	progress.setBusy("Generating Beans!");
				generateBean(generateBeanList, getPackageName(), outputDir);
				
	           	progress.setBusy("Generating BeanInfo!");
				generateBeanInfo(generateBeanList, getPackageName(),  outputDir);
				
				progress.finished(WizardPanelNavResult.PROCEED);
			} catch (Exception e) {
                progress.failed(String.format("Error on Bean creation\n%s", e.getMessage()), true /*canNavigateBack*/);
			}

		}

	}

	File outputDir;
	final List<GenerateBean> generateBeanList = ObservableCollections.observableList( new ArrayList<GenerateBean>() );
	
	public GenerateBeanPage() {
		super("generate", "Generate Bean&BeanInfo");
		
		try {
			Map<String,PropertyDescriptor> map = new HashMap<String,PropertyDescriptor>();
			for( PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(GenerateBean.class) ) {
				map.put( pd.getName(), pd);
			}
			
			{
			PropertyDescriptor pd = map.get("selected"); 
			BindingUtils.setTableColumnEditable(pd, true);
			BindingUtils.setTableColumnIndex(pd, 1);
			BindingUtils.setTableColumnIsBound(pd, true);
			pd.setDisplayName("");
			}
			{
			PropertyDescriptor pd = map.get("tableName"); 
			BindingUtils.setTableColumnEditable(pd, false);
			BindingUtils.setTableColumnIndex(pd, 2);
			BindingUtils.setTableColumnIsBound(pd, true);
			}
			{
			PropertyDescriptor pd = map.get("beanName"); 
			BindingUtils.setTableColumnEditable(pd, true);
			BindingUtils.setTableColumnIndex(pd, 3);
			BindingUtils.setTableColumnIsBound(pd, true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public WizardPanelNavResult allowFinish(String stepName, Map settings,	Wizard wizard) {
		return new GenerateBeanTask();
	}

	@Override
	protected void renderingPage() {
		super.renderingPage();
		
		setGenerateBean(true);
		
        Database db = (Database) getWizardData(DATABASE_MODEL);
        
        for( Table table : db.getTables() ) {
        	
        	getGenerateBeanList().add( new GenerateBean( table ) );
        }

		
	}

	@Override
	protected String validateContents(Component component, Object event) {
		if( !isGenerateBean() ) return null;
		if( DDLWizardApplication.isEmpty( getOutputDir() )) return "Select Output directory";
		
		return null;
	}

	public final List<GenerateBean> getGenerateBeanList() {
		return generateBeanList;
	}
	
	public final Class<GenerateBean> getGenerateBeanClass() {
		return GenerateBean.class;
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
