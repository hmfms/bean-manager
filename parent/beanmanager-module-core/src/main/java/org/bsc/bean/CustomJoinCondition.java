package org.bsc.bean;

import java.text.MessageFormat;

public class CustomJoinCondition extends JoinCondition {

  public CustomJoinCondition(String patternCondition) {
    super(patternCondition, null); //use fieldA as pattern of condition
  }

  public void init(String table1, String table2) {
    condition.append(MessageFormat.format(fieldA, table1, table2 ));
  }

  public String toString()
  {
      return condition.toString();
  }

}
