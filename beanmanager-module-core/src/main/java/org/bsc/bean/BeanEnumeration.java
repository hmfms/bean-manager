package org.bsc.bean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;
import org.bsc.util.Log;

/**
 * Title:        Bartolomeo Sorrentino Classi
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      ITD
 * @author Bartolomeo Sorrentino
 * @version 1.0
 */
public class BeanEnumeration<T> implements Enumeration<T>, Iterable<T> {

  private BeanManager<T> manager;
  private ResultSet rs;


  private class BeanIterator implements java.util.Iterator<T> {

	public boolean hasNext() {
		return hasMoreElements();
	}

	public T next() {
		return nextElement();
	}

	public void remove() {
		throw new UnsupportedOperationException("remove is not support on bean enumeration!");
	}
	  
  }
  
  /**
   *
   */
  protected BeanEnumeration( BeanManager<T> manager, ResultSet rs ) {
    this.manager = manager;
    this.rs = rs;
  }

  /**
   *
   */
  public boolean hasMoreElements() {
    try {
    return rs.next();
    }
    catch (Exception ex) {}

    return false;
  }

  /**
   *
   */
  public T nextElement() {

    try {
    T bean = manager.instantiateBean();
    manager.setBeanProperties(bean,rs);

    return bean;
    }
    catch( Exception ex ) {
      throw new java.lang.Error(ex.getMessage());
    }

  }

  /**
   * 
   */
  public Iterator<T> iterator() {
	return new BeanIterator();
  }

  /**
   * close recordset
   */
  public void close() throws java.sql.SQLException {
    rs.close();
  }

 
  public static <T> void close( BeanEnumeration<T> enums ) {
      
      if( null!=enums ) {
            try {
                enums.close();
            } catch (SQLException ex) {
                Log.warn( "error occurred on BeanEnumeration close", ex );
            }
      } 
  }

}