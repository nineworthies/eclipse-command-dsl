package org.nineworthies.eclipse.command.director

interface DirectorArgumentsAccessor {

	String getDestination()
	
	Iterable<String> getRepositories()
	
	Iterable<InstallableUnitArgumentsAccessor> getInstallableUnits()
	
	// FIXME not strictly an argument?
	DirectorOperation getOperation()
}
