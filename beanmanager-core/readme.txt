Release 1.3.1
================
add propertyDescriptorField copy constructor
add possibilty to pass command other than commandKey in prepareCustomFind

Release 1.3
================
add support IN clause
change signature for findById method from findById(Connection conn, Object id) to findById(Connection conn, Object...id);
change signature for removeById method from removeById(Connection conn, Object id) to removeById(Connection conn, Object...id);
add method Configurator.loadCustomCommands to load/cache custom commands

FIX
DateTimeAdapter NullPointerException on getValue when null value come in

Release 1.2
================

add property size to PropertyDescriptorField
add property required to PropertyDescriptorField

generic improvements

Release 1.1
================

OSGI 
 added method lookupBeanManager() in org.bsc.bean.osgi.BeanManagerService
 added method regiaterBeanManagerEx() in org.bsc.bean.osgi.BeanManagerService to register a beanManager using a custom key
 added org.bsc.bean.osgi.BeanManagerServiceTracker as OSGI ServiceTracker

FIX
 CharBooleanAdapter didn't work with strings' length greater than 1

BEANINFO
 Added 'required' properties to PropertyDescriptorField (useful to DdlUtils integration)

BEANMANAGER
 Added 'CLOBAdapter'
 Added 'storeAll' method to update all rows using bean's properties
 added 'findAndRemove' method to remove rows using filter

IMPROVEMENTS
 Change StringBuffer to StringBuilder when possible
 Use var args instead of Object array when possible 
 
Release 1.2
================

OSGI 

FIX
 DateTimeAdapter didn't return time information

BEANINFO

BEANMANAGER

IMPROVEMENTS

Release 1.3.1.001
================

add support of internationalization-plugin

