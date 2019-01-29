package edu.smith.cs.csc262.coopsh.text;

import java.util.List;

/**
 * This class is used to represent some part of the user's input.
 * @author jfoley
 *
 */
public class Token {
	/**
	 * This is the type of the token.
	 */
	public TokenType what;
	/**
	 * This is the contents that were actually typed.
	 */
	public String contents;
	/**
	 * Standard constructor: needs both a type and contents.
	 * @param what - the type of the token.
	 * @param contents - the contents of the token.
	 */
	public Token(TokenType what, String contents) {
		this.what = what;
		this.contents = contents;
	}
	@Override
	public String toString() {
		return what+": ["+contents+"]";
	}
	public boolean isError() {
		return this.what == TokenType.Error;
	}
	public boolean isDone() {
		return this.what == TokenType.Error && this.contents.equals("EOF");
	}
	public static Token id(String what) {
		return new Token(TokenType.Identifier, what);
	}
	
	public static String join(List<Token> tokens) {
		StringBuilder sb = new StringBuilder();
		for (Token tok : tokens) {
			sb.append(tok.contents);
		}
		return sb.toString();
	}
}