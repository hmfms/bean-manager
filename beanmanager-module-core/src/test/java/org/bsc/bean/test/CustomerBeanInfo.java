package org.bsc.bean.test;

import static org.bsc.bean.BeanManagerUtils.EMPTY_PROPERTYDESCRIPTOR;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import java.sql.Types;
import org.bsc.bean.AbstractManagedBeanInfo;
import org.bsc.bean.BeanDescriptorEntity;
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.bean.PropertyDescriptorPK;
import org.bsc.bean.adapters.CLOBAdapter;
import org.bsc.bean.adapters.CharBooleanAdapter;

/**
 * 
 * @author Sorrentino
 *
 */
public class CustomerBeanInfo extends AbstractManagedBeanInfo<Customer> {


	public CustomerBeanInfo() {
            super(Customer.class);
	}

	public BeanDescriptor getBeanDescriptor() {
		return new BeanDescriptorEntity(getBeanClass(), "CUSTOMER");
	}

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
                    return new PropertyDescriptor[] {

                        new PropertyDescriptorPK("id", getBeanClass(), "getCustomerId", "setCustomerId" ),
                        new PropertyDescriptorField("firstName", getBeanClass(), "getFirstName", "setFirstName" )
                                .setFieldName("FIRST_NAME"),
                        new PropertyDescriptorField("lastName", getBeanClass(), "getLastName", "setLastName" )
                                .setFieldName("LAST_NAME"),
                        new PropertyDescriptorField("account_id", getBeanClass(), "getAccountId", "setAccountId" )
                                .setSQLType(Types.INTEGER),
                        new PropertyDescriptorField("vip", getBeanClass(), "isVip", "setVip" )
                                .setSQLType(Types.CHAR)
                                .setSize(1)
                                .setAdapter( new CharBooleanAdapter()),
                        new PropertyDescriptorField("note", getBeanClass(), "getNote", "setNote" )
                                .setSQLType(Types.CLOB)
                                .setSize(32*1024)
                                .setRequired(false)
                    };
		} catch (IntrospectionException e) {
			e.printStackTrace();
			
		}
		
		return EMPTY_PROPERTYDESCRIPTOR;
	}
	
}
