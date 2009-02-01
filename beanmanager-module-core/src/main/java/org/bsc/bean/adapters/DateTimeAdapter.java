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

    int sqlType = p.getSQLType();

    long time = 0;
    
    switch( sqlType ) {
        case Types.DATE:
            java.util.Date d = rs.getDate(fieldName);
            if( d==null ) return null;
            time = d.getTime();
            break;
        case Types.TIMESTAMP:
            Timestamp ts = rs.getTimestamp(fieldName);
            if( ts==null ) return null;
            time = ts.getTime();
            break;
        case Types.TIME:
            Time t = rs.getTime(fieldName);
            if( t==null ) return null;
            time = t.getTime();
            break;
    }
    
    if( targetClass.equals(Timestamp.class) ) {
      result = new Timestamp(time);
    }
    else if( targetClass.equals(Time.class) ) {
      result = new Time(time);
    }
    else {
      result = new java.util.Date(time);
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

    long time = 0;
    
    Class<?> valueClass = p.getPropertyType();
    
    if( (valueClass.isPrimitive() && Long.TYPE.equals(valueClass)) || Long.class.equals(valueClass) ) {
        time = (Long)value;
    }
    else if( java.util.Date.class.isAssignableFrom(valueClass)) {
        time = ((java.util.Date)value).getTime();
    }
    else {
        Log.warn( "DateTimeAdapter.setValue: value is not a vaild type {0}", valueClass);
        return;
    }

    switch( type ) {
    case Types.DATE:
      ps.setDate( parameterIndex, new java.sql.Date( time ) );
      break;
    case Types.TIMESTAMP:
      ps.setTimestamp( parameterIndex, new java.sql.Timestamp(time) );
      break;
    case Types.TIME:
      ps.setTime( parameterIndex, new java.sql.Time(time));
      break;
    }
  }

}
