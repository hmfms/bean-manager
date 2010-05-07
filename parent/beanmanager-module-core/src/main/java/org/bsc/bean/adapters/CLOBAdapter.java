/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.adapters;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialClob;

import org.bsc.bean.DataAdapter;
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.util.Log;

/**
 *
 * @author sorrentino
 */
public class CLOBAdapter implements DataAdapter {

	static final int READING_BUFFER_SIZE = 1024;
	
    /**
     * 
     * @param type
     * @return
     */
    private boolean isCharArray( Class<?> type ) {
       return (type.isArray() && (type.getComponentType().equals(Character.class) || type.getComponentType().equals(Character.TYPE))); 
    }
    
    private void free( Clob value ) {
    	try {
			Method m = Clob.class.getMethod( "free" );
			
			if( null!=m ){
				m.invoke(value);
			}
			
		} catch (Exception e) {
			Log.warn( "Clob free error: {0}", (e.getCause()!=null) ? e.getCause().getMessage() : e.getMessage() );
		} 
    	
    	
    }
    
    private char[] getValueAsCharArray(Clob value) throws java.sql.SQLException {
        
    	char [] result = null;
    	
    	CharArrayWriter sb = new CharArrayWriter();
        
    	Reader reader = value.getCharacterStream();
        
        try {

        	char buffer[] = new char[READING_BUFFER_SIZE]; 
        	int len;

            while ((len = reader.read(buffer)) == READING_BUFFER_SIZE) {
                sb.write(buffer, 0, READING_BUFFER_SIZE);
            }
            if( len>0 ) {
                sb.write( buffer, 0, len);
            }
            result = sb.toCharArray();

            free( value );

        } catch (IOException ex) {            
            Log.warn( "{0} error ", ex, "getValueAsCharArray");
            return new char[0];
		}
        
        return result;
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

      if( !rs.wasNull() ) {
      
          if( isCharArray(type) ) {
            result = getValueAsCharArray(value);
          }
          else if( type.equals(String.class) ){
        	char ch[] = getValueAsCharArray(value);
        	
        	return new String(ch);
          }
          else if( Reader.class.isAssignableFrom(type)) {
        	  //TODO implement support for reader
              Log.error( "{0} Reader not supported yet", method );
                       }
          else {
              Log.error( "{0} type not recognized {1}", method, type.getName());
          }
      
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
        else if( Reader.class.isAssignableFrom(type)) {
        	Method m = null;
        	try {
				m = PreparedStatement.class.getMethod( "setBlob", Integer.TYPE, java.io.Reader.class );
			} catch (Exception e) {
		        Log.error( "{0} type not supported {1}", method, type.getName());
		        return;
			}

			try {
				m.invoke(ps, parameterIndex, value);
			} catch (Exception e) {
		        Log.error( "{0} setBlob exception", e,  method);
			}
        }
        else {
          Log.error( "{0} type not recognized {1}", method, type.getName());
        }
    }

}
