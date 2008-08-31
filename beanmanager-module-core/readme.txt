Release 1.1

OSGI 
 added method lookupBeanManager() in org.bsc.bean.osgi.BeanManagerService
 added org.bsc.bean.osgi.BeanManagerServiceTracker as OSGI ServiceTracker

FIX
 CharBooleanAdapter didn't work with strings' length greater than 1

BEANINFO
 Added 'required' properties to PropertyDescriptorField (useful to DdlUtils integration)

BEANMANAGER
 Added 'storeAll' method to update all rows using one bean 

IMPROVEMENTS
 Change StringBuffer to StringBuilder when possible
 Use var args instead of Object array when possible 
 