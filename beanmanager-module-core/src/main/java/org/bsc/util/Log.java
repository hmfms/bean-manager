package org.bsc.util;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Internal log manager
 *
 * class utility for manage log functionality
 *
 * @author Bartolomeo Sorrentino
 * @version 1.0
 */

public class Log {

    private static final Logger out = Logger.getLogger("bsc");
    private static final Logger cmd = Logger.getLogger("bsc.command");

    public static void trace( String msgPattern, Object ...params ) {
        out.log( Level.FINER, MessageFormat.format(msgPattern, params) );
    }
    
    public static void debug( String msgPattern, Object ...params ) {
        out.log( Level.FINE,  MessageFormat.format(msgPattern, params) );
    }
    
    public static void info( String msgPattern, Object ...params ) {
        out.log( Level.INFO, MessageFormat.format(msgPattern, params) );
    }

    public static void error( String msgPattern, Object ...params ) {
        out.severe( MessageFormat.format(msgPattern, params) );
    }

    public static void error( String msgPattern, Throwable t, Object ...params ) {
        out.log( Level.SEVERE, MessageFormat.format(msgPattern, params), t );
    }
    
    public static void warn( String msgPattern, Object ...params ) {
        out.warning( MessageFormat.format(msgPattern, params) );
    }

    public static void warn( String msgPattern, Throwable t, Object ...params ) {
        out.log( Level.WARNING, MessageFormat.format(msgPattern, params), t );
    }
    
    public static boolean isTraceEnabled() {
        return out.isLoggable(Level.FINER);    
    }

    public static boolean isDebugEnabled() {
        return out.isLoggable(Level.FINE);    
    }
    
    /////////////////// TRACE SECTION ///////////////////////

    public static void TRACE_CMD( String descr, CharSequence value ) {
      cmd.info( descr + ": " + value );
    }

    public static long TRACE_TIME_BEGIN( ) {
      return System.currentTimeMillis();
    }

    public static void TRACE_TIME_END( String descr , long time ) {
      
        long e_time = System.currentTimeMillis() - time;

        cmd.info( java.text.MessageFormat.format(descr + " time msec. {0} sec. {1}", e_time, e_time/1000) );
    }


    /////////////////// TRACE SECTION ///////////////////////

}