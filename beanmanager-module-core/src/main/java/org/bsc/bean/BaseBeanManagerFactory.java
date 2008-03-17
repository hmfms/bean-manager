package org.bsc.bean;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.beans.BeanDescriptor;
import java.beans.BeanInfo;

/**
 *
 * @author Sorrentino
 */
public class BaseBeanManagerFactory extends BeanManagerFactory {
    
    /**
     * 
     */
    protected BaseBeanManagerFactory() {
    }
    
    /**
     *
     * @param beanClass
     * @param beanInfo
     * @return
     */
    @SuppressWarnings("unchecked")
	public BeanManager<?> createBeanManager( Class<?> beanClass, BeanInfo beanInfo ) {

      BeanManager<?> base = new BeanManagerImpl(beanClass, beanInfo);

      BeanInfo[] additional = beanInfo.getAdditionalBeanInfo();

      if( additional!=null && additional.length>0 ) {
        java.util.List<BeanInfo> aggList = new java.util.ArrayList<BeanInfo>(additional.length);

        BeanDescriptor bd=null;

        for( int i=0; i<additional.length; ++i ) {
          bd = additional[i].getBeanDescriptor();
          if( bd!=null && bd.getValue(BeanDescriptorEntity.ENTITY_NAME)!=null ) {
            aggList.add( additional[i] );
          }
        }
        if( !aggList.isEmpty() ) {
          BeanInfo[] aggArray = new BeanInfo[ aggList.size() ];
          aggList.toArray(aggArray);
          aggList = null;

          return  new AggregateBeanManager(base,aggArray);
        }
      }

      return base;
    }

  }

