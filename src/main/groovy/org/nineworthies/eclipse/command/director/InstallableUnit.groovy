package org.nineworthies.eclipse.command.director

import groovy.transform.ToString

@ToString(includeFields = true, includePackage = false, ignoreNulls = true)
class InstallableUnit implements InstallableUnitAccessor {

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