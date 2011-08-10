package org.bsc.bean.ddl;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

public class DDLProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> elementSet,	RoundEnvironment env) {
		return false;
	}

}
