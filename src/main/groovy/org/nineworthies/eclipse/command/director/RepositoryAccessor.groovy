package org.nineworthies.eclipse.command.director

interface RepositoryAccessor {

	String getUrl()
	
	String getName()
	
	List getInstallableUnits()
}
