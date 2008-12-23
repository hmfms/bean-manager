package org.bsc.bean;

import java.beans.BeanInfo;
import org.bsc.util.Configurator;
import org.bsc.util.Log;

/**
* It's a implementation of DAO factory (see DAO pattern ) for create
* bean manager instances ( DAO interface )
*
* You can extends this class to create a custom version of bean manager
*
* @author BARTOLOMEO Sorrentino
* @version 2.2.0
*/
public abstract class BeanManagerFactory {

  static final String FACTORY_CLASS = "bsc.factory";


  /**
   *
   * @return
   */
  public static BeanManagerFactory getFactory() {
    String pFactoryClass = Configurator.getPropertyString(FACTORY_CLASS, null);

    return getFactory( pFactoryClass );
  }

  /**
   *
   * @return
   */
  public static BeanManagerFactory getFactory( String factoryClass ) {
    BeanManagerFactory result = null;

    if( factoryClass!=null ) {

      Class<?> factoryClazz = null;

      try {
        factoryClazz = Class.forName(factoryClass);
      }
      catch (ClassNotFoundException ex) {
         ClassLoader cl = Thread.currentThread().getContextClassLoader();

        try {
          factoryClazz = Class.forName(factoryClass, true, cl);
        }
        catch (ClassNotFoundException ex2) {
          Log.warn( "factory class " + factoryClass + " not found ");
        }
      }

      if( factoryClazz!=null ) {
        try {
          Object factoryInstance = factoryClazz.newInstance();

          if (factoryInstance instanceof BeanManagerFactory) {

            result = ((BeanManagerFactory) factoryInstance);
          }
        }
        catch (Exception ex1) {
          Log.warn( "factory "  + factoryClass + " instantation error ", ex1 );
        }
      }

    }

    if( result==null) {
      result = new BaseBeanManagerFactory();
    }

    return result;
  }

  /**
   *
   * @param beanClass
   * @param beanInfo
   * @return
   */
  public abstract <T> BeanManager<T> createBeanManager( Class<T> beanClass, BeanInfo beanInfo );

  /**
   * method for creation of bean manager instance using the default
   * associated bean info
   *
   * @see bsc.bean.BeanManagerUtils#loadBeanInfo
   * @param beanClass
   * @return BeanManager instance
   */
  public <T> BeanManager<T> createBeanManager( Class<T> beanClass ) {
    BeanInfo info = BeanManagerUtils.loadBeanInfo( beanClass.getClassLoader(), beanClass );
    return createBeanManager( beanClass, info );
  }


}
