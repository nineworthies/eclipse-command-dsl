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

		def cli = new CliBuilder(usage: "$callingType -s [-m <args>] [argspath]")
		cli.with {
			s "show the command"
			m args: 1, argName: "args", "command arguments"
		}
		def opts = cli.parse(args)
		if (!opts) {
			System.exit(0)
		}
		if (!opts.arguments() && !opts.m) {
			cli.usage()
			System.exit(0)
		}
		
		def eclipseArgs
		if (opts.arguments()) {
			def invalidOpts = opts.arguments().findAll {
				it.startsWith("-")
			}
			if (invalidOpts) {
				cli.usage()
				System.exit(0)
			}
			// FIXME second or subsequent arg files are ignored for now...
			eclipseArgs = EclipseArguments.createFrom(opts.arguments().first())
			if (opts.m) {
				Closure otherArgs = new GroovyShell().evaluate("{->$opts.m}")
				eclipseArgs.mergeArgumentsFrom(EclipseArguments.createFrom(otherArgs))
			}
		} else {
			Closure otherArgs = new GroovyShell().evaluate("{->$opts.m}")
			eclipseArgs = EclipseArguments.createFrom(otherArgs)
		}

		def command = createFrom(eclipseArgs)
		if (opts.s) {
			command.show()
		} else {
			command.exec()
		}
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
		process.waitForProcessOutput(System.out, System.err)
		println "command return code: ${process.exitValue()}"
	}
}
