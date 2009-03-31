/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.test.beans;

import org.bsc.bean.test.beans.BankAccountBeanInfo;
import org.bsc.bean.test.beans.CustomerBeanInfo;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import org.bsc.bean.BeanDescriptorEntity;
import org.bsc.bean.BeanManagerUtils;
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

    final BeanInfo[] aggregate = new BeanInfo[] { new BankAccountBeanInfo() };

    /**
     * Aggregate BeanInfo
     * 
     * @return
     */
    @Override
    public BeanInfo[] getAdditionalBeanInfo() {
        return aggregate;
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {

        return BeanManagerUtils.aggregateProperties( getBeanClass(), super.getPropertyDescriptors(), aggregate );
    }


}
