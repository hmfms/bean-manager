package org.bsc.bean.ddl.processor;

/*
* 
* 
* JDK 1.6
*/

 import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.bsc.bean.ManagedBeanInfo;
import org.bsc.bean.ddl.DDLUtil;
import org.bsc.bean.jpa.JPAManagedBeanInfo;

@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes("javax.persistence.Entity")
@SupportedOptions( {"driver", "connectionUrl","localDb"} )
public class JPAProcessor extends AbstractDDLProcessor {

	public JPAProcessor() {
	}
	

    
	@SuppressWarnings("unchecked")
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver())      return false;
        
        java.util.Map<String,String> optionMap = processingEnv.getOptions();
        
        for( java.util.Map.Entry<String,String> e : optionMap.entrySet() ) {
        
        	info( String.format("option[%s]=%s", e.getKey(), e.getValue()));
        }
        
        Database db = new Database(); 

        java.util.Map<String,Table> tableMap = new java.util.HashMap<String,Table>();
        
        for (TypeElement type : (Collection<TypeElement>)roundEnv.getElementsAnnotatedWith(javax.persistence.Entity.class)) {
        	info( String.format( "entity fqn=[%s]" , type.getQualifiedName() ));
        	
        	Class<?> clazz = null;;
			try {
				//clazz = Class.forName( type.getQualifiedName().toString(), true, javax.persistence.Entity.class.getClassLoader() );
				clazz = Class.forName( type.getQualifiedName().toString());

				ManagedBeanInfo<?> beanInfo = JPAManagedBeanInfo.create(clazz);
				
				Table table = DDLUtil.fromBeanInfoToTable(beanInfo);
				
				Table prevTable = tableMap.get(table.getName());
				if( prevTable!=null && table.getColumnCount() < prevTable.getColumnCount() ) {
					continue;
				}

				
				info( String.format( "processing table [%s]" ,table.getName() ));
				
				
				tableMap.put( table.getName(), table);
				
			} catch (ClassNotFoundException e) {
				warn( String.format("class [%s] not found in classloader!. Skipped", type.getQualifiedName() ));
			} catch (IntrospectionException e) {
				warn( String.format("error processing metadata over class [%s]!. Skipped", type.getQualifiedName() ), e );
			}
        	
        }
        
        db.addTables( tableMap.values() );
  
        super.generateSQL(db, optionMap.get("driver"), optionMap.get("connectionUrl"));
        String localDb = optionMap.get("localDb");
        if( null!=localDb ) {
        	super.createLocalDatabase(db, localDb);
        }

        return false;
	}

}
