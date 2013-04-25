package org.nineworthies.eclipse.command

import static org.junit.Assert.*

import org.junit.*

// FIXME don't test with asCommand() and assert on string unless that is 
// what's actually under test
class EclipseArgumentsTest {

	@Test
	void testCreateFromClosureWithNoApplication() {
		
		def args = EclipseArguments.createFrom { }
		
		def expected = "eclipse"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithNoApplicationAndEclipsecPath() {
		
		def args = EclipseArguments.createFrom {
			eclipsec "eclipsec"
		}
		
		def expected = "eclipsec"
		assertEquals(expected, args.asCommand())
	}

	@Test
	void testCreateFromClosureWithNoApplicationAndConsolelogAndDebugAndNosplash() {
		
		def args = EclipseArguments.createFrom {
			consolelog()
			debug()
			nosplash()
		}
		
		def expected = "eclipse -consolelog -debug -nosplash"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasNoOperation() {
		
		def args = EclipseArguments.createFrom {
			director {
				destination "eclipse-install-path"
				unitsFromRepository ("http://an.update/site") {
					installableUnit {
						id "a.feature.group"
					}
				}
			}
		}
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director"
		assertEquals(expected, args.asCommand())
	}

	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasNoOperationAndNoDestination() {
		
		def args = EclipseArguments.createFrom {
			director {
				unitsFromRepository ("http://an.update/site") {
					installableUnit {
						id "a.feature.group"
					}
				}
			}
		}
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasListOperation() {
		
		def args = EclipseArguments.createFrom {
			director {
				repository "http://an.update/site"
				listUnits()
			}
		}
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director" +
			" -repository http://an.update/site" +
			" -list"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasThatHasUnitsFromRepositoryAndInstallOperationForAllUnits() {
		
		def args = EclipseArguments.createFrom {
			director {
				destination "eclipse-install-path"
				unitsFromRepository ("http://an.update/site") {
					installableUnit {
						id "a.feature.group"
					}
				}
				installUnits()
			}
		}
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path" +
			" -repository http://an.update/site" +
			" -installIU a.feature.group"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasUnitsFromRepositoryAndUnitsFromArgsAndInstallOperationForAllUnits() {
		
		def fromArgsFile = new File(getClass().getResource("/units/test_args_director_iu.groovy").toURI())
		def args = EclipseArguments.createFrom {
			director {
				destination "eclipse-install-path"
				unitsFromRepository ("http://an.update/site") {
					installableUnit {
						id "a.feature.group"
					}
				}
				unitsFrom fromArgsFile.canonicalPath
				installUnits()
			}
		}
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path" +
			" -repository http://an.update/site" +
			' -installIU "a.feature.group, a.feature.group.from"'
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasNoUnitsFromRepositoryAndInstallOperationForAllUnits() {
		
		def args = EclipseArguments.createFrom {
			director {
				destination "eclipse-install-path"
				unitsFromRepository ("http://ignored.update/site") { }
				installUnits()
			}
		}
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasNamedRepositoryAndInstallOperationForAllUnits() {
		
		def args = EclipseArguments.createFrom {
			director {
				destination "eclipse-install-path"
				repositoryNamed "asite", "http://an.update/site"
				unitsFromRepositoryNamed ("asite") {
					installableUnit {
						id "a.feature.group"
					}
				}
				installUnits()
			}
		}
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path" +
			" -repository http://an.update/site" +
			" -installIU a.feature.group"
		assertEquals(expected, args.asCommand())
	}
	
	@Test(expected = RuntimeException)
	void testCreateFromClosureWithDirectorApplicationThatHasUnitsFromNamedRepositoryButNameNotFound() {
		
		EclipseArguments.createFrom {
			director {
				destination "eclipse-install-path"
				repositoryNamed "asite", "http://an.update/site"
				unitsFromRepositoryNamed ("does-not-exist") {
					installableUnit {
						id "a.feature.group"
					}
				}
			}
		}
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasInstallOperationForUnit() {
		
		def args = EclipseArguments.createFrom {
			director {
				destination "eclipse-install-path"
				unitsFromRepository ("http://ignored.update/site") {
					installableUnit {
						id "ignored.feature.group"
					}
				}
				install {
					unitsFromRepository ("http://an.update/site") {
						installableUnit {
							id "a.feature.group"
						}
					}
				}
			}
		}
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path" +
			" -repository http://an.update/site" + 
			" -installIU a.feature.group"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasInstallOperationForUnitFromNamedRepository() {
		
		def args = EclipseArguments.createFrom {
			director {
				destination "eclipse-install-path"
				repositoryNamed "asite", "http://an.update/site"
				install {
					unitsFromRepositoryNamed ("asite") {
						installableUnit {
							id "a.feature.group"
						}
					}
				}
			}
		}
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path" +
			" -repository http://an.update/site" +
			" -installIU a.feature.group"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasUninstallOperationForUnit() {
		
		def args = EclipseArguments.createFrom {
			director {
				destination "eclipse-install-path"
				uninstall {
					installableUnit {
						id "a.feature.group"
					}
				}
			}
		}
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path" +
			" -uninstallIU a.feature.group"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasInstallOperationForTwoUnits() {
		
		def args = EclipseArguments.createFrom {
			director {
				destination "eclipse-install-path"
				install {
					unitsFromRepository ("http://an.update/site") {
						installableUnit {
							id "a.feature.group"
						}
						installableUnit {
							id "another.feature.group"
						}
					}
				}
			}
		}
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path" +
			" -repository http://an.update/site" + 
			' -installIU "a.feature.group, another.feature.group"'
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromFileWithDirectorApplicationThatHasInstallOperationForUnit() {
		
		def argsFile = new File(getClass().getResource("/test_args_director_install_iu.groovy").toURI())
		def args = EclipseArguments.createFrom(argsFile)
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path" +
			" -repository http://an.update/site" +
			" -installIU a.feature.group"
		assertEquals(expected, args.asCommand())
	}
	
	@Test(expected = FileNotFoundException)
	void testCreateFromFileButFileNotFound() {
		
		def args = EclipseArguments.createFrom("/does/not/exist.groovy")
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasInstallOperationForUnitsFrom() {
		
		def fromArgsFile = new File(getClass().getResource("/units/test_args_director_iu.groovy").toURI())
		def args = EclipseArguments.createFrom {
			director {
				destination "eclipse-install-path"
				install {
					unitsFrom fromArgsFile.getCanonicalPath()
				}
			}
		}
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path" +
			" -repository http://an.update/site" + 
			" -installIU a.feature.group.from"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasAdditionalRepositoryAndInstallOperationForUnitsFrom() {
		
		def fromArgsFile = new File(getClass().getResource("/units/test_args_director_iu.groovy").toURI())
		def args = EclipseArguments.createFrom {
			director {
				destination "eclipse-install-path"
				repository "http://another.update/site"
				install {
					unitsFrom fromArgsFile.getCanonicalPath()
				}
			}
		}
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path" +
			' -repository "http://another.update/site, http://an.update/site"' +
			" -installIU a.feature.group.from"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasUninstallOperationForUnitsFrom() {
		
		def fromArgsFile = new File(getClass().getResource("/units/test_args_director_iu.groovy").toURI())
		def args = EclipseArguments.createFrom {
			director {
				destination "eclipse-install-path"
				uninstall {
					unitsFrom fromArgsFile.getCanonicalPath()
				}
			}
		}
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path" +
			" -uninstallIU a.feature.group.from"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasInstallOperationForUnitAndUnitsFrom() {
		
		def fromArgsFile = new File(getClass().getResource("/units/test_args_director_iu.groovy").toURI())
		def args = EclipseArguments.createFrom {
			director {
				destination "eclipse-install-path"
				install {
					unitsFromRepository ("http://another.update/site") {
						installableUnit {
							id "another.feature.group"
						}
					}
					unitsFrom fromArgsFile.getCanonicalPath()
				}
			}
		}
		
		def expected = "eclipse -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path" +
			' -repository "http://another.update/site, http://an.update/site"' +
			' -installIU "another.feature.group, a.feature.group.from"'
		assertEquals(expected, args.asCommand())
	}
	
	@Test(expected = FileNotFoundException)
	void testCreateFromClosureWithDirectorApplicationThatHasInstallOperationForUnitsFromButFileNotFound() {
		EclipseArguments.createFrom {
			director {
				install {
					unitsFrom "/does/not/exist.groovy"
				}
			}
		}
	}
	
	@Test
	void testCreateFromClosureWithIncludeAndDirectorApplicationThatHasInstallOperationForUnit() {
		
		def includeArgsFile = new File(getClass().getResource("/include/test_args_director_included.groovy").toURI())
		def args = EclipseArguments.createFrom {
			include includeArgsFile.getCanonicalPath()
			director {
				install {
					unitsFromRepository ("http://an.update/site") {
						installableUnit {
							id "a.feature.group"
						}
					}
				}
			}
		}

		def expected = "eclipse -consolelog -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path" +
			" -repository http://an.update/site" +
			" -installIU a.feature.group"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithIncludeAndDirectorApplicationThatHasInstallOperationForUnitFromNamedRepository() {
		
		def includeArgsFile = new File(getClass().getResource("/include/test_args_director_included.groovy").toURI())
		def args = EclipseArguments.createFrom {
			include includeArgsFile.getCanonicalPath()
			director {
				install {
					unitsFromRepositoryNamed ("asite") {
						installableUnit {
							id "a.feature.group"
						}
					}
				}
			}
		}

		def expected = "eclipse -consolelog -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path" +
			" -repository http://an.update/site" +
			" -installIU a.feature.group"
		assertEquals(expected, args.asCommand())
	}

	@Test
	void testCreateFromFileWithIncludeAndDirectorApplicationThatHasInstallOperationForUnitsFrom() {
		
		def argsFile = new File(getClass().getResource("/units/test_args_director_install_iu_from.groovy").toURI())
		def args = EclipseArguments.createFrom(argsFile)
		
		def expected = "eclipse -consolelog -application org.eclipse.equinox.p2.director" +
			" -destination eclipse-install-path" +
			" -repository http://an.update/site" +
			" -installIU a.feature.group.from"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureAndAssertQuotingForEclipsecAndDestination() {
		
		def args = EclipseArguments.createFrom {
			eclipsec "eclipse install path/eclipse"
			director {
				destination "eclipse install path"
				install {
					unitsFromRepository ("http://an.update/site") {
						installableUnit {
							id "a.feature.group"
						}
					}
				}
			}
		}
		
		def expected = /"eclipse install path${File.separator}eclipse"/.toString() + 
			" -application org.eclipse.equinox.p2.director" +
			' -destination "eclipse install path"' +
			" -repository http://an.update/site" +
			" -installIU a.feature.group"
		assertEquals(expected, args.asCommand())
	}

	@Test
	void testCreateFromClosureWithConfig() {
		
		def configFile = new File(getClass().getResource("/test_config.properties").toURI())
		def args = EclipseArguments.createFrom {
			configFrom configFile.getCanonicalPath()
			eclipsec "$eclipse.command"
		}
		
		def expected = "eclipsec".toString()
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromFileWithConfig() {
		
		def argsFile = new File(getClass().getResource("/test_args_config.groovy").toURI())
		def args = EclipseArguments.createFrom(argsFile)
		
		def expected = "eclipsec".toString()
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithIncludeThatHasConfig() {
		
		def includeArgsFile = new File(getClass().getResource("/test_args_config.groovy").toURI())
		def args = EclipseArguments.createFrom {
			include includeArgsFile.getCanonicalPath()
		}
		
		def expected = "eclipsec".toString()
		assertEquals(expected, args.asCommand())
	}
}