package org.bsc.bean.adapters;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import static org.bsc.bean.BeanManagerUtils.messages;
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

public class JAVA_OBJECTAdapter implements DataAdapter {

  /**
   *
   * @param rs
   * @param fieldName
   * @param p
   * @return
   * @throws SQLException
   */
  public Object getValue(ResultSet rs, PropertyDescriptorField p) throws SQLException {
      Object result = null;
      try {

      Blob b = rs.getBlob(p.getDerefFieldName());
      if( b!=null ) {
        java.io.ObjectInputStream oos = new java.io.ObjectInputStream(b.
            getBinaryStream());
        result = oos.readObject();

        ///// DEBUG SECTION ///////////////////////////////
        Class<?> targetClass = p.getPropertyType();

        if (!targetClass.isAssignableFrom(result.getClass())) {

          Log.error( 
              "JAVA_OBJECTAdapter.getValue type mismatch : expected {0} returned {1}",
              targetClass, result.getClass());

          return null;
        }
      }
      ///// //////////// ///////////////////////////////

      }
      catch (Exception ex) {
        Log.error( "JAVA_OBJECTAdapter.getValue exception ", ex );
      }

      return result;
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
  public void setValue(PreparedStatement ps, int parameterIndex, Object value, PropertyDescriptorField p) throws SQLException {
      if( value==null ) {
        ps.setNull( parameterIndex, java.sql.Types.VARBINARY);
        return;
      }

      // PRE_CONDITION
      if( !(value instanceof java.io.Serializable) ) {
        throw new SQLException( messages.prop_not_serializable(p.getName()) );
      }

      // SERIALIZE OBJECT IN BINARY FORMAT
      try {
      java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream(4096);
      java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);

      oos.writeObject( value );

      //Blob blob = new PropertyBlob(baos.toByteArray());

      Blob blob = new SerialBlob(baos.toByteArray());
      
      // SET BINARY
      //ps.setBinaryStream( ordinal, blob.getBinaryStream(), (int)blob.length() );
      // SET BYTES
      //ps.setBytes( ordinal, blob.getBytes() );

      // SET BLOB
      ps.setBlob( parameterIndex, blob );

      baos.close();

      }
      catch( java.io.IOException ioex ) {
        Log.error( "JAVA_OBJECTAdapter.setValue", ioex );
        throw new SQLException( ioex.getMessage() );
      }


  }
}
