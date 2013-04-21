package org.nineworthies.eclipse.command

import org.nineworthies.eclipse.command.director.DirectorArguments
import org.nineworthies.eclipse.command.director.DirectorArgumentsAccessor

class EclipseArguments extends ConfigurableArguments implements EclipseArgumentsAccessor {

	private File argsFile
	
	private File eclipsec = new File("eclipsec")
	
	private boolean nosplash
	
	private boolean consolelog
	
	private boolean debug
	
	private DirectorArguments directorArgs
	
	static EclipseArguments createFrom(String argsPath) {
		return createFrom(new File(argsPath))
	}
	
	static EclipseArguments createFrom(File argsFile) {

		def eclipseArgs = new EclipseArguments(argsFile)
		// groovy allegedly tries to guess the encoding of argsFile...
		Closure args = new GroovyShell().evaluate("{->$argsFile.text}")
		args.setDelegate(eclipseArgs)
		args.setResolveStrategy(Closure.DELEGATE_ONLY)
		args.call()
		return eclipseArgs
	}
	
	static EclipseArguments createFrom(
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = EclipseArguments) 
		Closure args) {
		
		def eclipseArgs = new EclipseArguments()
		args.setDelegate(eclipseArgs)
		args.setResolveStrategy(Closure.DELEGATE_ONLY)
		args.call()
		return eclipseArgs
	}
	
	EclipseArguments() { }
	
	EclipseArguments(File argsFile) {
		this.argsFile = argsFile
	}
	
	void configFrom(String configPath) {
		
		def configFile
		if (argsFile) {
			configFile = new File(argsFile.parent, configPath)
		} else {
			configFile = new File(configPath)
		}
		mergeConfigFrom(configFile)
	}
	
	void include(String argsPath) {
		def otherArgs
		if (argsFile) {
			def otherArgsFile = new File(argsFile.getParent(), argsPath)
			otherArgs = EclipseArguments.createFrom(otherArgsFile)
		} else {
			otherArgs = EclipseArguments.createFrom(argsPath)
		}
		merge(otherArgs)
	}
	
	void eclipsec(String path) {
		eclipsec = new File(path)
	}

	void consolelog() {
		consolelog = true
	}
	
	void debug() {
		debug = true
	}
	
	void nosplash() {
		nosplash = true
	}
	
	void director(
		@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DirectorArguments) 
		Closure directorArgs) {
		
		def args = new DirectorArguments(config, argsFile?.getParent(), 
			this.directorArgs?.repositories.findAll { it.installableUnits.empty })
		directorArgs.setDelegate(args)
		directorArgs.setResolveStrategy(Closure.DELEGATE_ONLY)
		directorArgs.call()
		mergeDirectorArguments(args)
	}
	
	// TODO define the meaning of 'merge' here
	void merge(EclipseArguments otherArgs) {
		// FIXME use public access (EclipseArgumentsAccessor) not private!
		config.merge(otherArgs.config)
		if (otherArgs.eclipsec && !otherArgs.eclipsec.path.equals("eclipsec")) {
			eclipsec = otherArgs.eclipsec
		}
		if (otherArgs.consolelog) {
			consolelog = otherArgs.consolelog
		}
		if (otherArgs.debug) {
			debug = otherArgs.debug
		}
		if (otherArgs.nosplash) {
			nosplash = otherArgs.nosplash
		}
		mergeDirectorArguments(otherArgs.directorArguments)
	}
	
	private void mergeDirectorArguments(DirectorArguments otherDirectorArgs) {
		if (!directorArgs) {
			directorArgs = new DirectorArguments(config, argsFile?.getParent())
		} 
		directorArgs.merge(otherDirectorArgs)
	}
	
	DirectorArgumentsAccessor getDirectorArguments() {
		return directorArgs
	}
	
	String asCommand() {
		def command = new StringBuilder()
		command << (eclipsec.path.contains(" ") ? /"$eclipsec"/ : "$eclipsec")
		if (consolelog) {
			command << " -consolelog"
		}
		if (debug) {
			command << " -debug"
		}
		if (nosplash) {
			command << " -nosplash"
		}
		// now delegate to 'application'
		if (directorArgs) {
			command << " -application org.eclipse.equinox.p2.director"
			directorArgs.appendArgs(command)
		}
		return command.toString()
	}
}