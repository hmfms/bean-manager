package org.bsc.bean.ddl.processor;

import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.bsc.bean.ManagedBeanInfo;
import org.bsc.bean.ddl.ClassDiscovery;
import org.bsc.bean.ddl.DDLUtil;

@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes("*")
@SupportedOptions( {"driver", "connectionUrl", "seedClass", "localDb"} )
public class DDLProcessor extends AbstractDDLProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver())      return false;
        
        java.util.Map<String,String> optionMap = processingEnv.getOptions();
        
        for( java.util.Map.Entry<String,String> e : optionMap.entrySet() ) {
        
        	info( String.format("option[%s]=%s", e.getKey(), e.getValue()));
        }
        
        String seedClassName = optionMap.get("seedClass");
        
        if( seedClassName==null )  {
        	
        	error( "seedClass option is mandatory");
        	return false;
        }

        Class<?> seedClass  = null;
        
        try {
			seedClass = Class.forName( seedClassName );
			
		} catch (ClassNotFoundException e1) {
        	error( String.format("seedClass [%s] doesn't exist!", seedClassName ));
        	return false;
		}
        
        Class<? extends ManagedBeanInfo>[] result = ClassDiscovery.DiscoverClasses(seedClass, null, ManagedBeanInfo.class);
        
        Database db = new Database(); 

        java.util.Map<String,Table> tableMap = new java.util.HashMap<String,Table>();
        
        for ( Class<? extends ManagedBeanInfo> bic : result ) {
        	
        	info( String.format( "BeanInfo fqn=[%s]" , bic.getName()));
        	
			try {

				ManagedBeanInfo<?> beanInfo = bic.newInstance();
        					
				Table table = DDLUtil.fromBeanInfoToTable(beanInfo);

				Table prevTable = tableMap.get(table.getName());
				if( prevTable!=null && table.getColumnCount() < prevTable.getColumnCount() ) {
					continue;
				}

				
				info( String.format( "processing table [%s]" ,table.getName() ));
				
				
				tableMap.put( table.getName(), table);
				
			} catch (InstantiationException e) {
				warn( String.format("error processing metadata over class [%s]!. Skipped", bic.getName() ), e );
			} catch (IllegalAccessException e) {
				warn( String.format("error processing metadata over class [%s]!. Skipped", bic.getName() ), e );
			}
        	
        }
        
		db.addTables(tableMap.values());
		
        
        super.generateSQL(db, optionMap.get("driver"), optionMap.get("connectionUrl"));
        
        String localDb = optionMap.get("localDb");
        if( null!=localDb ) {
        	super.createLocalDatabase(db, localDb);
        }
        return false;
	}
}
