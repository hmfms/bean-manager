package org.bsc.bean.osgi;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author Sorrentino
 *
 */
public class BeanManagerActivator implements BundleActivator {

	/**
	 * 
	 */
	public void start(BundleContext context) throws Exception {
		
		BeanManagerService service = new BeanManagerServiceImpl( context );
		
		// register the service
		context.registerService(BeanManagerService.class.getName(), service, new Hashtable<String,Object>() );
	

	}

	/**
	 * 
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
