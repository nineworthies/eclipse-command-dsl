package org.nineworthies.eclipse.command.director

interface DirectorArgumentsHandler extends InstallableUnitsFromRepositoryHandler {
	
	void destination(String path)
	
	void repository(String url)
	
	void repositoryNamed(String name, String url)
	
	void install(Closure installArgs)

	void installUnits()
	
	void uninstall(Closure uninstallArgs)
	
	void uninstallUnits()
	
	void list()
}