package org.bsc.beanmanager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Types;

import org.apache.ddlutils.model.Column;
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
	 

}
