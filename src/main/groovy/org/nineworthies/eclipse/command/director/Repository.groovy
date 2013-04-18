package org.nineworthies.eclipse.command.director

import groovy.transform.ToString
import groovy.transform.TupleConstructor

import org.nineworthies.eclipse.command.ConfigurableArguments

@TupleConstructor(callSuper = true, includeSuperProperties = true)
@ToString(includeFields = true, includePackage = false, excludes = "installableUnits")
class Repository extends ConfigurableArguments 
	implements InstallableUnitsHandler, RepositoryAccessor {

	final String url
	
	private installableUnits = []
	
	void installableUnit(
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = InstallableUnit)
		Closure installableUnitArgs) {
		
		def installableUnit = new InstallableUnit()
		installableUnitArgs.setDelegate(installableUnit)
		installableUnitArgs.setResolveStrategy(Closure.DELEGATE_ONLY)
		installableUnitArgs.call()
		installableUnits.add(installableUnit)
	}
	
	void merge(RepositoryAccessor repository) {
		installableUnits.addAll(repository.getInstallableUnits())
	}
	
	List<InstallableUnitAccessor> getInstallableUnits() {
		return installableUnits.asImmutable()
	}
}
