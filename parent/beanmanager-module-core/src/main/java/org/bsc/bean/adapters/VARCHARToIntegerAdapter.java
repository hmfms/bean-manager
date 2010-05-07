/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.adapters;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bsc.bean.DataAdapter;
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.util.Log;

/**
 *
 * @author Sorrentino
 */
public class VARCHARToIntegerAdapter implements DataAdapter {

    /**
     * 
     * @param rs
     * @param p
     * @return
     * @throws java.sql.SQLException
     */
    public Object getValue(ResultSet rs, PropertyDescriptorField p) throws SQLException {
        
        String value = rs.getString(p.getFieldName()); 
        
        try {
            
        return Integer.parseInt(value);
        
        }
        catch(NumberFormatException ex ) {
            Log.warn( "the value {0} is not valid number. -1 will be returned", value);
        }
        return -1;
    }

    /**
     * 
     * @param ps
     * @param parameterIndex
     * @param value
     * @param p
     * @throws java.sql.SQLException
     */
    public void setValue(PreparedStatement ps, int parameterIndex, Object value, PropertyDescriptorField p) throws SQLException {

        if( value instanceof Number ) {
            
            int intValue = ((Number)value).intValue();
            
            ps.setString(parameterIndex, Integer.toString(intValue));
        }
        else {
            throw new IllegalArgumentException( "value " + value + " is not a number");
        }
    }

}
