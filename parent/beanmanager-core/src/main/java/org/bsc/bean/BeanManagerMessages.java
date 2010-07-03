
package org.bsc.bean;

//import javax.annotation.Generated;
import java.util.Locale;
import java.text.MessageFormat;

//@Generated(value="org.bsc.processor.implementation.ResourceProcessorAnnotationLess",date="2010-07-03T02:54:23.284+0200")
public class  BeanManagerMessages {

    public synchronized static BeanManagerMessages createInstance( Locale locale ) {

        if( locale==null ) locale = Locale.getDefault();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle( "org/bsc/bean/BeanManagerMessages", locale,  BeanManagerMessages.class.getClassLoader() );
        BeanManagerMessages instance = new BeanManagerMessages( bundle );

        return instance;
    }

    public final java.util.ResourceBundle bundle;

    private BeanManagerMessages( java.util.ResourceBundle bundle ) {
        this.bundle = bundle;

    }


    public String blob_required( Object...args) { return (bundle==null) ? "blob_required" : MessageFormat.format(bundle.getString("blob_required"), (Object[])args); }

    public String prop_not_serializable( Object...args) { return (bundle==null) ? "prop_not_serializable" : MessageFormat.format(bundle.getString("prop_not_serializable"), (Object[])args); }


}
