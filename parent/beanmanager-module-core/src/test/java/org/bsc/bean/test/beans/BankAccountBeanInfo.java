/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.test.beans;

import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.sql.Types;

import org.bsc.bean.AbstractManagedBeanInfo;
import org.bsc.bean.BeanDescriptorEntity;
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.bean.PropertyDescriptorPK;

/**
 *
 * @author Sorrentino
 */
public class BankAccountBeanInfo extends AbstractManagedBeanInfo<BankAccount> {

    
    public BankAccountBeanInfo() {
        super( BankAccount.class );
    } 
    
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptorEntity(getBeanClass(), "ACCOUNT");
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            
            return new PropertyDescriptor[] {
                new PropertyDescriptorPK( "id", getBeanClass(), "getAccountId", "setAccountId"),
                new PropertyDescriptorField( "balance", getBeanClass(), "getBalance", "setBalance")
                        .setSQLType(Types.INTEGER),
                new PropertyDescriptorField( "openDate", getBeanClass(), "getOpenDate", "setOpenDate")
                        .setSQLType(Types.TIMESTAMP),                
                new PropertyDescriptorField( "number", getBeanClass(), "getNumber", "setNumber")                
            };
            
        }
        catch( Exception ex ) {
            throw new IllegalStateException(ex);
        } 
        
    }

}
