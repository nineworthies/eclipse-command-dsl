package org.nineworthies.eclipse.command.director

import groovy.transform.Immutable

@Immutable
class UninstallOperation extends DirectorOperation {

	final boolean useDirectorUnits
	
	final List installableUnits = []

	void appendArgs(List command, DirectorArgumentsAccessor directorArgs) {
		
		def units = this.useDirectorUnits ? directorArgs.installableUnits : this.installableUnits
		println "Uninstalling $units from '$directorArgs.destination'"
		if (directorArgs.destination) {
			appendDestination(command, directorArgs.destination)
		}
		if (units) {
			command << "-uninstallIU" << units.collect { it.id }.join(", ")
		}
	}
}
