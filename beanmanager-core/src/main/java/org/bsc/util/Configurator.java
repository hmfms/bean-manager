package org.bsc.util;

import static org.bsc.bean.BeanManagerUtils.getMessage;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.bsc.bean.BeanManagerMessages;

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
    public static final String PROP_DEFAULT_SCHEMA = "default.schema";
    public static final Logger log = Logger.getLogger(Configurator.class.getName());
    static final String RESOURCE = "BeanManager.properties";
    static final String CUSTOM_COMMANDS = "sqlCommands.properties";
    static final String PROP_CASE_SENSITIVE = "lowercase";
    /** */
    static java.util.Properties props = new java.util.Properties();
    static java.util.Properties customCommands = null;

    /**
     *
     * @return
     */
    private static ClassLoader getTCL() {
        java.lang.ClassLoader cl = null;


        cl = java.lang.Thread.currentThread().getContextClassLoader();

        if (cl == null) {
            cl = Configurator.class.getClassLoader();
        }

        return cl;
    }

    /**
     *
     */
    private static void init() {

        try {

            java.io.InputStream is = getTCL().getResourceAsStream(RESOURCE);

            if (is != null) {
                props.load(is);
            }

        } catch (java.io.IOException ioex) {
            log.log(Level.WARNING, "BeanManager.Configurator.load", ioex);
        }

    }

    /**
     * clear cached data
     */
    public static void clear() {
        synchronized (Configurator.class) {
            props.clear();
            if (null != customCommands) {
                customCommands.clear();
                customCommands = null;
            }
        }
    }

    ////////////////// STATIC INITIALIZER ///////////////////
    static {
        init();
    }
    ////////////////// STATIC INITIALIZER ///////////////////

    /**
     *
     * @return
     */
    public static BeanManagerMessages getMessages() {
        return BeanManagerMessages.createInstance(Locale.getDefault());
    }

    /**
     * Load custom commands from classloader path.
     * <pre>
     * try to find resource where there are all
     * custom sql commands. These commands will be added to internal command cache
     *
     * if the command start with '/' char it will be considered an path to another resource
     * and the system will try to init it through ClassLoader.getResourceAsStream API
     *
     * assumptions:
     * - Each resource must contain one SQL command
     *
     * </pre>
     *
     * @param propertyResourceName  resource name (file must be .properties compliant)
     * @return all commads resolved as properties MAP
     * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
     * @see java.util.Properties
     * @see org.bsc.bean.BeanManager#prepareCustomFind(java.sql.Connection, java.lang.String, java.lang.String)
     */
    public static java.util.Properties loadCustomCommands(String propertyResourceName) {
        if (propertyResourceName == null) {
            throw new IllegalArgumentException("property resource name is null!");
        }

        java.util.Properties result = new java.util.Properties();

        try {

            java.io.InputStream is = getTCL().getResourceAsStream(propertyResourceName);

            if (is != null) {
                result = new java.util.Properties();
                result.load(is);


                for (Map.Entry<Object, Object> e : result.entrySet()) {

                    String value = (String) e.getValue();
                    if (null != value && value.charAt(0) == '/') { // check if is a resource

                        Log.debug("loading custom command {0} resource [{1}]", e.getKey(), e.getValue());
                        java.io.InputStream ris = getTCL().getResourceAsStream(value.substring(1));

                        if (null != ris) {

                            String content = null;

                            try {
                                content = loadContent(new java.io.InputStreamReader(ris));

                                result.put(e.getKey(), content);

                            } catch (Exception ex) {
                                Log.warn("error on loading  content for custom command {0} uri [{1}]", ex, e.getKey(), e.getValue());
                            }

                        } else {
                            Log.warn("Does not exist content for custom command {0} uri [{1}]", e.getKey(), e.getValue());
                        }
                    }
                }

                synchronized (Configurator.class) {

                    if (customCommands == null) {

                        customCommands = new java.util.Properties();
                    }

                    customCommands.putAll(result);
                }
            }
        } catch (IOException ex) {
            log.log(Level.WARNING, "loadCustomCommands.exception", ex);
        }

        return result;
    }

    /**
     * Load custom commands from classloader path.
     * <pre>
     * try to find resource 'sqlCommands.properties' where there are all
     * custom sql commands
     *
     * if the command start with '/' char it will be considered an path to another resource
     * and the system will try to init it through ClassLoader.getResourceAsStream API
     *
     * assumptions:
     * - Each resource must contain one SQL command
     *
     * </pre>
     *
     * @deprecated  use #loadCustomCommands(java.lang.String)
     * @return all commads resolved as properties MAP
     * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
     * @see java.util.Properties
     */
    public static java.util.Properties loadCustomCommands() {
        return loadCustomCommands(CUSTOM_COMMANDS);
    }

    private static String loadContent(java.io.Reader reader) throws Exception {
        if (null == reader) {
            throw new IllegalArgumentException("param reader is null!");
        }

        final int buffer_lenght = 256 * 1024;

        char buffer[] = new char[buffer_lenght];

        java.io.StringWriter sw = new java.io.StringWriter(buffer_lenght);

        int num_read = 0;
        int len_to_read = buffer_lenght;

        while ((num_read = reader.read(buffer, 0, len_to_read)) == len_to_read) {

            sw.write(buffer);
        }

        if (num_read > 0) {
            sw.write(buffer, 0, num_read);
        }

        return sw.toString();
    }

    private static String loadFileContent(File f) throws Exception {
        if (null == f) {
            throw new IllegalArgumentException("param f is null!");
        }

        java.io.FileReader fir = new java.io.FileReader(f);

        String content = loadContent(fir);

        fir.close();

        return content;

    }

    /**
     * Load custom commands from folder.
     * Scan a given folder and init file with given extension.
     * These commands will be added to internal command cache
     *
     * assumptions:
     * - The file must be textual file
     * - Each file must contain one SQL command
     *
     * @param result Properties where add the commands - Key=file name without extension, value=file content
     * @param folder folder where are located the file
     * @param ext file extension
     * @return the same instance given in input with new values
     * @see org.bsc.bean.BeanManager#prepareCustomFind(java.sql.Connection, java.lang.String, java.lang.String)
     */
    public static java.util.Properties loadCustomCommands(final File folder, final String ext) {
        java.util.Properties result = new java.util.Properties();

        loadCustomCommands(result, folder, ext);

        synchronized (Configurator.class) {

            if (customCommands == null) {

                customCommands = new java.util.Properties();
            }

            customCommands.putAll(result);
        }

        return result;
    }

    /**
     * Load custom commands from folder.
     * Scan a given folder and init file with given extension
     *
     * assumptions:
     * - The file must be textual file
     * - Each file must contain one SQL command
     *
     * @param result Properties where add the commands - Key=file name without extension, value=file content
     * @param folder folder where are located the file
     * @param ext file extension
     * @return the same instance given in input with new values
     */
    public static java.util.Properties loadCustomCommands(final java.util.Properties result, final File folder, final String ext) {
        if (null == result) {
            throw new IllegalArgumentException(getMessage("ex.param_0_is_null", "result"));
        }
        if (null == folder) {
            throw new IllegalArgumentException(getMessage("ex.param_0_is_null", "folder"));
        }
        if (null == ext) {
            throw new IllegalArgumentException(getMessage("ex.param_0_is_null", "ext"));
        }

        if (!folder.exists()) {
            throw new IllegalArgumentException(String.format("folder [%s] doesn't exist!", folder.getPath()));
        }
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException(String.format("folder [%s] is not a directory!", folder.getPath()));
        }

        File[] files = folder.listFiles(new FileFilter() {

            public boolean accept(File pathname) {

                return (pathname.isFile() && pathname.getName().endsWith("." + ext));
            }
        });

        for (File f : files) {
            try {
                String name = f.getName().substring(0, f.getName().length() - (ext.length() + 1));

                if (result.containsKey(name)) {
                    Log.warn("result properties already contains command [{0}+", name);
                }

                Log.debug("loaded command {0}", name);

                result.put(
                        name,
                        loadFileContent(f));
            } catch (Exception ex) {
                Log.warn("error loading file {0}", ex, f.getName());
            }
        }

        return result;
    }

    /**
     *
     * @param level
     * @return
     */
    public static boolean getPropertyBoolean(String name, boolean defaultValue) {
        String value = props.getProperty(name);
        return (value == null) ? defaultValue : Boolean.valueOf(value).booleanValue();
    }

    /**
     *
     * @param level
     * @return
     */
    public static String getPropertyString(String name, String defaultValue) {
        return props.getProperty(name, defaultValue);
    }

    /**
     *
     * @return
     */
    public static boolean isLowerCase() {
        return getPropertyBoolean(PROP_CASE_SENSITIVE, false);
    }

    public static String getDefaultSchema() {
        return props.getProperty( PROP_DEFAULT_SCHEMA);
    }

    private static java.util.regex.Pattern varPattern = java.util.regex.Pattern.compile("\\$\\{(\\w+[\\.]?\\w+)\\}");

    public static String variableSubstitution(String input) {

        Matcher m = varPattern.matcher(input);

        StringBuilder sb = new StringBuilder();
        int index = 0;
        while (m.find()) {

            sb.append(input.substring(index, m.start()));
            if (props.containsKey(m.group(1))) {
                sb.append(props.get(m.group(1)));
            } else {
                sb.append(System.getProperty(m.group(1), m.group(0)));
            }

            index = m.end();


        }

        return (index == 0) ? input : sb.append(input.substring(index)).toString();
    }

}