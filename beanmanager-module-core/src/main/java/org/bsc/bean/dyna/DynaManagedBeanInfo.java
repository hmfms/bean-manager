/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.dyna;

import static org.bsc.bean.PropertyDescriptorField.FIELDNAME;

import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bsc.bean.AbstractManagedBeanInfo;
import org.bsc.bean.BeanDescriptorUtils;
import org.bsc.bean.BeanManager;
import org.bsc.bean.BeanManagerFactory;
import org.bsc.bean.metadata.ColumnBean;
import org.bsc.bean.metadata.ColumnBeanInfo;
/**
 *
 * @author Sorrentino
 */
public class DynaManagedBeanInfo<T> extends AbstractManagedBeanInfo<T> {

    private BeanDescriptor descriptor = null;
    private Map<Object,PropertyDescriptor> properties = new LinkedHashMap<Object,PropertyDescriptor>();
    
    public DynaManagedBeanInfo() {
        super( null );
    }
    
    /**
     * 
     * @param descriptor
     */
    public void setBeanDescriptor( BeanDescriptor descriptor  ) {

        if( null!=this.descriptor ) {
            throw new IllegalArgumentException( "BeanDescriptor already exist!");
        }
        
        this.descriptor = descriptor;
        setBeanClass( (Class<? extends T>) descriptor.getBeanClass());
    }
    
    /**
     * 
     * @return
     */
    public BeanDescriptor getBeanDescriptor() {
        return descriptor;
    }

    /**
     * 
     * @return
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return properties.values().toArray( new PropertyDescriptor[properties.size()]);
    }

    /**
     * 
     * @param pd
     */
    public void addPropertyDescriptor( PropertyDescriptor pd ) {
        
        Object fieldName = pd.getValue(FIELDNAME);
        
        if( null==fieldName) {
            throw new IllegalStateException( "fieldName for property " + pd.getName() + " is not set!");
        }
        
        if( properties.containsKey(fieldName)) {
            throw new IllegalArgumentException( "property " + fieldName + " already exist!");
        }
        properties.put(fieldName.toString().toUpperCase(), pd);
    }

    /**
     * Add dynaPropertyDescriptor from database meta data using getColumns feature
     * 
     * pre condition are:
     * - BeanDescriptor(Entity) must be already set (need of table name)
     * 
     * This method does't overwrite the property descriport already set
     * 
     * @param metaData - data base metadata aquired through call connection.getMetaData()
     * @catalog - a catalog name; must match the catalog name as it is stored in the database; "" retrieves those without a catalog; null means that the catalog name should not be used to narrow the search
     * @schemaPattern - a schema name pattern; must match the schema name as it is stored in the database; "" retrieves those without a schema; null means that the schema name should not be used to narrow the search
     * @param getter - generic getter method name (i.e. Object <getter>( String name ) )
     * @param setter - generic setter method name (i.e. void <setter>( String name, Object value )
     * @see java.sql.DatabaseMetaData#getColumns
     */
    public void addDynaPropertyDescriptors(  DatabaseMetaData metaData, 
                                         String catalog, 
                                         String schemaPattern, 
                                         String getter, 
                                         String setter ) throws Exception {
    {
        final String msg = "metaData parameter is null!";
        assert null!=metaData : msg;
        
        if( null==metaData) 
            throw new IllegalArgumentException(msg);
        }

        BeanDescriptor desc = getBeanDescriptor();
 
        {
        final String msg = "BeanDescriptor not set yet!";
        assert null!=desc : msg;
        
        if( null==desc) 
            throw new IllegalStateException(msg);
        }

        String entityName = BeanDescriptorUtils.getEntityName(desc);
        
        {
        final String msg = "Entity Name not set!";
        assert null!=entityName : msg;
        
        if( null==entityName) 
            throw new IllegalStateException(msg);
        }
 
        ResultSet rs = metaData.getColumns(catalog, schemaPattern, entityName, null);
        
        BeanManager<ColumnBean> manager = (BeanManager<ColumnBean>) BeanManagerFactory.getFactory().createBeanManager(ColumnBean.class, new ColumnBeanInfo());
        
        while( rs.next() ) {
                
            ColumnBean bean = manager.instantiateBean();
        
            manager.setBeanProperties(bean, rs);

            String fieldName = bean.getName().toUpperCase();
            
            if( !properties.containsKey(fieldName) ) {
                
                DynaPropertyDescriptorField f = new DynaPropertyDescriptorField( bean, getBeanClass(), getter, setter);
                
                properties.put(fieldName, f);
            }
        }
        

    }
    

}
