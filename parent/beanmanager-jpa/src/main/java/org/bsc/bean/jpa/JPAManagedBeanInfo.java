/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.jpa;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.bsc.bean.BeanDescriptorEntity;
import org.bsc.bean.BeanManagerUtils;
import org.bsc.bean.ManagedBeanInfo;
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.bean.PropertyDescriptorPK;
import org.bsc.bean.generators.UUIDValueGenerator;
import org.bsc.util.Log;

/**
 *
 * @author softphone
 */
public class JPAManagedBeanInfo<T> implements  ManagedBeanInfo<T> {


    /**
     * 
     * @param <T>
     * @param result
     * @param beanClass
     * @param properties
     * @throws IntrospectionException
     */
    private static <T> void processFields( JPAManagedBeanInfo result, Class<T> beanClass,  PropertyDescriptor[] properties )  {

        for( PropertyDescriptor pd : properties ) {
            try {
                Field f = beanClass.getDeclaredField(pd.getName());

                Transient t = f.getAnnotation( Transient.class );
                if( t!=null ) {
                    Log.debug( "the field [{0}] is transient",  pd.getName() );

                    result.properties.put( pd.getName(), pd);
                    continue;

                }

                PropertyDescriptorField pf = null;

                Id id = f.getAnnotation( Id.class );
                if( id!=null ) {
                    if( !pd.getPropertyType().equals(String.class) )
                        throw new UnsupportedOperationException("the property annotated as Id must be a String type!");
                    PropertyDescriptorPK pk = new PropertyDescriptorPK( pd );

                    GeneratedValue gv = f.getAnnotation(GeneratedValue.class);

                    if( gv != null ) {
                        pk.setValueGenerator( new UUIDValueGenerator() );
                    }

                    pf = pk;
                }
                else {
                    pf = new PropertyDescriptorField( pd );
                }

                pf.setSQLType( BeanManagerUtils.getSQLType(pd.getPropertyType()));
                
                result.properties.put( pd.getName(), pf);
                
            } catch (IntrospectionException ex) {
                Log.error( "reflection issue on the field [{0}]", ex, pd.getName() );
            } catch (NoSuchFieldException ex) {
                Log.error( "the field [{0}] doesn't exist!", pd.getName() );
            } catch (SecurityException ex) {
                Log.error( "security issue on the field [{0}]", ex, pd.getName() );
            }
        }

        
    }
    public static <T> JPAManagedBeanInfo<T> create( Class<T> beanClass ) throws IntrospectionException {
        if( beanClass == null ) throw new IllegalArgumentException( "beanClass parametere is null!");

        JPAManagedBeanInfo result = new JPAManagedBeanInfo();

        //BeanInfo beanInfo = BeanManagerUtils.loadBeanInfo( JPAManagedBeanInfo.class.getClassLoader(), beanClass);
        BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(beanClass);

        if( null==beanInfo ) throw new IllegalStateException( "beanInfo not found!");
        
        String tableName = null;

        Entity entity = beanClass.getAnnotation( Entity.class );
        if( entity == null ) {
            throw new IllegalArgumentException( String.format("class [%s] is not an Entity!", beanClass.getName()));
        }

        tableName = beanClass.getSimpleName();
        Table table = beanClass.getAnnotation( Table.class );
        if( table != null ) {

            tableName = table.name();

        }

        Log.debug( "table name [{0}]", tableName);

        result.beanDescriptor = new BeanDescriptorEntity( beanClass, tableName );

        // TODO
        //BeanDescriptor bd = beanInfo.getBeanDescriptor();
        //result.beanDescriptor = new BeanDescriptorEntity( bd );
        //result.beanDescriptor.setEntityName(tableName);

        PropertyDescriptor pd[] = beanInfo.getPropertyDescriptors();

        result.properties = new java.util.LinkedHashMap<String, PropertyDescriptor>( pd.length );

        processFields( result, beanClass, pd );

        return result;
        
    }

    protected Class<T> beanClass;

    protected java.util.Map<String,PropertyDescriptor> properties = null;
    protected BeanDescriptorEntity beanDescriptor = null;

    protected JPAManagedBeanInfo() {
    }

    public Class<T> getBeanClass() {
       return beanClass;
    }

    public void setBeanClass(Class<? extends T> type) {
        beanClass = (Class<T>) type;
    }

    public BeanDescriptor getBeanDescriptor() {
        return beanDescriptor;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return properties.values().toArray( new PropertyDescriptor[ properties.size() ]);
    }

    public EventSetDescriptor[] getEventSetDescriptors() {
        return new EventSetDescriptor[0];
    }

    public int getDefaultEventIndex() {
        return -1;
    }

    public int getDefaultPropertyIndex() {
        return -1;
    }

    public MethodDescriptor[] getMethodDescriptors() {
        return new MethodDescriptor[0];
    }

    public BeanInfo[] getAdditionalBeanInfo() {
        return null;
    }

    public Image getIcon(int i) {
        return null;
    }


}
