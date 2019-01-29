package edu.smith.cs.csc262.coopsh.apps;

import java.io.File;

import edu.smith.cs.csc262.coopsh.ShellEnvironment;
import edu.smith.cs.csc262.coopsh.Task;

public class Pwd extends Task {
	
	File workingDir;

	public Pwd(ShellEnvironment env, String[] args) {
		super(env, args);
		this.workingDir = env.currentDirectory;
	}

	@Override
	protected void update() {
		this.println(workingDir.getAbsolutePath());
		this.closeOutput();
		this.exit(0);
	}

}
