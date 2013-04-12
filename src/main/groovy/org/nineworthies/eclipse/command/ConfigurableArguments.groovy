package org.nineworthies.eclipse.command

import groovy.util.ConfigObject;

abstract class ConfigurableArguments {

	protected ConfigObject config = new ConfigObject()
	
	ConfigurableArguments() { }
	
	ConfigurableArguments(ConfigObject config) {
		if (config) {
			this.config.merge(config)
		}
	}
	
	def propertyMissing(String name) {
		config.get(name)
	}
	
	void mergeConfigFrom(File configFile) {
		def configProps = new Properties()
		configFile.withInputStream {
			stream -> configProps.load(stream)
		}
		config.merge(new ConfigSlurper().parse(configProps))
	}
}
