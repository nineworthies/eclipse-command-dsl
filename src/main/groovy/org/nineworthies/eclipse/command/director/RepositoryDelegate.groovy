package org.nineworthies.eclipse.command.director

import org.nineworthies.eclipse.command.ConfigurableArguments
import org.nineworthies.eclipse.command.EclipseArguments

class RepositoryDelegate extends ConfigurableArguments 
	implements InstallableUnitsFromRepositoryHandler, InstallableUnitsFromArgumentsHandler {
	
	final String basePath
	
	private nameToRepositoryUrlMapping = [:]
	
	private urlToRepositoryMapping = [:]
	
	RepositoryDelegate(ConfigObject config = null, String basePath = null, 
		List repositories = null) {
		
		super(config)
		this.basePath = basePath
		repositories?.each { mergeRepository(it) }
	}
	
	void addRepository(String url, String name = null) {
		if (!urlToRepositoryMapping.containsKey(url)) {
			urlToRepositoryMapping[url] = new Repository(config, url, name)
		}
		// TODO what if name is already mapped to a different url?
		if (name) {
			nameToRepositoryUrlMapping[name] = url
		}
	}
	
	void unitsFromRepository(String url, Closure repositoryArgs) {
		
		def repository = new Repository(config, url)
		repositoryArgs.setDelegate(repository)
		repositoryArgs.setResolveStrategy(Closure.DELEGATE_ONLY)
		repositoryArgs.call()
		// if the repository has no units, do not merge
		if (!repository.installableUnits.empty) {
			mergeRepository(repository)
		}
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
			urlToRepositoryMapping[repository.url] = new Repository(config, repository.url, repository.name)
		}
		urlToRepositoryMapping[repository.url].merge(repository)
		// TODO what if name is already mapped to a different url?
		if (repository.name) {
			nameToRepositoryUrlMapping[repository.name] = repository.url
		}
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
