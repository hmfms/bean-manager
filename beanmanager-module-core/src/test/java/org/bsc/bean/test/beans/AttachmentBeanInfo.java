package org.bsc.bean.test.beans;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.sql.Types;

import org.bsc.bean.AbstractManagedBeanInfo;
import org.bsc.bean.BeanDescriptorEntity;
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.bean.PropertyDescriptorPK;

public class AttachmentBeanInfo extends AbstractManagedBeanInfo<Attachment> {

	public AttachmentBeanInfo() {
		this( Attachment.class );
	}
	
	protected AttachmentBeanInfo(Class<? extends Attachment> beanClass) {
		super(beanClass);
	}

	public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptorEntity(getBeanClass(), "ATTACHMENTS");
	}

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			return new PropertyDescriptor[] { 

			        new PropertyDescriptorPK( "id", getBeanClass(), "getId", "setId")
	                	.setSQLType(Types.VARCHAR)
	                	.setSize(50),

	                new PropertyDescriptorField( "content", getBeanClass(), "getContent", "setContent")
			                .setSQLType(Types.CLOB)


					
			};
			
		} catch (IntrospectionException e) {
			
			throw new RuntimeException(e);
		}
	}

}
