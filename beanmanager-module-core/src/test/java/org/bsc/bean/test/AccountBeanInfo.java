/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.test;

import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import org.bsc.bean.AbstractManagedBeanInfo;
import org.bsc.bean.BeanDescriptorEntity;
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.bean.PropertyDescriptorPK;

/**
 *
 * @author Sorrentino
 */
public class AccountBeanInfo extends AbstractManagedBeanInfo<Account> {

    
    public AccountBeanInfo() {
        super( Account.class );
    } 
    
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptorEntity(getBeanClass(), "ACCOUNT");
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            
            return new PropertyDescriptor[] {
                new PropertyDescriptorPK( "id", getBeanClass(), "getAccountId", "setAccountId"),
                new PropertyDescriptorField( "balance", getBeanClass(), "getBalance", "setBalance"),
                new PropertyDescriptorField( "number", getBeanClass(), "getNumber", "setNumber")                
            };
            
        }
        catch( Exception ex ) {
            throw new IllegalStateException(ex);
        } 
        
    }

}
