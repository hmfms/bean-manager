package org.bsc.bean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bsc.util.Log;

/**
 *
 * Get the words ( not sql operators like 'AND' 'OR' .. ) from string that
 * rapprent a SQL WHERE CLAUSE
 *
 * <p>Title: Bartolomeo Sorrentino Classi</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ITD</p>
 * @author unascribed
 * @version 1.0
 */
class PropertyParser<T>  {
  /**
   * 
   * @param where
   * @param allProps
   * @param properties
   * @return
   */
  public static <T> String parseWhere( String where, java.util.Map<String,PropertyDescriptorField> allProps, java.util.Collection<PropertyPosition> properties ) {
	  
		String pattern = "(?:[$|#][{](\\w*)[}])";
		
		Pattern p = Pattern.compile( pattern );
		
		Matcher m = p.matcher(where);

		StringBuilder sb = new StringBuilder();
		
		int start = 0;
		int end = 0;
		int s = 0;
		while( m.find(s)  ) {
		
			start = m.start(1);
			end = m.end(1);
			
			String value = m.group(1);
			
			Log.debug( "group={0}, start={1}, end={2}",  value, start, end );
			
			if( !allProps.containsKey(value) ) {
				throw new IllegalArgumentException( "attribute " + value + " is not a valid field" );
			}
			
			PropertyDescriptorField f = allProps.get(value);

			if( where.charAt(start-2)=='$' ) {
				properties.add( new PropertyPosition( start, f  ) );
			}
			
			sb.append( where.substring(s, start-2) ).append( f.getFieldName() );

			s = end + 1;
			
		}

		sb.append( where.substring(s) );
		
		return sb.toString();
  }
}

