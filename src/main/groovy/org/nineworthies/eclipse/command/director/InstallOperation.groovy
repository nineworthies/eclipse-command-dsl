package org.nineworthies.eclipse.command.director

import groovy.transform.Immutable

@Immutable
class InstallOperation extends DirectorOperation {

	final boolean useDirectorUnits
	
	// FIXME this field was originally named 'args', but with that name
	// the following error was seen at runtime:
	// GroovyCastException: Cannot cast object 'xxx' with 
	// class 'xxx' to class 'java.util.HashMap'
	final List<InstallableUnitAccessor> installableUnits = []
	
	void appendArgs(Appendable command, DirectorArgumentsAccessor directorArgs) {
		
		def units = this.useDirectorUnits ? directorArgs.installableUnits : this.installableUnits
		println("installing $units from $directorArgs.repositories to '$directorArgs.destination'")
		if (directorArgs.destination) {
			appendDestination(command, directorArgs.destination)
		}
		if (directorArgs.repositories) {
			appendRepositories(command, directorArgs.repositories)
		}
		units.each { unit ->
			if (unit == units.first()) {
				command << (units.size() > 1 ? / -installIU "$unit.id/ : " -installIU $unit.id")
			} else if (unit == units.last()) {
				command << (units.size() > 1 ? /, $unit.id"/ : ", $unit.id")
			} else {
				command << ", $unit.id"
			}
		}
	}
}
