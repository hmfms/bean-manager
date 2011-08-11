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
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.bsc.bean.ManagedBeanInfo;
import org.bsc.bean.ddl.DDLUtil;
import org.bsc.bean.jpa.JPAManagedBeanInfo;

@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes("javax.persistence.Entity")
@SupportedOptions( {"driver", "connectionUrl"} )
public class JPAProcessor extends javax.annotation.processing.AbstractProcessor {

	public JPAProcessor() {
	}
	
    protected void info( String msg ) {
        //logger.info(msg);
        processingEnv.getMessager().printMessage(Kind.NOTE, msg );
    }

    protected void warn( String msg ) {
        //logger.warning(msg);
        processingEnv.getMessager().printMessage(Kind.WARNING, msg );
    }
    
    protected void warn( String msg, Throwable t ) {
        //logger.log(Level.WARNING, msg, t );
        processingEnv.getMessager().printMessage(Kind.WARNING, msg );
    }

    protected void error( String msg ) {
        //logger.severe(msg);
        processingEnv.getMessager().printMessage(Kind.ERROR, msg );
    }
    
    protected void error( String msg, Throwable t ) {
        //logger.log(Level.SEVERE, msg, t );
        processingEnv.getMessager().printMessage(Kind.ERROR, msg );
    }

    
	@SuppressWarnings("unchecked")
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver())      return false;
        
        final Filer filer = processingEnv.getFiler();

        java.util.Map<String,String> optionMap = processingEnv.getOptions();
        
        for( java.util.Map.Entry<String,String> e : optionMap.entrySet() ) {
        
        	info( String.format("option[%s]=%s", e.getKey(), e.getValue()));
        }
        
        Database db = new Database(); 

        
        for (TypeElement type : (Collection<TypeElement>)roundEnv.getElementsAnnotatedWith(javax.persistence.Entity.class)) {
        	info( String.format( "entity fqn=[%s]" , type.getQualifiedName() ));
        	
        	Class<?> clazz = null;;
			try {
				clazz = Class.forName( type.getQualifiedName().toString(), true, javax.persistence.Entity.class.getClassLoader() );

				ManagedBeanInfo<?> beanInfo = JPAManagedBeanInfo.create(clazz);
				
				Table table = DDLUtil.fromBeanInfoToTable(beanInfo);
				
				db.addTable(table);
				
			} catch (ClassNotFoundException e) {
				warn( String.format("class [%s] not found in classloader!. Skipped", type.getQualifiedName() ));
			} catch (IntrospectionException e) {
				warn( String.format("error processing metadata over class [%s]!. Skipped", type.getQualifiedName() ), e );
			}
        	
        }
        
        String sql = DDLUtil.generateSQL(db, optionMap.get("driver"), optionMap.get("connectionUrl"));
        
		try {
	        FileObject res = filer.createResource(StandardLocation.SOURCE_OUTPUT, "sql", "createDB.sql", (javax.lang.model.element.Element)null);

	        java.io.Writer w = res.openWriter();
	        
	        w.write(sql);
	        
	        w.close();
		} catch (IOException e) {
			warn( String.format("error writing output file" ), e );
			
			System.err.println( sql );
		}
        
        
        return false;
	}

}
