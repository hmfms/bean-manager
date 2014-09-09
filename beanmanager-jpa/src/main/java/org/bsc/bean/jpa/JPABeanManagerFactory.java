/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.jpa;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import org.bsc.bean.AggregateBeanManager;
import org.bsc.bean.BeanManager;
import org.bsc.bean.BeanManagerFactory;
import org.bsc.bean.ManagedBeanInfo;
import org.bsc.util.Log;
import org.kohsuke.MetaInfServices;

/**
 *
 * @author bsorentino
 */
@MetaInfServices(BeanManagerFactory.class)
public class JPABeanManagerFactory extends BeanManagerFactory {

  /**
   *
   * @param beanClass
   * @param beanInfo
   * @return
   */
  @Override
  public <T> BeanManager<T> createBeanManager( Class<T> beanClass, BeanInfo beanInfo )
  {
        try {
            JPAManagedBeanInfo<T> _beanInfo;
            
            if( beanInfo instanceof JPAManagedBeanInfo ) {
                _beanInfo = (JPAManagedBeanInfo<T>) beanInfo;
            }
            else {
                _beanInfo = JPAManagedBeanInfo.create(beanClass);
            }

            BeanInfo[] additionalBeanInfo = _beanInfo.getAdditionalBeanInfo();


            if (additionalBeanInfo != null && additionalBeanInfo.length > 0) {

                BeanManager<?> aggregateBeanManager[] = new BeanManager[additionalBeanInfo.length];

                for (int i = 0; i < additionalBeanInfo.length; ++i) {
                    aggregateBeanManager[i] = new JPABeanManager((ManagedBeanInfo) additionalBeanInfo[i]);
                }
                return new AggregateBeanManager(new JPABeanManager<T>(_beanInfo), aggregateBeanManager);
            } else {
                return new JPABeanManager<T>(_beanInfo);
            }

        } catch (IntrospectionException ex) {
            Log.error("error reading metadata from [{0}]", ex, beanClass.getName());
        }

        return null;
      
  }

    @Override
    public <T> BeanManager<T> createBeanManager(Class<T> beanClass) {
        return this.createBeanManager(beanClass,null);
    }


 
}
