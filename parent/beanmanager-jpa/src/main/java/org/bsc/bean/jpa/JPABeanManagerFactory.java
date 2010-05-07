/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.jpa;

import java.beans.IntrospectionException;
import org.bsc.bean.BeanManager;
import org.bsc.util.Log;

/**
 *
 * @author bsorentino
 */
public class JPABeanManagerFactory  {

    public static <T> BeanManager<T> createBeanManager(Class<T> beanClass)  {
        
        try {
            JPAManagedBeanInfo<T> beanInfo = JPAManagedBeanInfo.create(beanClass);
            JPABeanManager<T> manager = new JPABeanManager<T>(beanInfo);
            return manager;
        } catch (IntrospectionException ex) {
            Log.error( "error reading metadata from [{0}]", ex, beanClass.getName());
        }

        return null;
    }

}
