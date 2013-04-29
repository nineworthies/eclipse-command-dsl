package org.nineworthies.eclipse.command.director

abstract class DirectorOperation {
	
	protected void appendDestination(List command, File destination) {
		command << "-destination" << destination.path
	}
	
	protected void appendRepositories(List command, List<RepositoryAccessor> repositories) {
		command << "-repository" << repositories*.url.join(", ")
	}
	
	abstract void appendArgs(List command, DirectorArgumentsAccessor directorArgs)
}