package org.bsc.bean.res;

import java.util.*;


/**
 * Title:        Bartolomeo Sorrentino Classi
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      ITD
 * @author Bartolomeo Sorrentino
 * @version 1.0
 */

public class BeanManagerMessages extends ListResourceBundle {
  static final Object[][] contents = {
    { "ex.param_0_is null", "param {0} is null!" },

    { "blob_required", "for JAVA_OBJECT type is required a BLOB field" },

    { "ex.set_store_stmt", "setStoreStatement() exception: [{0}] on property: {1}"},
    { "ex.set_create_stmt", "setCreateStatement() exception: [{0}] on property: {1}"},
    { "ex.set_prop_value", "setPropertyValue() exception: {0} on property: {1} field: {2}  value {3} class: {4}" },
    { "ex.invalid_key_param_type", "found a composite key, the param id must be an array: type mismatch" },
    { "ex.invalid_key_param_length", "found a composite key, the param id must be an array: wrong length" },
    { "ex.parameter_is_empty", " parameter ''{0}'' is empty (or null)!" },
    { "ex.need_primary_key", "need at least one primary key: primary key not found" },
    { "ex.param_result_null", "param result collection is null" },
    { "ex.param_bean_null", "param bean is null" },
    { "ex.custom_cmd_null", "custom command is null" },
    { "ex.store_props_not_found", "property ''{0}'' not found in beanInfo!" },
    { "ex.get_property_descriptors", "Beaninfo getPropertyDescriptors() error!" },

    { "find.assertion", "the number of the WHERE clause fields must be equal to find parameters" },

    { "prop_not_serializable", "for JAVA_OBJECT type is required that {0} property is a Serializable object" },

    { "jsp.connection_not_found", " connection not found! use tag <xxx:useConnection ...> before" },

    { "ex.parse.bean-info_class_not_found", " tag bean-info attribute ''type'' class ''Types.{0}'' not found! " },
    { "ex.parse.relation_class_not_found", " tag relation attribute ''type'' class ''JoinRelations.{0}'' not found! " },
    { "ex.parse.field_adapter_not_found", " tag property-field attribute ''field-adapter'' class ''{0}'' not found! " },
    { "ex.parse.field_adapter_bad_type", " tag property-field attribute ''field-adapter'' class ''{0}'' not implement DataAdapter interface! " },
    { "ex.parse.field_adapter_create_error", " tag property-field attribute ''field-adapter'' class ''{0}'' newInstance() error! " },
    { "ex.parse.property_jdbc_type_not_found", " tag property-field field-type attribute jdbc type ''java.sql.Types.{0}'' not found! " },
    { "ex.parse.property_join_table_not_found", " tag property-field name=''{0}'' field-table attribute not found! " },
    { "ex.parse.bean-info-ref_invalid_uri", " tag bean-info-ref uri=''{0}'' not valid! " }
  };


  protected Object[][] getContents() {
    return contents;
  }
}
