package org.nineworthies.eclipse.command.director

interface RepositoryAccessor {

	String getUrl()
	
	List<InstallableUnitAccessor> getInstallableUnits()
}
