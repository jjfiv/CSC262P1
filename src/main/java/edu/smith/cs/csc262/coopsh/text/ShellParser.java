package edu.smith.cs.csc262.coopsh.text;

import java.util.ArrayList;
import java.util.List;

import edu.smith.cs.csc262.coopsh.ShellEnvironment;

public class ShellParser {
	
	/**
	 * Parse a line input by the user into a list of Token objects.
	 * @param env - where to get variables / the current directory.
	 * @param input - what the user typed.
	 * @return - a list of tokens, all variables setup.
	 */
	public static List<Token> parse(ShellEnvironment env, String input) {
		ShellLexer lexer = new ShellLexer(input);
		List<Token> raw = lexer.finish();
		List<Token> subst = substitute(env, raw);
		return subst;
	}
	
	public static List<List<Token>> splitAtOperator(String op, List<Token> input) {
		List<List<Token>> commands = new ArrayList<>();
		List<Token> current = new ArrayList<>();
		for (Token t : input) {
			if (t.what == TokenType.Operator && t.contents.equals(op)) {
				commands.add(current);
				current = new ArrayList<>();
			} else {
				current.add(t);
			}
		}
		if (!current.isEmpty()) {
			commands.add(current);
		}
		return commands;
	}
	
	public static List<String> toStrings(List<Token> input) {
		List<String> parts = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		for (Token t : input) {
			if (t.what == TokenType.Whitespace) {
				if (current.length() > 0) {
					parts.add(current.toString());
					current.setLength(0);
				}
			} else {
				current.append(t.contents);
			}
		}
		if (current.length() > 0) {
			parts.add(current.toString());
		}
		
		return parts;
	}
	
	
	
	/**
	 * Recursive variable substitution.
	 * @param env - environment containing variables.
	 * @param tokens - list of tokens parsed, maybe with variables (e.g., $USER) inside.
	 * @return a flattened list of tokens with variables substituted for values!
	 */
	public static List<Token> substitute(ShellEnvironment env, List<Token> tokens) {
		List<Token> output = new ArrayList<>(tokens.size());
		for (Token token : tokens) {
			if (token.what == TokenType.Variable) {
				// Work on variables:

				String value = env.getVariable(token.contents);
				if (value == null) {
					throw new MissingVariableError(token.contents);
				}
				output.add(Token.id(value));
				
			} else if (token.what == TokenType.DoubleQuotedStr) {
				// Work on variables inside double-quotes:

				ShellLexer lexer = new ShellLexer(token.contents);
				List<Token> before = lexer.finish();
				output.add(Token.id(Token.join(substitute(env, before))));
			} else {
				output.add(token);
			}
		}
		
		return output;
	}
}
