package org.nineworthies.eclipse.command.director

import groovy.transform.Immutable

@Immutable
class InstallOperation extends DirectorOperation {

	final boolean useDirectorRepositories
	
	// FIXME this field was originally named 'args', but with that name
	// the following error was seen at runtime:
	// GroovyCastException: Cannot cast object 'xxx' with 
	// class 'xxx' to class 'java.util.HashMap'
	final List repositories = []
	
	void appendArgs(List command, DirectorArgumentsAccessor directorArgs) {
		
		def repositories = this.useDirectorRepositories ? directorArgs.repositories : this.repositories
		def units = (repositories*.installableUnits).flatten()
		println "Installing $units from $repositories to '$directorArgs.destination'"
		if (directorArgs.destination) {
			appendDestination(command, directorArgs.destination)
		}
		if (repositories) {
			appendRepositories(command, repositories)
		}
		if (units) {
			command << "-installIU" << units.collect { it.id }.join(", ")
		}
	}
}
