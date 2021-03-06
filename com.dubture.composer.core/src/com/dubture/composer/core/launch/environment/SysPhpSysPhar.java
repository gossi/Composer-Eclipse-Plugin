package com.dubture.composer.core.launch.environment;

import org.apache.commons.exec.CommandLine;
import org.eclipse.core.resources.IProject;

public class SysPhpSysPhar implements Environment {

	private String php;
	private String phar;
	
	public SysPhpSysPhar() {
		php = EnvironmentFinder.findPhp();
		phar = EnvironmentFinder.findComposerPhar();
	}
	
	public boolean isAvailable() {
		return php != null && phar != null;
	}

	public void setUp(IProject project) {
		
	}

	public CommandLine getCommand() {
		CommandLine cmd = new CommandLine(php);
		cmd.addArgument(phar);
		
		return cmd;
	}
}
