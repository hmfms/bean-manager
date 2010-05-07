/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.metadata;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.sql.Types;

import org.bsc.bean.AbstractManagedBeanInfo;
import org.bsc.bean.BeanDescriptorEntity;
import org.bsc.bean.DataAdapter;
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.bean.PropertyDescriptorPK;
import org.bsc.bean.adapters.VARCHARToIntegerAdapter;
import org.bsc.util.Log;

/**
 *
 * @author Sorrentino
 */
public class ColumnBeanInfo extends AbstractManagedBeanInfo<ColumnBean> {

    public ColumnBeanInfo() {
        super( ColumnBean.class );
    }
    
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptorEntity(getBeanClass(), "COLUMNS");
    }

    /**
     * 
     * COLUMN table_cat=
       COLUMN table_schem=APP
       COLUMN table_name=CUSTOMER
       COLUMN column_name=ID
       COLUMN data_type=4
       COLUMN type_name=INTEGER
       COLUMN column_size=10
       COLUMN buffer_length=null
       COLUMN decimal_digits=0
       COLUMN num_prec_radix=10
       COLUMN nullable=0
       COLUMN remarks=
       COLUMN column_def=null
       COLUMN sql_data_type=null
       COLUMN sql_datetime_sub=null
       COLUMN char_octet_length=null
       COLUMN ordinal_position=3
       COLUMN is_nullable=NO
       COLUMN scope_catlog=null
       COLUMN scope_schema=null
       COLUMN scope_table=null
       COLUMN source_data_type=null
       COLUMN is_autoincrement=NO

     * @return
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        DataAdapter VC2INT = new VARCHARToIntegerAdapter();
        
        try {

            PropertyDescriptor[] result = {
                new PropertyDescriptorPK("column_name", getBeanClass(), "getName", "setName")
                        .setSQLType(Types.VARCHAR), 
                new PropertyDescriptorField("data_type", getBeanClass(), "getType", "setType")
                        .setAdapter(VC2INT), 
                new PropertyDescriptorField("type_name", getBeanClass(), "getTypeName", "setTypeName"), 
                new PropertyDescriptorField("column_size", getBeanClass(), "getSize", "setSize")
                        .setAdapter(VC2INT),
                new PropertyDescriptorField("column_def", getBeanClass(), "getDefault", "setDefault")
            };

            return result;
        } catch (IntrospectionException ex) {
            Log.error( "getBeanDescriptor", ex);
        }
        
        return null;
    }

    
}
