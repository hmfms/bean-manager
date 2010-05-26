/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.jpa;

import java.beans.BeanInfo;
import java.beans.Beans;
import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils.PropertyUtils;
import org.bsc.bean.AbstractBeanManager;
import org.bsc.bean.ManagedBeanInfo;
import org.bsc.bean.ManagedBeanInfoProxy;
import org.bsc.util.Log;

/**
 *
 * @author bsorrentino
 */
public class JPABeanManager<T> extends AbstractBeanManager<T> {

    public JPABeanManager(ManagedBeanInfo<T> beanInfo) {
        super( beanInfo );
    }

    public JPABeanManager(Class<T> bean, BeanInfo beanInfo) {
        super( bean, beanInfo);
    }



    @Override
    protected Object invokeReadMethod(PropertyDescriptor pd, Object beanInstance) throws Exception {
        String rattr = (String) pd.getValue("relation.attribute");

        if( rattr!=null ) {
              String nestedProperty =  String.format( "%s.%s", pd.getName(), rattr );
              Log.debug( "====> GET JOIN PROPERTY " + nestedProperty);
              return PropertyUtils.getNestedProperty(beanInstance, nestedProperty);

        }
        return super.invokeReadMethod(pd, beanInstance);
    }

    @Override
    protected Object invokeWriteMethod(PropertyDescriptor pd, Object beanInstance, Object value) throws Exception {

        String rname = (String) pd.getValue("relation.name");
        if( rname!=null ) {
              String nestedProperty =  String.format( "%s.%s", rname,pd.getName() );
              Log.debug( "====> SET JOIN PROPERTY [{0}]=[{1}]", nestedProperty, value.getClass().getName());
              PropertyUtils.setNestedProperty(beanInstance, nestedProperty, value);

              return null;

        }
        String rattr = (String) pd.getValue("relation.attribute");

        if( rattr!=null ) {
              String nestedProperty =  String.format( "%s.%s", pd.getName(), rattr );
              Log.debug( "====> SET JOIN PROPERTY {0}]=[{1}]", nestedProperty, value );
              PropertyUtils.setNestedProperty(beanInstance, nestedProperty, value);

              return null;

        }
        return super.invokeWriteMethod(pd, beanInstance, value);
    }

    @SuppressWarnings("unchecked")
	@Override
    public T instantiateBean() throws java.lang.InstantiationException  {
        Log.debug( "INSTANTIATE BEAN [{0}]", getBeanClass()  );

        try {
            T result =  (T) Beans.instantiate( getBeanClass().getClassLoader(), getBeanClass().getName());

            Log.debug( "INSTANTIATE BEAN [{0}]", result  );

            BeanInfo bi = super.getBeanInfo();
            JPAManagedBeanInfo<?> beanInfo = null;

            if( bi instanceof ManagedBeanInfoProxy ) {
                beanInfo = (JPAManagedBeanInfo<?>)((ManagedBeanInfoProxy)bi).getDelegate();
            }
            else {
                beanInfo = (JPAManagedBeanInfo<?>)bi;
            }


            if( beanInfo.relationBeanList!=null ) {

                for( JPAManagedBeanInfo.RelationBeanFactory rbf : beanInfo.relationBeanList) {

                    rbf.createAndSet(result);
                }
            }

            return result;

        } catch (Exception ex) {
            // TODO logging
            throw new InstantiationException(ex.getMessage());
        }
    }

}
