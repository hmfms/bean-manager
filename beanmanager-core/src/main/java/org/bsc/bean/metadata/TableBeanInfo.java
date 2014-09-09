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
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.bean.PropertyDescriptorPK;
import org.bsc.util.Log;

/**
 *
 * @author Sorrentino
 */
public class TableBeanInfo extends AbstractManagedBeanInfo<TableBean> {

    public TableBeanInfo() {
        super( TableBean.class );
    }

    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptorEntity(getBeanClass(), "TABLES");
    }

    /**
     *Retrieves a description of the tables available in the given catalog. 
     * Only table descriptions matching the catalog, schema, table name and type criteria are returned. 
     * They are ordered by TABLE_TYPE, TABLE_SCHEM and TABLE_NAME.

     * Each table description has the following columns:

     *  1. TABLE_CAT String => table catalog (may be null)
     *  2. TABLE_SCHEM String => table schema (may be null)
     *  3. TABLE_NAME String => table name
     *  4. TABLE_TYPE String => table type. Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
     *  5. REMARKS String => explanatory comment on the table
     *  6. TYPE_CAT String => the types catalog (may be null)
     *  7. TYPE_SCHEM String => the types schema (may be null)
     *  8. TYPE_NAME String => type name (may be null)
     *  9. SELF_REFERENCING_COL_NAME String => name of the designated "identifier" column of a typed table (may be null)
     * 10. REF_GENERATION String => specifies how values in SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER", "DERIVED". (may be null) 
     *
     * Note: Some databases may not return information for all tables.


     * @return
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
    
        try {

            PropertyDescriptor[] result = {
                new PropertyDescriptorPK("table_name", getBeanClass(), "getName", "setName")
                        .setSQLType(Types.VARCHAR), 
                new PropertyDescriptorField("table_schem", getBeanClass(), "getSchema", "setSchema"),
                new PropertyDescriptorField("table_type", getBeanClass(), "getType", "setType"),
                new PropertyDescriptorField("remarks", getBeanClass(), "getRemarks", "setRemarks")
            };

            return result;
        } catch (IntrospectionException ex) {
            Log.error( "getBeanDescriptor", ex);
        }
        
        return null;
    }

}
