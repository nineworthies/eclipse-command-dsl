package org.nineworthies.eclipse.command.director

import groovy.transform.Immutable;

import java.io.File;

@Immutable
class UninstallOperation implements DirectorOperation {

	boolean useDirectorUnits
	
	Collection<InstallableUnitArgumentsAccessor> units = []

	void appendArgs(Appendable command, DirectorArgumentsAccessor args) {
		
		def units = this.useDirectorUnits ? args.installableUnits : this.units
		println("uninstalling $units from '$args.destination'")
		units.eachWithIndex { unit, index ->
			command << (index > 0 ? ", $unit.id" : " -uninstallIU $unit.id")
		}
	}
}
