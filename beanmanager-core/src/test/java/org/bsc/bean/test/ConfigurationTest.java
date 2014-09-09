/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bsc.bean.test;

import junit.framework.Assert;
import org.bsc.util.Configurator;
import org.junit.Test;


/**
 *
 * @author softphone
 */
public class ConfigurationTest {



    @Test
    public void variableSubstitution() {

        String v = Configurator.variableSubstitution("TEST");

        Assert.assertEquals("TEST", v);

        v = Configurator.variableSubstitution("${TEST}");

        Assert.assertEquals("${TEST}", v);

        System.setProperty( "global.schema", "MYSCHEMA");

        v = Configurator.variableSubstitution("${global.schema}");

        Assert.assertEquals("MYSCHEMA", v);

        v = Configurator.variableSubstitution("X_${global.schema}");

        Assert.assertEquals("X_MYSCHEMA", v);

        v = Configurator.variableSubstitution("${global.schema}.MYTABLE");

        Assert.assertEquals("MYSCHEMA.MYTABLE", v);
    }
}
