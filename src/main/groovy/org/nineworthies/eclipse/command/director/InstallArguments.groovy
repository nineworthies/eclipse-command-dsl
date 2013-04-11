package org.nineworthies.eclipse.command.director

import java.util.Properties;

import groovy.lang.Closure;

import org.nineworthies.eclipse.command.EclipseArguments

class InstallArguments implements InstallArgumentsHandler, InstallableUnitsHandler {
	
	static InstallArguments createFrom(
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = InstallArguments)
		Closure argsClosure,
		Properties config = null,
		String basePath = null) {
		
		def args = new InstallArguments(config, basePath)
		argsClosure.setDelegate(args)
		argsClosure.setResolveStrategy(Closure.DELEGATE_ONLY)
		argsClosure.call()
		return args
	}

	private DirectorArguments directorArgs

	private Properties config = [:]
	
	private String basePath
	
	InstallArguments() {
		this.directorArgs = new DirectorArguments()
	}
	
	InstallArguments(Properties config, String basePath) {
		if (config) {
			this.config.putAll(config)
		}
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
	
	Properties getConfig() {
		return config
	}
}