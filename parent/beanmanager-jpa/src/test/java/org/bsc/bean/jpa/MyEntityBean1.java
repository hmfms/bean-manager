/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author softphone
 */
@Entity
@Table( name="MASTER_ENTITY")
//@PrimaryKeyJoinColumn(name="CUST_ID")
public class MyEntityBean1 extends MyEntityBean2 {

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String property1_1;
    
    private String property1_2;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProperty1_1() {
        return property1_1;
    }

    public void setProperty1_1(String property1_1) {
        this.property1_1 = property1_1;
    }

    public String getProperty1_2() {
        return property1_2;
    }

    public void setProperty1_2(String property1_2) {
        this.property1_2 = property1_2;
    }


}
