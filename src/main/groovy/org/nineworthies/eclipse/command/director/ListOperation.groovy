package org.nineworthies.eclipse.command.director

import groovy.transform.Immutable;

import java.io.File;

@Immutable
class ListOperation implements DirectorOperation {

	void appendArgs(Appendable command, DirectorArgumentsAccessor args) {
		println("listing units in $args.repositories")
		args.repositories.each { url ->
			if (url == args.repositories.first()) {
				command << (args.repositories.size() > 1 ? / -repository "$url/ : " -repository $url")
			} else if (url == args.repositories.last()) {
				command << (args.repositories.size() > 1 ? /, $url"/ : ", $url")
			} else {
				command << ", $url"
			}
		}
		command << " -list"
	}
}
