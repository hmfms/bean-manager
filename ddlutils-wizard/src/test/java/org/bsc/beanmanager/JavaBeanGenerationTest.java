package org.bsc.beanmanager;

import static org.bsc.beanmanager.BeanGeneratorUtils.addAttribute;
import static org.bsc.beanmanager.BeanGeneratorUtils.addProperty;
import static org.bsc.beanmanager.BeanGeneratorUtils.addPropertyDescriptorField;
import static org.bsc.beanmanager.BeanGeneratorUtils.addPropertyDescriptorPK;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.junit.Before;
import org.junit.Test;

import biz.source_code.miniTemplator.MiniTemplator;

public class JavaBeanGenerationTest {

	Database db = null;
	
	@Before
	public void init() {
		
		java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("testDB.xml");
		
        db = new DatabaseIO().read( new java.io.InputStreamReader(is) );

	}
	
	@Test
	public void generateBean() throws Exception {
		
		java.io.Reader reader = new java.io.InputStreamReader( getClass().getClassLoader().getResourceAsStream("Bean.txt"));
		  	
		for( Table table : db.getTables() ) {
			MiniTemplator t = new MiniTemplator(reader);
			
			t.setVariable ("className", table.getName());
					
			for( Column c : table.getColumns() ) {
					addAttribute( t, c );
			
			}
			
			for( Column c : table.getColumns() ) {
				addProperty( t, c );
		
			}

			File outputDir = new File( "target/generatedSources" );
			outputDir.mkdirs();
			
			File bean = new File( outputDir, String.format("%s.java", table.getName() ) );

			t.generateOutput(bean);
		}
		
		
		
		//t.generateOutput (outputFileName);

	}
	
	@Test
	public void generateBeanInfo() throws Exception {
		
		java.io.Reader reader = new java.io.InputStreamReader( getClass().getClassLoader().getResourceAsStream("BeanInfo.txt"));
		  	
		for( Table table : db.getTables() ) {
			MiniTemplator t = new MiniTemplator(reader);
			
			t.setVariable ("className", table.getName());
			t.setVariable ("tableName", table.getName());
			
			java.util.List<Column> pkList = new java.util.ArrayList<Column>(table.getColumnCount());
			java.util.List<Column> fieldList = new java.util.ArrayList<Column>(table.getColumnCount());
			
			Predicate pkPredicate = new Predicate() {
				public boolean evaluate(Object o) {
					Column c = (Column) o;
						
					return c.isPrimaryKey();
				}			
			};
			
			CollectionUtils.select( Arrays.asList(table.getColumns()), pkPredicate, pkList );
			CollectionUtils.selectRejected( Arrays.asList(table.getColumns()), pkPredicate, fieldList );
			
			
			// PK
			int i = 0;
			for( Column c : pkList ) {
				addPropertyDescriptorPK( t, c, fieldList.isEmpty() && (++i==pkList.size()) );
			}

			// FIELD
			i = 0 ;
			for( Column c : fieldList ) {
				addPropertyDescriptorField( t, c, (++i==fieldList.size()));
			}
			
			File outputDir = new File( "target/generatedSources" );
			outputDir.mkdirs();
			
			File beanInfo = new File( outputDir, String.format("%sBeanInfo.java", table.getName() ) );
			
			t.generateOutput(beanInfo);
		}
		
		
		
		//t.generateOutput (outputFileName);

	}
  		
}
