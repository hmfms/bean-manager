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

public class CHARAdapter implements DataAdapter {

  /**
   *
   * @param rs
   * @param fieldName
   * @return
   * @throws SQLException
   */
  public Object getValue(ResultSet rs, PropertyDescriptorField p) throws SQLException {
        Log.debug("CHARAdapter.getValue " + p.getDerefFieldName() );

        //byte charValue = rs.getByte(p.getDerefFieldName());
        String charValue = rs.getString(p.getDerefFieldName());
        return (rs.wasNull()) ? null : new Character((char)charValue.charAt(0)) ;
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
      Log.debug( "CHARAdapter.setValue({0})={1}", p.getDerefFieldName(),value);

    if( value==null ) {
      ps.setNull(parameterIndex,p.getSQLType());
    }
    else {
      char ch = ((Character)value).charValue();
      //Log.debug( "char=", new Integer(Character.getNumericValue(ch)) );

      if( Character.getNumericValue(ch)==-1 ) {
        ps.setNull(parameterIndex,p.getSQLType());
      }
      else {
        //Log.debug( "char=", ((Character)value) );
        ps.setString(parameterIndex, ((Character)value).toString() );
        //ps.setByte( parameterIndex, (byte)ch);
      }
    }
  }

}