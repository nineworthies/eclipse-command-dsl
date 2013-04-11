#!/usr/bin/env groovy

// this script makes the assumption that it is being run from an 'installed' distribution
// of the project, as produced by running 'grade distZip'; i.e. the script is located in 
// a 'bin' directory, alongside a 'lib' directory where any required classpath jars are
// located. 

// get the directory where this script is located
// n.b. using workaround solution from http://stackoverflow.com/questions/1163093/how-do-you-get-the-path-of-the-running-script-in-groovy
binDir = new File(getClass().protectionDomain.codeSource.location.path).parentFile
println "script directory path is $binDir"

loader = this.class.classLoader.rootLoader
jardir = new File(binDir.parentFile, "lib")
jars = jardir.listFiles().findAll { 
	it.name.endsWith('.jar') && !it.name.startsWith("groovy")
}
println "libs to add are $jars"
jars.each { 
	loader.addURL(it.toURI().toURL())
}

EclipseCommand = Class.forName("org.nineworthies.eclipse.command.EclipseCommand")

EclipseCommand.exec(args, getClass().getSimpleName())

