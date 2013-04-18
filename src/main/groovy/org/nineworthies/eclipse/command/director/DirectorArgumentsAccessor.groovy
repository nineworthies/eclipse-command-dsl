package org.nineworthies.eclipse.command.director

interface DirectorArgumentsAccessor {

	String getDestination()
	
	List<RepositoryAccessor> getRepositories()
	
	List<InstallableUnitAccessor> getInstallableUnits()
	
	// FIXME not strictly an argument?
	DirectorOperation getOperation()
}
