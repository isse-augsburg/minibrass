package isse.mbr.parsing;

import java.util.HashMap;
import java.util.Map;

public class MiniBrassVotingKeywords {
	public static final String VOTER_COUNT = "mbr_voter_count_";
	public static final String VOTER_ENUM = "mbr_voter_enum_";
	public static final String VOTER_STRING_NAMES = "mbr_voter_string_names_";
	
	protected Map<String, String> keywordLookup;
	
	public MiniBrassVotingKeywords() {
		keywordLookup = new HashMap<String, String>();
		keywordLookup.put("voterCount", VOTER_COUNT);
		keywordLookup.put("voterEnum", VOTER_ENUM);
		keywordLookup.put("voterStringNames", VOTER_STRING_NAMES);
	}

	public String lookup(String key) {
		return keywordLookup.get(key);
	}

	public boolean contains(String metaVariable) {
		return keywordLookup.containsKey(metaVariable);
	}
}
