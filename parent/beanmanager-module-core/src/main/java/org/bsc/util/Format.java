

package org.bsc.util;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 class utility

 formatting features

*/
public class Format
{
   public static DecimalFormatSymbols numbersym = new DecimalFormatSymbols(Locale.ITALIAN);
   public static DateFormatSymbols datesym = new DateFormatSymbols(Locale.ITALIAN);

   /////////////////////// DATE SECTION ////////////////////////////

   /**
   format a Date object into string using a SimpleDateFormat pattern
   @param value
   @param pattern date pattern used in SimpleDateFormat
    <pre>
    Symbol   Meaning                 Presentation        Example
    ------   -------                 ------------        -------
    G        era designator          (Text)              AD
    y        year                    (Number)            1996
    M        month in year           (Text & Number)     July & 07
    d        day in month            (Number)            10
    h        hour in am/pm (1~12)    (Number)            12
    H        hour in day (0~23)      (Number)            0
    m        minute in hour          (Number)            30
    s        second in minute        (Number)            55
    S        millisecond             (Number)            978
    E        day in week             (Text)              Tuesday
    D        day in year             (Number)            189
    F        day of week in month    (Number)            2 (2nd Wed in July)
    w        week in year            (Number)            27
    W        week in month           (Number)            2
    a        am/pm marker            (Text)              PM
    k        hour in day (1~24)      (Number)            24
    K        hour in am/pm (0~11)    (Number)            0
    z        time zone               (Text)              Pacific Standard Time
    '        escape for text         (Delimiter)
    ''       single quote            (Literal)           '
    </pre>
    @return formatted string value
   @see java.util.Date
   @see java.text.SimpleDateFormat
   */
   public static String formatDate(java.util.Date value, java.lang.String pattern) {
    SimpleDateFormat df = new SimpleDateFormat(pattern,datesym);
   	return df.format(value).toString();
   }

   /**
    @param value
    @param pattern date pattern used in SimpleDateFormat
    @return Date
    @exception ParseException
    @see java.util.Date
    @see java.text.SimpleDateFormat
    */
   public static java.util.Date parseDate(java.lang.String value, java.lang.String pattern ) throws ParseException {
    SimpleDateFormat df = new SimpleDateFormat(pattern,datesym);
   	return df.parse(value);
   }

   /////////////////////// NUMBER SECTION ////////////////////////////

   /**
   format a number object into string using a decimalFormat pattern
   @param value
   @param pattern date pattern used in DecimalFormat
        <pre>
        Symbol Meaning
        0      a digit
        #      a digit, zero shows as absent
        .      placeholder for decimal separator
        ,      placeholder for grouping separator.
        ;      separates formats.
        -      default negative prefix.
        %      divide by 100 and show as percentage
        X      any other characters can be used in the prefix or suffix
        '      used to quote special characters in a prefix or suffix.
        </pre>
   @return formatted string value
   */
   public static String formatNumber( java.lang.Number value, java.lang.String pattern ) {
    DecimalFormat df = new DecimalFormat(pattern,numbersym);
    return df.format(value);
   }

   /**
   convert string into number using a decimalFormat pattern
   @param value
   @param pattern date pattern used in DecimalFormat
   @return Number value
   @exception ParseException
   @see java.text.DecimalFormat
   */
   public static Number parseNumber(java.lang.String value, java.lang.String pattern) throws ParseException {
    DecimalFormat df = new DecimalFormat(pattern,numbersym);
    return df.parse(value);
   }

   /////////////////////// BOOLEAN SECTION ////////////////////////////

   /**
   format a boolean object into string using a FormatMessage pattern

   @param value
   @param pattern {0,choice,0#<i>false string</i>|1#<i>true string</i>}"

   <pre>
   java.text.MessageFormat mf =
   new java.text.MessageFormat("{0,choice,0#are no files|1#is one file}");
    Boolean value = new Boolean(false);
   Object params[] = { new Integer(value.booleanValue()?1:0) };
   System.out.println( mf.format(params) );
   </pre>

   @return formatted string value

   */
   public static String formatBoolean( java.lang.Boolean value, java.lang.String pattern ) {
    if( value==null ) return "";
    try {

    java.text.MessageFormat mf = new java.text.MessageFormat(pattern);
    Object params[] = { new Integer(value.booleanValue()?1:0) };
    return mf.format(params);

    } catch( Exception ex ) {}

    return value.toString();
   }

}
