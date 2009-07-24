package org.bsc.beanmanager;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.bsc.bean.BeanManagerUtils;

import biz.source_code.miniTemplator.MiniTemplator;

public class BeanGeneratorUtils {
	
	private BeanGeneratorUtils() {}
		
	
	public static String capitalize( String value ) {
		if( null==value ) throw new IllegalArgumentException( "value is null!");
		
		char [] cc = value.toCharArray();
		
		cc[0] = Character.toUpperCase(cc[0]);
		
		return String.valueOf(cc);
		
	}
	
	public static String getSQLType( int value ) throws Exception {
		for( Field f : Types.class.getFields() ) {
			if( Modifier.isStatic(f.getModifiers()) && f.getInt(null)==value ) {
				return String.format("Types.%s", f.getName());
			}
		}
	
		return null;
	}
	
	private static void addPropertyDescriptor( MiniTemplator t, String block, Column c, boolean isLast ) throws Exception {
		t.setVariable("name", c.getName());
		t.setVariable("propGet", String.format("get%s", capitalize(c.getName())));
		t.setVariable("propSet", String.format("set%s", capitalize(c.getName())));
		t.setVariable("sqlType", getSQLType(c.getTypeCode()));
		if( isLast ) {
			t.setVariable("sep", "");
		}
		else {
			t.setVariable("sep", ",");			
		}
		t.addBlock(block);	
	}
	
	/**
	 *	<!-- $BeginBlock propertiesPK -->
     *           new PropertyDescriptorPK( "${name}", getBeanClass(), "${propGet}", "${propSet}")
     *                   .setSQLType(${sqlType})${sep}
	 *	<!-- $EndBlock propertiesField -->
	 *
	 * @param t
	 * @param c
	 * @throws Exception
	 */
	 public static void addPropertyDescriptorPK( MiniTemplator t, Column c, boolean isLast ) throws Exception {
		 addPropertyDescriptor(t, "propertiesPK", c, isLast);
	 }

	 /**
	 *	<!-- $BeginBlock propertiesField -->
     *           new PropertyDescriptorField( "${name}", getBeanClass(), "${propGet}", "${propSet}")
     *                   .setSQLType(${sqlType})${sep}
	 *	<!-- $EndBlock propertiesField -->
	 *
	 * @param t
	 * @param c
	 * @throws Exception
	 */
	 public static void addPropertyDescriptorField( MiniTemplator t, Column c, boolean isLast ) throws Exception {
		 addPropertyDescriptor(t, "propertiesField", c, isLast);
	 }
	
	 
	public static void addAttribute( MiniTemplator t, Column c ) throws Exception {
			t.setVariable("name", c.getName());
			t.setVariable("type", BeanManagerUtils.getJavaType(c.getTypeCode(), true /*returnRawType*/, true /*ignoreSqlType*/).getName() );
			t.addBlock("attributes");	
	 }
	 
	public static void addProperty( MiniTemplator t, Column c ) throws Exception {
			t.setVariable("name", capitalize(c.getName()));
			t.setVariable("attributeName", c.getName());
			t.setVariable("type", BeanManagerUtils.getJavaType(c.getTypeCode(), true /*returnRawType*/, true /*ignoreSqlType*/).getName() );
			t.addBlock("properties");	
	}
	 
	public static void generateBean( List<GenerateBean> beanList, String packageName, File outputDir ) throws Exception {
		if( null==beanList ) throw new IllegalArgumentException( "beanList parameter is null!");
		if( null==outputDir ) throw new IllegalArgumentException( "outputDir parameter is null!");
		
		final String sep = System.getProperty("file.separator");

		if( !DDLWizardApplication.isEmpty(packageName)) {
			String newPath = new StringBuilder()
				.append( outputDir.getAbsolutePath() )
				.append( sep )
				.append( packageName.replace(".", sep) )
				.toString();
			outputDir = new File( newPath );
		}

		outputDir.mkdirs();
		
		for( GenerateBean bean : beanList ) {
			
			if( !bean.isSelected()) continue;

			java.io.Reader reader = new java.io.InputStreamReader( BeanGeneratorUtils.class.getClassLoader().getResourceAsStream("Bean.txt"));
		  	
			MiniTemplator t = new MiniTemplator(reader);
			
			t.setVariable ("className", bean.getBeanName());
			if( !DDLWizardApplication.isEmpty(packageName)) {
				t.setVariable ("package", String.format("package %s;",packageName));				
			}
					
			for( Column c : bean.table.getColumns() ) {
					addAttribute( t, c );
			
			}
			
			for( Column c : bean.table.getColumns() ) {
				addProperty( t, c );
		
			}

			
			File beanFile = new File( outputDir, String.format("%s.java", bean.getBeanName() ) );

			t.generateOutput(beanFile);
		}
	}
	
	public static void generateBeanInfo(List<GenerateBean> beanList, String packageName, File outputDir ) throws Exception {
		if( null==beanList ) throw new IllegalArgumentException( "beanList parameter is null!");
		if( null==outputDir ) throw new IllegalArgumentException( "outputDir parameter is null!");

		final String sep = System.getProperty("file.separator");
		
		if( !DDLWizardApplication.isEmpty(packageName)) {
			String newPath = new StringBuilder()
				.append( outputDir.getAbsolutePath() )
				.append( sep )
				.append( packageName.replace(".", sep) )
				.toString();
			outputDir = new File( newPath );
		}

		outputDir.mkdirs();
		

		for( GenerateBean bean : beanList ) {
			
			if( !bean.isSelected()) continue;

			java.io.Reader reader = new java.io.InputStreamReader( BeanGeneratorUtils.class.getClassLoader().getResourceAsStream("BeanInfo.txt"));
		  	
			MiniTemplator t = new MiniTemplator(reader);
			
			if( !DDLWizardApplication.isEmpty(packageName)) {
				t.setVariable ("package", String.format("package %s;",packageName));				
			}
			t.setVariable ("className", bean.getBeanName());
			t.setVariable ("tableName", bean.getTableName());
			
			java.util.List<Column> pkList = new java.util.ArrayList<Column>(bean.table.getColumnCount());
			java.util.List<Column> fieldList = new java.util.ArrayList<Column>(bean.table.getColumnCount());
			
			Predicate pkPredicate = new Predicate() {
				public boolean evaluate(Object o) {
					Column c = (Column) o;
						
					return c.isPrimaryKey();
				}			
			};
			
			CollectionUtils.select( Arrays.asList(bean.table.getColumns()), pkPredicate, pkList );
			CollectionUtils.selectRejected( Arrays.asList(bean.table.getColumns()), pkPredicate, fieldList );
			
			
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

			File beanInfo = new File( outputDir, String.format("%sBeanInfo.java", bean.getBeanName() ) );
			
			t.generateOutput(beanInfo);
			
		}

	}

}
