package edu.smith.cs.csc262.coopsh;

/**
 * This task exists at the end of a pipeline, in order to print output to the screen.
 * @author jfoley
 *
 */
public class ConsolePrinter extends Task {
	/**
	 * Create a new console printer. This is a "secret" task that prints for us.
	 * @param env - the shell environment (has working directory).
	 */
	public ConsolePrinter(ShellEnvironment env) {
		super(env, new String[0]);
	}

	@Override
	public void update() {
		// Get the head of our input list if it's available.
		InputLine maybe = this.input.poll();
		
		// Still waiting for input. Done for this time-slice.
		if (maybe == null) {
			return;
		}
		// When we stop receiving input, we're done.
		if (maybe.isEndOfFile()) {
			this.exit(0);
			return;
		}
		
		// Otherwise, just print one line!
		System.out.println("o: "+maybe.get());
		// Track this manually, since it's not using our "OS" println.
		this.outputLines++;
	}

	
}
