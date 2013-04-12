package org.nineworthies.eclipse.command.director

import java.util.Map;

import groovy.lang.Closure;

import org.nineworthies.eclipse.command.EclipseArguments


class DirectorArguments implements DirectorArgumentsAccessor, 
	DirectorArgumentsHandler, InstallableUnitsHandler {
	
	static DirectorArguments createFrom(
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DirectorArguments)
		Closure args,
		Properties config = null,
		String basePath = null) {
		
		def directorArgs = new DirectorArguments(config, basePath)
		args.setDelegate(directorArgs)
		args.setResolveStrategy(Closure.DELEGATE_ONLY)
		args.call()
		return directorArgs
	}

	private Properties config = [:]
	
	private String basePath
	
	private String destination

	private def repositories = []
	
	private def installableUnits = []
	
	private DirectorOperation operation
	
	DirectorArguments() { }
	
	DirectorArguments(Properties config, String basePath) {
		if (config) {
			this.config.putAll(config)
		}
		this.basePath = basePath
	}
	
	void destination(String path) {
		destination = path
	}

	void repository(String url) {
		repositories.add(url)
	}
	
	void installableUnit(
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = InstallableUnitArguments)
		Closure args) {
		
		def iuArgs = new InstallableUnitArguments()
		args.setDelegate(iuArgs)
		args.setResolveStrategy(Closure.DELEGATE_ONLY)
		args.call()
		installableUnits.add(iuArgs)
	}
	
	// sets install operation with only the installable units in argsClosure
	void install(
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = InstallArguments)
		Closure args) {
		
		def installArgs
		if (basePath) {
			installArgs = InstallArguments.createFrom(args, config, basePath)
		} else {
			installArgs = InstallArguments.createFrom(args, config)
		}
		operation = new InstallOperation(installArgs.installableUnits)
		mergeArgumentsFrom(installArgs.directorArguments)
	}

	void uninstall(
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = InstallArguments)
		Closure args) {
		
		def installArgs
		if (basePath) {
			installArgs = InstallArguments.createFrom(args, config, basePath)
		} else {
			installArgs = InstallArguments.createFrom(args, config)
		}
		operation = new UninstallOperation(installArgs.installableUnits)
		mergeArgumentsFrom(installArgs.directorArguments)
	}
	
	// TODO define the meaning of 'merge' here
	void mergeArgumentsFrom(DirectorArgumentsAccessor otherArgs) {
		if (otherArgs.destination) {
			destination = otherArgs.destination
		}
		repositories.addAll(otherArgs.repositories)
		installableUnits.addAll(otherArgs.installableUnits)
		if (otherArgs.operation) {
			operation = otherArgs.operation
		}
	}
	
	void appendArgs(Appendable command) {
		if (destination) {
			command << " -destination"
			command << (destination.contains(" ") ? / "$destination"/ : " $destination")
		}
		operation?.appendArgs(command, this)
	}
	
	Properties getConfig() {
		return config
	}
	
	String getDestination() {
		return destination
	}
	
	Iterable<String> getRepositories() {
		return repositories
	}
	
	List<InstallableUnitArgumentsAccessor> getInstallableUnits() {
		return installableUnits
	}
	
	DirectorOperation getOperation() {
		return operation;
	}
}