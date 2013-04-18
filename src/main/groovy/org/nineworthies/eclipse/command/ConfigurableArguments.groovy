package org.nineworthies.eclipse.command

abstract class ConfigurableArguments {

	final ConfigObject config = new ConfigObject()
	
	ConfigurableArguments(ConfigObject config = null) {
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
	
	// override groovy generated getter for 'config' property
	ConfigObject getConfig() {
		config.clone()
	}
}
