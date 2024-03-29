package org.bsc.bean.adapters;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bsc.bean.DataAdapter;
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

public class VARCHARAdapter implements DataAdapter {

  /**
   *
   * @param rs
   * @param fieldName
   * @param targetClass (String|StringBuffer)
   * @return
   * @throws SQLException
   */
  public Object getValue(ResultSet rs, PropertyDescriptorField p ) throws SQLException
  {
      Log.debug("VARCHARAdapter.getValue " + p.getDerefFieldName() );

      String s = rs.getString( p.getDerefFieldName());
      if( null!=s && StringBuffer.class.equals(p.getPropertyType()) ) {
        return new StringBuffer(s);
      }

      return s;
  }

  /**
   *
   * @param ps
   * @param parmeterIndex
   * @param value
   * @param targetSqlType
   * @param scale
   * @throws SQLException
   */
  public void setValue( PreparedStatement ps,
        int parameterIndex,
        Object value,
        PropertyDescriptorField p ) throws SQLException {

    if( Log.isDebugEnabled() )
      Log.debug( java.text.MessageFormat.format("VARCHARAdapter.setValue({0})={1}", new Object[]{p.getDerefFieldName(),value} ));

    if( value==null ) {
      ps.setNull(parameterIndex,p.getSQLType());
    }
    else {
      ps.setString( parameterIndex, value.toString() );
    }
  }

}