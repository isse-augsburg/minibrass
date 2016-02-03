package isse.mbr.parsing;

import java.util.Scanner;


/**
 * Tokenizes an input stream of MiniBrass code
 * 
 * @author Alexander Schiendorfer
 *
 */
public class MiniBrassLexer {

	private Scanner scanner;
	private char currentChar;
	private String line;
	private int colPtr;

	// to be accessed from ATGs
	String lastIdent;
	int lastInt;
	double lastFloat;
	private boolean hasNext;

	// for better error messages 
	int lineNo;
	
	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
		scanner.useDelimiter("");
		hasNext = true;
		lineNo = 0;
	}

	public MiniBrassSymbol getNextSymbol() {
		if (!hasNext)
			return MiniBrassSymbol.EofSy;
		else {
			switch (currentChar) {
			case ',':
				return mv(MiniBrassSymbol.CommaSy);
			case ';':
				return mv(MiniBrassSymbol.SemicolonSy);
			case '(':
				return mv(MiniBrassSymbol.LeftParenSy);
			case ')':
				return mv(MiniBrassSymbol.RightParenSy);
			case '=':
				return mv(MiniBrassSymbol.EqualsSy);
			}

			if(currentChar == '.') {
				if(readNextChar() && currentChar == '.') {
					return MiniBrassSymbol.DotsSy;
				} else 
					return MiniBrassSymbol.NoSy;
				
			}  else if (Character.isDigit(currentChar)) {
				boolean floating = false;
				
				StringBuilder sb = new StringBuilder();
				boolean seenDot = false;
				sb.append(currentChar);
				while (readNextChar() && ( (currentChar == '.' && !floating) || Character.isDigit(currentChar))) {
					sb.append(currentChar);						
					floating = floating || (currentChar == '.');
				}
				String numberStr = sb.toString();
				if (floating) {
					try {
						lastFloat = Double.parseDouble(numberStr);
						return MiniBrassSymbol.FloatLitSy;
					} catch (Exception e) {
						System.err.println(
								"Could not parse floating point literal: " + numberStr + " (" + e.getMessage() + ")");
						return MiniBrassSymbol.NoSy;
					}
				} else {
					try {
						lastInt = Integer.parseInt(numberStr);
						return MiniBrassSymbol.IntLitSy;
					} catch (Exception e) {
						System.err.println(
								"Could not parse integer literal: " + numberStr + " (" + e.getMessage() + ")");
						return MiniBrassSymbol.NoSy;
					}
				}

			} else if(currentChar == '\'' || currentChar == '"') {
				char initChar = currentChar;
				StringBuilder sb = new StringBuilder();
				
				while(readNextChar() && currentChar != initChar) {
					sb.append(currentChar);
				}
				
				lastIdent = sb.toString();
			
				return mv(MiniBrassSymbol.StringLitSy);
			}
			else if (Character.isLetter(currentChar)) { // either an ident or
															// a keyword
				// while letters are coming, append them
				StringBuilder sb = new StringBuilder();
				sb.append(currentChar);

				while (readNextChar() && validIdent(currentChar)) {
					sb.append(currentChar);
				}
				eatWhiteSpace();
				String s = sb.toString();
				switch (s) {
				case "include":
					return MiniBrassSymbol.IncludeSy;
				case "type":
					return MiniBrassSymbol.TypeSy;
				case "pvs":
					return MiniBrassSymbol.PvsSy;
				case "PVS":
					return MiniBrassSymbol.PVSSy;
				case "set":
					return MiniBrassSymbol.SetSy;
				case "of":
					return MiniBrassSymbol.OfSy;
				case "int":
					return MiniBrassSymbol.IntSy;
				case "bool":
					return MiniBrassSymbol.BoolSy;
				case "float":
					return MiniBrassSymbol.FloatSy;
				}
				// otherwise it's an ident
				lastIdent = s;
				return MiniBrassSymbol.IdentSy;
			}
		}
		return MiniBrassSymbol.NoSy;
	}

	private boolean validIdent(char currentChar) {
		return Character.isUnicodeIdentifierPart(currentChar);
	}

	private void eatWhiteSpace() {
		while (Character.isWhitespace(currentChar)) {
			boolean b = readNextChar();
			if (!b)
				return;
		}
	}

	/**
	 * Returns the last symbol and moves the pointer to the next char (if
	 * applicable)
	 * 
	 * @param sym
	 * @return sym
	 */
	private MiniBrassSymbol mv(MiniBrassSymbol sym) {
		readNextChar();
		eatWhiteSpace();
		return sym;
	}

	public boolean readNextChar() {

		if (line == null || colPtr > line.length() - 1) {
			if (scanner.hasNextLine()) {
				line = scanner.nextLine().trim();
				System.out.println("Reading line ... "+line);
				colPtr = 0;
				++lineNo;
				if(line.isEmpty()){
					line = null;
					return readNextChar();
				}
			} else {
				hasNext = false;
				return false;
			}
		}
		currentChar = line.charAt(colPtr);
		++colPtr;
		
		if (currentChar == '%') { // line end comment
			line = null;
			return readNextChar();			
		} 
		if (currentChar == '/') {
			if ((colPtr + 1) <= line.length()-1) {
				char lookahead = line.charAt(colPtr+1);
				if(lookahead == '*') { // we're in comment mode until we see */
					colPtr += 1; // we're standing on * now
					boolean commEnded = false;
					boolean starSeen = false;
					
					while(!commEnded) {
						boolean b = readNextChar();
						if(!b)
							return false;
						
						if(currentChar == '*') {
							starSeen = true;
						} else  if(starSeen && currentChar != '/'){
							starSeen = false;
						} else if(currentChar == '/' && starSeen) {
							// the comment has ended here
							commEnded = true;
							return readNextChar();
						}
					}
				}
			}
		}
		return true;
	}

	public char getCurrentChar() {
		return currentChar;
	}

	public int getColPtr() {
		return colPtr;
	}

	public int getLineNo() {
		return lineNo;
	}

	public String getLastIdent() {
		return lastIdent;
	}

	public int getLastInt() {
		return lastInt;
	}

	public double getLastFloat() {
		return lastFloat;
	}

}
