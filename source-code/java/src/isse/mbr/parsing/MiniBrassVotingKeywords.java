package isse.mbr.parsing;

import java.util.HashMap;
import java.util.Map;

public class MiniBrassVotingKeywords {

	// these constants represent prefixes for binding keywords that are used in generated MiniZinc code
	public static final String VOTER_COUNT = "mbr_voter_count_";
	public static final String VOTER_ENUM = "mbr_voter_enum_";
	public static final String VOTER_STRING_NAMES = "mbr_voter_string_names_";
	
	// these constants represent keywords that are actually in MiniBrass code
	public static final String VOTER_COUNT_SYNTACTIC = "voterCount";
	public static final String VOTER_ENUM_SYNTACTIC = "voterEnum";
	public static final String VOTER_STRING_SYNTACTIC = "voterStringNames";
	
	protected Map<String, String> keywordLookup;
	
	public MiniBrassVotingKeywords() {
		keywordLookup = new HashMap<>();
		keywordLookup.put(VOTER_COUNT_SYNTACTIC, VOTER_COUNT);
		keywordLookup.put(VOTER_ENUM_SYNTACTIC, VOTER_ENUM);
		keywordLookup.put(VOTER_STRING_SYNTACTIC, VOTER_STRING_NAMES);
		// a synonym for voterStringNames
		keywordLookup.put("voterIdentifiers", VOTER_STRING_NAMES);
	}

	public String lookup(String key) {
		return keywordLookup.get(key);
	}

	public boolean contains(String metaVariable) {
		return keywordLookup.containsKey(metaVariable);
	}
}
