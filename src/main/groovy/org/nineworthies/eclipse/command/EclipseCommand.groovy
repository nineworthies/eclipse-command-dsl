package org.nineworthies.eclipse.command

import groovy.transform.Immutable;
import org.nineworthies.eclipse.command.EclipseArguments

@Immutable(knownImmutableClasses = [EclipseArguments, EclipseArgumentsAccessor])
class EclipseCommand {
	
	final EclipseArgumentsAccessor eclipseArgs;
	
	static void main(String[] args) {
		exec(args)
	}
	
	static void exec(String[] args, String callingType = "EclipseCommand") {

		def cli = new CliBuilder(usage: "$callingType -s [-m <args>] [argspath]")
		cli.with {
			s "Show the command"
			m args: 1, argName: "args", "Command arguments"
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
				eclipseArgs.merge(EclipseArguments.createFrom(otherArgs))
			}
		} else {
			Closure otherArgs = new GroovyShell().evaluate("{->$opts.m}")
			eclipseArgs = EclipseArguments.createFrom(otherArgs)
		}

		def command = new EclipseCommand(eclipseArgs)
		if (opts.s) {
			println "Eclipse command to exec would be '${command.show()}'"
		} else {
			command.exec()
		}
	}

	def show() {
		def command = new StringBuilder()
		eclipseArgs.asCommand().eachWithIndex { arg, index ->
			if (index > 0) { 
				command << " "
			}
			if (arg.contains(" ")) {
				command << /"$arg"/
			} else {
				command << "$arg"
			}
		}
		return command.toString()
	}
	
	void exec() {
		def command = eclipseArgs.asCommand()
		def process = command.execute()
		process.waitForProcessOutput(System.out, System.err)
		println "Eclipse command exited with value ${process.exitValue()}."
	}
}
