package org.nineworthies.eclipse.command.director

import org.nineworthies.eclipse.command.ConfigurableArguments

class DirectorArguments extends ConfigurableArguments 
	implements DirectorArgumentsAccessor, DirectorArgumentsHandler {
	
	final String basePath
	
	private String destination
	
	// FIXME can't use @Delegate with Eclipse until https://jira.codehaus.org/browse/GRECLIPSE-331 is fixed 
	private RepositoryDelegate repositoryDelegate
	
	private DirectorOperation operation
	
	DirectorArguments(ConfigObject config = null, String basePath = null) {
		super(config)
		this.basePath = basePath
		repositoryDelegate = new RepositoryDelegate(config, basePath)
	}
	
	void destination(String path) {
		destination = path
	}
	
	void repository(String url) {
		repositoryDelegate.addRepository(url)
	}
	
	void repositoryNamed(String name, String url) {
		repositoryDelegate.addRepository(name, url)
	}
	
	void unitsFromRepository(
		String url,
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = Repository)
		Closure repositoryArgs) {
		
		repositoryDelegate.unitsFromRepository(url, repositoryArgs)
	}
	
	void unitsFromRepositoryNamed(
		String name,
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = Repository)
		Closure repositoryArgs) {
		
		repositoryDelegate.unitsFromRepositoryNamed(name, repositoryArgs)
	}
	
	// TODO support list arguments (i.e. iu's, p2 query) 
	void list() {
		operation = new ListOperation();
	}
	
	// sets install operation with only the enclosed installable units
	void install(
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = InstallArguments)
		Closure installArgs) {
		
		def args = new InstallArguments(config, basePath)
		installArgs.setDelegate(args)
		installArgs.setResolveStrategy(Closure.DELEGATE_ONLY)
		installArgs.call()
		operation = new InstallOperation(installableUnits: args.installableUnits)
		args.repositories.each { repositoryDelegate.mergeRepository(it) }
	}

	void installUnits() {
		operation = new InstallOperation(useDirectorUnits: true)
	}
	
	// sets uninstall operation with only the enclosed installable units
	void uninstall(
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = InstallArguments)
		Closure uninstallArgs) {
		
		def args = new InstallArguments(config, basePath)
		uninstallArgs.setDelegate(args)
		uninstallArgs.setResolveStrategy(Closure.DELEGATE_ONLY)
		uninstallArgs.call()
		operation = new UninstallOperation(installableUnits: args.installableUnits)
		args.repositories.each { repositoryDelegate.mergeRepository(it) }
	}
	
	void uninstallUnits() {
		operation = new UninstallOperation(useDirectorUnits: true)
	}
	
	// TODO define the meaning of 'merge' here
	void merge(DirectorArgumentsAccessor otherArgs) {
		if (otherArgs.destination) {
			destination = otherArgs.destination
		}
		otherArgs.repositories.each { repositoryDelegate.mergeRepository(it) }
		if (otherArgs.operation) {
			operation = otherArgs.operation
		}
	}
	
	void appendArgs(Appendable command) {
		operation?.appendArgs(command, this)
	}
	
	String getDestination() {
		return destination
	}
	
	List<RepositoryAccessor> getRepositories() {
		return repositoryDelegate.repositories
	}
	
	List<InstallableUnitAccessor> getInstallableUnits() {
		return repositoryDelegate.installableUnits
	}
	
	DirectorOperation getOperation() {
		return operation;
	}
}