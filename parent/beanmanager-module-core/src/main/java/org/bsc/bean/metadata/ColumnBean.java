/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.metadata;

/**
 *
 * @author Sorrentino
 */
public class ColumnBean {

    private String name;
    private int type;
    private String typeName;
    private int size;
    private String defaultValue;
    
    /**
     * 
     * @return
     */
    @Override
    public final String toString() {
    
        return new StringBuilder(150)
                .append( "name=")
                .append(name)
                .append(':')
                .append("type=")
                .append(type)
                .append(':')
                .append("typeName=")
                .append(typeName)
                .append(':')
                .append("size=")
                .append(size)
                .append(':')
                .append("default=")
                .append(defaultValue)
                .toString();
              
                
    }
    
    
    public final String getName()  {
        return name;
    }

    public final void setName( String value )  {
        
        name = value;
    }

    public final String getTypeName()  {
        return typeName;
    }

    public final void setTypeName( String value )  {
        
        typeName = value;
    }

    public final int getType()  {
        return type;
    }

    public final void setType( int value )  {
        
        type = value;
    }
    
    public final int getSize()  {
        return size;
    }

    public final void setSize( int value )  {
        
        size = value;
    }
    
    public final String getDefault()  {
        return defaultValue;
    }

    public final void setDefault( String value )  {
        
        defaultValue = value;
    }
}
