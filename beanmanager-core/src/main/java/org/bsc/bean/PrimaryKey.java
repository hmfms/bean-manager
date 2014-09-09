package org.bsc.bean;


/**
 *
 * class for support of composite primary key
 *
 * @author Bartolomeo Sorrentino
 * @version 1.3
 */

public class PrimaryKey {
  private java.util.ArrayList<PropertyDescriptorPK> keys;

  protected PrimaryKey() {
    keys = new java.util.ArrayList<PropertyDescriptorPK>();
  }

  /**
   *
   */
  public PropertyDescriptorPK get( int index ) {
    return keys.get(index);
  }

  /**
   *
   */
  public int getKeyCount() {
    return keys.size();
  }

  protected void add( PropertyDescriptorPK pk ) {
    keys.add(pk);
  }

  /**
   * create a param list using primary key
   * <b>NB</b>
   * <pre>
   * last character is a comma
   * </pre>
   *
   * @param sb string buffer to append result
   * @return
   */
  protected StringBuilder appendToPropertyValues( StringBuilder sb ) {

    for( int i=0; i<getKeyCount(); ++i ) {
      // ignore counter fields
      sb.append( "?," );
    }

    return sb;
  }

  /**
   *
   * @return
   */
  private String getUpdateStatementParameters() {
    StringBuffer sb = new StringBuffer();
    int i;
    for( i=0; i<getKeyCount()-1; ++i ) {
      sb.append( get(i).getFieldName() );
      sb.append("=? AND ");
    }
    sb.append( get(i).getFieldName() );
    sb.append("=?");

    return sb.toString();
  }

  /**
   *
   * @return
   */
  protected String getStoreStatementParameters() {
    return this.getUpdateStatementParameters();
  }

  /**
   *
   * @return
   */
  protected String getRemoveStatementParameters() {
    return this.getUpdateStatementParameters();
  }

  /**
   *
   * @param entity
   * @return
   */
  protected String getFindByIdStatementParameters( String entity ) {
    StringBuffer sb = new StringBuffer();
    int i;
    for( i=0; i<getKeyCount()-1; ++i ) {
      sb.append( entity );
      sb.append('.');
      sb.append( get(i).getFieldName() );
      sb.append("=? AND ");
    }
    sb.append( entity );
    sb.append('.');
    sb.append( get(i).getFieldName() );
    sb.append("=?");

    return sb.toString();
  }

  protected StringBuilder appendToFindPropertyList( StringBuilder sb, String sep, String entity ) {
    for( int i=0; i<getKeyCount(); ++i ) {
      //sb.append( entity );
      //sb.append('.');
      //sb.append( get(i).getQueryFieldName() );
      get(i).appendQueryFieldName(sb,entity);
      sb.append( sep );
    }
    return sb;
  }

  protected StringBuilder appendToCreatePropertyList( StringBuilder sb, String sep ) {
    for( int i=0; i<getKeyCount(); ++i ) {


      sb.append( get(i).getFieldName() );
      sb.append( sep );

    }
    return sb;
  }

}