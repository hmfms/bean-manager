/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.test;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Sorrentino
 */
public class DynaCustomer {

    private Map<String,Object> attributes;
    private int id;
    
    /**
     * 
     */
    public DynaCustomer() {
        attributes = new HashMap<String,Object>(10);
    }
  
    @Override
    public final String toString() {
    
        StringBuilder sb = new StringBuilder(100);
        
        sb.append( "ID=").append(id).append(':');
        
        for( Map.Entry<String,Object> e : attributes.entrySet() ) {
            
            sb.append( e.getKey() ).append( '=').append(e.getValue()).append(':');
        }
        
        return sb.toString();
    }
    
    public final void set( String name, Object value ) {
        if( null==name ) {
            throw new IllegalArgumentException( "argument name is null!"); 
        }
        
        attributes.put(name, value);
    }
    
    public final Object get( String name ) {
        if( null==name ) {
            throw new IllegalArgumentException( "argument name is null!"); 
        }
        
        //if( !attributes.containsKey(name)) {
        //    throw new IllegalStateException( "attribute " + name + " doesn't exist!");
        //}
        
        return attributes.get(name);
    }
    
    /**
     * 
     * @return
     */
    public final int getId() {
            return id;
    }
    
    /**
     * 
     * @param id
     */
    public final void setId(int id) {
            this.id = id;
    }

    
}
