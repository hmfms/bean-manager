package org.bsc.bean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bsc.bean.adapters.BLOBAdapter;
import org.bsc.bean.adapters.CHARAdapter;
import org.bsc.bean.adapters.CLOBAdapter;
import org.bsc.bean.adapters.DateTimeAdapter;
import org.bsc.bean.adapters.GenericAdapter;
import org.bsc.bean.adapters.JAVA_OBJECTAdapter;
import org.bsc.bean.adapters.NumberAdapter;
import org.bsc.bean.adapters.VARCHARAdapter;


/**
 * <p>Title: Bartolomeo Sorrentino Classi</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ITD</p>
 * @author Bartolomeo Sorrentino
 * @version 1.0
 */

public interface DataAdapter {

  public static final DataAdapter GENERIC     = new GenericAdapter();
  public static final DataAdapter VARCHAR     = new VARCHARAdapter();
  public static final DataAdapter DATETIME    = new DateTimeAdapter();
  public static final DataAdapter NUMBER      = new NumberAdapter();
  public static final DataAdapter CHAR        = new CHARAdapter();
  public static final DataAdapter JAVA_OBJECT = new JAVA_OBJECTAdapter();
  public static final DataAdapter BLOB        = new BLOBAdapter();
  public static final DataAdapter CLOB        = new CLOBAdapter();

  /**
   * return right value from resultset
   *
   * <pre>
   * NB.
   *
   * use on property field the getDerefFieldName to obtain field name
   * for retrieving data from resultset
   *
   *
   * @param rs
   * @param p
   * @return
   * @throws SQLException
   * @see PropertyDescriptorField#getDerefFieldName
   */
  public Object getValue( ResultSet rs, PropertyDescriptorField p ) throws SQLException;

  /**
   *
   * @param ps
   * @param parameterIndex
   * @param value
   * @param p
   * @throws SQLException
   */
  public void setValue( PreparedStatement ps, int parameterIndex, Object value, PropertyDescriptorField p ) throws SQLException;

}
