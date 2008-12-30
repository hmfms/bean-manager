/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.test;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import org.bsc.bean.BeanDescriptorEntity;
import org.bsc.bean.JoinCondition;
/**
 *
 * @author Sorrentino
 */
public class CustomerAccountBeanInfo extends CustomerBeanInfo {

    @Override
    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptorEntity descriptor =  (BeanDescriptorEntity) super.getBeanDescriptor();
        
        setBeanClass( CustomerAccount.class );
        
        descriptor.createJoinRelation("ACCOUNT", 
                    new JoinCondition("customer_id","id") );
        return descriptor;
    }

    /**
     * Aggregate BeanInfo
     * 
     * @return
     */
    @Override
    public BeanInfo[] getAdditionalBeanInfo() {
        return new BeanInfo[] {
            new BankAccountBeanInfo()
        };
    }


}
