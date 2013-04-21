package org.nineworthies.eclipse.command.director

interface DirectorArgumentsAccessor {

	String getDestination()
	
	List<RepositoryAccessor> getRepositories()
	
	// FIXME not strictly an argument?
	DirectorOperation getOperation()
}
