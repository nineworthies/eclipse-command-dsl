package org.nineworthies.eclipse.command.director

abstract class DirectorOperation {
	
	protected void appendDestination(Appendable command, String destination) {
		command << " -destination"
		command << (destination.contains(" ") ? / "$destination"/ : " $destination")
	}
	
	protected void appendRepositories(Appendable command, List<RepositoryAccessor> repositories) {
		repositories.each { repo ->
			if (repo == repositories.first()) {
				command << (repositories.size() > 1 ? / -repository "$repo.url/ : " -repository $repo.url")
			} else if (repo == repositories.last()) {
				command << (repositories.size() > 1 ? /, $repo.url"/ : ", $repo.url")
			} else {
				command << ", $repo.url"
			}
		}
	}
	
	abstract void appendArgs(Appendable command, DirectorArgumentsAccessor directorArgs)
}
