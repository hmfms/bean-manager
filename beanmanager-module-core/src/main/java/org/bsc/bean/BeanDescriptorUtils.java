/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean;

import static org.bsc.bean.BeanDescriptorEntity.ENTITY_NAME;
import static org.bsc.bean.BeanDescriptorEntity.JOIN_RELATIONS;

import java.beans.BeanDescriptor;
/**
 *
 * @author Sorrentino
 */
public class BeanDescriptorUtils {

    /**
    * utility method that convert a simple BeanDescriptor into BeanDescriptorEntity
    *
    * @param bd
    * @return
    */
    @SuppressWarnings("unchecked")
    static public BeanDescriptorEntity createDescriptorEntity( BeanDescriptor bd ) {
        if( bd instanceof BeanDescriptorEntity )
          return (BeanDescriptorEntity)bd;

        BeanDescriptorEntity result = new BeanDescriptorEntity( (Class<?>)bd.getBeanClass() )
                                                                                    .setEntityName((String)bd.getValue(ENTITY_NAME)) ;

        Object jr = bd.getValue(JOIN_RELATIONS);
        if( jr!=null )
          result.setJoinRelations( (JoinRelations)jr );

        return result;
    }


    /**
     * 
     * @param bd generic bean descriptor
     * @return entity name. null whether doesn't exist
     */
    static public String getEntityName( BeanDescriptor bd ) {
        
        return (String)bd.getValue(ENTITY_NAME);
    }
}
