package org.bsc.bean.adapters;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bsc.bean.DataAdapter;
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.util.Log;

/**
 *
 * <p>Title: Bartolomeo Sorrentino Classi</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ITD</p>
 * @author unascribed
 * @version 1.0
 */
public class BLOBAdapter implements DataAdapter {

  /**
   *
   * @param rs
   * @param p
   * @return
   * @throws SQLException
   */
  public Object getValue(ResultSet rs, PropertyDescriptorField p) throws SQLException {
      Log.debug("BLOBAdapter.getValue " + p.getDerefFieldName() );

      Class<?> type = p.getPropertyType();

      Blob value = rs.getBlob( p.getFieldName() );
      Object result = null;

      if( type.isArray() ) {
        result = (Object)value.getBytes(0L, (int)value.length() );
      }
      else if( type.equals( java.io.InputStream.class )) {
        result = value.getBinaryStream();
      }
      else {
        result = value;
      }

      return result;
  }

  /**
   *
   * @param ps
   * @param parameterIndex
   * @param value
   * @param p
   * @throws SQLException
   */
  public void setValue(PreparedStatement ps, int parameterIndex, Object value, PropertyDescriptorField p) throws SQLException {
    if( Log.isDebugEnabled() )
      Log.debug( "BLOBAdapter.setValue({0})={1}", p.getDerefFieldName(),value );

    if( value==null ) {
      ps.setNull(parameterIndex, p.getSQLType());
      return ;
    }

    Class<?> type = value.getClass();
    Blob prop = null;

    if( type.isArray() ) {
      prop = new PropertyBlob( (byte[])value );
      ps.setBlob(parameterIndex, prop);
    }
    else if( java.io.InputStream.class.isAssignableFrom(type)) {
      try {
      prop = new PropertyBlob( (java.io.InputStream)value );
      ps.setBlob( parameterIndex, prop );
      }
      catch (java.io.IOException ex) {
        throw new SQLException( "BLOBAdapter " + ex.getMessage() );
      }
    }
    else {
      ps.setBlob( parameterIndex, (Blob)value );
    }

  }
}