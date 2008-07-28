/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.test;

import java.beans.IntrospectionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bsc.bean.BeanDescriptorEntity;
import org.bsc.bean.PropertyDescriptorPK;
import org.bsc.bean.dyna.DynaManagedBeanInfo;

/**
 *
 * @author Sorrentino
 */
public class DynaCustomerBeanInfo extends DynaManagedBeanInfo {

    /**
     * 
     */
    public DynaCustomerBeanInfo() {
        super();
        try {

            super.setBeanDescriptor(new BeanDescriptorEntity(DynaCustomer.class).setEntityName("CUSTOMER"));

            super.addPropertyDescriptor(new PropertyDescriptorPK("ID", DynaCustomer.class, "getId", "setId"));
            
        } catch (IntrospectionException ex) {
            Logger.getLogger(DynaCustomerBeanInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
            

    }
}
