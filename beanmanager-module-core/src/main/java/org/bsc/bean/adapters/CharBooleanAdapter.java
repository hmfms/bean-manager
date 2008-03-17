package org.bsc.bean.adapters;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bsc.bean.DataAdapter;
import org.bsc.bean.PropertyDescriptorField;
import org.bsc.util.Log;

/**
 *
 * <p>Title: Progetto ECI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Italdata S.p.A.</p>
 * @author Vincenzo Sorrentino
 * @version 1.0
 */

public class CharBooleanAdapter implements DataAdapter {

  static final String TRUE = "Y";
  static final String FALSE = "N";

  /**
   *
   * @param resultSet ResultSet
   * @param PropertyDescriptorField<?> PropertyDescriptorField<?>
   * @throws SQLException
   * @return Object
   */
  public Object getValue(ResultSet resultSet, PropertyDescriptorField PropertyDescriptorField) throws SQLException {

    String fieldName =PropertyDescriptorField.getDerefFieldName();
    Log.trace("CharBooleanAdapter.getValue "+ fieldName);

    boolean booleanValue = resultSet.getBoolean(fieldName);

    return resultSet.wasNull() ? null : new Boolean(booleanValue);
  }

  public void setValue(PreparedStatement preparedStatement, int i, Object object, PropertyDescriptorField PropertyDescriptorField) throws SQLException {
    if( Log.isTraceEnabled() ) {
      Log.trace("CharBooleanAdapter.setValue({0})={1}",  PropertyDescriptorField.getFieldName(), object );
    }

    if(object == null) {
      preparedStatement.setNull(i, PropertyDescriptorField.getSQLType());
    }
    else {
       java.lang.Boolean boolValue = (java.lang.Boolean)object;
       if (Boolean.TRUE.equals(boolValue)) {
         preparedStatement.setString(i, TRUE);
       }
       else {
         preparedStatement.setString(i, FALSE);
       }
    }
  }

}
