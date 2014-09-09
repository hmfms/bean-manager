/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean;

import org.bsc.bean.generators.UUIDValueGenerator;

/**
 *
 * @author softphone
 */
public interface ValueGenerator<T> {

    UUIDValueGenerator uuidGenerator = new UUIDValueGenerator();

    T generate( java.sql.Connection c, PropertyDescriptorField pd );
}
