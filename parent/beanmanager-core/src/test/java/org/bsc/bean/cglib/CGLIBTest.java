/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.cglib;

import java.beans.PropertyDescriptor;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.junit.Test;


/**
 *
 * @author softphone
 */
public class CGLIBTest {


    @Test
    public void fastMethod() throws Exception {

        java.beans.BeanInfo bi = java.beans.Introspector.getBeanInfo( CGBean.class );


        PropertyDescriptor[] pp = bi.getPropertyDescriptors();

        FastClass clazz = FastClass.create(CGBean.class);

        for( PropertyDescriptor p : pp ) {

            java.lang.reflect.Method m = p.getWriteMethod();

            System.out.printf( "method [%s]\n", p.getName());
            if( m!=null ) {
                p.setValue( "fastWrite", clazz.getMethod(m) );
            }
        }

{
        long start_time = System.currentTimeMillis();

        for( int i = 0 ; i < 100000 ; ++i ) {

            CGBean c = new CGBean();

            for( PropertyDescriptor p : pp ) {

                String value = "value" + i;

                java.lang.reflect.Method m = p.getWriteMethod();

                if( m!=null ) {
                    m.invoke( c, value );
                }
            }
        }
        long end_time = System.currentTimeMillis();

        System.out.printf( "classic reflection s [%d] e [%d] time [%d] msec\n",  start_time, end_time, (end_time-start_time)  );
}

{
        long start_time = System.currentTimeMillis();

        Object [] v = new Object[1];

        for( int i = 0 ; i < 100000 ; ++i ) {

            CGBean c = new CGBean();

            for( PropertyDescriptor p : pp ) {

                String value = "value" + i;

                FastMethod m = (FastMethod) p.getValue("fastWrite");

                if( m!=null ) {
                    v[0] = value;
                    m.invoke(  c, v );
                }
            }
        }
        long end_time = System.currentTimeMillis();

        System.out.printf( "classic reflection s [%d] e [%d] time [%d] msec\n",  start_time, end_time, (end_time-start_time)  );
}
    }
}
