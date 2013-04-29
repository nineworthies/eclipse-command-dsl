package org.nineworthies.eclipse.command

import static org.junit.Assert.*

import org.junit.*

class EclipseCommandTest {

	@Ignore
	@Test
	void testExecCommandWithArgumentsThatHaveShowOption() {
		
		def command = EclipseCommand.exec("-s -m '{eclipsec 'eclipse-install-path/eclipsec'}'")
		// TODO assert show() was called on command
	}
	
	@Test
	void testShowCommandAndAssertQuotingForEclipsecAndDestination() {
		
		def eclipseArgs = EclipseArguments.createFrom {
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
		def command = new EclipseCommand(eclipseArgs)
		
		def expected = /"eclipse install path${File.separator}eclipse"/.toString() +
			" -application org.eclipse.equinox.p2.director" +
			' -destination "eclipse install path"' +
			" -repository http://an.update/site" +
			" -installIU a.feature.group"
		assertEquals(expected, command.show())
	}
}