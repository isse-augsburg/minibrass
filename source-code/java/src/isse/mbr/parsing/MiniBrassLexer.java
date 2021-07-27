package isse.mbr.parsing;

import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Logger;


/**
 * Tokenizes an input stream of MiniBrass code
 *
 * @author Alexander Schiendorfer
 *
 */
public class MiniBrassLexer {

	private final static Logger LOGGER = Logger.getGlobal();

	private Scanner scanner;
	private char currentChar;
	private String line;
	private int colPtr;
	private Stack<StringBuilder> buffers;
	private boolean buffering = false;


	// to be accessed from ATGs
	String bufferContent;
	String lastIdent;
	int lastInt;
	double lastFloat;
	private boolean hasNext;
	private char lastStringLitChar;

	// for better error messages
	int lineNo;

	public MiniBrassLexer() {
		buffers = new Stack<>();
	}

	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
		scanner.useDelimiter("");
		hasNext = true;
		lineNo = 0;
	}

	public MiniBrassSymbol getNextSymbol() {
		eatWhiteSpace();
		if (!hasNext)
			return MiniBrassSymbol.EofSy;
		else {
			switch (currentChar) {
			case ',':
				return mv(MiniBrassSymbol.CommaSy);
			case '*':
				return mv(MiniBrassSymbol.AsteriskSy);
			case ';':
				return mv(MiniBrassSymbol.SemicolonSy);
			case '.':
				if( peekNextChar() == '.') {
					readNextChar();
					return mv(MiniBrassSymbol.DotsSy);
				} else
				return mv(MiniBrassSymbol.DotSy);
			case ':':
				if( peekNextChar() == ':') {
					readNextChar();
					return mv(MiniBrassSymbol.DoubleColonSy);
				} else
				return mv(MiniBrassSymbol.ColonSy);
			case '(':
				return mv(MiniBrassSymbol.LeftParenSy);
			case ')':
				return mv(MiniBrassSymbol.RightParenSy);
			case '=':
				return mv(MiniBrassSymbol.EqualsSy);
			case '<':
				return mv(MiniBrassSymbol.LeftAngleSy);
			case '>':
				return mv(MiniBrassSymbol.RightAngleSy);
			case '{':
				return mv(MiniBrassSymbol.LeftCurlSy);
			case '}':
				return mv(MiniBrassSymbol.RightCurlSy);
			case '[':
				return mv(MiniBrassSymbol.LeftBracketSy);
			case ']':
				return mv(MiniBrassSymbol.RightBracketSy);
			case '-':
				if(readNextChar())
					if(currentChar == '>')
						return mv(MiniBrassSymbol.ArrowSy);
					else {
						eatWhiteSpace();
						// could be MinusSy if needed
						return MiniBrassSymbol.MinusSy;
					}
			}

			if (Character.isDigit(currentChar)) {
				boolean isFloatValue = false;

				StringBuilder sb = new StringBuilder();
				sb.append(currentChar);
				while (readNextChar() && ( (currentChar == '.' && !isFloatValue) || Character.isDigit(currentChar))) {
					if(currentChar == '.') { // this could be the start of a dotsSy
						Character peekNext = peekNextChar();
						if(peekNext == '.') {
							break; // we stand on a '.' now, do not move
						}
					}
					sb.append(currentChar);
					isFloatValue = isFloatValue || (currentChar == '.');
				}
				String numberStr = sb.toString();
				if (isFloatValue) {
					try {
						lastFloat = Double.parseDouble(numberStr);
						return MiniBrassSymbol.FloatLitSy;
					} catch (Exception e) {
						LOGGER.severe(
								"Could not parse floating point literal: " + numberStr + " (" + e.getMessage() + ")");
						return MiniBrassSymbol.NoSy;
					}
				} else {
					try {
						lastInt = Integer.parseInt(numberStr);
						return MiniBrassSymbol.IntLitSy;
					} catch (Exception e) {
						LOGGER.severe(
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
				lastStringLitChar = initChar;
				return mv(MiniBrassSymbol.StringLitSy);
			}
			else if (Character.isLetter(currentChar) || currentChar == '_') { // either an ident or
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
				case "solve":
					return MiniBrassSymbol.SolveSy;
				case "type":
					return MiniBrassSymbol.TypeSy;
				case "PVS":
					return MiniBrassSymbol.PvsSy;
				case "PVSType":
					return MiniBrassSymbol.PVSTypeSy;
				case "params":
					return MiniBrassSymbol.ParamsSy;
				case "instantiates":
					return MiniBrassSymbol.InstantiatesSy;
				case "default":
					return MiniBrassSymbol.DefaultSy;
				case "generatedBy":
					return MiniBrassSymbol.GeneratedBySy;
				case "generated":
					return MiniBrassSymbol.GeneratedSy;
				case "wrappedBy":
					return MiniBrassSymbol.WrappedBySy;
				case "offers":
					return MiniBrassSymbol.OffersSy;
				case "heuristics":
					return MiniBrassSymbol.HeuristicsSy;
				case "lex":
					return MiniBrassSymbol.LexSy;
				case "represents":
					return MiniBrassSymbol.RepresentsSy;
				case "bind":
					return MiniBrassSymbol.BindSy;
				case "to":
					return MiniBrassSymbol.ToSy;
				case "pareto":
					return MiniBrassSymbol.ParetoSy;
				case "direct":
					return MiniBrassSymbol.DirectSy;
				case "morph":
					return MiniBrassSymbol.MorphismSy;
				case "new":
					return MiniBrassSymbol.NewSy;
				case "in":
					return MiniBrassSymbol.InSy;
				case "with":
					return MiniBrassSymbol.WithSy;
				case "set":
					return MiniBrassSymbol.SetSy;
				case "mset":
					return MiniBrassSymbol.MSetSy;
				case "array1d":
					return MiniBrassSymbol.Array1dSy;
				case "array2d":
					return MiniBrassSymbol.Array2dSy;
				case "array3d":
					return MiniBrassSymbol.Array3dSy;
				case "array4d":
					return MiniBrassSymbol.Array4dSy;
				case "array5d":
					return MiniBrassSymbol.Array5dSy;
				case "array6d":
					return MiniBrassSymbol.Array6dSy;
				case "array":
					return MiniBrassSymbol.ArraySy;
				case "of":
					return MiniBrassSymbol.OfSy;
				case "int":
					return MiniBrassSymbol.IntSy;
				case "bool":
					return MiniBrassSymbol.BoolSy;
				case "float":
					return MiniBrassSymbol.FloatSy;
				case "string":
					return MiniBrassSymbol.StringSy;
				case "vote":
					return MiniBrassSymbol.VotingSy;
				case "output":
					return MiniBrassSymbol.OutputSy;
				case "false":
					return MiniBrassSymbol.FalseLitSy;
				case "true":
					return MiniBrassSymbol.TrueLitSy;
				}
				// otherwise it's an ident
				lastIdent = s;
				return MiniBrassSymbol.IdentSy;
			}
		}
		return MiniBrassSymbol.NoSy;
	}

	/**
	 * Pretty ugly code duplication for now
	 * Only works for the next char only (if we are at the end of a line -> return '\n')
	 **/
	private Character peekNextChar() {

		if (line == null || colPtr > line.length() - 1) {
			return null;
		}
		char peekedChar = line.charAt(colPtr);
		return peekedChar;
	}

	private boolean validIdent(char currentChar) {
		return Character.isUnicodeIdentifierPart(currentChar) || currentChar == '-' || currentChar == '_';
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

	/**
	 * Useful if we want to read a string also verbatim while parsing it
	 * */
	public void startBuffering() {
		buffers.add(new StringBuilder());
		buffering = true;
	}

	public String closeBuffer() {
		bufferContent = buffers.pop().toString();
		if(buffers.isEmpty())
			buffering = false;
		return bufferContent;
	}

	private void reportToBuffer() {
		if(buffering)
			for (StringBuilder buffer : buffers)
				buffer.append(currentChar);
	}

	public boolean readNextChar() {
		reportToBuffer();
		boolean endOfLine = false;

		if (line == null || ( colPtr > line.length() - 1)) {
			endOfLine = line != null && ( colPtr > line.length() - 1);
			if (scanner.hasNextLine()) {
				line = scanner.nextLine().trim();
				// System.out.println("Reading line ... "+line);
				colPtr = 0;
				++lineNo;
				while(line.isEmpty()){
					if(!scanner.hasNextLine()) {
						hasNext = false;
						return false;
					} else {
						line = scanner.nextLine().trim();
						++lineNo;
					}
				}
			} else {
				hasNext = false;
				return false;
			}
		}

		if(endOfLine) {
			currentChar = '\n';
			return true;
		}
		currentChar = line.charAt(colPtr);

		++colPtr;

		if (currentChar == '%') { // line end comment
			line = null;
			return readNextChar();
		}
		if (currentChar == '/') {
			if ((colPtr) <= line.length()-1) {
				char lookahead = line.charAt(colPtr);
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

	public String readVerbatimUntil(char c) {
		StringBuilder sb = new StringBuilder();
		while(currentChar != c) {
			sb.append(currentChar);
			readNextChar();
			if(!hasNext)
				break;
		}
		return sb.toString();
	}

	public char getLastStringLitChar() {
		return lastStringLitChar;
	}

	public void setLastStringLitChar(char lastStringLitChar) {
		this.lastStringLitChar = lastStringLitChar;
	}

	public String getBufferContent() {
		return bufferContent;
	}

}
