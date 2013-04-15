package org.nineworthies.eclipse.command.director

interface DirectorArgumentsAccessor {

	String getDestination()
	
	List<String> getRepositories()
	
	List<InstallableUnitArgumentsAccessor> getInstallableUnits()
	
	// FIXME not strictly an argument?
	DirectorOperation getOperation()
}
