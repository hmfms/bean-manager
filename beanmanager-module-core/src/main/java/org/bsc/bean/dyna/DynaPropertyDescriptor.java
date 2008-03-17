/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.dyna;

import java.lang.reflect.Method;

/**
 *
 * @author Sorrentino
 */
public interface DynaPropertyDescriptor {

    Method getMappedReadMethod();
    
    Method getMappedWriteMethod();
    
    
}
