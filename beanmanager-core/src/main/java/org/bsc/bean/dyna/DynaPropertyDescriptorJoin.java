package org.bsc.bean.dyna;

import java.beans.IntrospectionException;
import java.lang.reflect.Method;

import org.bsc.bean.BeanManagerUtils;
import org.bsc.bean.DataAdapter;
import org.bsc.bean.PropertyDescriptorJoin;
import org.bsc.bean.metadata.ColumnBean;
import org.bsc.util.Log;

public class DynaPropertyDescriptorJoin extends PropertyDescriptorJoin implements DynaPropertyDescriptor {
    private Method mappedRead;
    private Method mappedWrite;
    private Class<?> propertyType;

    /**
     * 
     * @param field
     * @throws IntrospectionException
     * @throws NoSuchMethodException
     */
    public DynaPropertyDescriptorJoin( DynaPropertyDescriptor field ) throws IntrospectionException, NoSuchMethodException {
    	this( field.getName(), null, null, null, field.getPropertyType());
    	
    	this.mappedRead = field.getMappedReadMethod();
    	this.mappedWrite = field.getMappedWriteMethod();
    	
    }
    
    /**
     * 
     * @param propertyName
     * @param beanClass
     * @param getterName
     * @param setterName
     * @throws java.beans.IntrospectionException
     * @throws java.lang.NoSuchMethodException
     */
    public DynaPropertyDescriptorJoin(
                            String propertyName,
                            Class<?> beanClass,                            
                            String getterName,
                            String setterName,
                            Class<?> propertyType) throws IntrospectionException, NoSuchMethodException
    {
        super( propertyName, null, null );
  
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

    public DynaPropertyDescriptorJoin( ColumnBean columnMetaData,
                                        Class<?> beanClass,                            
                                        String getterName,
                                        String setterName ) throws Exception
    {
        super( columnMetaData.getName(), null, null );
  
        this.propertyType = Object.class;
  
        setSQLType( columnMetaData.getType() );
        setAdapter( DataAdapter.GENERIC );
        setSize( columnMetaData.getSize() );
        
        if( null!=columnMetaData.getDefault())
            super.setValue(DEFAULT_VALUE, columnMetaData.getDefault());
        
        Log.trace( "colunmMetaData.type={0} sqlType={1} adapter={2}", columnMetaData.getType(), getSQLType(), BeanManagerUtils.lookupAdapter(this) );
        
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
