package org.nineworthies.eclipse.command.director

interface DirectorArgumentsHandler {
	
	void destination(String path)
	
	void repository(String url)
	
	void install(Closure args)

	void installUnits()
	
	void uninstall(Closure args)
	
	void uninstallUnits()
}