package org.nineworthies.eclipse.command

import org.nineworthies.eclipse.command.director.DirectorArguments;
import org.nineworthies.eclipse.command.director.DirectorArgumentsAccessor;

import groovy.lang.Closure;


class EclipseArguments implements EclipseArgumentsAccessor {

	private File argsFile
	
	private Properties config = [:]
	
	private File eclipsec = new File("eclipsec")
	
	private boolean nosplash
	
	private boolean consolelog
	
	private boolean debug
	
	private DirectorArguments directorArgs
	
	static EclipseArguments createFrom(String argsPath) {
		return createFrom(new File(argsPath))
	}
	
	static EclipseArguments createFrom(File argsFile) {
		def binding = new Binding()
		def eclipseArgs = new EclipseArguments(argsFile)
		binding.config = eclipseArgs.config
		binding.configFrom = { eclipseArgs.configFrom(it) }
		binding.include = { eclipseArgs.include(it) }
		binding.eclipsec = { eclipseArgs.eclipsec(it) }
		binding.consolelog = { eclipseArgs.consolelog() }
		binding.debug = { eclipseArgs.debug() }
		binding.nosplash = { eclipseArgs.nosplash() }
		binding.director = { eclipseArgs.director(it) }
		def shell = new GroovyShell(binding)
		shell.evaluate(argsFile)
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
		def configProps = new Properties()
		configFile.withInputStream {
			stream -> configProps.load(stream)
		}
		this.config.putAll(configProps)
	}
	
	void include(String argsPath) {
		def otherArgs
		if (argsFile) {
			def otherArgsFile = new File(argsFile.getParent(), argsPath)
			otherArgs = EclipseArguments.createFrom(otherArgsFile)
		} else {
			otherArgs = EclipseArguments.createFrom(argsPath)
		}
		mergeArgumentsFrom(otherArgs)
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
		Closure args) {
		
		def directorArgs
		if (argsFile) {
			directorArgs = DirectorArguments.createFrom(args, config, argsFile.getParent())
		} else {
			directorArgs = DirectorArguments.createFrom(args, config)
		}
		mergeDirectorArgumentsFrom(directorArgs)
	}
	
	// TODO define the meaning of 'merge' here
	void mergeArgumentsFrom(EclipseArguments otherArgs) {
		// FIXME use public access (EclipseArgumentsAccessor) not private!
		if (otherArgs.eclipsec) {
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
		mergeDirectorArgumentsFrom(otherArgs.directorArguments)
	}
	
	private void mergeDirectorArgumentsFrom(DirectorArguments otherDirectorArgs) {
		if (!directorArgs) {
			// hmmm... maybe should instantiate new args instead, and then call mergeArgumentsFrom(..)
			directorArgs = otherDirectorArgs
		} else {
			directorArgs.mergeArgumentsFrom(otherDirectorArgs)
		}
	}
	
	Properties getConfig() {
		return config
	}
	
	DirectorArguments getDirectorArguments() {
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
		command.toString()
	}
}