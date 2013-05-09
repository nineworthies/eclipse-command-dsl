#Eclipse-Command-Dsl

This project provides a tool for automating the installation of customized combinations of Eclipse feature groups, in Windows, Linux and Mac environments.

These customized combinations can be specified as platform-independent Eclipse 'command scripts'; the structure of these scripts conforms to a simple DSL that has been designed to correspond to the Eclipse command line arguments.

More specifically, the DSL is a partial implementation of the facilities provided by the Eclipse command line, with a particular focus on the Eclipse Director Application for the purpose of installing (and
uninstalling, or listing) feature groups (sometimes referred to as 'installable units').

For example, here is a simple script, which when executed will invoke Eclipse and install the specified installable unit (in this case, the 'EGit' Eclipse features, which provides support for Git projects):

```groovy
director {
	install {
		unitsFromRepository("http://download.eclipse.org/releases/juno") {
			installableUnit {
				id "org.eclipse.egit.feature.group"
			}
		}
	}
}
```

##Benefits

 - Install a customized combination of many Eclipse features with a single command
 - Install the same combination on multiple computers with different environments (Windows, Mac, Linux)
 - Command scripts are easy to create in Eclipse, with auto completion and documented arguments corresponding to the Eclipse command line
 - Command scripts are easy to read and provide a form of documentation in addition to being executable 

##Runtime System Requirements

 - Java 1.6+
 - Eclipse 4.2+ (either the basic platform, or any other bundle that includes the Eclipse command line client)

##Optional Runtime Requirements

 - Groovy 2.1.0+ (to run the included Groovy script wrapper, instead of the bat or shell script wrappers)

##Development System Requirements

 - Groovy 2.1.0+
 - Groovy Eclipse 2.8.0+, Gradle Eclipse 3.2.0+ (to create command scripts using Eclipse auto completion)

##Installation

 - Download the runtime zip distribution of the latest version from the [public repository](http://nineworthies-public-repo.s3-website-eu-west-1.amazonaws.com/?prefix=release/org/nineworthies/eclipse/eclipse-command-dsl/)
 - Extract the zip archive to a local directory
 - Add the 'bin' directory to the PATH
 - Confirm a successful installation by invoking the following command:

 `eclipse-command-dsl`

##Usage

```
eclipse-command-dsl -s [-m <args>] [argspath]..
 -m <args>   Command arguments
 -s          Show the command
```

##Simple Tutorial
This tutorial shows how to create a command script and execute it, in order to install an Eclipse feature group.

 - Save the following command script as 'egit-install.groovy', substituting the relevant paths:

```groovy
eclipsec "<path-of-eclipse-command-line-client>"
consolelog()
nosplash()
director {
	destination "<path-of-eclipse-installation>"
	install {
		unitsFromRepository("http://download.eclipse.org/releases/juno") {
			installableUnit {
				id "org.eclipse.egit.feature.group"
			}
		}
	}
}
```

 - Invoke the following command to show the Eclipse command without actually executing it:

 `eclipse-command-dsl -s egit-install.groovy`

 - Finally, invoke the following command to execute the Eclipse command:

 `eclipse-command-dsl egit-install.groovy`

 - Alternatively if Groovy 2.1.0+ is installed, the Groovy script wrapper can be used instead of the bat or shell script wrappers by invoking the following command:

 `eclipse-command-dsl.groovy egit-install.groovy`

##Example Scripts
There are a range of example command scripts in the [example-eclipse-setup](http://github.com/nineworthies/example-eclipse-setup) project, illustrating the current vocabulary of the DSL.
 
To try out these examples:

 - Clone the [example-eclipse-setup](http://github.com/nineworthies/example-eclipse-setup) project
 - Copy 'eclipse.properties.example' to 'eclipse.properties'
 - Substitute the relevant paths in eclipse.properties

##Advanced Tutorial
This tutorial shows how to create a command script using Eclipse auto completion.
 
 - Install [Gradle Eclipse](https://github.com/SpringSource/eclipse-integration-gradle); the 'sts-gradle-install.groovy' command script in the [example-eclipse-setup](http://github.com/nineworthies/example-eclipse-setup) project can be used to do this
 - Create a new general project in Eclipse, called 'my-eclipse-setup'
 - Save the following Gradle build script as 'build.gradle', substituting the relevant version:
 
```groovy
apply plugin: 'groovy'

repositories {
	mavenCentral()
	maven {
		url "https://nineworthies-public-repo.s3-website-eu-west-1.amazonaws.com/release"
	}
}

dependencies {
	compile 'org.codehaus.groovy:groovy-all:2.1.1'
	compile 'org.nineworthies.eclipse:eclipse-command-dsl:<version>'
}
```

 - Right-click on the project, and select Configure -> Convert to Gradle Project
 - Create a new directory path in the project: '/src/main/scripts'
 - Create a new file in the 'scripts' directory called 'somefeature-install.groovy'
 - Open 'somefeature-install.groovy' in the Eclipse editor
 - Use <Ctrl+Space> to see method calls permitted by the DSL in the current context; these calls will be annotated with "(DSL Descriptor)"
 
##Motivation
The motivations for this project were:

 - To provide an easier and faster way of installing a customized combination of Eclipse features, on several computers with different environments (Windows, Linux)
 - To evaluate the suitability of Groovy for implementing a simple internal DSL
 - To evaluate the DSLD features of Groovy Eclipse when creating scripts based on a Groovy DSL
 - To gain more experience of Groovy generally
 
## Getting Help
To ask questions or report bugs, please use the GitHub project.

* Project page: [http://github.com/nineworthies/eclipse-command-dsl](http://github.com/nineworthies/eclipse-command-dsl)
* Asking questions: [http://github.com/nineworthies/eclipse-command-dsl/issues](http://github.com/nineworthies/eclipse-command-dsl/issues)
* Reporting bugs: [http://github.com/nineworthies/eclipse-command-dsl/issues](http://github.com/nineworthies/eclipse-command-dsl/issues)

## Copyright
Copyright 2013 Nineworthies Technology Ltd.

