package org.nineworthies.eclipse.command

import static org.junit.Assert.*

import org.junit.Test

// FIXME don't test with asCommand() and assert on string unless that is 
// what's actually under test
class EclipseArgumentsTest {

	@Test
	void testCreateFromClosureWithNoApplication() {
		
		def args = EclipseArguments.createFrom { }
		
		def expected = "eclipsec"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithNoApplicationAndEclipsecPath() {
		
		def args = EclipseArguments.createFrom {
			eclipsec "C:\\eclipse\\install\\path\\eclipsec"
		}
		
		def expected = "C:\\eclipse\\install\\path\\eclipsec"
		assertEquals(expected, args.asCommand())
	}

	@Test
	void testCreateFromClosureWithNoApplicationAndConsolelogAndDebugAndNosplash() {
		
		def args = EclipseArguments.createFrom {
			consolelog()
			debug()
			nosplash()
		}
		
		def expected = "eclipsec -consolelog -debug -nosplash"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasNoOperation() {
		
		def args = EclipseArguments.createFrom {
			director {
				destination "/eclipse/install/path"
				repository "http://an.update/site"
				installableUnit {
					id "a.feature.group"
				}
			}
		}
		
		def expected = "eclipsec -application org.eclipse.equinox.p2.director" +
			" -destination /eclipse/install/path"
		assertEquals(expected, args.asCommand())
	}

	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasNoOperationAndNoDestination() {
		
		def args = EclipseArguments.createFrom {
			director {
				repository "http://an.update/site"
				installableUnit {
					id "a.feature.group"
				}
			}
		}
		
		def expected = "eclipsec -application org.eclipse.equinox.p2.director"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasInstallOperationForUnit() {
		
		def args = EclipseArguments.createFrom {
			director {
				destination "/eclipse/install/path"
				repository "http://an.update/site"
				install {
					installableUnit {
						id "a.feature.group"
					}
				}
			}
		}
		
		def expected = "eclipsec -application org.eclipse.equinox.p2.director" +
			" -destination /eclipse/install/path" +
			" -repository http://an.update/site" + 
			" -installIU a.feature.group"
		assertEquals(expected, args.asCommand())
	}

	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasUninstallOperationForUnit() {
		
		def args = EclipseArguments.createFrom {
			director {
				destination "/eclipse/install/path"
				repository "http://an.update/site"
				uninstall {
					installableUnit {
						id "a.feature.group"
					}
				}
			}
		}
		
		def expected = "eclipsec -application org.eclipse.equinox.p2.director" +
			" -destination /eclipse/install/path" +
			" -uninstallIU a.feature.group"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasInstallOperationForTwoUnits() {
		
		def args = EclipseArguments.createFrom {
			director {
				destination "/eclipse/install/path"
				repository "http://an.update/site"
				install {
					installableUnit {
						id "a.feature.group"
					}
					installableUnit {
						id "another.feature.group"
					}
				}
			}
		}
		
		def expected = "eclipsec -application org.eclipse.equinox.p2.director" +
			" -destination /eclipse/install/path" +
			" -repository http://an.update/site" + 
			' -installIU "a.feature.group, another.feature.group"'
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromFileWithDirectorApplicationThatHasInstallOperationForUnit() {
		
		def argsFile = new File(getClass().getResource("/test_args_director_install_iu.groovy").toURI())
		def args = EclipseArguments.createFrom(argsFile)
		
		def expected = "eclipsec -application org.eclipse.equinox.p2.director" +
			" -destination /eclipse/install/path" +
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
				destination "/eclipse/install/path"
				install {
					installableUnitsFrom fromArgsFile.getCanonicalPath()
				}
			}
		}
		
		def expected = "eclipsec -application org.eclipse.equinox.p2.director" +
			" -destination /eclipse/install/path" +
			" -repository http://an.update/site" + 
			" -installIU a.feature.group.from"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasUninstallOperationForUnitsFrom() {
		
		def fromArgsFile = new File(getClass().getResource("/units/test_args_director_iu.groovy").toURI())
		def args = EclipseArguments.createFrom {
			director {
				destination "/eclipse/install/path"
				uninstall {
					installableUnitsFrom fromArgsFile.getCanonicalPath()
				}
			}
		}
		
		def expected = "eclipsec -application org.eclipse.equinox.p2.director" +
			" -destination /eclipse/install/path" +
			" -uninstallIU a.feature.group.from"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureWithDirectorApplicationThatHasInstallOperationForUnitAndUnitsFrom() {
		
		def fromArgsFile = new File(getClass().getResource("/units/test_args_director_iu.groovy").toURI())
		def args = EclipseArguments.createFrom {
			director {
				destination "/eclipse/install/path"
				repository "http://another.update/site"
				install {
					installableUnit {
						id "another.feature.group"
					}
					installableUnitsFrom fromArgsFile.getCanonicalPath()
				}
			}
		}
		
		def expected = "eclipsec -application org.eclipse.equinox.p2.director" +
			" -destination /eclipse/install/path" +
			' -repository "http://another.update/site, http://an.update/site"' +
			' -installIU "another.feature.group, a.feature.group.from"'
		assertEquals(expected, args.asCommand())
	}
	
	@Test(expected = FileNotFoundException)
	void testCreateFromClosureWithDirectorApplicationThatHasInstallOperationForUnitsFromButFileNotFound() {
		EclipseArguments.createFrom {
			director {
				destination "/eclipse/install/path"
				repository "http://an.update/site"
				install {
					installableUnitsFrom "/does/not/exist.groovy"
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
				repository "http://an.update/site"
				install {
					installableUnit {
						id "a.feature.group"
					}
				}
			}
		}

		def expected = "eclipsec -consolelog -application org.eclipse.equinox.p2.director" +
			" -destination /eclipse/install/path" +
			" -repository http://an.update/site" +
			" -installIU a.feature.group"
		assertEquals(expected, args.asCommand())
	}

	@Test
	void testCreateFromFileWithRelativeIncludeAndDirectorApplicationThatHasInstallOperationForUnitsFrom() {
		
		def argsFile = new File(getClass().getResource("/units/test_args_director_install_iu_from.groovy").toURI())
		def args = EclipseArguments.createFrom(argsFile)
		
		def expected = "eclipsec -consolelog -application org.eclipse.equinox.p2.director" +
			" -destination /eclipse/install/path" +
			" -repository http://an.update/site" +
			" -installIU a.feature.group.from"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromClosureAndAssertCorrectQuoting() {
		
		def args = EclipseArguments.createFrom {
			eclipsec "C:\\Program Files\\Eclipse\\eclipsec"
			director {
				destination "C:\\Program Files\\Eclipse"
				repository "http://an.update/site"
				install {
					installableUnit {
						id "a.feature.group"
					}
				}
			}
		}
		
		def expected = '"C:\\Program Files\\Eclipse\\eclipsec" -application org.eclipse.equinox.p2.director' +
			' -destination "C:\\Program Files\\Eclipse"' +
			" -repository http://an.update/site" +
			" -installIU a.feature.group"
		assertEquals(expected, args.asCommand())
	}

	@Test
	void testCreateFromClosureWithConfig() {
		
		def configFile = new File(getClass().getResource("/test_config.properties").toURI())
		def args = EclipseArguments.createFrom {
			configFrom configFile.getCanonicalPath()
			eclipsec "${config['eclipse.home']}/eclipsec"
			director {
				destination "${config['eclipse.home']}"
			}
		}
		
		def expected = "C:\\my\\eclipse\\path\\eclipsec -application org.eclipse.equinox.p2.director" +
			" -destination C:\\my\\eclipse\\path"
		assertEquals(expected, args.asCommand())
	}
	
	@Test
	void testCreateFromFileWithConfig() {
		
		def argsFile = new File(getClass().getResource("/test_args_config.groovy").toURI())
		def args = EclipseArguments.createFrom(argsFile)
		
		def expected = "C:\\my\\eclipse\\path\\eclipsec"
		assertEquals(expected, args.asCommand())
	}
}