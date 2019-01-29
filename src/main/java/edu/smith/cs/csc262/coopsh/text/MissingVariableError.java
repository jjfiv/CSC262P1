package edu.smith.cs.csc262.coopsh.text;

public class MissingVariableError extends RuntimeException {
	/**
	 * Since Exceptions are {@linkplain java.lang.Serializable}, we must have a version.
	 */
	private static final long serialVersionUID = 1L;
	
	public MissingVariableError(String which) {
		super("Missing Variable: "+which);
	}
}
