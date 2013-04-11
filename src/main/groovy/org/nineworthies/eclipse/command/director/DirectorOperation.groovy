package org.nineworthies.eclipse.command.director

interface DirectorOperation {
	
	void appendArgs(Appendable command, DirectorArgumentsAccessor args)
}
