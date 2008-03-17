/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.test;

/**
 *
 * @author Sorrentino
 */
public class Account {
    private int id;
    private int balance;
    private String number;
            
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
            
}
