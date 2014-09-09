package org.bsc.bean.adapters;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bsc.bean.PropertyDescriptorField;
import org.bsc.util.Log;

/**
 * <p>Title: Bartolomeo Sorrentino Classi</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ITD</p>
 * @author Bartolomeo Sorrentino
 * @version 1.0
 */

public class NumberAdapter extends GenericAdapter {

  private Number getNumberValue(
      Class<?> propertyClass,
      String fieldName,
      ResultSet resultset
       ) throws SQLException
    {
      Number result = null;
      if( propertyClass.isPrimitive() ) {
        if(Integer.TYPE.equals(propertyClass)) {
            result = new Integer(resultset.getInt(fieldName));
        } else if(Double.TYPE.equals(propertyClass)) {
            result = new Double(resultset.getDouble(fieldName));
        } else if(Long.TYPE.equals(propertyClass)) {
            result = new Long(resultset.getLong(fieldName));
        } else if(Float.TYPE.equals(propertyClass)) {
            result = new Float(resultset.getFloat(fieldName));
        } else if(Short.TYPE.equals(propertyClass)) {
            result = new Short(resultset.getShort(fieldName));
        } else if(Byte.TYPE.equals(propertyClass)) {
            result = new Byte(resultset.getByte(fieldName));
        }
      }
      else {
        if(Integer.class.equals(propertyClass)) {
            result = new Integer(resultset.getInt(fieldName));
        } else if(Double.class.equals(propertyClass)) {
            result = new Double(resultset.getDouble(fieldName));
        } else if(Long.class.equals(propertyClass)) {
            result = new Long(resultset.getLong(fieldName));
        } else if(Float.class.equals(propertyClass)) {
            result = new Float(resultset.getFloat(fieldName));
        } else if(Short.class.equals(propertyClass)) {
            result = new Short(resultset.getShort(fieldName));
        } else if(Byte.class.equals(propertyClass)) {
            result = new Byte(resultset.getByte(fieldName));
        }

      }
      return result;
    }

  /**
   *
   * @param rs
   * @param fieldName
   * @param targetClass
   * @return
   * @throws SQLException
   */
    @Override
  public Object getValue(ResultSet rs, PropertyDescriptorField p) throws SQLException {
    Log.debug( "NumberAdapter.getValue " + p.getDerefFieldName() );

    Class<?> targetClass = p.getPropertyType();
    String fieldName = p.getDerefFieldName();

    return getNumberValue(targetClass,fieldName,rs);

  }

  /**
   *
   * @param ps
   * @param parameterIndex
   * @param value
   * @param targetSqlType
   * @param scale
   * @throws SQLException
   */
    @Override
  public void setValue(PreparedStatement ps, int parameterIndex, Object value, PropertyDescriptorField p) throws SQLException {
    super.setValue( ps, parameterIndex, value, p );
  }
}
