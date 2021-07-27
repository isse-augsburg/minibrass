package isse.mbr.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import isse.mbr.model.types.ArrayType;
import isse.mbr.model.types.BoolType;
import isse.mbr.model.types.FloatType;
import isse.mbr.model.types.IntType;
import isse.mbr.model.types.IntervalType;
import isse.mbr.model.types.NumericValue;
import isse.mbr.model.types.PrimitiveType;
import isse.mbr.model.types.SetType;
import isse.mbr.model.types.StringType;
import isse.mbr.tools.execution.MiniZincTensor;
import isse.mbr.tools.execution.MiniZincVariable;

public class DznParser extends MiniBrassParser {


	private static List<MiniBrassSymbol> arraySymbols;

	public MiniZincVariable parseVariable(String dznLine) throws MiniBrassParseException {
		Scanner scanner = new Scanner(dznLine);
		lexer = new MiniBrassLexer();
		lexer.setScanner(scanner);
		lexer.readNextChar(); // to initialize
		//System.out.println("DZN Parsing: " + dznLine);
		// top level non-terminal, initialize before
		getNextSy();

		// first production
		MiniZincVariable variable = dznStatement();
		scanner.close();
		return variable;
	}

	/**
	 * dznStatement := IdentSy "=" dznLiteral
	 * @return
	 * @throws MiniBrassParseException
	 */
	private MiniZincVariable dznStatement() throws MiniBrassParseException {
		expectSymbol(MiniBrassSymbol.IdentSy);
		String varIdent = lexer.getLastIdent();
		getNextSy();
		lexer.startBuffering();
		expectSymbolAndNext(MiniBrassSymbol.EqualsSy);

		MiniZincVariable variable = new MiniZincVariable(varIdent);
		dznLiteral(variable);
		lexer.closeBuffer();

		String mznExpression = lexer.getBufferContent().replaceAll(";", "");
		variable.setMznExpression(mznExpression);
		expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);

		return variable;
	}

	/**
	 * dznStatement := array( | SetDznLiteral | IntDznLiteral
	 * @param variable
	 * @throws MiniBrassParseException
	 */
	private void dznLiteral(MiniZincVariable variable) throws MiniBrassParseException {
		arraySymbols = Arrays.asList(MiniBrassSymbol.ArraySy,  MiniBrassSymbol.Array1dSy, MiniBrassSymbol.Array2dSy,MiniBrassSymbol.Array3dSy, MiniBrassSymbol.Array4dSy, MiniBrassSymbol.Array5dSy,MiniBrassSymbol.Array6dSy);

		if (arraySymbols.stream().anyMatch(s -> s == currSy)) { // we're dealing with an array type now
			getArrayLit(variable);
		} else {
			getElement(variable);
		}
	}

	private void getElement(MiniZincVariable variable) throws MiniBrassParseException {
		boolean negative = false;
		if (currSy == MiniBrassSymbol.MinusSy) {
			negative = true;
			getNextSy();
		}

		if(currSy == MiniBrassSymbol.LeftCurlSy) {
			Set<Integer> setLit = getSetLit();
			variable.setValue(setLit);
			variable.setType(new SetType(new IntType()));
		} else if (currSy == MiniBrassSymbol.IntLitSy) {
			int litVal = lexer.getLastInt();
			getNextSy();

			if(negative)
				litVal *= (-1);

			if(currSy == MiniBrassSymbol.DotsSy) {
				getNextSy();
				negative = false;
				if(currSy == MiniBrassSymbol.MinusSy) {
					negative = true;
					getNextSy();
				}

				expectSymbol(MiniBrassSymbol.IntLitSy);
				int upper = lexer.getLastInt();
				if(negative)
					upper *= (-1);
				Set<Integer> setLit = getContinuousSet(litVal, upper);
				variable.setValue(setLit);
				variable.setType(new SetType(new IntType()));
				getNextSy();
			} else {
				variable.setValue(litVal);
				variable.setType(new IntType());
			}
		} else if(currSy == MiniBrassSymbol.FloatLitSy) {
			double litVal = lexer.getLastFloat();
			if(negative)
				litVal *= (-1);

			variable.setValue(litVal);
			variable.setType(new FloatType());
			getNextSy();
		} else if (currSy == MiniBrassSymbol.FalseLitSy || currSy == MiniBrassSymbol.TrueLitSy) {
			if(currSy == MiniBrassSymbol.TrueLitSy) {
				variable.setValue(true);
			} else {
				variable.setValue(false);
			}
			variable.setType(new BoolType());
			getNextSy();
		} else if (currSy == MiniBrassSymbol.StringLitSy) {
			String value = lexer.getLastIdent();
			variable.setValue(value);
			variable.setType(new StringType());
			getNextSy();
		}
	}

	private Set<Integer> getContinuousSet(int litVal, int upper) {
		Set<Integer> contSet = new HashSet<>();
		for(int i = litVal; i <= upper; ++i) {
			contSet.add(i);
		}
		return contSet;
	}

	private void getArrayLit(MiniZincVariable variable) throws MiniBrassParseException {
		expectSymbol(arraySymbols);
		MiniZincTensor tensor = new MiniZincTensor();

		int dim = 0;

		switch(currSy) {
		case Array1dSy:
			dim = 1;
			break;
		case Array2dSy:
			dim = 2;
			break;
		case Array3dSy:
			dim = 3;
			break;
		case Array4dSy:
			dim = 4;
			break;
		case Array5dSy:
			dim = 5;
			break;
		case Array6dSy:
			dim = 6;
			break;
		default:
			break;
		}
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.LeftParenSy);

		tensor.setDimension(dim);
		// read all the index sets
		List<PrimitiveType> indexSets = new ArrayList<>(dim);

		for(int i = 0; i < dim; ++i) {
			IntervalType it = intervalLitType();
			expectSymbolAndNext(MiniBrassSymbol.CommaSy);
			tensor.addIndexSet(it);
			indexSets.add(it);
		}
		boolean firstElement = true;
		int index = 0;
		expectSymbol(MiniBrassSymbol.LeftBracketSy);
		lexer.startBuffering();
		expectSymbolAndNext(MiniBrassSymbol.LeftBracketSy);

		while (currSy != MiniBrassSymbol.RightBracketSy) {
			MiniZincVariable var = new MiniZincVariable(variable.getName()+"_"+index);

			getElement(var);

			if(firstElement) { // has to define the element type of the tensor
				isse.mbr.model.types.MiniZincVarType elementType = (isse.mbr.model.types.MiniZincVarType) var.getType();
				ArrayType at = new ArrayType(elementType, indexSets);
				variable.setType(at);
				variable.setValue(tensor);
			}

			String mznExpr = lexer.closeBuffer().trim();
			// TODO this is kinda ugly, maybe we could do better
			if(!"true".equals(mznExpr) && !"false".equals(mznExpr))
				mznExpr = mznExpr.substring(0, mznExpr.length() - 1);
			var.setMznExpression(mznExpr);

			if(currSy == MiniBrassSymbol.CommaSy) {
				lexer.startBuffering();
				getNextSy();
			}
			tensor.addFlatValue(var);
			++index;
		} // parsed all elements


		expectSymbolAndNext(MiniBrassSymbol.RightBracketSy);
		expectSymbolAndNext(MiniBrassSymbol.RightParenSy);
	}

	private IntervalType intervalLitType() throws MiniBrassParseException {
		NumericValue lower;
		NumericValue upper;

		expectSymbol(MiniBrassSymbol.IntLitSy);
		lower = new NumericValue(lexer.getLastInt());
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.DotsSy);

		expectSymbol(MiniBrassSymbol.IntLitSy);
		upper = new NumericValue(lexer.getLastInt());
		getNextSy();
		return new IntervalType(lower, upper);
	}

	/**
	 * Currently, set types can only be integer
	 * @return
	 * @throws MiniBrassParseException
	 */
	private Set<Integer> getSetLit() throws MiniBrassParseException {
		expectSymbolAndNext(MiniBrassSymbol.LeftCurlSy);
		Set<Integer> returnSet = new HashSet<>();
		while (currSy != MiniBrassSymbol.RightCurlSy) {
			if(currSy == MiniBrassSymbol.CommaSy)
				getNextSy();
			expectSymbol(MiniBrassSymbol.IntLitSy);
			int intVal = lexer.getLastInt();
			returnSet.add(intVal);
			getNextSy();
		}
		expectSymbolAndNext(MiniBrassSymbol.RightCurlSy);
		return returnSet;
	}

}
