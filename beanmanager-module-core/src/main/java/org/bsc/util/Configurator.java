package org.bsc.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configurator manager 
 *
 * Internal class utility for manage configuration properties
 *
 * Configuration file is named : <b>BeanManager.properties</b>
 *
 * <pre>
 * <table border=1 cellspacing=5 cellpadding=0>
 * <tr>
 * <th>name</th><th>values</th><th>default</th>
 * </tr>
 * <tr>
 * <td colspan=3><b>common section</b></td>
 * </tr>
 * <tr>
 * <td>lowercase</td><td>true|false</td><td>false</td>
 * </tr>
 * </table>
 * </pre>
 *
 * @author Bartolomeo Sorrentino
 * @version 1.0
 */

public class Configurator {
  public static final Logger log = Logger.getLogger( Configurator.class.getName() );

  static final String BUNDLE = "org.bsc.bean.res.BeanManagerMessages";
  static final String RESOURCE = "BeanManager.properties";
  static final String CUSTOM_COMMANDS = "beanmanagerCommands.properties";


  static final String PROP_CASE_SENSITIVE = "lowercase";

  /** */
  static java.util.Properties props = new java.util.Properties();

  /**
   *
   * @return
   */
  private static ClassLoader getTCL() {
    java.lang.ClassLoader cl=null;


    cl = java.lang.Thread.currentThread().getContextClassLoader();

    if(cl==null) {
      cl = Configurator.class.getClassLoader();
    }

    return cl;
  }

  /**
   *
   */
  private static void load() {

      try {

        java.io.InputStream is = getTCL().getResourceAsStream(RESOURCE);

        if( is!=null ) {
          props.load(is);
        }

      }
      catch (java.io.IOException ioex) {
        log.log( Level.WARNING, "BeanManager.Configurator.load", ioex);
      }

  }

  ////////////////// STATIC INITIALIZER ///////////////////
  static {
    load();
  }
  ////////////////// STATIC INITIALIZER ///////////////////


  /**
   *
   * @return
   */
  public static java.util.ResourceBundle getMessages() {
    return java.util.ResourceBundle.getBundle(BUNDLE);
  }

  /**
   *
   * @return
   */
  public static java.util.Properties loadCustomCommands() {
      java.util.Properties p = new java.util.Properties();

      try {
      java.io.InputStream is = getTCL().getResourceAsStream(CUSTOM_COMMANDS);

      if( is!=null ) {
          p.load(is);
      }

      }
      catch (Exception ex) {
        log.log( Level.WARNING, "loadCustomCommands.exception", ex);
      }

      return p;
  }

  /**
   *
   * @param level
   * @return
   */
  public static boolean getPropertyBoolean( String name, boolean defaultValue ) {
    String value = props.getProperty(name);
    return (value==null) ? defaultValue :  Boolean.valueOf(value).booleanValue();
  }
  /**
   *
   * @param level
   * @return
   */
  public static String getPropertyString( String name, String defaultValue ) {
    return props.getProperty(name,defaultValue);
  }


  /**
   *
   * @return
   */
  public static boolean isLowerCase() {
    return getPropertyBoolean( PROP_CASE_SENSITIVE, false );
  }

}