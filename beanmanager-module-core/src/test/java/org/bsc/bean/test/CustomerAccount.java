/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.test;

/**
 *
 * @author Sorrentino
 */
public class CustomerAccount extends Customer {
    private int account;
    private String number;

    public int getBalance() {
        return account;
    }

    public void setBalance(int account) {
        this.account = account;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
