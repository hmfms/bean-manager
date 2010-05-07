package org.bsc.bean;

import java.beans.BeanInfo;

/**
 * define a bean info that expose the associated bean class property
 */

public interface ManagedBeanInfo<T> extends BeanInfo {

  /**
   * get associated bean class
   *
   * @return bean class
   */
  public Class<T> getBeanClass();

  /**
   * set associated bean class
   * <pre>
   * implement this method if want share BeanInfo
   * between multiple bean class. (<i>useful for aggregate bean</i>)
   * </pre>
   * @param beanClass bean class
   */
  public void setBeanClass( Class<? extends T> beanClass );

}