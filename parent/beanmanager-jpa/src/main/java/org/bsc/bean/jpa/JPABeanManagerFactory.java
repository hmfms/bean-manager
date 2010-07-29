/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.jpa;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import org.bsc.bean.AggregateBeanManager;
import org.bsc.bean.BeanManager;
import org.bsc.bean.ManagedBeanInfo;
import org.bsc.util.Log;

/**
 *
 * @author bsorentino
 */
public class JPABeanManagerFactory  {

    @SuppressWarnings("unchecked")
    public static <T> BeanManager<T> createBeanManager(Class<T> beanClass) {

        try {
            JPAManagedBeanInfo<T> beanInfo = JPAManagedBeanInfo.create(beanClass);

            BeanInfo[] additionalBeanInfo = beanInfo.getAdditionalBeanInfo();


            if (additionalBeanInfo != null && additionalBeanInfo.length > 0) {

                BeanManager<?> aggregateBeanManager[] = new BeanManager[additionalBeanInfo.length];

                for (int i = 0; i < additionalBeanInfo.length; ++i) {
                    aggregateBeanManager[i] = new JPABeanManager((ManagedBeanInfo) additionalBeanInfo[i]);
                }
                return new AggregateBeanManager(new JPABeanManager<T>(beanInfo), aggregateBeanManager);
            } else {
                return new JPABeanManager<T>(beanInfo);
            }

        } catch (IntrospectionException ex) {
            Log.error("error reading metadata from [{0}]", ex, beanClass.getName());
        }

        return null;
    }

}
