package edu.smith.cs.csc262.coopsh.text;

import java.util.ArrayList;
import java.util.List;

// cat hello.txt
// ls [dir]
// cd
// wc -l foo.txt
// echo "Hello World $USER"
// echo 'Hello World'
public class ShellLexer {
	public void error(String msg) {
		throw new LexError(msg+": "+this.toString());
	}
	private char[] data;
	int position;
	
	public ShellLexer(String line) {
		this.data = line.toCharArray();
		this.position = 0;
	}
	
	private int peek() {
		if (position < data.length) {
			return data[position];
		}
		return -1;
	}
	public int getc() {
		int x = peek();
		position++;
		return x;
	}
	
	public int remaining() {
		return data.length - position;
	}
	
	// rest="abcd" 
	// (then .getc()=> "a") 
	// then rest="bcd"
	public String rest() {
		if (position >= data.length) {
			return "";
		}
		return new String(data, position, this.remaining());
	}
	
	// "abcd".consume(2) => "ab", rest="cd"
	public String consume(int amt) {
		String out = new String(data, position, amt);
		position += amt;
		return out;
	}
	
	// "foo]", query=']' -> "foo"
	public String consumeUntilExclusive(char query) {
		int start = position;
		StringBuilder sb = new StringBuilder();
		for (; position < data.length; position++) {
			if (data[position] == query) {
				position++;
				return sb.toString();
			}
			sb.append(data[position]);
		}
		// Rewind, report error.
		position = start;
		error("Could not find matching '"+query+"'");
		return null;
	}
	
	// "  two" -> "  "
	public String consumeWhitespace() {
		StringBuilder sb = new StringBuilder();
		for (; position < data.length; position++) {
			if (!Character.isWhitespace(data[position])) {
				break;
			}
			sb.append(data[position]);
		}
		return sb.toString();
	}
	
	public boolean isBreak(char ch) {
		if (Character.isWhitespace(ch)) {
			return true;
		}
		switch(ch) {
			case '\'':
			case '"':
			case '=':
			case '|':
			case '&':
			case '<':
			case '>':
			case ';':
			case '$':
			case '#':
				return true;
		}
		return false;
	}
	
	// "abcds " -> "abcds"
	public String consumeUntilBreak() {
		StringBuilder sb = new StringBuilder();
		for (; position < data.length; position++) {
			if (isBreak(data[position])) {
				break;
			}
			sb.append(data[position]);
		}
		return sb.toString();
	}
	
	// "hello", 'hello', "hello\n1\n2\n3"
	public String consumeQuoted() {
		char quote = (char) getc();
		StringBuilder sb = new StringBuilder();
		for (; position < data.length; position++) {
			if (data[position] == '\\') {
				getc();
				
				// what is it escaping?
				int escaped = peek();
				switch (escaped) {	
					case -1:
						error("unexpected end of line in escape");
					case '"':
					case '\'':
						sb.append((char) getc());
						break;
					case 'n':
						sb.append('\n');
						getc();
						break;
					case 't':
						sb.append('\t');
						getc();
						break;
					default:
						error("unhandled escape character: '"+(char) escaped+"'");	
				}
			}
			if (data[position] == quote) {
				position++;
				break;
			}
			sb.append(data[position]);
		}
		return sb.toString();
	}
	
	public String toString() {
		return "ShellLexer(@"+position+", ..."+rest()+")";
	}
	
	
	// ${USER} or $USER
	public Token lexVariable() {
		// Should only call this when we see a $
		if(this.getc() != '$') throw new AssertionError();
		
		int peek = this.peek();
		String varName;
		if (peek == '{') {
			this.getc();
			varName = consumeUntilExclusive('}');
		} else {
			varName = consumeUntilBreak();
		}
		return new Token(TokenType.Variable, varName);		
	}
	
	public Token nextToken() {
		int nextItem = this.peek();
		if (nextItem == -1) {
			return new Token(TokenType.Error, "EOF");
		}
		char next = (char) nextItem;
		
		if (Character.isWhitespace(next)) {
			return new Token(TokenType.Whitespace, consumeWhitespace());
		}
		
		switch(next) {
			case '\'':
				return new Token(TokenType.SingleQuotedStr, consumeQuoted());
			case '"':
				return new Token(TokenType.DoubleQuotedStr, consumeQuoted());
			case '=':
			case '|':
			case '&':
			case '<':
			case '>':
			case ';':
				return new Token(TokenType.Operator, this.consume(1));
			case '$':
				return this.lexVariable();
			case '#':
				return new Token(TokenType.Comment, this.consume(this.remaining()));
			default:
				return new Token(TokenType.Identifier, this.consumeUntilBreak());	
		}
	}
	
	public List<Token> finish() {
		ArrayList<Token> output = new ArrayList<Token>();
		
		while(true) {
			Token tok = this.nextToken();
			if (tok.isDone()) {
				break;
			}
			if (tok.isError()) {
				error(tok.contents);
			}
			output.add(tok);
		}
		return output;
	}
}
