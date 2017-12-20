package isse.mbr.MiniBrassWeb.shared.parsing;

/**
 * Defines the symbols that are output by the lexer
 * @author Alexander Schiendorfer
 *
 */
public enum MiniBrassSymbol {
	CommaSy,            // , 
	SemicolonSy,        // ;
	ColonSy,            // :
	DoubleColonSy,      // ::
	MinusSy,            // - 
	LeftParenSy,        // (
	RightParenSy,       // )
	LeftBracketSy,      // [
	RightBracketSy,     // ]
	LeftAngleSy,        // <
	RightAngleSy,       // >
	ArrowSy,            // ->
	LeftCurlSy,         // {
	RightCurlSy,        // }
	AsteriskSy,         // *
	LexSy,              // lex
	ParetoSy,           // pareto
	EqualsSy,           // =
	StringLitSy,        // any string literal enclosed by ' or "
	IdentSy, 
	IncludeSy,          // include
	SolveSy,            // solve
	TypeSy,             // type
	PvsSy,              // pvs
	PVSTypeSy,          // PVSType (for types)
	DefaultSy,          // default
	WrappedBySy,        // wrappedBy
	GeneratedBySy,      // generatedBy
	GeneratedSy,        // generated
	RepresentsSy,       // represents
	SetSy,              // set 
	MSetSy,             // mset (multiset)
	ArraySy,            // array
	OfSy,               // of
	IntSy,              // int
	BoolSy,             // bool
	FloatSy,            // float
	IntLitSy,           // any integer
	FloatLitSy,         // any float
	DotsSy,             // .. 
	ParamsSy,           // "params"
	OffersSy,           // "offers"
	HeuristicsSy,       // "heuristics"
	InSy,               // "in"
	InstantiatesSy,     // "instantiates"
	WithSy,             // "with"
	MorphismSy,         // "morph"
	NewSy, 				// "new
	VotingSy,           // "vote"
	OutputSy,           // "output"
	BindSy,             // "bind"
	ToSy,               // "to"
	NoSy,
	EofSy     
}
