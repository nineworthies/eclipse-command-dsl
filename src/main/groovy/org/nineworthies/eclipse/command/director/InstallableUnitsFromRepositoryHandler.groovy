package org.nineworthies.eclipse.command.director

interface InstallableUnitsFromRepositoryHandler {

	void unitsFromRepository(String url, Closure repositoryArgs)
	
	void unitsFromRepositoryNamed(String name, Closure repositoryArgs)

}
