package org.nineworthies.eclipse.command.director

import groovy.transform.TupleConstructor

import org.nineworthies.eclipse.command.ConfigurableArguments
import org.nineworthies.eclipse.command.EclipseArguments

@TupleConstructor(callSuper = true, includeSuperProperties = true)
class RepositoryDelegate extends ConfigurableArguments 
	implements InstallableUnitsFromRepositoryHandler, InstallableUnitsFromArgumentsHandler {

	private nameToRepositoryUrlMapping = [:]
	
	private urlToRepositoryMapping = [:]
	
	final String basePath
	
	void addRepository(String url) {
		if (!urlToRepositoryMapping.containsKey(url)) {
			urlToRepositoryMapping[url] = new Repository(config, url)
		}
	}
	
	void addRepository(String name, String url) {
		nameToRepositoryUrlMapping.put(name, url)
		addRepository(url)
	}
	
	void unitsFromRepository(String url, Closure repositoryArgs) {
		
		def repository = new Repository(config, url)
		repositoryArgs.setDelegate(repository)
		repositoryArgs.setResolveStrategy(Closure.DELEGATE_ONLY)
		repositoryArgs.call()
		mergeRepository(repository)
	}
	
	void unitsFromRepositoryNamed(String name, Closure repositoryArgs) {
		if (nameToRepositoryUrlMapping.containsKey(name)) {
			unitsFromRepository(nameToRepositoryUrlMapping[name], repositoryArgs)
		} else {
			throw new RuntimeException("No repository named $name")
		}
	}

	void unitsFrom(String argsPath) {
		def argsFile = new File((String) basePath, argsPath)
		def args = EclipseArguments.createFrom(argsFile).getDirectorArguments()
		args.repositories.each { mergeRepository(it) }
	}
	
	void mergeRepository(RepositoryAccessor repository) {
		if (!urlToRepositoryMapping.containsKey(repository.url)) {
			urlToRepositoryMapping[repository.url] = new Repository(config, repository.url)
		}
		urlToRepositoryMapping[repository.url].merge(repository)
	}
	
	List<RepositoryAccessor> getRepositories() {
		return urlToRepositoryMapping.values().toList().asImmutable()
	}
	
	List<InstallableUnitAccessor> getInstallableUnits() {
		def installableUnits = []
		urlToRepositoryMapping.values().each {
			installableUnits.addAll(it.installableUnits)
		}
		return installableUnits.asImmutable()
	}
}
