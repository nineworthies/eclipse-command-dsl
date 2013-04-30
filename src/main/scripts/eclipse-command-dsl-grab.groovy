#!/usr/bin/env groovy

@Grab(group='org.nineworthies.eclipse', module='eclipse-command-dsl', version='0.1.0-SNAPSHOT', changing=true)
import org.nineworthies.eclipse.command.EclipseCommand

EclipseCommand.invoke(args, getClass().getSimpleName())

