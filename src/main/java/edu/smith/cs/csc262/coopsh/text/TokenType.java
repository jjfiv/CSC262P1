package edu.smith.cs.csc262.coopsh.text;

public enum TokenType {
	Identifier,
	SingleQuotedStr,
	DoubleQuotedStr,
	Whitespace,
	Operator,
	Comment,
	Variable,
	// = < > >> | ;
	Newline,
	Error,
}