package edu.smith.cs.csc262.coopsh;

/**
 * InputLine is a class that wraps a String
 * @author jfoley
 *
 */
public class InputLine {
	/**
	 * This is a constant EOF available (end-of-file).
	 */
	public static InputLine EOF = new InputLine(null);
	
	/**
	 * This is some actual "printed data" or an end-of-file, aka null.
	 */
	private String data;
	
	/**
	 * Create a new InputLine object.
	 * @param data - the data to store.
	 */
	public InputLine(String data) {
		this.data = data;
	}
	
	/**
	 * Get non-EOF data or crash.
	 * @return the line of input.
	 */
	public String get() {
		if (isEndOfFile()) throw new RuntimeException("EOF Error!");
		return this.data;
	}
	
	/**
	 * Check if this is an EOF.
	 * @return true if end of file.
	 */
	public boolean isEndOfFile() {
		return this.data == null;
	}
}
