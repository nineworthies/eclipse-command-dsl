package org.nineworthies.eclipse.command.director

import org.nineworthies.eclipse.command.ConfigurableArguments

class InstallArguments extends ConfigurableArguments 
	implements InstallArgumentsHandler, UninstallArgumentsHandler {
	
	// FIXME can't use @Delegate with Eclipse until https://jira.codehaus.org/browse/GRECLIPSE-331 is fixed 
	private RepositoryDelegate repositoryDelegate

	private installableUnits = []
	
	InstallArguments(ConfigObject config = null, String basePath = null, 
		List repositories = null) {
		
		super(config)
		repositoryDelegate = new RepositoryDelegate(config, basePath, repositories)
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
	
	// TODO effectively duplicates Repository.installableUnit(Closure)
	// TODO only sensible for uninstall
	void installableUnit(
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = InstallableUnit)
		Closure installableUnitArgs) {
		
		def installableUnit = new InstallableUnit()
		installableUnitArgs.setDelegate(installableUnit)
		installableUnitArgs.setResolveStrategy(Closure.DELEGATE_ONLY)
		installableUnitArgs.call()
		installableUnits.add(installableUnit)
	}
	
	List<RepositoryAccessor> getRepositories() {
		return repositoryDelegate.repositories
	}
	
	List<InstallableUnitAccessor> getInstallableUnits() {
		def installableUnits = []
		installableUnits.addAll(this.installableUnits)
		installableUnits.addAll(repositoryDelegate.installableUnits)
		return installableUnits.asImmutable()
	}
}