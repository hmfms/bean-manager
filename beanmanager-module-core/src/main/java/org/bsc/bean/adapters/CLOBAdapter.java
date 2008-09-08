/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.adapters;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.serial.SerialClob;
import org.bsc.bean.DataAdapter;
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.util.Log;

/**
 *
 * @author sorrentino
 */
public class CLOBAdapter implements DataAdapter {

    /**
     * 
     * @param type
     * @return
     */
    private boolean isCharArray( Class<?> type ) {
       return (type.isArray() && (type.getComponentType().equals(Character.class) || type.getComponentType().equals(Character.TYPE))); 
    }
    
    /**
     * 
     * @param rs
     * @param p
     * @return
     * @throws java.sql.SQLException
     */
    public Object getValue(ResultSet rs, PropertyDescriptorField p) throws SQLException {
      final String method = "CLOBAdapter.getValue";
      
      Log.debug( "{0}({1})", method, p.getDerefFieldName() );

      Class<?> type = p.getPropertyType();

      Clob value = rs.getClob( p.getFieldName() );
      Object result = null;

      if( isCharArray(type) ) {
        result = value.getCharacterStream();
      }
      else if( type.equals(String.class) ){
        StringBuffer sb = new StringBuffer();
        Reader reader = value.getCharacterStream();
        char buffer[] = new char[1024]; 
        int len;
        try {

            while ((len = reader.read(buffer)) == 1024) {
                sb.append(buffer, 0, 1024);
            }
            if( len>0 ) {
                sb.append( buffer, 0, len);
            }
            result = sb.toString();
            
            value.free();
            
        } catch (IOException ex) {            
            Log.warn( "{0} error ", ex, method);
            result = null;
        }
      }
      else {
          Log.error( "{0} type not recognized {1}", method, type.getName());
      }
      

      return result;
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
        final String method = "CLOBAdapter.setValue";

        Log.debug( "{0}({1})={2}", method, p.getDerefFieldName(),value );

        if( value==null ) {
          ps.setNull(parameterIndex, p.getSQLType());
          return ;
        }

        Class<?> type = value.getClass();
        Clob prop = null;

        if( isCharArray(type) ) {
          prop = new SerialClob( (char[])value );
          ps.setClob(parameterIndex, prop);
        }
        else if( CharSequence.class.isAssignableFrom(type)) {
          prop = new SerialClob( ((CharSequence)value).toString().toCharArray() );
          ps.setClob( parameterIndex, prop );
        }
        else {
          Log.error( "{0} type not recognized {1}", method, type.getName());
        }
    }

}
