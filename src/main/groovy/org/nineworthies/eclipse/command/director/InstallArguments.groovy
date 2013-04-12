package org.nineworthies.eclipse.command.director

import org.nineworthies.eclipse.command.ConfigurableArguments;
import org.nineworthies.eclipse.command.EclipseArguments

class InstallArguments extends ConfigurableArguments 
	implements InstallArgumentsHandler, InstallableUnitsHandler {
	
	static InstallArguments createFrom(
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = InstallArguments)
		Closure argsClosure,
		ConfigObject config = null,
		String basePath = null) {
		
		def args = new InstallArguments(config, basePath)
		argsClosure.setDelegate(args)
		argsClosure.setResolveStrategy(Closure.DELEGATE_ONLY)
		argsClosure.call()
		return args
	}

	private DirectorArguments directorArgs
	
	private String basePath
	
	InstallArguments() {
		this.directorArgs = new DirectorArguments()
	}
	
	InstallArguments(ConfigObject config, String basePath) {
		super(config)
		this.directorArgs = new DirectorArguments(config, basePath)
		this.basePath = basePath
	}
	
	void installableUnitsFrom(String argsPath) {
		def argsFile = new File((String) basePath, argsPath)
		def args = EclipseArguments.createFrom(argsFile).getDirectorArguments()
		directorArgs.mergeArgumentsFrom(args)
	}
	
	void installableUnit(Closure args) {
		directorArgs.installableUnit(args)
	}
	
	DirectorArgumentsAccessor getDirectorArguments() {
		return directorArgs
	}
	
	List<InstallableUnitArgumentsAccessor> getInstallableUnits() {
		return directorArgs.installableUnits
	}
}