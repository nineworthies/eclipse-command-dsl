package org.nineworthies.eclipse.command.director

import groovy.transform.Immutable

@Immutable
class ListOperation extends DirectorOperation {

	void appendArgs(Appendable command, DirectorArgumentsAccessor directorArgs) {
		println("listing units in $directorArgs.repositories")
		if (directorArgs.repositories) {
			appendRepositories(command, directorArgs.repositories)
		}
		command << " -list"
	}
}
