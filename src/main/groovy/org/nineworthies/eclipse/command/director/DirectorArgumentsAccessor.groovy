package org.nineworthies.eclipse.command.director

interface DirectorArgumentsAccessor {

	File getDestination()
	
	List<RepositoryAccessor> getRepositories()
	
	// FIXME not strictly an argument?
	DirectorOperation getOperation()
}
