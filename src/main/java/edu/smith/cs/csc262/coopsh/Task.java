package edu.smith.cs.csc262.coopsh;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Cooperative Task abstract class. Each Task may send output to another
 * {@linkplain #outputTask} and it buffers input in {@linkplain input}.
 * 
 * @author jfoley
 *
 */
public abstract class Task {
	/**
	 * Unless you're working with really big files, this should be enough.
	 */
	public static int TOO_MUCH_TIME = 100_000;
	/**
	 * The task it is talking to, or maybe null.
	 */
	protected Task outputTask = null;
	/**
	 * The input it has received (a task may ignore!).
	 */
	protected LinkedList<InputLine> input = new LinkedList<>();
	/**
	 * This is how we know whether a task has finished.
	 */
	public Integer exitCode = null;
	/**
	 * How many times has this task been scheduled?
	 */
	int timesScheduled = 0;
	
	/**
	 * How many input lines did this task receive?
	 */
	int inputLines = 0;
	
	/**
	 * How many output lines did this task send?
	 */
	int outputLines = 0;
	
	/**
	 * Command line arguments.
	 */
	protected String[] args;
	/**
	 * For getting the current directory, variables, etc.
	 */
	protected ShellEnvironment env;
	
	/**
	 * All tasks are created with a possibly empty list of string arguments!
	 * @param args - much like public static void main!
	 */
	public Task(ShellEnvironment env, String[] args) {
		this.env = env;
		this.args = args;
	}

	/**
	 * On UNIX systems, a nonzero exit code indicates failure.
	 * Make sure all your "Tasks" call exit(0) or exit(-1) as appropriate.
	 * @param x - the exit code.
	 */
	public void exit(int x) {
		this.exitCode = x;
	}
	
	/**
	 * Is this Task complete?
	 * @return whether exit has been called.
	 */
	public boolean isDone() {
		return this.exitCode != null;
	}
	
	/**
	 * Caught exception! Exit this task with exitCode=1
	 * @param message - what happened?
	 * @param e - where did it happen?
	 */
	protected void caughtFatalException(String message, Exception e) {
		System.err.println(message);
		e.printStackTrace(System.err);
		this.exit(1);
	}
	
	/**
	 * Sending output to another task is a "sys-call" in this system. Therefore it
	 * is private. The public methods here are the "C library" that we get to use.
	 * 
	 * @param x - the InputLine data structure.
	 */
	private void sendOutput(InputLine x) {
		outputTask.input.addLast(x);
		outputTask.inputLines++;
		this.outputLines++;
	}

	/**
	 * Any object can be printed.
	 * @param o - what to print? Maybe a string already.
	 */
	public void println(Object o) {
		this.sendOutput(new InputLine(Objects.toString(o)));
	}

	/**
	 * When done with updates, we call "closeOutput".
	 * We prevent it from being called multiple times.
	 */
	public void closeOutput() {
		this.sendOutput(InputLine.EOF);
		this.outputTask = null;
	}

	/**
	 * Do a bite-size piece of work. DO NOT BLOCK.
	 */
	protected abstract void update();
	
	/**
	 * This is the public API for scheduling using our "Kernel".
	 */
	public void executeSingleTimeSlice() {
		this.timesScheduled++;
		if (this.timesScheduled > TOO_MUCH_TIME) {
			throw new RuntimeException("Probably forgot to call this.exit(status) in Task definition: " + this.getClass().getSimpleName());
		}
		this.update();
	}

	/**
	 * Connect the output of this task to the input of the next.
	 * @param next - the task to receive output from this.
	 * @return the task you've passed in.
	 */
	public Task pipesTo(Task next) {
		if (this.outputTask != null) {
			throw new AssertionError("Can only pipe to a single task!");
		}
		this.outputTask = next;
		return next;
	}

	/**
	 * Starting at the "front" task, collect them into a list.
	 * @return a list of Task objects.
	 */
	public List<Task> collectTasks() {
		ArrayList<Task> output = new ArrayList<>();
		
		// Tasks are a linked-list. Collect them.
		for (Task t = this; t != null; t = t.outputTask) {
			output.add(t);
		}
		return output;
	}
}
