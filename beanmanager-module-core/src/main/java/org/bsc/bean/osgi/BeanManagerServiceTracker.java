package org.bsc.bean.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
/**
 * 
 * @author Sorrentino
 *
 */
public class BeanManagerServiceTracker extends ServiceTracker {

	/**
	 * 
	 * @param context
	 */
	public BeanManagerServiceTracker( BundleContext context) {
		super( context, BeanManagerService.class.getName(), null );

	}


}
