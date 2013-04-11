package org.nineworthies.eclipse.command.director

import groovy.transform.Immutable;

import java.io.File;

@Immutable
class InstallOperation implements DirectorOperation {

	// FIXME this field was originally named 'args', but with that name
	// the following error was seen at runtime:
	// GroovyCastException: Cannot cast object 'xxx' with 
	// class 'xxx' to class 'java.util.HashMap'
	List<InstallableUnitArgumentsAccessor> units = []

	void appendArgs(Appendable command, DirectorArgumentsAccessor args) {
		println("installing $units from $args.repositories to '$args.destination'")
		args.repositories.each { url ->
			if (url == args.repositories.first()) {
				command << (args.repositories.size() > 1 ? / -repository "$url/ : " -repository $url")
			} else if (url == args.repositories.last()) {
				command << (args.repositories.size() > 1 ? /, $url"/ : ", $url")
			} else {
				command << ", $url"
			}
		}
		units.each { unit ->
//			command << (index > 0 ? ", $installableUnit.id" : " -installIU $installableUnit.id")
			if (unit == units.first()) {
				command << (units.size() > 1 ? / -installIU "$unit.id/ : " -installIU $unit.id")
			} else if (unit == units.last()) {
				command << (units.size() > 1 ? /, $unit.id"/ : ", $unit.id")
			} else {
				command << ", $unit.id"
			}
		}
	}
}
