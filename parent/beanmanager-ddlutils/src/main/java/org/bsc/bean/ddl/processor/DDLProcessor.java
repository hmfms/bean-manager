package org.bsc.bean.ddl.processor;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
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
import org.bsc.bean.ddl.ClassDiscovery;
import org.bsc.bean.ddl.DDLUtil;

@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes("*")
@SupportedOptions( {"driver", "connectionUrl", "seedClass"} )
public class DDLProcessor extends AbstractProcessor {

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
