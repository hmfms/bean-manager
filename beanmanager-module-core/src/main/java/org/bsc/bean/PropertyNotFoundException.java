package org.bsc.bean;


/**
 * <p>Title: Bean Manager </p>
 * <p>Description: ORM framework</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author BARTOLOMEO Sorrentino
 * @version 1.0
 */

@SuppressWarnings("serial")
public class PropertyNotFoundException extends IllegalArgumentException {

  public PropertyNotFoundException(final String s) {
    super(s);
  }
}