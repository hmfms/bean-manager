package org.bsc.bean;

import java.sql.*;
import org.bsc.util.*;

/**
 *
 * Title:        Bartolomeo Sorrentino Classi
 *
 * Class utility for db
 *
 * Copyright:    Copyright (c) 2001
 * Company:      ITD
 * @author Bartolomeo Sorrentino
 * @version 1.0
 */

public class DataBaseUtils {

  static final String CALLCMDWITHRESULT = "'{' ? = {0} ( {1} ) '}'";
  static final String CALLCMD           = "'{' CALL {0} ( {1} ) '}'";

  /**
   * call a stored procedure that return a Resultset
   * <pre>
   * the stored procedure must be designed for return a resultset
   * and the input parameter must be only of IN type , the OUT and INOUT parameters
   * are not currently supported.
   *
   *
   *@param conn database connection
   *@param name store procedure name
   *@param  params stored procedure parameter ( only IN parameter accepted )
   *@return ResultSet
   */
  public static ResultSet callStoredQuery( Connection conn, String name, Object[] params ) throws SQLException
  {
    try {

    StringBuilder sb = new StringBuilder();

    // BUILD THE STORED PROCEDURE PARAMETER LIST ( ?,?,...)
    if( null!=params && params.length > 0 ) {

      for( int i=0; i<params.length-1; ++i ) {
        sb.append('?');
        sb.append(',');
      }
      sb.append('?');

    }

    Log.debug( "params " + sb);

    String cmd = java.text.MessageFormat.format(
      CALLCMD,
      new Object[] { name, sb.toString() }
      );

    Log.debug( "exec command " + cmd);

    CallableStatement cs = conn.prepareCall( cmd );

    if( null!=params && params.length > 0 ) {

      int ordinal = 1;

      for( int i=0; i<params.length; ++i ) {
        cs.setObject(ordinal++,params[i]);
      }

    }

    return cs.executeQuery();

    }
    catch( SQLException sqlex ) {
      throw sqlex;
    }
    catch( Exception ex ) {
      //ex.printStackTrace();
      throw new SQLException( ex.getMessage() );
    }

  }

  /**
   * execute stored procedure for update command
   *
   *
   *@param conn database connection
   *@param name store procedure name
   *@param params stored procedure parameter ( only IN parameter accepted )
   *@param sqlResultType sql types of result ( if you aren't interested to result use: Types.NULL )
   *@return the stored procedure result
   */
  public static Object callStoredUpdate( Connection conn, String name, Object[] params, int sqlResultType ) throws SQLException
  {
    Object result = null;

    try {

    StringBuilder sb = new StringBuilder();

    // BUILD THE STORED PROCEDURE PARAMETER LIST ( ?,?,...)
    if( null!=params && params.length > 0 ) {

      for( int i=0; i<params.length-1; ++i ) {
        sb.append('?');
        sb.append(',');
      }
      sb.append('?');

    }

    Log.debug( "params " + sb);

    String cmd = java.text.MessageFormat.format(
        (sqlResultType==Types.NULL) ? CALLCMD : CALLCMDWITHRESULT,
        new Object[] { name, sb.toString() }
        );

    Log.debug( "exec command " + cmd);

    CallableStatement cs = conn.prepareCall( cmd );

    int ordinal = 1;

    // REGISTER RESULT TYPE INTO STATEMENT
    if( sqlResultType!=Types.NULL ) {
      cs.registerOutParameter(ordinal,sqlResultType);
      ++ordinal;
    }

    if( null!=params && params.length > 0 ) {
      for( int i=0; i<params.length; ++i ) {
        cs.setObject(ordinal++,params[i]);
      }
    }

    int execResult =  cs.executeUpdate();

    Log.debug( "exec result " + execResult );

    if( sqlResultType!=Types.NULL ) {
      result = cs.getObject(1);
    }

    cs.close();

    return result;

    }
    catch( SQLException sqlex ) {
      throw sqlex;
    }
    catch( Exception ex ) {
      //ex.printStackTrace();
      throw new SQLException( ex.getMessage() );
    }

  }


  /**
   * class utility for MySql database
   */
  public static class MySql {
    /**
     * get last insert id from mysql connection
     */
    public static Number getLastInsertId( java.sql.Connection conn ) throws java.sql.SQLException {
        java.sql.ResultSet rs = conn.createStatement().executeQuery("SELECT LAST_INSERT_ID()");
        return  (rs.next()) ? (Number)rs.getObject(1) : null;
    }

  };

  /**
   * class utility for SQLServer database
   */
  public static class SqlServer {
    /**
     * get last insert id from SQLServer connection
     */
    public static Number getLastInsertId( java.sql.Connection conn ) throws java.sql.SQLException {
            java.sql.ResultSet rs = conn.createStatement().executeQuery("SELECT @@IDENTITY AS 'identity'");

            if( rs.next() ) {
              Number value = (Number)rs.getBigDecimal(1);
              return value;
            }

            return null;
    }
  };

}
