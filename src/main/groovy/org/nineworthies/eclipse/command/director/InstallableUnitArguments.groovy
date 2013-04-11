package org.nineworthies.eclipse.command.director

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.nineworthies.eclipse.command.director.InstallableUnitArguments;

import groovy.lang.Closure
import groovy.transform.ToString;

@ToString(includeFields = true, includePackage = false)
class InstallableUnitArguments implements InstallableUnitArgumentsAccessor {

	private String id
	
	private String version
	
	def id(String id) {
		this.id = id
	}
	
	String getId() {
		return id;
	}
	
	def version(String version) {
		this.version = version
	}
	
	String getVersion() {
		return version;
	}
}