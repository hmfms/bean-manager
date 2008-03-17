package org.bsc.bean;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;

/**
 * 
 * @author Sorrentino
 *
 * @param <T>
 */
public class ManagedBeanInfoProxy<T> implements ManagedBeanInfo<T> {

	protected BeanInfo delegate;
	private Class<T> beanClass;
	
	/**
	 * 
	 * @param beanInfo
	 * @param beanClass
	 */
	@SuppressWarnings("unchecked")
	public ManagedBeanInfoProxy( BeanInfo beanInfo, Class<T> beanClass ) {
		if( null==beanInfo ) throw new IllegalArgumentException("beaninfo is null!");
		if( null==beanClass ) throw new IllegalArgumentException("bean class is null!");
		
		delegate = beanInfo;
		this.beanClass = beanClass;

		if( delegate instanceof ManagedBeanInfo ) {
			((ManagedBeanInfo<T>)delegate).setBeanClass(beanClass);
		}
	}

	/**
	 * 
	 * @param beanInfo
	 * @param beanClass
	 */
	@SuppressWarnings("unchecked")
	public ManagedBeanInfoProxy( ManagedBeanInfo<? extends T> beanInfo ) {
		if( null==beanInfo ) throw new IllegalArgumentException("beaninfo is null!");
		
		delegate = beanInfo;
	}

	/**
	 * 
	 */
	public String toString() {
		return delegate.toString();
	}


	@SuppressWarnings("unchecked")
	public Class<T> getBeanClass() {
		if( delegate instanceof ManagedBeanInfo ) {
			return ((ManagedBeanInfo<T>)delegate).getBeanClass();
		}
		return beanClass;
	}

	@SuppressWarnings("unchecked")
	public void setBeanClass(Class<? extends T> beanClass) {
		if( delegate instanceof ManagedBeanInfo ) {
			((ManagedBeanInfo<T>)delegate).setBeanClass(beanClass);
		}
		else {
			throw new UnsupportedOperationException("setBeanClass() is not supported!");
		}

	}

	public BeanInfo[] getAdditionalBeanInfo() {
		return delegate.getAdditionalBeanInfo();
	}

	public BeanDescriptor getBeanDescriptor() {
		return delegate.getBeanDescriptor();
	}

	public int getDefaultEventIndex() {
		return delegate.getDefaultEventIndex();
	}

	public int getDefaultPropertyIndex() {
		return delegate.getDefaultPropertyIndex();
	}

	public EventSetDescriptor[] getEventSetDescriptors() {
		return delegate.getEventSetDescriptors();
	}

	public Image getIcon(int iconKind) {
		return delegate.getIcon(iconKind);
	}

	public MethodDescriptor[] getMethodDescriptors() {
		return delegate.getMethodDescriptors();
	}

	public PropertyDescriptor[] getPropertyDescriptors() {
		return delegate.getPropertyDescriptors();
	}

}
