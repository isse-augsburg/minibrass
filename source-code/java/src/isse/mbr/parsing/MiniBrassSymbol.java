package isse.mbr.parsing;

/**
 * Defines the symbols that are output by the lexer
 * @author Alexander Schiendorfer
 *
 */
public enum MiniBrassSymbol {
	CommaSy,            // , 
	SemicolonSy,        // ;
	ColonSy,            // :
	LeftParenSy,        // (
	RightParenSy,       // )
	LeftBracketSy,      // [
	RightBracketSy,     // ]
	LeftAngleSy,        // <
	RightAngleSy,       // >
	ArrowSy,            // ->
	LeftCurlSy,         // {
	RightCurlSy,        // }
	EqualsSy,           // =
	StringLitSy,        // any string literal enclosed by ' or "
	IdentSy, 
	IncludeSy,          // include
	SolveSy,            // solve
	TypeSy,             // type
	PvsSy,              // pvs
	PVSTypeSy,              // PVSType (for types)
	SetSy,              // set 
	ArraySy,            // array
	OfSy,               // of
	IntSy,              // int
	BoolSy,             // bool
	FloatSy,            // float
	IntLitSy,           // any integer
	FloatLitSy,         // any float
	DotsSy,             // .. 
	ParamsSy,           // "params"
	InSy,               // "in"
	InstantiatesSy,     // "instantiates"
	WithSy,             // "with"
	MorphismSy,         // "morph"
	NewSy, 				// "new"
	NoSy,
	EofSy                 
}
