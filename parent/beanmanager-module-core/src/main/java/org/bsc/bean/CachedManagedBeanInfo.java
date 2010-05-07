package org.bsc.bean;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

/**
 *
 * <p>Title: Bean Manager </p>
 * <p>Description: ORM framework</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author BARTOLOMEO Sorrentino
 * @version 2.2.0
 */
class CachedManagedBeanInfo<T> extends ManagedBeanInfoProxy<T> {

  private PropertyDescriptor[] propertyDescriptors = null;
  private BeanDescriptor beanDescriptor = null;
  private BeanInfo[] additionalBeanInfo = null;

  /**
   *
   * @param beanInfo ManagedBeanInfo
   */
  public CachedManagedBeanInfo( BeanInfo beanInfo, Class <T> beanClass ) {
    super( beanInfo, beanClass );
    
  }

	/**
	 *
	 * @param beanInfo ManagedBeanInfo
	 */
	public CachedManagedBeanInfo( ManagedBeanInfo<T> beanInfo ) {
	   super( beanInfo );
	   
	}

  /**
   *
   * @param beanClass Class
   */
  public void setBeanClass(Class<? extends T> beanClass) {
      super.setBeanClass( beanClass );
      // INVALIDATE CACHE
      propertyDescriptors = null;
      beanDescriptor = null;
  }
  
  /**
   *
   * @return BeanDescriptor
   */
  public BeanDescriptor getBeanDescriptor() {
    if( beanDescriptor==null ) {
      beanDescriptor = super.getBeanDescriptor();
    }
    return beanDescriptor;
  }

  /**
   *
   * @return BeanInfo[]
   */
  public BeanInfo[] getAdditionalBeanInfo() {
    if( additionalBeanInfo==null ) {
      additionalBeanInfo = super.getAdditionalBeanInfo();
    }
    return additionalBeanInfo;
  }

  /**
   *
   * @return PropertyDescriptor[]
   */
  public PropertyDescriptor[] getPropertyDescriptors() {
    if( propertyDescriptors==null ) {
      propertyDescriptors = super.getPropertyDescriptors();
    }
    return propertyDescriptors;
  }


}
