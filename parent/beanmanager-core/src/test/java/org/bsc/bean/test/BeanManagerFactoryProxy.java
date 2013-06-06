/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bsc.bean.test;


import java.beans.BeanInfo;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import org.bsc.bean.BeanManager;
import org.bsc.bean.BeanManagerFactory;

/**
 *
 * @author softphone
 */
public class BeanManagerFactoryProxy extends BeanManagerFactory {
    
    final BeanManagerFactory delegate = BeanManagerFactory.getFactory();

    @Override
    public <T> BeanManager<T> createBeanManager(Class<T> type, BeanInfo bi) {

        final BeanManager<T> result = delegate.createBeanManager(type, bi);
        
        final InvocationHandler handler = new InvocationHandler() {

            public Object invoke(Object o, Method m, Object[] args) throws Throwable {

                System.out.println("Name: " + m.getName());
                try {
                    return m.invoke(result, args);  // execute the original method.
                }
                catch( Exception ex ) {
                    return null;
                }
            }
            
        };
        
        final Class proxyClass = Proxy.getProxyClass(
                 BeanManagerFactoryProxy.class.getClassLoader(), new Class[] { BeanManager.class });
        try {    
            return (BeanManager<T>) proxyClass
                    .getConstructor(InvocationHandler.class)
                    .newInstance(handler);
        } catch (Exception ex) {
            ex.printStackTrace();;
        }
        
        return result;
    }
    
    public <T> BeanManager<T> createBeanManagerUsingJavaassist(Class<T> type, BeanInfo bi) {

        final BeanManager<T> result = delegate.createBeanManager(type, bi);

        ProxyFactory f = new ProxyFactory();
        f.setInterfaces( new Class<?>[]{BeanManager.class});
        //f.setSuperclass(AbstractBeanManager.class);
        f.setFilter(new MethodFilter() {
            @Override
            public boolean isHandled(Method m) {
                Class<?> exceptions[] = m.getExceptionTypes();
                
                if( exceptions!=null ) {
                    for( Class<?> c : exceptions ) {
                        c.isAssignableFrom(SQLException.class);
                        return true;
                    }
                }
                return false;
            }
        });
        
        MethodHandler mi = new MethodHandler() {
            @Override
            public Object invoke(Object self, Method m, Method proceed,
                                     Object[] args) throws Throwable {
                    System.out.println("Name: " + m.getName());
                    //return proceed.invoke(self, args);  // execute the original method.
                    return m.invoke(result, args);  // execute the original method.
                }
            };
        try {
            return (BeanManager) f.create( new Class[0], new Object[0], mi);
        } catch (Exception ex) {
            ex.printStackTrace();;
        }
        
        return result;
    }
    
}
