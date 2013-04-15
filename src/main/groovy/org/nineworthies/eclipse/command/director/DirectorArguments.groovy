package org.nineworthies.eclipse.command.director

import java.util.Map;

import groovy.lang.Closure;

import org.nineworthies.eclipse.command.ConfigurableArguments;
import org.nineworthies.eclipse.command.EclipseArguments


class DirectorArguments extends ConfigurableArguments 
	implements DirectorArgumentsAccessor, DirectorArgumentsHandler, InstallableUnitsHandler {
	
	static DirectorArguments createFrom(
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DirectorArguments)
		Closure args,
		ConfigObject config = null,
		String basePath = null) {
		
		def directorArgs = new DirectorArguments(config, basePath)
		args.setDelegate(directorArgs)
		args.setResolveStrategy(Closure.DELEGATE_ONLY)
		args.call()
		return directorArgs
	}
	
	private String basePath
	
	private String destination

	private def repositories = []
	
	private def installableUnits = []
	
	private DirectorOperation operation
	
	DirectorArguments() { }
	
	DirectorArguments(ConfigObject config, String basePath) {
		super(config)
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
	
	// TODO support list arguments (i.e. iu's, p2 query) 
	void list() {
		operation = new ListOperation();
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
		operation = new InstallOperation(units: installArgs.installableUnits)
		mergeArgumentsFrom(installArgs.directorArguments)
	}

	void installUnits() {
		operation = new InstallOperation(useDirectorUnits: true)
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
		operation = new UninstallOperation(units: installArgs.installableUnits)
		mergeArgumentsFrom(installArgs.directorArguments)
	}
	
	void uninstallUnits() {
		operation = new UninstallOperation(useDirectorUnits: true)
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
	
	String getDestination() {
		return destination
	}
	
	List<String> getRepositories() {
		return repositories.asImmutable()
	}
	
	List<InstallableUnitArgumentsAccessor> getInstallableUnits() {
		return installableUnits.asImmutable()
	}
	
	DirectorOperation getOperation() {
		return operation;
	}
}