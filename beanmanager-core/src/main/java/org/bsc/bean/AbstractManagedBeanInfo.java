package org.bsc.bean;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;


/**
 * This is a support abstract class to make it easier for people to
 * provide ManagedBeanInfo classes.
 *
 */
public abstract class AbstractManagedBeanInfo<T> implements ManagedBeanInfo<T> {

	
	protected Class<T> beanClass;
	
        protected AbstractManagedBeanInfo( Class<? extends T> beanClass ) {
            setBeanClass( beanClass );
        }
        
	public Class<T> getBeanClass() {
               return beanClass;
	}

	@SuppressWarnings("unchecked")
	public void setBeanClass(Class<? extends T> beanClass) {
		this.beanClass = (Class<T>)beanClass;
	}

	public EventSetDescriptor[] getEventSetDescriptors() {
		return BeanManagerUtils.EMPTY_EVENTDESCRIPTOR;
	}

	public int getDefaultEventIndex() {
		return -1;
	}

	public int getDefaultPropertyIndex() {
		return -1;
	}

	public MethodDescriptor[] getMethodDescriptors() {
		return BeanManagerUtils.EMPTY_METHODDESCRIPTOR;
	}

	public BeanInfo[] getAdditionalBeanInfo() {
		return BeanManagerUtils.EMPTY_ADDITIONALBEANINFO;
	}

	public Image getIcon(int iconKind) {
		return null;
	}
}