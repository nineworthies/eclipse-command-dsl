package org.nineworthies.eclipse.command.director

import groovy.transform.Immutable;

import java.io.File;

@Immutable
class UninstallOperation implements DirectorOperation {

	Collection<InstallableUnitArgumentsAccessor> installableUnits = []

	void appendArgs(Appendable command, DirectorArgumentsAccessor args) {
		println("uninstalling $installableUnits from '$args.destination'")
		installableUnits.eachWithIndex { installableUnit, index ->
			command << (index > 0 ? ", $installableUnit.id" : " -uninstallIU $installableUnit.id")
		}
	}
}
