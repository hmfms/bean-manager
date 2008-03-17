package org.bsc.bean;

import org.bsc.util.Configurator;

/**
 * Title:        Bartolomeo Sorrentino Classi
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      ITD
 * @author Bartolomeo Sorrentino
 * @version 1.0
 */

public class JoinCondition {

    protected String fieldA;
    protected String fieldB;

    protected StringBuffer condition = new StringBuffer();

    /**
     * <pre>
     * create a single joincondition like:
     *
     * entityA.fieldA=entityB.fieldB
     *
     * </pre>
     *
     * @param fieldA field associated with master entity of the relation
     * @param fieldB field associated of secondary entity
     */
    public JoinCondition( String fieldA, String fieldB ) {
      if( Configurator.isLowerCase() ) {
        this.fieldA = fieldA.toLowerCase();
        this.fieldB = fieldB.toLowerCase();
      }
      else {
        this.fieldA = fieldA;
        this.fieldB = fieldB;
      }

    }

    /**
     *
     */
    public void init( String entityA, String entityB ) {
      condition.append( entityA );
      condition.append( '.' );
      condition.append( fieldA );
      condition.append( '=' );
      condition.append( entityB );
      condition.append( '.' );
      condition.append( fieldB );
    }

    /**
     *
     */
    public String toString() {
      return condition.toString();
    }

}
