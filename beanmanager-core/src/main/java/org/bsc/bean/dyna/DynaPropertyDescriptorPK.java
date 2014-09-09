/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.dyna;

import java.beans.IntrospectionException;
import java.lang.reflect.Method;

import org.bsc.bean.BeanManagerUtils;
import org.bsc.bean.PropertyDescriptorPK;

/**
 *
 * @author Sorrentino
 */
public class DynaPropertyDescriptorPK extends PropertyDescriptorPK implements DynaPropertyDescriptor {

    private Method mappedRead;
    private Method mappedWrite;
    private Class<?> propertyType;
    
    /**
     * 
     * @param propertyName
     * @param beanClass
     * @param getterName
     * @param setterName
     * @throws java.beans.IntrospectionException
     * @throws java.lang.NoSuchMethodException
     */
    public DynaPropertyDescriptorPK(
                            String propertyName,
                            Class<?> beanClass,
                            String getterName,
                            String setterName,
                            Class<?> propertyType) throws IntrospectionException, NoSuchMethodException
    {
        super( propertyName, beanClass, null, null );
       
        this.propertyType = propertyType;

        setSQLType( BeanManagerUtils.getSQLType(propertyType));

        if( null!=getterName) {
            try {
                mappedRead = beanClass.getMethod(getterName, String.class);            
            }
            catch( NoSuchMethodException ex ) {
                mappedRead = beanClass.getMethod(getterName, Object.class);            
            }
                    
        }
        if( null!=setterName ) {
            try {
                mappedWrite = beanClass.getMethod(setterName, String.class, Object.class);            
            }
            catch( NoSuchMethodException ex ) {
                mappedWrite = beanClass.getMethod(setterName, Object.class, Object.class);
            }
        }
        
        
    }

    @Override
    public Class<?> getPropertyType() {
      return propertyType;  
    } 
    
    public Method getMappedReadMethod() {
        return mappedRead;
    }

    public Method getMappedWriteMethod() {
        return mappedWrite;
    }
}
