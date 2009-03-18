/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.test.beans;

import java.util.Date;

/**
 *
 * @author Sorrentino
 */
public class BankAccount {
    private int id;
    private int balance;
    private String number;
    private java.util.Date openDate;

    public BankAccount() {
        openDate = new Date();
    }

    
    public final int getAccountId() {
        return id;
    }        

    public final void setAccountId(int value ) {
        id = value;
    }        

    public final int getBalance() {
        return balance;
    }        

    public final void setBalance(int value ) {
        balance = value;
    }        

    public final String getNumber() {
        return number;
    }        

    public final void setNumber(String value ) {
        number = value;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }
            
}
