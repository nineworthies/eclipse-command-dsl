package org.nineworthies.eclipse.command.director

import groovy.transform.Immutable

@Immutable
class InstallOperation extends DirectorOperation {

	final boolean useDirectorRepositories
	
	// FIXME this field was originally named 'args', but with that name
	// the following error was seen at runtime:
	// GroovyCastException: Cannot cast object 'xxx' with 
	// class 'xxx' to class 'java.util.HashMap'
	final List<RepositoryAccessor> repositories = []
	
	void appendArgs(Appendable command, DirectorArgumentsAccessor directorArgs) {
		
		def repositories = this.useDirectorRepositories ? directorArgs.repositories : this.repositories
		def units = (repositories*.installableUnits).flatten()
		println("installing $units from $repositories to '$directorArgs.destination'")
		if (directorArgs.destination) {
			appendDestination(command, directorArgs.destination)
		}
		if (repositories) {
			appendRepositories(command, repositories)
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
