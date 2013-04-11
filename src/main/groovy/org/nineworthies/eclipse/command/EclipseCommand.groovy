package org.nineworthies.eclipse.command

import groovy.transform.Immutable;
import org.nineworthies.eclipse.command.EclipseArguments

@Immutable(knownImmutableClasses = [EclipseArguments, EclipseArgumentsAccessor])
class EclipseCommand {
	
	EclipseArgumentsAccessor eclipseArgs;
	
	static void main(String[] args) {
		exec(args)
	}
	
	static void exec(String[] args, String callingType = "EclipseCommand") {

		if (!(1..2).contains(args.length)) {
			printUsageAndExit(callingType)
		}
		
		def eclipseArgs = EclipseArguments.createFrom(args[0])
		def command = createFrom(eclipseArgs)
		if (args.length == 1) {
			command.exec()
			System.exit(0)
		}
		if (args[1].equals("-showCommand")) {
			command.show()
			System.exit(0)
		}
		printUsageAndExit(callingType)
	}
	
	static void printUsageAndExit(callingType) {
		println "Usage: $callingType <script> -showCommand"
		System.exit(0)
	}
	
	static EclipseCommand createFrom(EclipseArguments args) {
		return new EclipseCommand(args)
	}

	void show() {
		def command = eclipseArgs.asCommand()
		println "command to exec would be '$command'"
	}
		
	void exec() {
		def command = eclipseArgs.asCommand()
		println "command to exec is '$command'"
		def process = command.execute()
//		process.consumeProcessOutput()
		// http://stackoverflow.com/questions/6365451/is-eachline-sufficient-to-keep-a-process-from-blocking
//		process.in.eachLine { line -> println "command output: $line" }
//		process.err.eachLine { line -> System.err.println "command output: $line" }
//		process.waitFor()
		process.waitForProcessOutput(System.out, System.err)
		
		println "command return code: ${process.exitValue()}"
	}
}
