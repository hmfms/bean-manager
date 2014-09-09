package org.bsc.bean;

/**
 *
 * @author Sorrentino
 */
public enum JoinType {
  /** LEFT OUTER JOIN */
  LEFT_OUTER_JOIN(0), 
  LEFT_JOIN(LEFT_OUTER_JOIN),
  /** RIGHT OUTER JOIN */
  RIGHT_OUTER_JOIN(1),
  RIGHT_JOIN(RIGHT_OUTER_JOIN),
  /** INNER JOIN */
  INNER_JOIN(2);

  private final int value;
  
  JoinType( int v ) {
    this.value = v;
  }
  
  JoinType( JoinType v ) {
    this.value = v.value;
  }
 
  int value() {
      return value;
  }
}
