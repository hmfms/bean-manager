
package org.bsc.bean;

import java.beans.BeanInfo;

/**
 * 
 * @author Sorrentino
 * @param T
 */
class BeanManagerImpl<T> extends AbstractBeanManager<T> {


  /**
   *
   * constructor for provide a custom bean info
   *
  @param bean the bean class
  @param beanInfo custom BeanInfo object
  */
  protected BeanManagerImpl( Class<T> bean, BeanInfo beanInfo ) {
    super( bean, beanInfo );
  }

  /**
   *
  @param beanInfo custom ManagedBeanInfo object
  */
  protected BeanManagerImpl( ManagedBeanInfo<T> beanInfo ) {
    super( beanInfo );
  }


    /**
    *
    * @param rs
    * @return
    */
    @SuppressWarnings("unchecked")
    public T instantiateBean() throws InstantiationException
    {
        return (T) BeanManagerUtils.instantiateBean(super.getBeanInfo());
    }

} // End class BeanManagerBase


