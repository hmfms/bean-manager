/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 *
 * @author softphone
 */
@Entity
@Table( name="JOINED_ENTITY")
@Inheritance(strategy=InheritanceType.JOINED)
public class MyEntityBean2 {
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private String id;

    private String property2_1;

    private String property2_2;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProperty2_1() {
        return property2_1;
    }

    public void setProperty2_1(String property2_1) {
        this.property2_1 = property2_1;
    }

    public String getProperty2_2() {
        return property2_2;
    }

    public void setProperty2_2(String property2_2) {
        this.property2_2 = property2_2;
    }

}
