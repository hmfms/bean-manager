package org.bsc.bean.adapters;

import org.bsc.bean.DataAdapter;
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.util.Log;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * <p>Title: Bartolomeo Sorrentino Classi</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ITD</p>
 * @author Bartolomeo Sorrentino
 * @version 1.0
 */

public class DateTimeAdapter implements DataAdapter {

  /**
   *
   * @param rs
   * @param p
   * @return
   * @throws SQLException
   */
  public Object getValue(ResultSet rs, PropertyDescriptorField p) throws SQLException {
    Log.debug( "DateTimeAdapter.getValue "+ p.getDerefFieldName() );

    Class<?> targetClass = p.getPropertyType();
    Object result = null;
    String fieldName = p.getDerefFieldName();

    Log.debug( "DateTimeAdapter.getValue targetClass " + targetClass );

    if( targetClass.equals(Timestamp.class) ) {
      result = rs.getTimestamp(fieldName);
    }
    else if( targetClass.equals(Time.class) ) {
      result = rs.getTime(fieldName);
    }
    else {
      result = rs.getDate(fieldName);
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
      Log.debug( "DateTimeAdapter.setValue({0})={1}", p.getDerefFieldName(),value );

    int type = p.getSQLType();

    if( value==null ) {
      ps.setNull( parameterIndex, type);
      return;
    }

    switch( type ) {
    case Types.DATE:
      {
      long time = ((java.util.Date)value).getTime();
      ps.setDate( parameterIndex, new java.sql.Date( time ) );
      }
      break;
    case Types.TIMESTAMP:
      ps.setTimestamp( parameterIndex, (java.sql.Timestamp)value);
      break;
    case Types.TIME:
      ps.setTime( parameterIndex, (java.sql.Time)value);
      break;
    }
  }

}