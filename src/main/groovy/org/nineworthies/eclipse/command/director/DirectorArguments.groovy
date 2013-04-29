package org.nineworthies.eclipse.command.director

import org.nineworthies.eclipse.command.ConfigurableArguments

class DirectorArguments extends ConfigurableArguments 
	implements DirectorArgumentsAccessor, DirectorArgumentsHandler {
	
	final String basePath
	
	private File destination
	
	// FIXME can't use @Delegate with Eclipse until https://jira.codehaus.org/browse/GRECLIPSE-331 is fixed 
	private RepositoryDelegate repositoryDelegate
	
	private DirectorOperation operation
	
	DirectorArguments(ConfigObject config = null, String basePath = null,
		List repositories = null) {
		
		super(config)
		this.basePath = basePath
		repositoryDelegate = new RepositoryDelegate(config, basePath, repositories)
	}
	
	void destination(String path) {
		destination = new File(path)
	}
	
	void repository(String url) {
		repositoryDelegate.addRepository(url)
	}
	
	void repositoryNamed(String name, String url) {
		repositoryDelegate.addRepository(url, name)
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
	
	void unitsFrom(String eclipseArgsPath) {
		repositoryDelegate.unitsFrom(eclipseArgsPath)
	}
	
	// TODO support list arguments (i.e. iu's, p2 query) 
	void listUnits() {
		operation = new ListOperation();
	}
	
	// sets install operation with only the enclosed installable units
	void install(
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = InstallArguments)
		Closure installArgs) {
		
		def args = new InstallArguments(config, basePath, 
			repositoryDelegate.repositories.findAll { it.installableUnits.empty })
		installArgs.setDelegate(args)
		installArgs.setResolveStrategy(Closure.DELEGATE_ONLY)
		installArgs.call()
		operation = new InstallOperation(repositories: args.repositories)
	}

	void installUnits() {
		operation = new InstallOperation(useDirectorRepositories: true)
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
	
	void appendArgs(List command) {
		operation?.appendArgs(command, this)
	}
	
	File getDestination() {
		return destination
	}
	
	List<RepositoryAccessor> getRepositories() {
		return repositoryDelegate.repositories
	}
	
	DirectorOperation getOperation() {
		return operation;
	}
}