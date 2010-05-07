/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.ddl;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.bsc.bean.BeanDescriptorUtils;
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.bean.PropertyDescriptorJoin;
import org.bsc.bean.PropertyDescriptorPK;

/**
 *
 * @author sorrentino
 */
public class DDLUtils {
    
/**
 * 
 * @param pd
 * @return
 */
public static Column fromPropertyDescriptorToColumn( PropertyDescriptor pd ) {
	
	Column result = null;
	
	if( pd instanceof PropertyDescriptorField ) {
	
	        if( pd instanceof PropertyDescriptorJoin ) return result;
	
	        PropertyDescriptorField f = (PropertyDescriptorField)pd;
	
	        result  = new Column();
	
	        result.setName( f.getFieldName() );
	        if( f.getSize()>0 ) result.setSizeAndScale( f.getSize(), 0 );
	        result.setTypeCode( f.getSQLType() );
	        result.setRequired( f.isRequided() );
	        result.setPrimaryKey( f instanceof PropertyDescriptorPK );
	
	}	
	
	return result;
}
	
	/**
	 * 
	 * @param info
	 * @return
	 */
	public static Table fromBeanInfoToTable( BeanInfo info ) {
		
		Table t = new Table();
		
		BeanDescriptor bd = info.getBeanDescriptor();
		
		String name = BeanDescriptorUtils.getEntityName(bd);
		
		if( null==name ) throw new IllegalArgumentException( "info is not a vaid beanManager beanInfo!");
		
		t.setName(name);
		
		
		for( PropertyDescriptor pd : info.getPropertyDescriptors() ) {

			Column c = fromPropertyDescriptorToColumn( pd );
			
			if( null!=c ) {
				t.addColumn(c);
			}
		}
		
		return t;
	}
	
	
	public static void createTablesModel( Database db, BeanInfo ...infos ) {
	
		for( BeanInfo info : infos ) {
			
			db.addTable( fromBeanInfoToTable(info) );
			
		}
	}

}
