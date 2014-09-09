/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.generators;

import java.sql.Connection;
import java.util.UUID;

import org.bsc.bean.PropertyDescriptorField;
import org.bsc.bean.ValueGenerator;

/**
 *
 * @author softphone
 */
public class UUIDValueGenerator implements ValueGenerator<String> {


    public String generate(Connection c, PropertyDescriptorField pd) {
        return UUID.randomUUID().toString();
    }

}
