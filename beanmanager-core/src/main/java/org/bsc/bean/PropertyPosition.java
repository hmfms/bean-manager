package org.bsc.bean;


/**
 *
 * <p>Title: Bartolomeo Sorrentino Classi</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ITD</p>
 * @author unascribed
 * @version 1.0
 */
class PropertyPosition implements Comparable<PropertyPosition> {
  private PropertyDescriptorField propertyObject ;
  private int index;

  /**
   *
   * @param index
   * @param propertyName
   */
  public PropertyPosition( int index, PropertyDescriptorField property ) {
      this.index = index;
      this.propertyObject = property;
    }

  /**
   *
   * @return
   */
  int getIndex() { return index; }

  /**
   *
   * @return
   */
  PropertyDescriptorField getProperty() { return propertyObject; }

  /**
   *
   * @param o
   * @return
   */
  @SuppressWarnings("unchecked")
  public int compareTo( PropertyPosition pp ) {
    if( getIndex()<pp.getIndex() ) return -1;
    if( getIndex()>pp.getIndex() ) return 1;
    return 0;
  }
}

