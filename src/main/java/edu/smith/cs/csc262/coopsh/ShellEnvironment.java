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

public class ShellEnvironment {
	public File currentDirectory;
	public Map<String, String> variables;
	
	public ShellEnvironment(File currentDirectory) {
		this.currentDirectory = currentDirectory;
		this.variables = new HashMap<>(System.getenv());
		this.executeChangeDir(".");
	}
	
	public Task makeProgram(String name, String[] args) {
		switch(name) {
			// Program: return a new Task object.
			case "cat":
				return new Cat(this, args);				
			case "pwd":
				return new Pwd(this, args);
			case "wc":
				return new WordCount(this, args);
			// cd is special.
			case "cd":
				if (args.length != 1) throw new IllegalArgumentException("More than one argument to cd!");
				executeChangeDir(args[0]);
				return null;
			// Agh!
			default:
				throw new RuntimeException("No such program: "+name);
		}
	}
	
	public File makeFile(String string) {
		if (string.startsWith("/")) {
			return new File(string);
		} else {
			return new File(this.currentDirectory, string);
		}
	}
	
	private void executeChangeDir(String string) {
		this.currentDirectory = makeFile(string);
		
		// Now make it meaningful...
		while (this.currentDirectory.isFile() || !this.currentDirectory.exists()) {
			this.currentDirectory = this.currentDirectory.getParentFile();
		}		
		try {
			this.currentDirectory = this.currentDirectory.getCanonicalFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

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
			
			for (int i=0; i<programs.size()-1; i++) {
				programs.get(i).pipesTo(programs.get(i+1));
			}
		}
		return programs;	
	}

	public String getVariable(String name) {
		return variables.get(name);
	}
	
	public void setVariable(String name, String value) {
		this.variables.put(name, value);
	}

	public String getOrElse(String name, String whenNull) {
		String found = getVariable(name);
		if (found == null) {
			return whenNull;
		}
		return found;
	}

	
	
	
	
}
