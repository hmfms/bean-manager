package org.bsc.bean;


/**
 * Title:        Bartolomeo Sorrentino Classi
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      ITD
 * @author Bartolomeo Sorrentino
 * @version 1.0
 */

public class JoinRelations {


  /** JOIN COMMAND PATTERNS */
  private static final String[] JOINCMD     = {
    " LEFT JOIN {0} ON {1}",
    " RIGHT JOIN {0} ON {1}",
    " JOIN {0} ON {1}"
  };

  /**
   *
   */
  class Relation  {
    java.util.List<JoinCondition> conditions;
    JoinType type;
    String name;

    public Relation( String name, JoinType type, java.util.List<JoinCondition> conditions ) {
      this.type = type;
      this.conditions = conditions;
      this.name = name;
    }

    public String getName() { return name; }
    public java.util.Collection<JoinCondition> getConditions() { return conditions; }

    public JoinType getType() { return type ; }

  }

  private java.util.List<Relation> relations;

  /**
   *
   */
  protected JoinRelations() {
    relations = new java.util.ArrayList<Relation>();
  }

  /**
   *
   */
  public void add( String relationName, String tableA, JoinType joinType, java.util.List<JoinCondition> conditions ) {
    this.add( tableA, new Relation( relationName, joinType,conditions) );
  }

  /**
   *
   */
  private void add( String tableA, Relation relation ) {
    
	for( JoinCondition c : relation.getConditions() ) {
	      c.init(tableA,relation.getName());		
	}
    relations.add(relation);
  }

  /**
   *
   */
  public java.util.Collection<Relation> getRelations() {
    return relations;
  }

  /**
   * generate SQL JOIN clouse
   */
    @Override
  public String toString() {
    return getJoinCommand();
  }

  /**
   * JOIN CONDITION SYNTAX
   *
   * return [LEFT/RIGHT] JOIN <table> ON <conditions> [LEFT/RIGHT] JOIN <table> ON <condition> .....
   */
  private String getJoinCommand() {

    StringBuilder result = new StringBuilder();
    
    for( Relation relation : this.getRelations() ) {

        result.append(
                java.text.MessageFormat.format(JOINCMD[relation.getType().value()],
                			relation.getName(),
                			getJoinConditions(relation) ) 
                			);
    	
    }

    return result.toString();
  }

  /**
   *
   * @param relation
   * @return
   */
  private String getJoinConditions( Relation relation ) {

    StringBuffer result = new StringBuffer();

    java.util.Iterator<JoinCondition> conditions = relation.getConditions().iterator();

    if( conditions.hasNext() ) {
      result.append(conditions.next());
    }
    while( conditions.hasNext() ) {
      result.append(" AND ");
      result.append(conditions.next());
    }

    return result.toString();
  }

  /*
  public Relation getRelation(String relationName) {
    if( !relations.containsKey( relationName ) ) {
      throw new java.lang.IllegalArgumentException(
        java.text.MessageFormat.format("relation {0} not exist", new Object[]{ relationName} )
        );
    }
    return (Relation)relations.get(relationName);
  }
*/

  /*
  private String getJoinTables() {

    StringBuffer result = new StringBuffer();
    java.util.Iterator i = this.getTables();

    if( i.hasNext() ) {
     result.append(i.next());
    }
    while( i.hasNext() ) {
      result.append(',');
      result.append(i.next());
    }

    return result.toString();
  }
  */

  /**
  */
  /*
  private String getJoinConditions() {

    StringBuffer result = new StringBuffer();
    java.util.Iterator tables = this.getTables();

   Relation relation;
   String relationName;

   if( tables.hasNext() ) {

    relationName = (String)tables.next();

    relation = getRelation( relationName );

    result.append(
      this.getJoinConditions(relationName, relation.getConditions() ) );
  }

    while( tables.hasNext() ) {
        relationName = (String)tables.next();

        relation = getRelation( relationName );

        result.append(" AND ");

        result.append(
          this.getJoinConditions(relationName, relation.getConditions()) );

    }
    return result.toString();
  }
  */


}
