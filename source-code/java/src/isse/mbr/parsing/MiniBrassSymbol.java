package isse.mbr.parsing;

/**
 * Defines the symbols that are output by the lexer
 * @author Alexander Schiendorfer
 *
 */
public enum MiniBrassSymbol {
	CommaSy,            // , 
	SemicolonSy,        // ;
	LeftParenSy,        // (
	RightParenSy,       // )
	EqualsSy,           // =
	StringLitSy,        // any string literal enclosed by ' or "
	IdentSy, 
	IncludeSy,          // include
	TypeSy,             // type
	PvsSy,              // pvs
	PVSSy,              // PVS (for types)
	SetSy,              // set 
	OfSy,               // of
	IntSy,              // int
	BoolSy,             // bool
	FloatSy,            // float
	IntLitSy,           // any integer
	FloatLitSy,         // any float
	DotsSy,             // .. 
	NoSy,
	EofSy                 
}
