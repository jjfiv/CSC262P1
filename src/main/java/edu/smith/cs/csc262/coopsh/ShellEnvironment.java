package edu.smith.cs.csc262.coopsh;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.smith.cs.csc262.coopsh.apps.Cat;
import edu.smith.cs.csc262.coopsh.apps.Pwd;
import edu.smith.cs.csc262.coopsh.apps.WordCount;
import edu.smith.cs.csc262.coopsh.text.ShellParser;
import edu.smith.cs.csc262.coopsh.text.Token;

/**
 * This represents the state of our shell. The current working directory is a
 * File, and there is a Map of environment variables. "cd" is already
 * implemented, and if you use {@linkplain #makeFile(String)} instead of
 * creating new {@linkplain java.io.File} objects directly, it will try to use
 * this current working directory.
 * 
 * You will mostly be editing the {@linkplain #makeProgram(String, String[])}
 * method.
 * 
 * @author jfoley
 *
 */
public class ShellEnvironment {
	/**
	 * This is the answer for pwd.
	 */
	public File currentDirectory;
	/**
	 * This is the answer for env.
	 */
	public Map<String, String> variables;

	/**
	 * Create a shell environment from the current directory.
	 * 
	 * @param currentDirectory - try {@code new File(".")}.
	 */
	public ShellEnvironment(File currentDirectory) {
		this.currentDirectory = currentDirectory;
		this.variables = new HashMap<>(System.getenv());
		this.executeChangeDir(".");
	}

	/**
	 * This is the core method of this environment. This is the "exec" system call
	 * for our toy OS here.
	 * 
	 * @param name - the name of the program to run.
	 * @param args - the arguments to pass to that program.
	 * @return a Task object.
	 */
	public Task makeProgram(String name, String[] args) {
		switch (name) {
		// Program: return a new Task object.
		case "cat":
			return new Cat(this, args);
		case "pwd":
			return new Pwd(this, args);
		case "wc":
			return new WordCount(this, args);
		// cd is special.
		case "cd":
			if (args.length != 1)
				throw new IllegalArgumentException("More than one argument to cd!");
			executeChangeDir(args[0]);
			return null;
		// Agh!
		default:
			throw new RuntimeException("No such program: " + name);
		}
	}

	/**
	 * This tries to append the string to the current directory if it makes sense...
	 * 
	 * @param path - the path the user typed in.
	 * @return a file from the local directory or an absolute path depending on
	 *         whether it starts with a /
	 */
	public File makeFile(String path) {
		if (path.startsWith("/")) {
			return new File(path);
		} else {
			return new File(this.currentDirectory, path);
		}
	}

	/**
	 * This is how "cd" works in our shell. It's kind of magical.
	 * 
	 * @param path
	 */
	private void executeChangeDir(String path) {
		// Well, now we have a path.
		this.currentDirectory = makeFile(path);

		// Now make it meaningful...
		// Only cd into directories...
		while (this.currentDirectory.isFile() || !this.currentDirectory.exists()) {
			this.currentDirectory = this.currentDirectory.getParentFile();
		}
		// Ask the real OS to clean up the path for us.
		// It's a syscall, at least on *nix so it might fail.
		try {
			this.currentDirectory = this.currentDirectory.getCanonicalFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Don't worry about the implementation here. This parses a subset of shell
	 * syntax, and finds the statements separated by pipes.
	 * 
	 * @param line a string
	 * @return a list of tasks, that were maybe separated by pipes before.
	 */
	public List<Task> execute(String line) {
		List<Token> input = ShellParser.parse(this, line);

		List<Task> programs = new ArrayList<>();
		for (List<Token> part : ShellParser.splitAtOperator("|", input)) {
			List<String> spec = ShellParser.toStrings(part);

			String program = spec.get(0);
			String[] arguments = spec.subList(1, spec.size()).toArray(new String[0]);
			Task maybe = makeProgram(program, arguments);
			if (maybe != null) {
				programs.add(maybe);
			}
		}

		if (programs.size() != 0) {
			// All programs "pipe" to the Console in our OS.
			programs.add(new ConsolePrinter(this));

			for (int i = 0; i < programs.size() - 1; i++) {
				programs.get(i).pipesTo(programs.get(i + 1));
			}
		}
		return programs;
	}

	/**
	 * Look up an environment variable in this environment.
	 * @param name - the name of the variable.
	 * @return the string value.
	 */
	public String getVariable(String name) {
		return variables.get(name);
	}

	/**
	 * Set a variable in the environment. We may never use this...
	 * @param name - the name of the variable.
	 * @param value - the new value.
	 */
	public void setVariable(String name, String value) {
		this.variables.put(name, value);
	}

	/**
	 * Look up a variable in the environment, or use a backup.
	 * @param name - the variable name.
	 * @param whenNull - the backup variable.
	 * @return the value or the backup.
	 */
	public String getOrElse(String name, String whenNull) {
		String found = getVariable(name);
		if (found == null) {
			return whenNull;
		}
		return found;
	}
}
