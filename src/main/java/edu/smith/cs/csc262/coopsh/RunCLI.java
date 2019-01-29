package edu.smith.cs.csc262.coopsh;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Main method for running a command-line-interface.
 * @author jfoley
 *
 */
public class RunCLI {

	/**
	 * The main entry point for our "Cooperative" Shell.
	 * @param args - command line arguments to this program. Ignore.
	 * @throws IOException - if we can't read from System.in.
	 */
	public static void main(String[] args) throws IOException {
		// A shell has some kind of state: variables, current working directory.
		// Represent that in this "env" variable.
		ShellEnvironment env = new ShellEnvironment(new File("."));
		// We need to read the input the user types here.
		// We wrap System.in in a BufferedReader so we can get a line at a time.
		BufferedReader cmds = new BufferedReader(new InputStreamReader(System.in));
		
		// Take many lines of input from the user.
		while(true) {
			// Print half a line, and flush so it really goes out.
			System.out.print(env.getOrElse("PS1", "$ "));
			// UNIX systems often don't print anything until they see a newline, because they're lazy.
			System.out.flush();
			
			String line = cmds.readLine();
			// Quit by many means:
			if (line == null || "exit".equals(line)) break;
			
			// Call execute on the environment to find Task objects for everything that was typed.
			List<Task> jobs;
			try {
				// Parse what they typed and get ready to run it.
				jobs = env.execute(line);
				// Run all the jobs until termination.
				executeRoundRobin(jobs);
			} catch (Exception e) {
				// If something bad happens, print out and let the user try again.
				System.err.println(e.getMessage());
				e.printStackTrace(System.err);
				continue;
			}
			
			// Print out some kind of information about the job.
			for (Task t : jobs) {
				System.out.println(t.getClass().getSimpleName()
						+" .steps="+t.timesScheduled
						+" .inputLines="+t.inputLines
						+" .outputLines="+t.outputLines);
			}
		} //-- while(true) of input
	}     //-- main
	
	/**
	 * This executes a round-robin scheduler for our cooperative tasks:
	 * @param jobs - a list of tasks (probably a pipe sequence).
	 */
	public static void executeRoundRobin(List<Task> jobs) {
		while(true) {
			// Unless we encounter a task still working, consider ourselves done.
			boolean allDone = true;
			
			// For every task:
			for (Task t : jobs) {
				if (!t.isDone()) {
					// Update it if not done:
					allDone = false;
					t.executeSingleTimeSlice();
				} else if (t.exitCode != 0) {
					// Exit if it was an error!
					System.err.println("Task "+t+" exited with exitCode="+t.exitCode);
					return;					
				}
			}
			// If they're all done, then we're good here.
			if (allDone) {
				return;
			}
		} //-- while(true)
	}     //-- executeRoundRobin
}
