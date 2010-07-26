package org.bsc.bean;

import java.beans.BeanDescriptor;

import org.bsc.util.Configurator;

/**
 * BeanDescriptor extension for manage DataBase entity (TABLE,VIEW,STORED)
 *  and relations
 * 
 * <pre>
 * example:
 * 
 * BeanDescriptorEntity _entity = new BeanDescriptorEntity(<i>class name</i>);
 * _entity.setEntityName("<i>entity name</i>");
 * 
 * //  create join relation 
 * 
 * _entity.createJoinRelation( "<i>entity in relation</i>", 
 * 									new JoinCondition("<i>fieldA</i>","<i>fieldB</B>"), 
 * 									new JoinCondition("<i>fieldA</i>","<i>fieldC</B>") );
 * OR
 * _entity.createJoinRelation( "<i>entity in relation</i>", 
 * 									RIGHT_JOIN,
 * 									new JoinCondition("<i>fieldA</i>","<i>fieldB</B>"), 
 * 									new JoinCondition("<i>fieldA</i>","<i>fieldC</B>") );
 * </pre>
 * @author Bartolomeo Sorrentino
 * @version 2.2.0
 */
public class BeanDescriptorEntity extends BeanDescriptor {

  /** */
  public static final String ENTITY_NAME    = "entityName";
  public static final String JOIN_RELATIONS = "joinRelations";
  public static final String IDENTITY_CONDITIONS = "idConditions";
  public static final String AGGREGATE_ORDER = "order";

  public static final Integer ORDER_FIRST = new Integer(0);
  public static final Integer ORDER_LAST = new Integer(1);
  /** */
  private boolean enableIdentityConditions = true;

  /** @link dependency 
   * @stereotype use*/
  /*# JoinCondition lnkJoinCondition; */


  /**
   * 
   * @param beanClass
   */
  public BeanDescriptorEntity(Class<?> beanClass) {
    super(beanClass);
    this.setValue( AGGREGATE_ORDER, ORDER_LAST );
  }

  /**
   * 
   * @param beanClass
   * @param entityName
   */
  public BeanDescriptorEntity(Class<?> beanClass, String entityName ) {
    this(beanClass);
    setEntityName( entityName );
  }

  /**
   *
   */
  public BeanDescriptorEntity(Class<?> beanClass, Class<?> customizerClass) {
    super(beanClass,customizerClass);
  }

  /**
   * set entity name mapped with bean
   *
   *@param entityName entity name
   */
  public BeanDescriptorEntity setEntityName( String entityName ) {
      String v = null;

      if (Configurator.isLowerCase()) {
          v = entityName.toLowerCase();
      } else {
          v = entityName;
      }

      //this.setValue(ENTITY_NAME, Configurator.variableSubstitution(v));
      this.setValue(ENTITY_NAME, v);

      return this;
  }

  /**
   * get entity name mapped with bean
   *
   *@return entity name (null if doesn't exist)
   */
  public String getEntityName() {
    return (String)this.getValue( ENTITY_NAME );
  }

  /**
   *
   * @param relationName name of table joined
   * @param conditions
   */
  public BeanDescriptorEntity createJoinRelation( String relationName, JoinCondition... conditions ) {
    return this.createJoinRelation(
                    relationName,
                    this.getEntityName(),
                    JoinType.LEFT_OUTER_JOIN,
                    conditions);
    
    
  }

  /**
   *
   * @param relationName name of table joined
   * @param joinType specify left or right join
   * @param conditions
   */
  public BeanDescriptorEntity createJoinRelation( String relationName, JoinType joinType, JoinCondition... conditions ) {
    return  this.createJoinRelation(
                    relationName,
                    this.getEntityName(),
                    joinType,
                    conditions);
  }

  /**
   *
   * @param relationName name of table joined associated with each condition keyB
   * @param tableA table associated with each condition keyA
   * @param conditions
   */
  public BeanDescriptorEntity createJoinRelation( String relationName, String tableA, JoinCondition... conditions ) {
    return this.createJoinRelation(
                    relationName,
                    tableA,
                    JoinType.LEFT_OUTER_JOIN,
                    conditions );
  }

  /**
   *
   * @param tableName
   * @param joinType
   * @param conditions
   */
  public BeanDescriptorEntity createJoinRelation( String relationName, String tableA, JoinType joinType, JoinCondition... conditions ) {
    return this.createJoinRelation(
                    relationName,
                    tableA,
                    joinType,
                    java.util.Arrays.asList(conditions) );
  }

  /**
   *
   * @param relationName name of table joined
   * @param conditions
   */
  public BeanDescriptorEntity createJoinRelation( String relationName, JoinType joinType, java.util.List<JoinCondition> conditions ) {
    return this.createJoinRelation(
                    relationName,
                    this.getEntityName(),
                    joinType,
                    conditions);
  }

  /**
   *
   * @param tableA
   * @param tableB
   * @param conditions
   */
  public BeanDescriptorEntity createJoinRelation( String relationName, String tableA, JoinType joinType, java.util.List<JoinCondition> conditions ) {
    JoinRelations jr = this.getJoinRelations();

    if( jr==null ) {
      jr = new JoinRelations();
      this.setJoinRelations( jr );
    }

    if( Configurator.isLowerCase() ) {
      jr.add( relationName.toLowerCase(), tableA.toLowerCase(), joinType, conditions );
    }
    else {
      jr.add( relationName, tableA, joinType, conditions );
    }

    return this;
  }

  /**
   *
   * @return
   */
  public JoinRelations getJoinRelations() {
    return (JoinRelations)this.getValue( JOIN_RELATIONS );
  }

  /**
   *
   */
    @Override
  public String toString() {
    return this.getEntityName();
  }

  /**
   *
   */
  void setJoinRelations( JoinRelations jr ) {
    this.setValue( JOIN_RELATIONS, jr );
  }

  /**
   * set an additional identity where conditions used in all find commands
   *
   * <pre>
   * note:
   *
   * the condition string must contains fields like <i>tableName<i/>.<i>fieldName</i>
   * that are evaluated through constant expression
   *
   * examples:
   *
   * bd.setIdentityConditions( "address.primary = 'Y' AND ...." );
   *
   * </pre>
   * @see BeanManagerBase#enableIdentityConditions
   * @param identityConditions
   */
  public void setIdentityConditions(String identityConditions) {
    this.setValue( IDENTITY_CONDITIONS, identityConditions );
  }

  /**
   *
   * @return additional where condition. <b>null</b> if doesn't exist
   */
  public String getIdentityConditions() {
    return (String)this.getValue(IDENTITY_CONDITIONS);
  }

  /**
   *
   * @param enableIdentityCondition
   */
  public void setEnableIdentityConditions(boolean enableIdentityCondition) {
    this.enableIdentityConditions = enableIdentityCondition;
  }

  /**
   *
   * @return
   */
  public boolean isEnableIdentityConditions() {
    return enableIdentityConditions;
  }

  /**
   *
   * @param aggregateOrder execution command order for the main entity (<i>used only in aggregate case</i>) - values: <b>ORDER_FIRST</b> or <b>ORDER_LAST</b>
   */
  public void setAggregateOrder(Integer aggregateOrder) {
    this.setValue( AGGREGATE_ORDER, aggregateOrder );
  }

  /**
   *
   * @return
   */
  public Integer getAggregateOrder() {
    return (Integer)this.getValue( AGGREGATE_ORDER );
  }


  /**
   * add a new custom find command
   *
   * <pre>
   * Writing SQL command you can use the BeanManager build-in parameter listed below:
   * {0} --> list of parameter
   * {1} --> entity name
   * {2} --> join conditions ( contains SQL JOIN clause too )
   * {3} --> where condition
   *
   *
   * Example:
   *
   * bd.setCustomFindCommand( "find100", "SELECT {0} FROM {1} WHERE {3} LIMIT 100" );
   * bd.setCustomFindCommand( "find200", "SELECT {0} FROM {1} WHERE {3} LIMIT 200" );
   *
   * bd.setCustomFindCommand( "find200", "SELECT {0},MAX(x) FROM {1} {2} WHERE {3} GROUP BY x" );
   *
   * </pre>
   *
   * @param key commad key
   * @param command SQL command
   */
  public void setCustomFindCommand( String key, String command ) {
    if(key==null || command==null) return;
    String prefix = this.getCustomCommandPrefix();

    if( Configurator.isLowerCase() ) {
      BeanManagerImpl.customCommands.setProperty(
          prefix+"."+key,
          command.toLowerCase() );
    }
    else {
      BeanManagerImpl.customCommands.setProperty(
          prefix+"."+key,
          command );
    }


  }

  /**
   *
   * @param key
   * @return
   */
  public String getCustomFindCommand( String key ) {
    String prefix = this.getCustomCommandPrefix();

    return BeanManagerImpl.customCommands.getProperty(prefix+"."+key);
  }

  /**
   *
   */
  protected String getCustomCommandPrefix() {
    return this.getBeanClass().getName();
    //return this.getEntityName();
  }
}
